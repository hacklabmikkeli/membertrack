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
import fi.ilmoeuro.membertrack.auth.Account;
import fi.ilmoeuro.membertrack.auth.Authenticator;
import fi.ilmoeuro.membertrack.auth.InvalidAuthenticationException;
import java.io.Serializable;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Locale;
import java.util.Optional;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.Charsets;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.DSLContext;

@Slf4j
@Dependent
public final class DbAuthenticator implements Authenticator {

    @SessionScoped
    public static @Data class Session implements Serializable {
        private static final long serialVersionUID = 0l;
        @Nullable Integer currentAccountId;
    }

    private final DSLContext jooq;
    private final Session session;
    
    @Inject
    public DbAuthenticator(
        DSLContext jooq,
        Session session
    ) {
        this.jooq = jooq;
        this.session = session;
    }

    private String hash(String candidate, String salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA1");
            KeySpec ks = new PBEKeySpec(
                candidate.toCharArray(),
                salt.getBytes(Charsets.US_ASCII),
                1024,
                128);
            SecretKey sk = skf.generateSecret(ks);
            Key k = new SecretKeySpec(sk.getEncoded(), "AES");
            return Hex.encodeHexString(k.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException("Error while hashing", ex);
        }
    }

    @Override
    public void startSession(
        String email,
        String password
    ) throws InvalidAuthenticationException {
        @Nullable String salt = jooq
            .select(ACCOUNT.SALT)
            .from(PERSON)
            .innerJoin(ACCOUNT)
                .on(ACCOUNT.PERSON_ID.eq(PERSON.ID))
            .where(PERSON.EMAIL.eq(email.toLowerCase(Locale.ROOT)))
            .fetchAny(ACCOUNT.SALT);
        if (salt != null) {
            String hashed = hash(password, salt);
            @Nullable Integer personId =
                jooq.select(PERSON.ID)
                    .from(PERSON)
                    .innerJoin(ACCOUNT)
                        .on(ACCOUNT.PERSON_ID.eq(PERSON.ID))
                    .where(ACCOUNT.HASH.eq(hashed))
                    .fetchAny(PERSON.ID);
            if (personId != null) {
                session.setCurrentAccountId(personId);
                return;
            }
        }

        throw new InvalidAuthenticationException();
    }

    @Override
    public Optional<Account> getActiveAccount() {
        Integer accountId = session.getCurrentAccountId();
        if (accountId != null) {
            String email = jooq
                .select(PERSON.EMAIL)
                .from(PERSON)
                .where(PERSON.ID.eq(accountId))
                .fetchAny(PERSON.EMAIL);
            return Optional.ofNullable(email).map(Account::new);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void endSession() {
        session.setCurrentAccountId(null);
    }
}
