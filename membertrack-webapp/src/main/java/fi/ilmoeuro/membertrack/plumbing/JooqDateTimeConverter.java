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
import java.time.LocalDateTime;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.Converter;

public final class JooqDateTimeConverter implements Converter<Timestamp, LocalDateTime> {

    public static final long serialVersionUID = 0l;

    @SuppressWarnings("nullness")
    @Override
    public @Nullable LocalDateTime from(@Nullable Timestamp databaseObject) {
        if (databaseObject == null) {
            return null;
        } else {
            return databaseObject.toLocalDateTime();
        }
    }

    @SuppressWarnings("nullness")
    @Override
    public @Nullable Timestamp to(@Nullable LocalDateTime localDate) {
        if (localDate == null) {
            return null;
        } else {
            return Timestamp.valueOf(localDate);
        }
    }

    @Override
    public Class<Timestamp> fromType() {
        return Timestamp.class;
    }

    @Override
    public Class<LocalDateTime> toType() {
        return LocalDateTime.class;
    }
}
