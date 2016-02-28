package com.ikueb.wallclock;

import static java.time.LocalTime.NOON;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.testng.Assert.fail;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalQuery;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class FixedAlarmClockTest {

    private static final ZoneId NO_DST = ZoneId.of("Asia/Tokyo");
    private static final LocalDate DATE = LocalDate.of(2016, 1, 1);
    private static final List<LocalDate> DATES = Arrays.asList(DATE, DATE.plusDays(1));
    private static final LocalTime TIME = NOON;
    private static final List<LocalTime> TIMES = Arrays.asList(TIME, TIME.plusHours(1));
    private static final LocalDateTime DATETIME = LocalDateTime.of(DATE, TIME);
    private static final List<LocalDateTime> DATETIMES = Arrays.asList(DATETIME,
                                                                        DATETIME.plusDays(1));
    private static final AlarmClockListener NO_ALARM = zdt -> fail("Not expecting any alarms.");

    private static final class TestCase<T extends Temporal> implements AlarmClockListener {

        private final String description;
        private final Supplier<FixedAlarmClock> supplier;
        private final TemporalQuery<T> query;
        private final Function<T, BiConsumer<FixedAlarmClock, AlarmClockListener>> alarmer;
        private final Function<T, BiConsumer<FixedAlarmClock, AlarmClockListener>> snoozer;
        private final BiConsumer<FixedAlarmClock, T> trigger;

        private AlarmClockListener listener;

        public TestCase(String description, Supplier<FixedAlarmClock> supplier,
                TemporalQuery<T> query,
                Function<T, BiConsumer<FixedAlarmClock, AlarmClockListener>> alarmer,
                Function<T, BiConsumer<FixedAlarmClock, AlarmClockListener>> snoozer,
                BiConsumer<FixedAlarmClock, T> trigger) {
            this.description = description;
            this.supplier = supplier;
            this.query = query;
            this.alarmer = alarmer;
            this.snoozer = snoozer;
            this.trigger = trigger;
        }

        public FixedAlarmClock alarmFor(List<T> temporals) {
            FixedAlarmClock clock = supplier.get();
            temporals.stream().map(alarmer).forEach(v -> v.accept(clock, this));
            return clock;
        }

        public void snoozeFor(FixedAlarmClock clock, List<T> temporals) {
            temporals.stream().map(snoozer).forEach(v -> v.accept(clock, this));
        }

        public TestCase<T> expect(T expected) {
            listener = zdt -> assertThat(zdt.query(query), equalTo(expected));
            return this;
        }

        public TestCase<T> expectSilence() {
            listener = NO_ALARM;
            return this;
        }

        public void trigger(FixedAlarmClock clock, T temporal) {
            trigger.accept(clock, temporal);
        }

        @Override
        public void alarmTriggered(ZonedDateTime zdt) {
            listener.alarmTriggered(zdt);
        }

        @Override
        public String toString() {
            return description;
        }
    }

    @DataProvider(name = "test")
    public Iterator<Object[]> getTestCases() {
        return Stream.of(
                test("date", DATES, () -> new FixedAlarmClock(LocalDate.now(UTC)),
                        LocalDate::from,
                        date -> (clock, listener) -> clock.alarm(listener, date),
                        date -> (clock, listener) -> clock.snooze(listener, date),
                        (clock, temporal) -> clock.setDate(temporal)),
                test("time", TIMES,
                        () -> new FixedAlarmClock(LocalTime.now(UTC).withNano(0)),
                        LocalTime::from,
                        time -> (clock, listener) -> clock.alarm(listener, time),
                        time -> (clock, listener) -> clock.snooze(listener, time),
                        (clock, temporal) -> clock.setTime(temporal)),
                test("date and time", DATETIMES,
                        () -> new FixedAlarmClock(LocalDateTime.now(UTC).withNano(0)),
                        LocalDateTime::from,
                        dateTime -> (clock, listener) -> clock.alarm(listener, dateTime),
                        dateTime -> (clock, listener) -> clock.snooze(listener, dateTime),
                        (clock, temporal) -> clock.setDateTime(temporal)),
                test("date and time, for all updates", DATETIMES,
                        () -> new FixedAlarmClock(ZonedDateTime.now(UTC).withNano(0)),
                        LocalDateTime::from,
                        x -> (clock, listener) -> clock.alarm(listener),
                        x -> (clock, listener) -> clock.snooze(listener),
                        (clock, temporal) -> clock.setDateTime(temporal)))
                .iterator();
    }

    @Test(dataProvider = "test")
    public <T extends Temporal> void testAlarmAndSnooze(TestCase<T> testCase,
            List<T> expected) {
        FixedAlarmClock clock = testCase.alarmFor(expected);
        expected.forEach(t -> testCase.expect(t).trigger(clock, t));
        testCase.snoozeFor(clock, expected);
        expected.forEach(t -> testCase.expectSilence().trigger(clock, t));
    }

    private static <T extends Temporal> Object[] test(String description,
            List<T> expected, Supplier<FixedAlarmClock> supplier, TemporalQuery<T> query,
            Function<T, BiConsumer<FixedAlarmClock, AlarmClockListener>> alarmer,
            Function<T, BiConsumer<FixedAlarmClock, AlarmClockListener>> snoozer,
            BiConsumer<FixedAlarmClock, T> trigger) {
        return new Object[] {
                new TestCase<T>(description, supplier, query, alarmer, snoozer, trigger),
                expected };
    }

    @Test
    public void testSetters() {
        assertThat(new FixedAlarmClock(DATE, UTC).setDate(DATE),
            not(equalTo(new FixedAlarmClock(DATE, NO_DST).setDate(DATE))));
        assertThat(new FixedAlarmClock(TIME, UTC).setTime(TIME),
            not(equalTo(new FixedAlarmClock(TIME, NO_DST).setTime(TIME))));
        assertThat(new FixedAlarmClock(DATETIME, UTC).setDateTime(DATETIME),
            not(equalTo(new FixedAlarmClock(DATETIME, NO_DST).setDateTime(DATETIME))));
    }

    @Test
    public void testOffset() {
        FixedAlarmClock testClock = new FixedAlarmClock();
        assertThat(testClock.offset(Duration.ZERO), sameInstance(testClock));
        Instant instant = testClock.instant();
        assertThat(testClock.offset(Duration.ofNanos(1)).instant(),
                not(equalTo(instant)));
    }

    @Test
    public void testEquality() {
        FixedAlarmClock testClock = new FixedAlarmClock();
        assertThat(testClock, equalTo(testClock));
        assertThat(testClock, equalTo(new FixedAlarmClock((WallClock) testClock)));
        assertThat(testClock, not(equalTo(new FixedAlarmClock(Instant.EPOCH, UTC))));
        assertThat(testClock, not(equalTo(Clock.systemUTC())));
        assertThat(testClock.getZone(), equalTo(UTC));
        Clock testClockUTC = testClock.withZone(UTC);
        assertThat(testClockUTC, not(sameInstance(testClock)));
        assertThat(testClockUTC, equalTo(testClock));
        assertThat(testClockUTC, equalTo(new FixedAlarmClock(testClockUTC)));
        assertThat(testClockUTC.hashCode(), equalTo(testClock.hashCode()));
        assertThat(testClockUTC.toString(), equalTo(testClock.toString()));
    }
}
