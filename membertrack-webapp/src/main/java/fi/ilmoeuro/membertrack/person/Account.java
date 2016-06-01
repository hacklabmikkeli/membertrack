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
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Random;
import java.util.UUID;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Account extends AccountBase implements Persistable {
    private static final long serialVersionUID = 0l;
    private static final Random random = new Random();

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
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        String salt = Hex.encodeHexString(randomBytes);
        return new Account(
            person,
            hash(password, salt),
            salt
        );
    }

    public static String hash(String candidate, String salt) {
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA1");
            KeySpec ks = new PBEKeySpec(
                candidate.toCharArray(),
                salt.getBytes(StandardCharsets.US_ASCII),
                1024,
                128);
            SecretKey sk = skf.generateSecret(ks);
            Key k = new SecretKeySpec(sk.getEncoded(), "AES");
            return Hex.encodeHexString(k.getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            throw new RuntimeException("Error while hashing", ex);
        }
    }
}
