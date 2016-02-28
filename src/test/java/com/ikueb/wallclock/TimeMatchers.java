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

import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class TimeMatchers {

    private TimeMatchers() {
        // empty
    }

    public static Matcher<TemporalAccessor> withinSecond(TemporalAccessor expected) {
        return new TypeSafeMatcher<TemporalAccessor>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("should be within a second of ")
                        .appendValue(expected);
            }

            @Override
            protected boolean matchesSafely(TemporalAccessor actual) {
                Temporal actualTemporal = actual.query(getQuery(actual));
                Temporal expectedTemporal = expected.query(getQuery(expected));
                return Math.abs(ChronoUnit.NANOS.between(actualTemporal,
                        expectedTemporal)) <= 1_000_000_000;
            }
        };
    }

    private static TemporalQuery<? extends Temporal> getQuery(TemporalAccessor accessor) {
        return accessor instanceof Instant ? Instant::from : LocalTime::from;
    }

}
