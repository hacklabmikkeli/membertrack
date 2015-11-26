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
package fi.ilmoeuro.membertrack.plumbing;

import fi.ilmoeuro.membertrack.MembertrackException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MembertrackExceptionMapper 
    implements ExceptionMapper<MembertrackException> {

    @Override
    @Produces(MediaType.TEXT_HTML)
    public Response toResponse(MembertrackException exception) {
        try {
            URI uri = new URI(exception.getRedirectURL());
            return Response
                .seeOther(uri)
                .build();
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
