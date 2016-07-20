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
import fi.ilmoeuro.membertrack.auth.Authenticator;
import fi.ilmoeuro.membertrack.auth.db.DbAuthenticator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.checkerframework.checker.nullness.qual.Nullable;

@Slf4j
public final class MtSession extends AuthenticatedWebSession {
    private static final long serialVersionUID = 0l;

    private final Authenticator authenticator;

    @Getter
    private @Nullable String email;

    @SuppressWarnings("method.invocation.invalid")
    public MtSession(Request req) {
        super(req);

        this.authenticator = new DbAuthenticator(
            MtApplication.get().getSessionRunner()
        );

        bind();
    }

    public MtSession(Request req, Authenticator authenticator) {
        super(req);

        this.authenticator = authenticator;
    }

    @Override
    protected boolean authenticate(String email, String pw) {
        boolean result = authenticator.authenticate(email, pw);
        if (result) {
            this.email = email;
        }
        return result;
    }

    @Override
    public void onInvalidate() {
        this.email = null;
        super.onInvalidate();
    }

    @Override
    public Roles getRoles() {
        return new Roles();
    }

    @SuppressWarnings("unchecked")
    public static MtSession get() {
        try {
            return (MtSession) AuthenticatedWebSession.get();
        } catch (ClassCastException ex) {
            String error = "Called get() from wrong app";
            log.error(error, ex);
            throw new RuntimeException(error, ex);
        }
    }
}