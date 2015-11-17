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
package fi.ilmoeuro.membertrack.entity;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
public final class Entity<T> {
    private final @Nullable Integer id;
    private final @NonNull T value;
    
    public static <T> Entity<T> existing(int id, @NonNull T value) {
        return new Entity<>(id, value);
    }

    public static <T> Entity<T> fresh(@NonNull T value) {
        return new Entity<>(null, value);
    }

    public @NonNull T getValue() {
        return value;
    }

    public int getId() {
        if (id == null) {
            throw new RuntimeException("Trying to access ID of a fresh Entity");
        } else {
            return id;
        }
    }

    public boolean isFresh() {
        return id == null;
    }
}
