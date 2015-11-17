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
import fi.ilmoeuro.membertrack.entity.PartitionedEntities;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jooq.DSLContext;

@Dependent
public final class PhoneNumbers implements KeyedManager<Integer, PhoneNumber> {

    private final DSLContext jooq;

    @Inject
    public PhoneNumbers(
        DSLContext jooq
    ) {
        this.jooq = jooq;
    }

    @Override
    public Collection<Entity<PhoneNumber>> put(
        Integer personId,
        Collection<Entity<PhoneNumber>> phoneNumbers
    ) {
        PartitionedEntities<PhoneNumber>
            partitioned = new PartitionedEntities<>(phoneNumbers);

        jooq.deleteFrom(PHONE_NUMBER)
            .where(PHONE_NUMBER.PERSON_ID.eq(personId),
                   PHONE_NUMBER.ID.notIn(partitioned.getExistingIds()))
            .execute();

        for (Entity<PhoneNumber> pne : partitioned.getExisting()) {
            jooq.update(PHONE_NUMBER)
                .set(PHONE_NUMBER.PHONE_NUMBER_,
                    pne.getValue().getPhoneNumber())
                .where(PHONE_NUMBER.ID.eq(pne.getId()))
                .execute();
        }

        List<Entity<PhoneNumber>> result = new ArrayList<>();
        for (PhoneNumber pn : partitioned.getFresh()) {
            int id = jooq.insertInto(
                PHONE_NUMBER,
                PHONE_NUMBER.PERSON_ID,
                PHONE_NUMBER.PHONE_NUMBER_)
                .values(personId, pn.getPhoneNumber())
                .returning(PHONE_NUMBER.ID)
                .execute();
            result.add(Entity.existing(id, pn));
        }

        result.addAll(partitioned.getExisting());
        return result;
    }
}