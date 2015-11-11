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
import static fi.ilmoeuro.membertrack.util.DataUtils.*;
import static fi.ilmoeuro.membertrack.util.OptionalUtils.*;
import java.util.Collections;
import java.util.List;
import static org.jooq.impl.DSL.*;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
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
            .select(PERSON.FULLNAME)
            .from(PERSON)
            .orderBy(PERSON.FULLNAME)
            .limit(pageNum * PAGE_SIZE, 1)
            .fetchAny(r -> r.getValue(PERSON.FULLNAME));

        @Nullable String end = jooq
            .select(PERSON.FULLNAME)
            .from(PERSON)
            .orderBy(PERSON.FULLNAME)
            .limit((pageNum + 1) * PAGE_SIZE, 1)
            .fetchAny(r -> r.getValue(PERSON.FULLNAME));

        if (start != null) {
            if (end != null) {
                return listByConditions(
                    PERSON.FULLNAME.ge(start),
                    PERSON.FULLNAME.lt(end)
                );
            } else {
                return listByConditions(
                    PERSON.FULLNAME.ge(start)
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
                    PERSON.FULLNAME,
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
                        PERSON.FULLNAME,
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
                    PERSON.FULLNAME,
                    SERVICE.TITLE,
                    SERVICE_SUBSCRIPTION.START_TIME)
                .fetchLazy()) {
            RelationMapper_2__1<
                Entity<Person>,
                Entity<Service>,
                Entity<PhoneNumber>,
                Entity<ServiceSubscription>>
                mapper = new RelationMapper_2__1<>();
            for (Record r : records) {
                ifAllPresent(RecordEntities.person(r),
                    p -> mapper.root(p));
                ifAllPresent(
                    RecordEntities.person(r),
                    RecordEntities.phoneNumber(r),
                    (p, pn) -> mapper.relate_2(p, pn));
                ifAllPresent(
                    RecordEntities.person(r),
                    RecordEntities.service(r),
                    (p, s) -> mapper.relate_1(p, s));
                ifAllPresent(
                    RecordEntities.person(r),
                    RecordEntities.service(r),
                    RecordEntities.subscription(r),
                    (p, s, sn) -> mapper.relate_1_1(p, s, sn));
            }
            return mapper.<Membership>build(
                (people, phoneNumbers, subscriptions) ->
                    new Membership(people, phoneNumbers, subscriptions)
            );
        }
    }
}
