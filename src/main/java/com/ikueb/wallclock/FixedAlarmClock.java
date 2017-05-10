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

import java.io.Serializable;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A mutable {@link WallClock} implementation that provides a fixed instant. <br>
 * Both dates and times are settable.
 *
 * @see AbstractFixedWallClock
 */
public final class FixedAlarmClock extends AbstractFixedWallClock
        implements AlarmFixedWallClock, Serializable {

    private static final long serialVersionUID = 1L;

    private final transient ConcurrentHashMap<AlarmClockListener, Set<Temporal>> listeners = new ConcurrentHashMap<>();

    /**
     * Creates an instance based on the current UTC date and time.
     */
    public FixedAlarmClock() {
        super();
    }

    /**
     * Creates an instance based on the given {@link Clock}.
     *
     * @param clock the clock to reference, not null
     */
    public FixedAlarmClock(Clock clock) {
        super(clock);
    }

    /**
     * Creates an instance based on the given {@link WallClock}.
     *
     * @param wallClock the clock to reference, not null
     */
    public FixedAlarmClock(WallClock wallClock) {
        super(wallClock);
    }

    /**
     * Creates an instance based on the given instant and time-zone. <br>
     * The immutable version of this can be {@link Clock#fixed(Instant, ZoneId)}.
     *
     * @param instant the instant to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public FixedAlarmClock(Instant instant, ZoneId zoneId) {
        super(instant, zoneId);
    }

    /**
     * Creates an instance based on the date's midnight at UTC.
     *
     * @param date the date to use, not null
     */
    public FixedAlarmClock(LocalDate date) {
        super(date);
    }

    /**
     * Creates an instance based on the date's midnight at the specified time-zone.
     *
     * @param date the date to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public FixedAlarmClock(LocalDate date, ZoneId zoneId) {
        super(date, zoneId);
    }

    /**
     * Creates an instance based on the time today at UTC.
     *
     * @param time the time to use, not null
     */
    public FixedAlarmClock(LocalTime time) {
        super(time);
    }

    /**
     * Creates an instance based on the time today at the specified time-zone.
     *
     * @param time the time to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public FixedAlarmClock(LocalTime time, ZoneId zoneId) {
        super(time, zoneId);
    }

    /**
     * Creates an instance based on the date and time at UTC.
     *
     * @param dateTime the date and time to use, not null
     */
    public FixedAlarmClock(LocalDateTime dateTime) {
        super(dateTime);
    }

    /**
     * Creates an instance based on the date and time at the specified time-zone.
     *
     * @param dateTime the date and time to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public FixedAlarmClock(LocalDateTime dateTime, ZoneId zoneId) {
        super(dateTime, zoneId);
    }

    /**
     * Creates an instance based on the date, time and specified time-zone.
     *
     * @param source the date, time and time-zone to use, not null
     */
    public FixedAlarmClock(ZonedDateTime source) {
        super(source);
    }

    /**
     * Sets the date of this clock, the same day is treated as no-op.
     *
     * @param date the date to set, not null
     * @return this instance
     */
    @Override
    public FixedAlarmClock setDate(LocalDate date) {
        if (!date.equals(date())) {
            set(zonedDateTime().with(date));
        }
        return this;
    }

    /**
     * Sets the time of this clock, the same time is treated as no-op.
     *
     * @param time the time to set, not null
     * @return this instance
     */
    @Override
    public FixedAlarmClock setTime(LocalTime time) {
        if (!time.equals(time())) {
            set(zonedDateTime().with(time));
        }
        return this;
    }

    /**
     * Sets the date and time of this clock, the same date and time is
     * treated as no-op.
     *
     * @param dateTime the date and time to set, not null
     * @return this instance
     */
    @Override
    public FixedAlarmClock setDateTime(LocalDateTime dateTime) {
        if (!dateTime.equals(dateTime())) {
            set(zonedDateTime().with(dateTime));
        }
        return this;
    }

    /**
     * Adds the duration to this clock, zero-length durations are treated
     * as no-op.
     *
     * @param duration the duration to add, not null
     * @return this instance
     */
    @Override
    public FixedAlarmClock offset(Duration duration) {
        if (!duration.isZero()) {
            set(zonedDateTime().plus(duration));
        }
        return this;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new FixedAlarmClock(zonedDateTime().withZoneSameInstant(zone));
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    void set(ZonedDateTime source) {
        super.set(source);
        listeners.forEach(1, (listener, times) -> {
            if (times.isEmpty()) {
                listener.alarmTriggered(source);
                return;
            }
            List<Temporal> temporals = Arrays.asList(source.toLocalDate(),
                    source.toLocalTime(), source.toLocalDateTime());
            times.parallelStream()
                    .filter(temporals::contains)
                    .forEach(v -> listener.alarmTriggered(source));
        });
    }

    @Override
    public void alarm(AlarmClockListener listener) {
        listeners.putIfAbsent(listener, new HashSet<>());
    }

    @Override
    public void alarm(AlarmClockListener listener, LocalDate dateToAlarm) {
        listeners.computeIfAbsent(listener, k -> new HashSet<>()).add(dateToAlarm);
    }

    @Override
    public void alarm(AlarmClockListener listener, LocalTime timeToAlarm) {
        listeners.computeIfAbsent(listener, k -> new HashSet<>()).add(timeToAlarm);
    }

    @Override
    public void alarm(AlarmClockListener listener, LocalDateTime dateTimeToAlarm) {
        listeners.computeIfAbsent(listener, k -> new HashSet<>()).add(dateTimeToAlarm);
    }

    @Override
    public void snooze(AlarmClockListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void snooze(AlarmClockListener listener, LocalDate dateToSnooze) {
        listeners.computeIfPresent(listener,
                (k, v) -> { v.remove(dateToSnooze); return v.isEmpty() ? null : v; });
    }

    @Override
    public void snooze(AlarmClockListener listener, LocalTime timeToSnooze) {
        listeners.computeIfPresent(listener,
                (k, v) -> { v.remove(timeToSnooze); return v.isEmpty() ? null : v; });
    }

    @Override
    public void snooze(AlarmClockListener listener, LocalDateTime dateTimeToSnooze) {
        listeners.computeIfPresent(listener,
                (k, v) -> { v.remove(dateTimeToSnooze); return v.isEmpty() ? null : v; });
    }
}
