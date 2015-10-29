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
package fi.ilmoeuro.membertrack.plumbing;

import java.sql.Timestamp;
import java.time.Instant;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.Converter;

public final class JooqDateConverter implements Converter<Timestamp, Instant> {

    public static final long serialVersionUID = 0l;

    @SuppressWarnings("nullness")
    @Override
    public @Nullable Instant from(@Nullable Timestamp databaseObject) {
        if (databaseObject == null) {
            return null;
        } else {
            return databaseObject.toInstant();
        }
    }

    @SuppressWarnings("nullness")
    @Override
    public @Nullable Timestamp to(@Nullable Instant instant) {
        if (instant == null) {
            return null;
        } else {
            return new Timestamp(instant.toEpochMilli());
        }
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<Instant> toType() {
        return Instant.class;
    }
}
