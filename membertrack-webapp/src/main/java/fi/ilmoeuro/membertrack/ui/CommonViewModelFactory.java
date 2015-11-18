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

import fi.ilmoeuro.membertrack.auth.Account;
import fi.ilmoeuro.membertrack.auth.Authenticator;
import java.util.Locale;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.codec.digest.DigestUtils;

@Dependent
public final class CommonViewModelFactory {
    private final Authenticator authenticator;
    private final HttpServletRequest request;

    @Inject
    private CommonViewModelFactory(
        Authenticator authenticator,
        HttpServletRequest request
    ) {
        this.authenticator = authenticator;
        this.request = request;
    }

    public CommonViewModel buildCommonViewModel() {
        return new CommonViewModel(
            authenticator
                .getActiveAccount()
                .isPresent(),
            authenticator
                .getActiveAccount()
                .map(Account::getEmail)
                .orElse(""),
            request.getRequestURI()
        );
    }
}
