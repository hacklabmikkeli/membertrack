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
package fi.ilmoeuro.membertrack.auth.ui;

import fi.ilmoeuro.membertrack.auth.Authenticator;
import fi.ilmoeuro.membertrack.auth.InvalidAuthenticationException;
import fi.ilmoeuro.membertrack.ui.Paths;
import java.net.URI;
import java.net.URISyntaxException;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.core.Response;
import lombok.Value;
import org.glassfish.jersey.server.mvc.Template;

@Path("/authentication/")
public class AuthenticationUI {

    public static final @Value class ViewModel {
        final Paths paths = new Paths();
    }

    private final Authenticator authenticator;

    @Inject
    public AuthenticationUI(
        Authenticator authenticator
    ) {
        this.authenticator = authenticator;
    }

    @GET
    @Template(name = "/authentication/default")
    public ViewModel index() {
        return new ViewModel();
    }
    
    @POST
    @Path("startSession")
    @Consumes("application/x-www-form-urlencoded")
    public Response startSession(
        @FormParam("goto") String gotoUrl,
        @FormParam("email") String email,
        @FormParam("password") String password
    ) throws InvalidAuthenticationException, URISyntaxException {
        authenticator.startSession(email, password);
        return Response.seeOther(new URI(gotoUrl)).build();
    }

    @POST
    @Path("endSession")
    @Consumes("application/x-www-form-urlencoded")
    public Response endSession(
        @FormParam("goto") String gotoUrl
    ) throws InvalidAuthenticationException, URISyntaxException {
        authenticator.endSession();
        return Response.seeOther(new URI(gotoUrl)).build();
    }
}