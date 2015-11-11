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
package fi.ilmoeuro.membertrack.member;

import fi.ilmoeuro.membertrack.auth.UnauthorizedException;
import fi.ilmoeuro.membertrack.ui.Paths;
import java.net.URI;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import lombok.Value;
import org.glassfish.jersey.server.mvc.Template;

@Path("/membership/")
public class MembershipsUI {

    @Context
    UriInfo uri;

    public static final @Value class ViewModel {
        final List<Membership> memberships;
        final int numPages;
        final int currentPage;
        final Paths paths = new Paths();
    }

    private final Memberships memberships;

    @Inject
    public MembershipsUI(
        Memberships memberships
    ) {
        this.memberships = memberships;
    }

    @GET
    public Response index() {
        URI newUri = URI.create(uri.getAbsolutePath().toString() + "/1");
        return Response.seeOther(newUri).build();
    }
    
    @GET
    @Template(name = "/membership/default")
    @Path("{PAGE}")
    public ViewModel listAll(
        @PathParam("PAGE") @DefaultValue("1") Integer pageNum
    ) throws UnauthorizedException {
        return new ViewModel(
            memberships.listPage(pageNum - 1),
            memberships.numPages(),
            pageNum
        );
    }
}
