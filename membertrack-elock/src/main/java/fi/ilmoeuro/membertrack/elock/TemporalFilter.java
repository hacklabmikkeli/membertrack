/*
 * Copyright (C) 2015 Ilmo Euro
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fi.ilmoeuro.membertrack.elock;

import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;
import java.util.Map;
import lombok.Synchronized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class TemporalFilter<T extends @NonNull Object> {

    private final TemporalAmount timeToLive;
    private final Map<T, Instant> lastAccessTimes;

    public TemporalFilter(TemporalAmount timeToLive) {
        this.lastAccessTimes = new HashMap<>();
        this.timeToLive = timeToLive;
    }

    @Synchronized
    public boolean accessAndCheckIfAlive(T obj) {
        boolean alive = false;
        Instant now = Instant.now();
        @Nullable Instant lastAccess = lastAccessTimes.get(obj);
        if (   lastAccess != null 
            && lastAccess.isAfter(now.minus(timeToLive))) {
            alive = true;
        }
        lastAccessTimes.put(obj, now);
        return alive;
    }
}
