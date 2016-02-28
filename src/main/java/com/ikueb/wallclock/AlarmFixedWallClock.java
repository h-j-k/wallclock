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
 * Specifies a form of clock that can set alarms.
 */
public interface AlarmFixedWallClock extends FixedWallClock {

    /**
     * Registers a listener for all updates. <br />
     * This will be overridden if a more specific date and/or time is given.
     *
     * @param listener the listener to register for all updates
     */
    void alarm(AlarmClockListener listener);

    /**
     * Registers a listener for all updates happening on that date. <br />
     * This overrides the 'all updates' specification.
     *
     * @param listener the listener to register
     * @param dateToAlarm the date to alarm for all updates at the date
     */
    void alarm(AlarmClockListener listener, LocalDate dateToAlarm);

    /**
     * Registers a listener for all updates happening on that time. <br />
     * This overrides the 'all updates' specification.
     *
     * @param listener the listener to register
     * @param timeToAlarm the time to alarm for all updates at the time
     */
    void alarm(AlarmClockListener listener, LocalTime timeToAlarm);

    /**
     * Registers a listener for all updates happening on that date and time. <br />
     * This overrides the 'all updates' specification.
     *
     * @param listener the listener to register
     * @param dateTimeToAlarm the date and time to alarm
     */
    void alarm(AlarmClockListener listener, LocalDateTime dateTimeToAlarm);

    /**
     * Unregisters a listener for all updates.
     *
     * @param listener the listener to snooze for all updates
     */
    void snooze(AlarmClockListener listener);

    /**
     * Unregisters a listener for all updates at the date. <br />
     * If this is the last alarm to be unregistered, the listener is effectively
     * unregistered for all updates.
     *
     * @param listener the listener to register
     * @param dateToSnooze the date to snooze for all updates at the date
     */
    void snooze(AlarmClockListener listener, LocalDate dateToSnooze);

    /**
     * Unregisters a listener for all updates at the time. <br />
     * If this is the last alarm to be unregistered, the listener is effectively
     * unregistered for all updates.
     *
     * @param listener the listener to register
     * @param timeToSnooze the time to snooze for all updates at the time
     */
    void snooze(AlarmClockListener listener, LocalTime timeToSnooze);

    /**
     * Unregisters a listener for all updates at the date and time. <br />
     * If this is the last alarm to be unregistered, the listener is effectively
     * unregistered for all updates.
     *
     * @param listener the listener to register
     * @param dateTimeToSnooze the date and time to snooze
     */
    void snooze(AlarmClockListener listener, LocalDateTime dateTimeToSnooze);
}
