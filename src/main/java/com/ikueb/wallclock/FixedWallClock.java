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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Specifies a form of clock that allows dates and times to be set.
 */
public interface FixedWallClock extends WallClock {

    /**
     * @param date the date to set to, not null
     */
    FixedWallClock setDate(LocalDate date);

    /**
     * @param time the time to set to, not null
     */
    FixedWallClock setTime(LocalTime time);

    /**
     * @param dateTime the date and time to set to, not null
     */
    FixedWallClock setDateTime(LocalDateTime dateTime);
}
