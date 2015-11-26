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
import fi.ilmoeuro.membertrack.ui.CommonViewModel;
import fi.ilmoeuro.membertrack.ui.CommonViewModelFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import lombok.Value;
import org.glassfish.jersey.server.mvc.Template;

@Path("/authentication/")
public class AuthenticationUI {

    public static final @Value class ViewModel {
        CommonViewModel common;
    }

    private final Authenticator authenticator;
    private final CommonViewModelFactory cvmf;

    @Inject
    public AuthenticationUI(
        Authenticator authenticator,
        CommonViewModelFactory cvmf
    ) {
        this.authenticator = authenticator;
        this.cvmf = cvmf;
    }

    @GET
    @Template(name = "/authentication/default")
    @Produces(MediaType.TEXT_HTML)
    public ViewModel index() {
        return new ViewModel(
            cvmf.buildCommonViewModel()
        );
    }
    
    @POST
    @Path("startSession")
    @Produces(MediaType.TEXT_HTML)
    @Consumes("application/x-www-form-urlencoded")
    public Response startSession(
        @FormParam("goto") String gotoUrl,
        @FormParam("fail") String failUrl,
        @FormParam("email") String email,
        @FormParam("password") String password
    ) throws InvalidAuthenticationException, URISyntaxException {
        try {
            authenticator.startSession(email, password);
            return Response.seeOther(new URI(gotoUrl)).build();
        } catch (InvalidAuthenticationException ex) {
            ex.addFormFieldSet(email, Arrays.asList(email));
            throw ex;
        }
    }

    @POST
    @Path("endSession")
    @Produces(MediaType.TEXT_HTML)
    @Consumes("application/x-www-form-urlencoded")
    public Response endSession(
        @FormParam("goto") String gotoUrl
    ) throws URISyntaxException {
        authenticator.endSession();
        return Response.seeOther(new URI(gotoUrl)).build();
    }
}