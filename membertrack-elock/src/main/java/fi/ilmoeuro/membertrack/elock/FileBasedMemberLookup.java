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
package fi.ilmoeuro.membertrack.elock;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.commons.io.FileUtils;

@RequiredArgsConstructor
@EqualsAndHashCode
@Log
public final class FileBasedMemberLookup implements MemberLookup {

    private final File whitelistFile;

    @Override
    public boolean isAuthorizedMember(String phoneNumber) {
        try {
            List<String> numbers = FileUtils.readLines(whitelistFile, StandardCharsets.UTF_8);
            return numbers.contains(phoneNumber);
        } catch (IOException ex) {
            log.severe(
                String.format(
                    "Couldn't read whitelist file: " + ex.getMessage()));
            return false;
        }
    }
}