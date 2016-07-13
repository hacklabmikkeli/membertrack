/*
 * Copyright (C) 2016 Ilmo Euro <ilmo.euro@gmail.com>
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
package fi.ilmoeuro.membertrack.auth.db;

import fi.ilmoeuro.membertrack.db.Persistable;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.schema.tables.pojos.PasswordResetTokenBase;
import fi.ilmoeuro.membertrack.util.ClockHolder;
import fi.ilmoeuro.membertrack.util.Crypto;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class
    PasswordResetToken
extends
    PasswordResetTokenBase
implements
    Persistable
{
    private static final long serialVersionUID = 0l;
    
    @SuppressWarnings(value = "nullness")
    @Deprecated
    public PasswordResetToken(
        @Nullable Integer pk,
        UUID id,
        UUID personId,
        String token,
        String salt,
        LocalDateTime issueTime,
        LocalDateTime validUntil
    ) {
        super(
            pk,
            id,
            personId,
            token,
            salt,
            issueTime,
            validUntil
        );
    }

    @SuppressWarnings(value = "deprecation")
    public PasswordResetToken(
        Person person,
        String token,
        String salt,
        LocalDateTime issueTime,
        LocalDateTime validUntil
    ) {
        this(
            null,
            UUID.randomUUID(),
            person.getId(),
            token,
            salt,
            issueTime,
            validUntil
        );
    }

    public static PasswordResetToken create(Person person, long validitySecs) {
        String salt = Crypto.randomSalt();
        LocalDateTime now = LocalDateTime.now(ClockHolder.get());
        return new PasswordResetToken(
            person,
            Crypto.hash(person.getEmail(), salt),
            salt,
            now,
            now.plusSeconds(validitySecs));
    }

    public boolean isValid() {
        return getValidUntil().isBefore(LocalDateTime.now(ClockHolder.get()));
    }

    public boolean matches(Person person) {
        String hashedEmail = Crypto.hash(person.getEmail(), getSalt());
        return Objects.equals(hashedEmail, getToken());
    }
}
