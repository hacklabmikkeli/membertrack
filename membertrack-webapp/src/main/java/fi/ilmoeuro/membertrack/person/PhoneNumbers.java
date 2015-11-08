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
import java.util.Iterator;
import java.util.stream.Collectors;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.SelectSelectStep;
import org.jooq.impl.DSL;

@Dependent
public final class PhoneNumbers {

    private final DSLContext jooq;

    @Inject
    public PhoneNumbers(
        DSLContext jooq
    ) {
        this.jooq = jooq;
    }

    public void update(
        Entity<Person> person,
        Collection<PhoneNumber> phoneNumbers
    ) {
        final Iterator<PhoneNumber> it = phoneNumbers.iterator();
        if (it.hasNext()) {
            PhoneNumber phoneNumber = it.next();
            final SelectSelectStep<Record2<Integer, String>> newPns = jooq.select(
                DSL.cast(person.getId(), PHONE_NUMBER.PERSON_ID),
                DSL.cast(phoneNumber.getPhoneNumber(), PHONE_NUMBER.PHONE_NUMBER_));
            while (it.hasNext()) {
                phoneNumber = it.next();
                newPns.unionAll(
                    DSL.select(
                        DSL.cast(person.getId(), PHONE_NUMBER.PERSON_ID),
                        DSL.cast(phoneNumber.getPhoneNumber(), PHONE_NUMBER.PHONE_NUMBER_)));
            }

            jooq.insertInto(PHONE_NUMBER,
                            PHONE_NUMBER.PERSON_ID,
                            PHONE_NUMBER.PHONE_NUMBER_)
                .select(newPns.except(
                    DSL.select(PHONE_NUMBER.PERSON_ID, PHONE_NUMBER.PHONE_NUMBER_)
                        .from(PHONE_NUMBER)
                        .where(PHONE_NUMBER.PERSON_ID.eq(person.getId()))))
                .execute();
        }
            
        jooq.deleteFrom(PHONE_NUMBER)
            .where(
                DSL.and(
                    PHONE_NUMBER.PERSON_ID.eq(person.getId()),
                    PHONE_NUMBER.PHONE_NUMBER_.notIn(
                        phoneNumbers.stream()
                                    .map(PhoneNumber::getPhoneNumber)
                                    .collect(Collectors.toList()))))
            .execute();
    }
}