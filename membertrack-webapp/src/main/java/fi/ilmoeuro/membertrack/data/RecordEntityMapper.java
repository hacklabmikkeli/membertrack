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
package fi.ilmoeuro.membertrack.data;

import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.Record;

public final class RecordEntityMapper {

    private RecordEntityMapper() {
        // Not meant to be instantiated
    }

    public static <T> Optional<Entity<@NonNull T>> mapToEntity(
        Record r,
        Class<@NonNull T> clazz
    ) {
        @Nullable Integer id = r.getValue("id", Integer.class);
        @Nullable T entity = r.into(clazz);
        if (entity == null) {
            throw new IllegalArgumentException("Record not mappable to class");
        } else {
            if (id != null) {
                return Optional.of(new Entity(id, r.into(clazz)));
            } else {
                return Optional.empty();
            }
        }
    }
}
