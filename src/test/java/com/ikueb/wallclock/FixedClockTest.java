/*
 * Copyright 2016 h-j-k. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ikueb.wallclock;

import static com.ikueb.wallclock.TimeMatchers.withinSecond;
import static java.time.LocalTime.MIDNIGHT;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FixedClockTest {

    // private static final ZoneId DST = ZoneId.of("US/Eastern");
    private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");
    private static final List<ZoneId> NO_DST = Arrays.asList(TOKYO, UTC);
    private static final LocalDate NEW_YEAR_2016 = LocalDate.of(2016, 1, 1);
    private static final LocalDateTime COUNTDOWN = NEW_YEAR_2016.atTime(MIDNIGHT);
    private static final Duration ONE_NANO = Duration.ofNanos(1);
    private static final Period ONE_DAY = Period.ofDays(1);


    private static final class TestCase {

        private final String description;
        private final Supplier<FixedClock> supplier;

        public TestCase(String description, Supplier<FixedClock> supplier) {
            this.description = description;
            this.supplier = supplier;
        }

        public FixedClock getClock() {
            return supplier.get();
        }

        @Override
        public String toString() {
            return description;
        }
    }

    private static Iterator<Object[]> getTestCases(List<ZoneId> zones) {
        Map<String, Function<ZoneId, Supplier<FixedClock>>> map = new HashMap<>();
        map.put("instant",
                zone -> () -> new FixedClock(Instant.now(), zone));
        map.put("zoned date and time",
                zone -> () -> new FixedClock(ZonedDateTime.now(zone)));
        return map.entrySet().stream()
                .flatMap(x -> zones.stream()
                                .map(zone -> Collections.singletonMap(new TestCase(
                                        x.getKey(), x.getValue().apply(zone)), zone)))
                .flatMap(m -> m.entrySet().stream())
                .map(entry -> new Object[] { entry.getKey(), entry.getValue() })
                .iterator();
    }

    @DataProvider(name = "no-dst-now")
    public static Iterator<Object[]> getNoDstTestCases() {
        return getTestCases(NO_DST);
    }

    @Test(dataProvider = "no-dst-now")
    public void testNoDstNow(TestCase testCase, ZoneId expectedZoneId) {
        FixedClock testClock = testCase.getClock();
        assertThat(testClock.getZone(), equalTo(expectedZoneId));
        assertThat(testClock.date(), equalTo(LocalDate.now(expectedZoneId)));
        assertThat(testClock.time(), withinSecond(LocalTime.now(expectedZoneId)));
        assertThat(testClock.dateTime(), withinSecond(LocalDateTime.now(expectedZoneId)));
        assertThat(testClock.zonedDateTime(),
                withinSecond(ZonedDateTime.now(expectedZoneId)));
        assertThat(testClock.instant(),
                withinSecond(Clock.system(expectedZoneId).instant()));
    }

    @Test(dataProvider = "no-dst-now")
    public void testNoDstSetters(TestCase testCase, ZoneId expectedZoneId) {
        FixedClock testClock = testCase.getClock();
        LocalDate initDate = testClock.date();
        LocalTime initTime = testClock.time();
        ZonedDateTime newDateZdt = ZonedDateTime.of(NEW_YEAR_2016, initTime,
                expectedZoneId);
        Stream.of(NEW_YEAR_2016, NEW_YEAR_2016)
                .map(testClock::setDate)
                .forEach(v -> doTest(v, NEW_YEAR_2016, initTime, newDateZdt));
        ZonedDateTime newTimeZdt = newDateZdt.with(MIDNIGHT);
        Stream.of(MIDNIGHT, MIDNIGHT)
                .map(testClock::setTime)
                .forEach(v -> doTest(v, NEW_YEAR_2016, MIDNIGHT, newTimeZdt));
        ZonedDateTime initZdt = ZonedDateTime.of(initDate, initTime, expectedZoneId);
        Stream.of(initZdt.toLocalDateTime(), initZdt.toLocalDateTime())
                .map(testClock::setDateTime)
                .forEach(v -> doTest(v, initDate, initTime, initZdt));
    }

    @Test(dataProvider = "no-dst-now")
    public void testNoDstOffset(TestCase testCase, ZoneId expectedZoneId) {
        FixedClock testClock = testCase.getClock();
        LocalDate initDate = testClock.date();
        LocalTime initTime = testClock.time();
        ZonedDateTime newDateZdt = ZonedDateTime.of(initDate.plus(ONE_DAY), initTime,
                expectedZoneId);
        Stream.of(Duration.ofDays(1), Duration.ZERO)
                .map(testClock::offset)
                .forEach(v -> doTest(v, initDate.plus(ONE_DAY), initTime, newDateZdt));
        ZonedDateTime newTimeZdt = newDateZdt.with(initTime.plus(ONE_NANO));
        Stream.of(ONE_NANO, Duration.ZERO)
                .map(testClock::offset)
                .forEach(v -> doTest(v, newDateZdt.toLocalDate(), initTime.plus(ONE_NANO),
                                        newTimeZdt));
    }

    @DataProvider(name = "no-dst-fixed")
    public static Iterator<Object[]> getClockAndZoneFixed() {
        return Stream.of(
                test("fixed date", () -> new FixedClock(NEW_YEAR_2016),
                        ZonedDateTime.of(NEW_YEAR_2016, MIDNIGHT, UTC)),
                test("fixed date", () -> new FixedClock(NEW_YEAR_2016, TOKYO),
                        ZonedDateTime.of(NEW_YEAR_2016, MIDNIGHT, TOKYO)),
                test("fixed time", () -> new FixedClock(MIDNIGHT),
                        ZonedDateTime.of(LocalDate.now(UTC), MIDNIGHT, UTC)),
                test("fixed time", () -> new FixedClock(MIDNIGHT, TOKYO),
                        ZonedDateTime.of(LocalDate.now(TOKYO), MIDNIGHT, TOKYO)),
                test("fixed date and time", () -> new FixedClock(COUNTDOWN),
                        COUNTDOWN.atZone(UTC)),
                test("fixed date and time", () -> new FixedClock(COUNTDOWN, TOKYO),
                        COUNTDOWN.atZone(TOKYO)))
                .iterator();
    }

    private static Object[] test(String description, Supplier<FixedClock> testClockSupplier,
            ZonedDateTime expectedZdt) {
        return new Object[] { new TestCase(description, testClockSupplier), expectedZdt };
    }

    @Test(dataProvider = "no-dst-fixed")
    public void testNoDstFixed(TestCase testCase, ZonedDateTime expectedZdt) {
        doTest(testCase.getClock(), expectedZdt.toLocalDate(), expectedZdt.toLocalTime(),
                expectedZdt);
    }

    private static void doTest(FixedClock testClock, LocalDate date, LocalTime time,
            ZonedDateTime zonedDateTime) {
        assertThat(testClock.date(), equalTo(date));
        assertThat(testClock.time(), equalTo(time));
        assertThat(testClock.dateTime(), equalTo(date.atTime(time)));
        assertThat(testClock.zonedDateTime(), equalTo(zonedDateTime));
        assertThat(testClock.instant(), equalTo(zonedDateTime.toInstant()));
    }

    @Test
    public void testEquality() {
        FixedClock testClock = new FixedClock();
        assertThat(testClock, equalTo(testClock));
        assertThat(testClock, equalTo(new FixedClock((WallClock) testClock)));
        assertThat(testClock, not(equalTo(new FixedClock(Instant.EPOCH, UTC))));
        assertThat(testClock, not(equalTo(Clock.systemUTC())));
        assertThat(testClock.getZone(), equalTo(UTC));
        Clock testClockUTC = testClock.withZone(UTC);
        assertThat(testClockUTC, not(sameInstance(testClock)));
        assertThat(testClockUTC, equalTo(testClock));
        assertThat(testClockUTC, equalTo(new FixedClock(testClockUTC)));
        assertThat(testClockUTC.hashCode(), equalTo(testClock.hashCode()));
        assertThat(testClockUTC.toString(), equalTo(testClock.toString()));
    }
}
