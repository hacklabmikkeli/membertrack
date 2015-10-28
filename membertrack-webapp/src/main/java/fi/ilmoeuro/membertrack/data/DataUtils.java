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

import static java.util.stream.Collectors.*;
import org.jooq.impl.DSL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import org.jooq.Field;

public class DataUtils {

    public static <T,K,A,D> Collector<T,?,Map<K,D>> orderedGroupingBy(
        Function<? super T,? extends K> fnctn, 
        Collector<? super T,A,D> clctr
    ) {
        return groupingBy(fnctn, LinkedHashMap::new, clctr);
    }

    public static <T> Field<T> asNull(Field<T> field) {
        return DSL.castNull(field).as(field);
    }
}
