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

import com.relatejava.relate.RelationMapper_2__1;
import fi.ilmoeuro.membertrack.data.Entity;
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.ServiceSubscription;
import static fi.ilmoeuro.membertrack.data.RecordEntityMapper.*;
import static fi.ilmoeuro.membertrack.util.DataUtils.*;
import static fi.ilmoeuro.membertrack.util.OptionalUtils.*;
import java.util.Collections;
import java.util.List;
import static org.jooq.impl.DSL.*;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.Record;
import org.jooq.Cursor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

@Dependent
public final class Memberships {

    private final DSLContext jooq;
    private static final int PAGE_SIZE = 10;

    @Inject
    public Memberships(
        DSLContext jooq
    ) {
       this.jooq = jooq;
    }

    public int numPages() {
        return (int)
            Math.ceil(
                jooq.select(DSL.count())
                    .from(PERSON)
                    .fetchAnyInto(Integer.class)
                / (double)PAGE_SIZE);
    }

    public List<Membership> listPage(int pageNum) {
        @Nullable String start = jooq
            .select(PERSON.FULL_NAME)
            .from(PERSON)
            .orderBy(PERSON.FULL_NAME)
            .limit(pageNum * PAGE_SIZE, 1)
            .fetchAny(this::personFullName);

        @Nullable String end = jooq
            .select(PERSON.FULL_NAME)
            .from(PERSON)
            .orderBy(PERSON.FULL_NAME)
            .limit((pageNum + 1) * PAGE_SIZE, 1)
            .fetchAny(this::personFullName);

        if (start != null) {
            if (end != null) {
                return listByConditions(
                    PERSON.FULL_NAME.ge(start),
                    PERSON.FULL_NAME.lt(end)
                );
            } else {
                return listByConditions(
                    PERSON.FULL_NAME.ge(start)
                );
            }
        }

        return Collections.emptyList();
    }

    public List<Membership> listAll() {
        return listByConditions();
    }

    private List<Membership> listByConditions(
        Condition... conditions
    ) {
        try (Cursor<? extends Record> records =
            jooq
                .select(
                    PERSON.ID,
                    PERSON.FULL_NAME,
                    PERSON.EMAIL,
                    asNull(PHONE_NUMBER.ID),
                    asNull(PHONE_NUMBER.PHONE_NUMBER_),
                    SERVICE.ID,
                    SERVICE.TITLE,
                    SERVICE.DESCRIPTION,
                    SERVICE_SUBSCRIPTION.ID,
                    SERVICE_SUBSCRIPTION.START_TIME,
                    SERVICE_SUBSCRIPTION.LENGTH,
                    SERVICE_SUBSCRIPTION.PAYMENT)
                .from(PERSON)
                .crossJoin(SERVICE)
                .leftOuterJoin(SERVICE_SUBSCRIPTION)
                    .on(SERVICE_SUBSCRIPTION.PERSON_ID.eq(PERSON.ID),
                        SERVICE_SUBSCRIPTION.SERVICE_ID.eq(SERVICE.ID))
                .where(conditions)
                .unionAll(
                    select(
                        PERSON.ID,
                        PERSON.FULL_NAME,
                        PERSON.EMAIL,
                        PHONE_NUMBER.ID,
                        PHONE_NUMBER.PHONE_NUMBER_,
                        asNull(SERVICE.ID),
                        asNull(SERVICE.TITLE),
                        asNull(SERVICE.DESCRIPTION),
                        asNull(SERVICE_SUBSCRIPTION.ID),
                        asNull(SERVICE_SUBSCRIPTION.START_TIME),
                        asNull(SERVICE_SUBSCRIPTION.LENGTH),
                        asNull(SERVICE_SUBSCRIPTION.PAYMENT))
                    .from(PHONE_NUMBER)
                    .join(PERSON).onKey()
                    .where(conditions))
                .orderBy(
                    PERSON.FULL_NAME,
                    SERVICE.TITLE,
                    SERVICE_SUBSCRIPTION.START_TIME)
                .fetchLazy()) {
            final @NonNull
                RelationMapper_2__1<
                Entity<Person>,
                Entity<Service>,
                Entity<PhoneNumber>,
                Entity<ServiceSubscription>>
                mapper = new RelationMapper_2__1<>();
            for (Record r : records) {
                ifAllPresent(
                    mapToEntity(r.into(r.fields(0,1,2)), Person.class),
                    p -> mapper.root(p));
                ifAllPresent(
                    mapToEntity(r.into(r.fields(0,1,2)), Person.class),
                    mapToEntity(r.into(r.fields(3,4)), PhoneNumber.class),
                    (p, pn) -> mapper.relate_2(p, pn));
                ifAllPresent(
                    mapToEntity(r.into(r.fields(0,1,2)), Person.class),
                    mapToEntity(r.into(r.fields(5,6,7)), Service.class),
                    (p, s) -> mapper.relate_1(p, s));
                ifAllPresent(
                    mapToEntity(r.into(r.fields(0,1,2)), Person.class),
                    mapToEntity(r.into(r.fields(5,6,7)), Service.class),
                    mapToEntity(r.into(r.fields(8,9,10,11)), ServiceSubscription.class),
                    (p, s, sn) -> mapper.relate_1_1(p, s, sn));
            }
            return mapper.<Membership>build(Membership::new);
        }
    }

    private String personFullName(Record r) {
        return r.getValue(PERSON.FULL_NAME);
    }
}
