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
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TickingClockTest {

    private static final ZoneId NO_DST = ZoneId.of("Asia/Tokyo");

    @DataProvider(name = "standard-test")
    public Iterator<Object[]> getData() {
        return Stream.of(
                    toTest(TickingClock.atUTC(), UTC),
                    toTest(new TickingClock(), UTC),
                    toTest(new TickingClock(NO_DST), NO_DST))
                .iterator();
    }

    private static Object[] toTest(TickingClock testClock, ZoneId zoneId) {
        return new Object[] { testClock, zoneId };
    }

    @Test(dataProvider = "standard-test")
    private void test(TickingClock testClock, ZoneId zoneId) {
        assertThat(testClock.getZone(), equalTo(zoneId));
        assertThat(testClock.date(), equalTo(LocalDate.now(zoneId)));
        assertThat(testClock.time(), withinSecond(LocalTime.now(zoneId)));
        assertThat(testClock.dateTime(), withinSecond(LocalDateTime.now(zoneId)));
        assertThat(testClock.zonedDateTime(), withinSecond(ZonedDateTime.now(zoneId)));
        assertThat(testClock.instant(), withinSecond(Clock.system(zoneId).instant()));
    }

    @Test
    public void testEquality() {
        assertThat(TickingClock.atUTC(), sameInstance(TickingClock.atUTC()));
        assertThat(TickingClock.atUTC(), equalTo(TickingClock.atUTC()));
        TickingClock newClock = new TickingClock();
        assertThat(TickingClock.atUTC(), equalTo(newClock));
        assertThat(TickingClock.atUTC().hashCode(), equalTo(newClock.hashCode()));
        assertThat(TickingClock.atUTC(), not(sameInstance(newClock)));
        assertThat(TickingClock.atUTC(), not(equalTo(Clock.systemUTC())));
    }

    @Test
    public void testWithZone() {
        assertThat(TickingClock.atUTC(), not(equalTo(TickingClock.atUTC().withZone(NO_DST))));
    }

    @Test
    public void testOffset() {
        TickingClock newClock = new TickingClock();
        assertThat(newClock, sameInstance(newClock.offset(Duration.ZERO)));
        TickingClock offsetClock = newClock.offset(Duration.ofSeconds(1));
        assertThat(offsetClock.date(), equalTo(newClock.date()));
        assertThat(offsetClock.time(), withinSecond(newClock.time()));
    }
}
