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
package fi.ilmoeuro.membertrack.membership;

import com.relatejava.relate.RelationMapper_2__1;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import org.jooq.DSLContext;
import org.jooq.Record;
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import fi.ilmoeuro.membertrack.schema.tables.records.PersonRecord;
import fi.ilmoeuro.membertrack.schema.tables.records.PhoneNumberRecord;
import fi.ilmoeuro.membertrack.schema.tables.records.ServiceRecord;
import fi.ilmoeuro.membertrack.schema.tables.records.SubscriptionPeriodRecord;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jooq.Cursor;
import org.jooq.SelectField;

public final class MembershipRepository {

    private final DSLContext jooq;

    public MembershipRepository(
        DSLContext jooq
    ) {
        this.jooq = jooq;
    }

    public List<Membership> listMemberships() {
        List<SelectField<?>> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(PERSON.fields()));
        fields.addAll(Arrays.asList(PHONE_NUMBER.fields()));
        fields.addAll(Arrays.asList(SERVICE.fields()));
        fields.addAll(Arrays.asList(SUBSCRIPTION_PERIOD.fields()));
        RelationMapper_2__1<Person, Service, PhoneNumber, SubscriptionPeriod>
            mapper = new RelationMapper_2__1<>();
        try (Cursor<? extends Record> records = jooq
            .selectDistinct(fields)
            .from(PERSON)
            .leftOuterJoin(PHONE_NUMBER)
                .onKey()
            .crossJoin(SERVICE)
            .leftOuterJoin(SUBSCRIPTION_PERIOD)
                .on(SUBSCRIPTION_PERIOD.PERSON_ID.eq(PERSON.ID),
                    SUBSCRIPTION_PERIOD.SERVICE_ID.eq(SERVICE.ID))
            .fetchLazy()) {
            for (Record r : records) {
                PersonRecord pr = r.into(PERSON);
                PhoneNumberRecord pnr = r.into(PHONE_NUMBER);
                ServiceRecord sr = r.into(SERVICE);
                SubscriptionPeriodRecord spr = r.into(SUBSCRIPTION_PERIOD);

                if (pr.getId() != null) {
                    Person p = pr.into(Person.class);
                    mapper.root(p);

                    if (sr.getId() != null) {
                        Service s = sr.into(Service.class);
                        mapper.relate_1(p, s);

                        if (spr.getId() != null) {
                            SubscriptionPeriod sp = spr.into(SubscriptionPeriod.class);

                            mapper.relate_1_1(p, s, sp);
                        }
                    }

                    if (pnr.getId() != null) {
                        PhoneNumber pn = pnr.into(PhoneNumber.class);
                        mapper.relate_2(p, pn);
                    }
                }
            }
        }

        return mapper.<Membership>build(Membership::new);
    }
}
