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
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TimeMatchersTest {

    private static final Duration A_SEC = Duration.ofSeconds(1);
    private static final Duration A_SEC_PLUS = A_SEC.plusNanos(1);
    private static final Supplier<Stream<Temporal>> TIMES = () -> Stream.of(
            LocalTime.now(), LocalDateTime.now(), OffsetTime.now(), OffsetTime.now(),
            ZonedDateTime.now(), Instant.now());

    @DataProvider(name = "within-second")
    public static Iterator<Object[]> getWithinData() {
        return TIMES.get()
                .flatMap(v -> Stream.of(new Object[] { v, v.minus(A_SEC) },
                        new Object[] { v, v }, new Object[] { v, v.plus(A_SEC) }))
                .iterator();
    }

    @Test(dataProvider = "within-second")
    public void testWithinSecond(TemporalAccessor actual, TemporalAccessor expected) {
        assertThat(actual, withinSecond(expected));
    }

    @DataProvider(name = "exceed-second")
    public static Iterator<Object[]> getExceedData() {
        return TIMES.get()
                .flatMap(v -> Stream.of(new Object[] { v, v.minus(A_SEC_PLUS) },
                        new Object[] { v, v.plus(A_SEC_PLUS) }))
                .iterator();
    }

    @Test(dataProvider = "exceed-second", expectedExceptions = java.lang.AssertionError.class)
    public void testExceedSecond(TemporalAccessor actual, TemporalAccessor expected) {
        assertThat(actual, withinSecond(expected));
    }
}
