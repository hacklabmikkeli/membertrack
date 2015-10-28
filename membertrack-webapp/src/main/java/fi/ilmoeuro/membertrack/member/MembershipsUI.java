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
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import lombok.Value;
import org.glassfish.jersey.server.mvc.Template;

@Path("/membership/")
public class MembershipsUI {

    public static final @Value class ViewModel {
        final List<Membership> memberships;
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
    @Template(name = "/membership/default")
    public ViewModel listAll() throws UnauthorizedException {
        return new ViewModel(
            memberships.listAll()
        );
    }
}
