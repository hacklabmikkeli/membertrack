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

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class OptionalUtils {

    private OptionalUtils() {
        // not meant to be instantiated
    }

    public static <T> void ifAllPresent(
        Optional<T> opt1,
        Consumer<T> action
    ) {
        opt1.ifPresent(v -> action.accept(v));
    }

    public static <T1, T2> void ifAllPresent(
        Optional<T1> opt1,
        Optional<T2> opt2,
        BiConsumer<T1, T2> action
    ) {
        opt1.ifPresent(v1 ->
            opt2.ifPresent(v2 ->
                action.accept(v1, v2)));
    }

    public static <T1, T2, T3> void ifAllPresent(
        Optional<T1> opt1,
        Optional<T2> opt2,
        Optional<T3> opt3,
        TriConsumer<T1, T2, T3> action
    ) {
        opt1.ifPresent(v1 ->
            opt2.ifPresent(v2 ->
                opt3.ifPresent(v3 ->
                action.accept(v1, v2, v3))));
    }
}
