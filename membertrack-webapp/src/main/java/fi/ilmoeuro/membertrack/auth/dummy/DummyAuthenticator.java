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
package fi.ilmoeuro.membertrack.auth.dummy;

import fi.ilmoeuro.membertrack.auth.Account;
import fi.ilmoeuro.membertrack.auth.Authenticator;
import fi.ilmoeuro.membertrack.auth.InvalidAuthenticationException;
import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;

@Dependent
public final class DummyAuthenticator implements Authenticator {

    @Override
    public void startSession(String email, String password) throws InvalidAuthenticationException {
        // do nothing
    }

    @Override
    public @Nullable Account getActiveAccount() {
        return null;
    }

    @Override
    public void endSession() {
        // do nothing
    }
    
}
