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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A mutable {@link WallClock} abstract class that provides a fixed instant. <br>
 * Note: This is essentially a glorified {@link ZonedDateTime} wrapper class. There is an
 * additional underlying {@link Instant} field, but that is really a cache of calling
 * {@link ZonedDateTime#toInstant()} whenever the date and/or time is updated. The
 * time-zone is encoded within the {@link ZonedDateTime} instance, and is an immutable
 * property.
 */
public abstract class AbstractFixedWallClock extends Clock
        implements FixedWallClock, Serializable {

    private static final long serialVersionUID = 1L;
    private ZonedDateTime zdt = ZonedDateTime.now();
    private Instant instant = zdt.toInstant();

    /**
     * Creates an instance based on the current UTC date and time.
     */
    public AbstractFixedWallClock() {
        this(Clock.systemUTC());
    }

    /**
     * Creates an instance based on the given {@link Clock}.
     *
     * @param clock the clock to reference, not null
     */
    public AbstractFixedWallClock(Clock clock) {
        this(ZonedDateTime.now(clock));
    }

    /**
     * Creates an instance based on the given {@link WallClock}.
     *
     * @param wallClock the clock to reference, not null
     */
    public AbstractFixedWallClock(WallClock wallClock) {
        this(wallClock.zonedDateTime());
    }

    /**
     * Creates an instance based on the given instant and time-zone. <br>
     * The immutable version of this can be {@link Clock#fixed(Instant, ZoneId)}.
     *
     * @param instant the instant to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public AbstractFixedWallClock(Instant instant, ZoneId zoneId) {
        this(ZonedDateTime.ofInstant(instant, zoneId));
    }

    /**
     * Creates an instance based on the date's midnight at UTC.
     *
     * @param date the date to use, not null
     */
    public AbstractFixedWallClock(LocalDate date) {
        this(date, ZoneOffset.UTC);
    }

    /**
     * Creates an instance based on the date's midnight at the specified time-zone.
     *
     * @param date the date to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public AbstractFixedWallClock(LocalDate date, ZoneId zoneId) {
        this(date.atTime(LocalTime.MIDNIGHT), zoneId);
    }

    /**
     * Creates an instance based on the time today at UTC.
     *
     * @param time the time to use, not null
     */
    public AbstractFixedWallClock(LocalTime time) {
        this(time, ZoneOffset.UTC);
    }

    /**
     * Creates an instance based on the time today at the specified time-zone.
     *
     * @param time the time to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public AbstractFixedWallClock(LocalTime time, ZoneId zoneId) {
        this(time.atDate(LocalDate.now(zoneId)), zoneId);
    }

    /**
     * Creates an instance based on the date and time at UTC.
     *
     * @param dateTime the date and time to use, not null
     */
    public AbstractFixedWallClock(LocalDateTime dateTime) {
        this(dateTime, ZoneOffset.UTC);
    }

    /**
     * Creates an instance based on the date and time at the specified time-zone.
     *
     * @param dateTime the date and time to use, not null
     * @param zoneId the time-zone to use, not null
     */
    public AbstractFixedWallClock(LocalDateTime dateTime, ZoneId zoneId) {
        this(ZonedDateTime.of(dateTime, zoneId));
    }

    /**
     * Creates an instance based on the date, time and specified time-zone.
     *
     * @param source the date, time and time-zone to use, not null
     */
    public AbstractFixedWallClock(ZonedDateTime source) {
        set(source);
    }

    @Override
    public ZonedDateTime zonedDateTime() {
        return zdt;
    }

    @Override
    public Instant instant() {
        return instant;
    }

    @Override
    public ZoneId getZone() {
        return zdt.getZone();
    }

    @Override
    public int hashCode() {
        return zdt.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + zdt;
    }

    /**
     * @param clock the other clock to test
     * @return {@code true} if underlying {@link ZonedDateTime} instances are equal
     */
    boolean equals(AbstractFixedWallClock clock) {
        return zdt.equals(clock.zonedDateTime());
    }

    /**
     * @param source the source to set to, not null
     */
    void set(ZonedDateTime source) {
        synchronized (zdt) {
            zdt = Objects.requireNonNull(source);
            instant = zdt.toInstant();
        }
    }
}
