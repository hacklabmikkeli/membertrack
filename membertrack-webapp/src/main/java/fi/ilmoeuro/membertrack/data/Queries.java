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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;

public final class Queries {
    private Queries() {
        // Not meant to be instantiated
    }

    public static <T extends @NonNull Object> Optional<Entity<T>> findOne(
        DSLContext jooq,
        Table<?> table,
        Class<T> targetClass,
        Field<Integer> idField,
        Condition... conditions
    ) {
        Record record = jooq
            .select(table.fields())
            .from(table)
            .where(conditions)
            .fetchAny();
        if (record == null) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(
                new Entity<>(
                    record.getValue(idField),
                    record.into(targetClass))
            );
        }
    }

    public static <T extends @NonNull Object> List<Entity<T>> findAll(
        DSLContext jooq,
        Table<?> table,
        Class<T> targetClass,
        Field<Integer> idField,
        Condition... conditions
    ) {
        // TODO streaming
        final ArrayList<Entity<T>> result = new ArrayList<>();
        Result<Record> dataSet = jooq
            .select(table.fields())
            .from(table)
            .where(conditions)
            .fetch();
        for (Record record : dataSet) {
            result.add(
                new Entity<>(
                    record.getValue(idField),
                    record.into(targetClass)));
        }
        return result;
    }
}
