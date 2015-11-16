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
package fi.ilmoeuro.membertrack.db;

import fi.ilmoeuro.membertrack.entity.Entity;
import fi.ilmoeuro.membertrack.entity.Manager;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.PersonData;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jooq.DSLContext;

@Dependent
public class Persons implements Manager<Person> {

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
        PersonData person,
        Collection<PhoneNumber> newPhoneNumbers
    ) {
    }

    @Override
    public Entity<Person> insert(Person value) {
        int id = jooq.insertInto(PERSON, PERSON.FULL_NAME, PERSON.EMAIL)
            .values(value.getFullName(), value.getEmail())
            .returning(PERSON.ID)
            .execute();
        phoneNumbers.update(id, value.getPhoneNumbers());
        return new Entity<>(id, value);
    }

    @Override
    public Entity<Person> update(int id, Person value) {
        jooq.update(PERSON)
            .set(PERSON.FULL_NAME, value.getFullName())
            .set(PERSON.EMAIL, value.getEmail())
            .where(PERSON.ID.eq(id))
            .execute();
        phoneNumbers.update(id, value.getPhoneNumbers());
        return new Entity<>(id, value);
    }

    @Override
    public Entity<Person> update(Entity<Person> entity) {
        return update(entity.getId(), entity.getValue());
    }

    @Override
    public void delete(int id) {
        jooq.deleteFrom(PERSON).where(PERSON.ID.eq(id)).execute();
    }
}
