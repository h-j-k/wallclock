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

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Specifies a form of clock that provides zoned and non-zoned dates and times.
 */
public interface WallClock {

    /**
     * @return a {@link ZonedDateTime} representation of current instant
     */
    ZonedDateTime zonedDateTime();

    /**
     * @return a {@link LocalDate} representation of current instant
     */
    default LocalDate date() {
        return zonedDateTime().toLocalDate();
    }

    /**
     * @return a {@link LocalTime} representation of current instant
     */
    default LocalTime time() {
        return zonedDateTime().toLocalTime();
    }

    /**
     * @return a {@link LocalDateTime} representation of current time
     */
    default LocalDateTime dateTime() {
        return zonedDateTime().toLocalDateTime();
    }

    /**
     * For compatibility with {@link Clock#instant()}. <br />
     * This is to ensure that even implementations that does not extend from {@link Clock}
     * can provide an instant.
     *
     * @return non-{@code null} instant from this clock
     */
    Instant instant();

    /**
     * For compatibility with {@link Clock#getZone()}. <br />
     * This is to ensure that even implementations that does not extend from {@link Clock}
     * can provide a time-zone.
     *
     * @return non-{@code null} time-zone used for interpreting instants
     */
    ZoneId getZone();

    /**
     * Provides an offset {@link WallClock}, similar to what
     * {@link Clock#offset(Clock, Duration)} does.
     *
     * @param duration the duration to offset by, not null
     * @return a {@link WallClock} that returns dates and times based on this
     *         {@link WallClock} with the duration added
     */
    WallClock offset(Duration duration);
}
