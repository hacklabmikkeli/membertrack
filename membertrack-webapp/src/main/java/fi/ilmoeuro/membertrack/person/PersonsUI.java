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
package fi.ilmoeuro.membertrack.person;

import fi.ilmoeuro.membertrack.auth.Authorizer;
import fi.ilmoeuro.membertrack.auth.Permission;
import fi.ilmoeuro.membertrack.auth.UnauthorizedException;
import fi.ilmoeuro.membertrack.entity.Manager;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;

@Path("/person/")
public class PersonsUI {

    private final Manager<Person> personManager;
    private final Authorizer authorizer;

    @Inject
    public PersonsUI(
        Manager<Person> personManager,
        Authorizer authorizer
    ) {
        this.personManager = personManager;
        this.authorizer = authorizer;
    }
    
    @POST
    @Path("update")
    @Consumes("application/x-www-form-urlencoded")
    @Transactional
    public Response update(
        @FormParam("goto") String gotoUrl,
        @FormParam("personId") int personId,
        @FormParam("fullName") String fullName,
        @FormParam("email") String email,
        @FormParam("phoneNumber") List<String> phoneNumberStrings
    ) throws URISyntaxException, UnauthorizedException {
        authorizer.ensureAuthorized(Permission.LOGGED_IN);
        List<PhoneNumber> phoneNumbers = phoneNumberStrings
            .stream()
            .filter(this::notBlank)
            .<PhoneNumber>map(PhoneNumber::new)
            .collect(Collectors.<PhoneNumber>toList());
        personManager.update(
            personId,
            new Person(
                new PersonData(fullName, email),
                phoneNumbers));
        return Response.seeOther(new URI(gotoUrl)).build();
    }

    @POST
    @Path("create")
    @Consumes("application/x-www-form-urlencoded")
    @Transactional
    public Response create(
        @FormParam("goto") String gotoUrl,
        @FormParam("fullName") String fullName,
        @FormParam("email") String email,
        @FormParam("phoneNumber") List<String> phoneNumberStrings
    ) throws URISyntaxException, UnauthorizedException {
        authorizer.ensureAuthorized(Permission.LOGGED_IN);
        List<PhoneNumber> phoneNumbers = phoneNumberStrings
            .stream()
            .filter(this::notBlank)
            .<PhoneNumber>map(PhoneNumber::new)
            .collect(Collectors.<PhoneNumber>toList());
        personManager.insert(
            new Person(
                new PersonData(fullName, email),
                phoneNumbers));
        return Response.seeOther(new URI(gotoUrl)).build();
    }

    private boolean notBlank(String s) {
        return !StringUtils.isBlank(s);
    }
}