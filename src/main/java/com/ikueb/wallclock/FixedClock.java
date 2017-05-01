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
import java.util.Objects;

/**
 * A mutable {@link WallClock} implementation that provides a fixed instant. <br>
 * Both dates and times are settable.
 *
 * @see AbstractFixedWallClock
 */
public final class FixedClock extends AbstractFixedWallClock implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Creates an instance based on the current UTC date and time.
     */
    public FixedClock() {
        super();
    }

    /**
     * Creates an instance based on the given {@link Clock}.
     *
     * @param clock the clock to reference, not null
     */
    public FixedClock(Clock clock) {
        super(clock);
    }

    /**
     * Creates an instance based on the given {@link WallClock}.
     *
     * @param wallClock the clock to reference, not null
     */
    public FixedClock(WallClock wallClock) {
        super(wallClock);
    }

    /**
     * Creates an instance based on the given instant and time-zone. <br>
     * The immutable version of this can be {@link Clock#fixed(Instant, ZoneId)}.
     *
     * @param instant the instant to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public FixedClock(Instant instant, ZoneId zoneId) {
        super(instant, zoneId);
    }

    /**
     * Creates an instance based on the date's midnight at UTC.
     *
     * @param date the date to use, not null
     */
    public FixedClock(LocalDate date) {
        super(date);
    }

    /**
     * Creates an instance based on the date's midnight at the specified time-zone.
     *
     * @param date the date to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public FixedClock(LocalDate date, ZoneId zoneId) {
        super(date, zoneId);
    }

    /**
     * Creates an instance based on the time today at UTC.
     *
     * @param time the time to use, not null
     */
    public FixedClock(LocalTime time) {
        super(time);
    }

    /**
     * Creates an instance based on the time today at the specified time-zone.
     *
     * @param time the time to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public FixedClock(LocalTime time, ZoneId zoneId) {
        super(time, zoneId);
    }

    /**
     * Creates an instance based on the date and time at UTC.
     *
     * @param dateTime the date and time to use, not null
     */
    public FixedClock(LocalDateTime dateTime) {
        super(dateTime);
    }

    /**
     * Creates an instance based on the date and time at the specified time-zone.
     *
     * @param dateTime the date and time to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public FixedClock(LocalDateTime dateTime, ZoneId zoneId) {
        super(dateTime, zoneId);
    }

    /**
     * Creates an instance based on the date, time and specified time-zone.
     *
     * @param source the date, time and time-zone to use, not null
     */
    public FixedClock(ZonedDateTime source) {
        super(source);
    }

    /**
     * Sets the date of this clock, the same day is treated as no-op.
     *
     * @param date the date to set, not null
     * @return this instance
     */
    @Override
    public FixedClock setDate(LocalDate date) {
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
    public FixedClock setTime(LocalTime time) {
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
    public FixedClock setDateTime(LocalDateTime dateTime) {
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
    public FixedClock offset(Duration duration) {
        if (!duration.isZero()) {
            set(zonedDateTime().plus(duration));
        }
        return this;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return new FixedClock(zonedDateTime().withZoneSameInstant(zone));
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
