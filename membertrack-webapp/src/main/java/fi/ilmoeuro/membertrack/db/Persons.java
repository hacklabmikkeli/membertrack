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

import static fi.ilmoeuro.membertrack.schema.Tables.*;
import fi.ilmoeuro.membertrack.entity.Entity;
import fi.ilmoeuro.membertrack.entity.KeyedManager;
import fi.ilmoeuro.membertrack.entity.Manager;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import java.util.Collection;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jooq.DSLContext;

@Dependent
public final class Persons implements Manager<Person> {

    private final DSLContext jooq;
    private final KeyedManager<Integer, PhoneNumber> phoneNumbers;

    @Inject
    public Persons(
        DSLContext jooq,
        KeyedManager<Integer, PhoneNumber> phoneNumbers
    ) {
        this.jooq = jooq;
        this.phoneNumbers = phoneNumbers;
    }

    private Entity<Person> insert(Person value) {
        int id = jooq.insertInto(PERSON, PERSON.FULL_NAME, PERSON.EMAIL)
            .values(value.getFullName(), value.getEmail())
            .returning(PERSON.ID)
            .execute();
        Collection<Entity<PhoneNumber>> newPhoneNumbers = phoneNumbers.put(
            id,
            value.getPhoneNumbers());
        return Entity.existing(id, new Person(value.getData(), newPhoneNumbers));
    }

    private Entity<Person> update(int id, Person value) {
        jooq.update(PERSON)
            .set(PERSON.FULL_NAME, value.getFullName())
            .set(PERSON.EMAIL, value.getEmail())
            .where(PERSON.ID.eq(id))
            .execute();
        Collection<Entity<PhoneNumber>> newPhoneNumbers = phoneNumbers.put(
            id,
            value.getPhoneNumbers());
        return Entity.existing(id, new Person(value.getData(), newPhoneNumbers));
    }

    @Override
    public Entity<Person> put(Entity<Person> entity) {
        if (entity.isFresh()) {
            return insert(entity.getValue());
        } else {
            return update(entity.getId(), entity.getValue());
        }
    }

    @Override
    public void delete(int id) {
        jooq.deleteFrom(PERSON).where(PERSON.ID.eq(id)).execute();
    }
}
