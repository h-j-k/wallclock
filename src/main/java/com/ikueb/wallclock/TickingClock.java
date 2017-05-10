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
import java.time.*;
import java.util.Objects;

/**
 * An immutable, ticking {@link WallClock} implementation that also extends from
 * {@link Clock}.<br>
 *
 * @implNote This is essentially a wrapper for an underlying {@link Clock}
 * instance, and there are more immediate ways of obtaining date and times from one, e.g.
 * {@link LocalDate#now(Clock)}. This is meant to be a convenience class providing a
 * non-fixed implementation of {@link WallClock}. {@link #atUTC()} is likely to be more
 * useful providing a singleton instance of UTC system clock.
 */
public final class TickingClock extends Clock implements WallClock, Serializable {

    private static final long serialVersionUID = 1L;
    private final transient Clock clock;

    /**
     * Bases a new clock with the time-zone {@link ZoneOffset#UTC}.
     */
    public TickingClock() {
        this(ZoneOffset.UTC);
    }

    /**
     * Bases a new clock with the given time-zone.
     *
     * @param zoneId the time-zone to use, not null
     */
    public TickingClock(ZoneId zoneId) {
        this(Clock.system(zoneId));
    }

    /**
     * Bases a new clock with the given one.
     *
     * @param clock the clock to use, not null
     */
    private TickingClock(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public ZonedDateTime zonedDateTime() {
        return ZonedDateTime.now(clock);
    }

    @Override
    public LocalDate date() {
        return LocalDate.now(clock);
    }

    @Override
    public LocalTime time() {
        return LocalTime.now(clock);
    }

    @Override
    public LocalDateTime dateTime() {
        return LocalDateTime.now(clock);
    }

    @Override
    public Instant instant() {
        return clock.instant();
    }

    @Override
    public ZoneId getZone() {
        return clock.getZone();
    }

    @Override
    public Clock withZone(ZoneId zoneId) {
        return new TickingClock(zoneId);
    }

    /**
     * Returns either a new {@link WallClock} with a non-zero {@code duration} offset,
     * else this.
     *
     * @param duration the duration to offset, not null
     * @return either a new {@link WallClock} with a non-zero {@code duration} offset,
     * else this
     */
    @Override
    public TickingClock offset(Duration duration) {
        return duration.isZero() ? this : new TickingClock(Clock.offset(clock, duration));
    }

    @Override
    public boolean equals(Object o) {
        return o == this
                || (o instanceof TickingClock && ((TickingClock) o).clock.equals(clock));
    }

    @Override
    public int hashCode() {
        return clock.hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "@" + zonedDateTime();
    }

    /**
     * An enum-driven singleton instance for a UTC-based {@link TickingClock}.
     */
    private enum UTCTickingClock {
        INSTANCE;

        private final TickingClock clock = new TickingClock();
    }

    /**
     * Provides an {@code enum}-based UTC singleton {@link TickingClock}. <br>
     * Useful for ensuring that there is only one {@link Clock#systemUTC()} returned per
     * classloader.
     *
     * @return a singleton UTC clock
     */
    public static TickingClock atUTC() {
        return UTCTickingClock.INSTANCE.clock;
    }
}
