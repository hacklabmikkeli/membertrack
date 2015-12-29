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
package fi.ilmoeuro.membertrack.auth.db;

import static fi.ilmoeuro.membertrack.schema.Tables.*;
import fi.ilmoeuro.membertrack.auth.Authenticator;
import fi.ilmoeuro.membertrack.person.Account;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.SessionToken;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Locale;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.Charsets;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.DSLContext;

@Slf4j
@RequiredArgsConstructor
public final class DbAuthenticator implements Authenticator {
    private static final long serialVersionUID = 0l;
    
    private final SessionRunner<DSLContext> sessionRunner;

    @Override
    public boolean authenticate(
        String email,
        String password
    ) {
        return sessionRunner.<Boolean>eval((SessionToken<DSLContext> token) -> {
            DSLContext jooq = token.getValue();
            @Nullable String salt = jooq
                .select(ACCOUNT.SALT)
                .from(PERSON)
                .innerJoin(ACCOUNT)
                    .on(ACCOUNT.PERSON_ID.eq(PERSON.ID))
                .where(PERSON.EMAIL.eq(email.trim().toLowerCase(Locale.ROOT)))
                .fetchAny(ACCOUNT.SALT);
            if (salt != null) {
                String hashed = Account.hash(password, salt);
                @Nullable UUID personId =
                    jooq.select(PERSON.ID)
                        .from(PERSON)
                        .innerJoin(ACCOUNT)
                            .on(ACCOUNT.PERSON_ID.eq(PERSON.ID))
                        .where(ACCOUNT.HASH.eq(hashed))
                        .fetchAny(PERSON.ID);
                if (personId != null) {
                    return true;
                }
            }

            return false;
        });
    }
}