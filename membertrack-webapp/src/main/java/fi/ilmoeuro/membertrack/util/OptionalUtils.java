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
package fi.ilmoeuro.membertrack.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class OptionalUtils {

    private OptionalUtils() {
        // not meant to be instantiated
    }

    public static @Value class PartitionedMap
        <K extends @NonNull Object,
         V extends @NonNull Object> {
        Map<K,V> meal;
        Optional<V> leftovers;
    }

    public static <T extends @NonNull Object,
                   R extends @NonNull Object> 
    R optMatch(
        Optional<T> value, 
        Function<T, R> ifPresent, 
        Supplier<R> ifNotPresent
    ) {
        return value.map(ifPresent).orElseGet(ifNotPresent);
    }

    public static <T extends @NonNull Object>
    void optMatch(
        Optional<T> value,
        Consumer<T> ifPresent, 
        Runnable ifNotPresent
    ) {
        value.map((T v) -> {
            ifPresent.accept(v);
            return false;
        }).orElseGet(() -> {
            ifNotPresent.run();
            return false;
        });
    }

    public static <K extends @NonNull Object,
                   V extends @NonNull Object>
    PartitionedMap<K, V> partitionMap(
        Map<Optional<K>, V> map
    ) {
        // TODO streaming
        final Map<K,V> meal = new HashMap<>();
        Optional<V> leftover = Optional.empty();
        for (Map.Entry<Optional<K>, V> entry : map.entrySet()) {
            K key = entry.getKey().get();
            if (key != null) {
                meal.put(key, entry.getValue());
            } else {
                leftover = Optional.of(entry.getValue());
            }
        }
        return new PartitionedMap<>(meal, leftover);
    }

    public static <E extends @NonNull Object> List<E> catMaybes(
        List<Optional<E>> source
    ) {
        // TODO streaming
        final List<E> result = new ArrayList<>();
        for (Optional<E> elem : source) {
            elem.ifPresent(val -> result.add(val));
        }
        return result;
    }
}
