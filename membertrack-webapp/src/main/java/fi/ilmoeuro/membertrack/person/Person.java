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
package fi.ilmoeuro.membertrack.person;

import fi.ilmoeuro.membertrack.entity.Entity;
import java.util.Collection;
import java.util.Locale;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
public final class Person {
    private final @Getter PersonData data;
    private final @Getter Collection<Entity<PhoneNumber>> phoneNumbers;

    public String getEmail() {
        return data.getEmail();
    }

    public String getFullName() {
        return data.getFullName();
    }

    public String getGravatarUrl() {
        return String.format("//gravatar.com/avatar/%s",
            DigestUtils.md5Hex(
                data.getEmail().trim().toLowerCase(Locale.ROOT)
            )
        );
    }
}
