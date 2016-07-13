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
import fi.ilmoeuro.membertrack.schema.tables.pojos.AccountBase;
import fi.ilmoeuro.membertrack.util.Crypto;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Account extends AccountBase implements Persistable {
    private static final long serialVersionUID = 0l;

    @SuppressWarnings("nullness") // Interface with autogen code
    @Deprecated
    public Account(
        @Nullable Integer pk,
        UUID id,
        UUID personId,
        String hash,
        String salt
    ) {
        super(pk, id, personId, hash, salt);
    }

    @SuppressWarnings("deprecation")
    public Account(
        Person person,
        String hash,
        String salt
    ) {
        this(null, UUID.randomUUID(), person.getId(), hash, salt);
    }

    public static Account create(
        Person person,
        String password
    ) {
        String salt = Crypto.randomSalt();
        return new Account(
            person,
            Crypto.hash(password, salt),
            salt
        );
    }
    
    public void setPassword(String password) {
        String salt = Crypto.randomSalt();
        setHash(Crypto.hash(password, salt));
        setSalt(salt);
    }
}
