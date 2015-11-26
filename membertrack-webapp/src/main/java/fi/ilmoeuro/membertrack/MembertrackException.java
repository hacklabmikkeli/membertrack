/*
 * Copyright (C) 2015 Ilmo Euro <ilmo.euro@gmail.com>
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
package fi.ilmoeuro.membertrack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public abstract class MembertrackException extends Exception {
    @Getter
    @Setter
    private String redirectURL = "";
    
    @Getter
    private final Map<String, Collection<String>>
        formFields = new LinkedHashMap<>();

    @Getter
    private final Collection<String>
        errors = new ArrayList<>();

    public void addFormFieldSet(String key, Collection<String> value) {
        formFields.put(key, value);
    }

    public void addError(String error) {
        errors.add(error);
    }
}
