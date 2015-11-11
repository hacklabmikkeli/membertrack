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

import fi.ilmoeuro.membertrack.data.Entity;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.StringUtils;

@Path("/person/")
public class PersonsUI {

    private final Persons persons;

    @Inject
    public PersonsUI(
        Persons persons
    ) {
        this.persons = persons;
    }
    
    @POST
    @Path("update")
    @Consumes("application/x-www-form-urlencoded")
    public Response update(
        @FormParam("goto") String gotoUrl,
        @FormParam("personId") int personId,
        @FormParam("fullName") String fullName,
        @FormParam("email") String email,
        @FormParam("phoneNumber") List<String> phoneNumberStrings
    ) throws URISyntaxException {
        List<PhoneNumber>
            phoneNumbers = phoneNumberStrings.stream()
                                             .filter(s -> !StringUtils.isBlank(s))
                                             .map(PhoneNumber::new)
                                             .collect(Collectors.toList());
        Entity<Person>
            person = new Entity<>(personId, new Person(fullName, email));
        persons.put(person, phoneNumbers);
        return Response.seeOther(new URI(gotoUrl)).build();
    }

    @POST
    @Path("create")
    @Consumes("application/x-www-form-urlencoded")
    public Response create(
        @FormParam("goto") String gotoUrl,
        @FormParam("fullName") String fullName,
        @FormParam("email") String email,
        @FormParam("phoneNumber") List<String> phoneNumberStrings
    ) throws URISyntaxException {
        List<PhoneNumber>
            phoneNumbers = phoneNumberStrings.stream()
                                             .filter(s -> !StringUtils.isBlank(s))
                                             .map(PhoneNumber::new)
                                             .collect(Collectors.toList());
        Person person = new Person(fullName, email);
        persons.put(person, phoneNumbers);
        return Response.seeOther(new URI(gotoUrl)).build();
    }
}