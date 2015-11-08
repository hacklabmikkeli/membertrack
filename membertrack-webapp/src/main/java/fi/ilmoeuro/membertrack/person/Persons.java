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
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jooq.DSLContext;

@Dependent
public class Persons {

    private final DSLContext jooq;
    private final PhoneNumbers phoneNumbers;

    @Inject
    public Persons(
        DSLContext jooq,
        PhoneNumbers phoneNumbers
    ) {
        this.jooq = jooq;
        this.phoneNumbers = phoneNumbers;
    }

    public void put(
        Person person,
        Collection<PhoneNumber> newPhoneNumbers
    ) {
        jooq.insertInto(PERSON, PERSON.FULLNAME, PERSON.EMAIL)
            .values(person.getFullName(), person.getEmail())
            .execute();
        Entity<Person>
            insertedEntity = new Entity<>(jooq.lastID().intValue(), person);
        phoneNumbers.update(insertedEntity, newPhoneNumbers);
    }

    public void put(
        Entity<Person> person,
        Collection<PhoneNumber> newPhoneNumbers
    ) {
        jooq.update(PERSON)
            .set(PERSON.FULLNAME, person.getValue().getFullName())
            .set(PERSON.EMAIL, person.getValue().getEmail())
            .where(PERSON.ID.eq(person.getId()))
            .execute();
        phoneNumbers.update(person, newPhoneNumbers);
    }
}
