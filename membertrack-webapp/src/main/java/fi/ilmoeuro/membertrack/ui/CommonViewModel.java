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
package fi.ilmoeuro.membertrack.ui;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import lombok.Value;
import org.apache.commons.codec.digest.DigestUtils;

public final @Value class CommonViewModel {
    Paths paths = new Paths();
    boolean loggedIn;
    String loggedInEmail;
    String myUrl;

    public String getGravatarUrl() {
        return String.format("//gravatar.com/avatar/%s?s=40&d=mm",
            DigestUtils.md5Hex(
                loggedInEmail.trim().toLowerCase(Locale.ROOT)
            )
        );
    }
}
