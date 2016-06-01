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

import fi.ilmoeuro.membertrack.db.Persistable;
import fi.ilmoeuro.membertrack.schema.tables.pojos.PersonBase;
import java.util.Locale;
import java.util.UUID;
import org.apache.commons.codec.digest.DigestUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Person extends PersonBase implements Persistable {
    private static final long serialVersionUID = 0l;

    @SuppressWarnings("nullness") // Interface with autogen code
    @Deprecated
    public Person(
        @Nullable Integer pk,
        UUID id,
        String fullName,
        String email
    ) {
        super(pk, id, fullName, email);
    }

    @SuppressWarnings("deprecation")
    public Person(
        String fullName,
        String email
    ) {
        this(null, UUID.randomUUID(), fullName, email);
    }

    public String getGravatarUrl() {
        return String.format(
            "//gravatar.com/avatar/%s?d=mm",
            DigestUtils.md5Hex(getEmail().toLowerCase(Locale.ROOT).trim()));
    }

    public void delete() {
        setDeleted(true);
    }
}
