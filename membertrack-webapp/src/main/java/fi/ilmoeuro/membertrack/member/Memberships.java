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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.mapping;
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import static fi.ilmoeuro.membertrack.util.DataUtils.*;
import static fi.ilmoeuro.membertrack.util.Collectors.*;
import static fi.ilmoeuro.membertrack.util.OptionalUtils.*;
import static org.jooq.impl.DSL.*;
import fi.ilmoeuro.membertrack.data.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jooq.Record;
import org.jooq.Condition;
import org.jooq.DSLContext;

@Dependent
public final class Memberships {

    private final DSLContext jooq;

    @Inject
    public Memberships(
        DSLContext jooq
    ) {
       this.jooq = jooq;
    }

    public List<Membership> listAll() {
        return listByConditions();
    }

    private List<Membership> listByConditions(
        Condition... conditions
    ) {
        return buildMemberships(
            jooq
                .select(
                    PERSON.ID,
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
                .leftOuterJoin(SERVICE_SUBSCRIPTION)
                    .on(SERVICE_SUBSCRIPTION.PERSON_ID.eq(PERSON.ID))
                .join(SERVICE)
                    .on(SERVICE_SUBSCRIPTION.SERVICE_ID.eq(SERVICE.ID))
                .where(conditions)
                .unionAll(
                    select(
                        PERSON.ID,
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
                    PERSON.EMAIL,
                    SERVICE.TITLE,
                    SERVICE_SUBSCRIPTION.START_TIME)
                .fetch()
                .stream()
                .collect(
                    orderedGroupingBy((Record r) -> RecordEntities.person(r),
                    orderedGroupingBy((Record r) -> RecordEntities.phoneNumber(r),
                    orderedGroupingBy((Record r) -> RecordEntities.service(r),
                    mapping((Record r) -> RecordEntities.subscription(r),
                    toNonNullList()))))));
    }

    private List<Membership> buildMemberships(
        Map<Optional<Entity<Person>>, 
        Map<Optional<Entity<PhoneNumber>>,
        Map<Optional<Entity<Service>>, 
        List<Optional<Entity<ServiceSubscription>>>>>>
            source
    ) {
        final List<Membership> result = new ArrayList<>();
        final PartitionedMap<Entity<Person>,
            Map<Optional<Entity<PhoneNumber>>,
            Map<Optional<Entity<Service>>,
            List<Optional<Entity<ServiceSubscription>>>>>>
            persons = partitionMap(source);
        for (Map.Entry<Entity<Person>,
                Map<Optional<Entity<PhoneNumber>>,
                Map<Optional<Entity<Service>>,
                List<Optional<Entity<ServiceSubscription>>>>>>
                personsEntry : persons.getMeal().entrySet()) {
            final Entity<Person> person = personsEntry.getKey();
            final PartitionedMap<Entity<PhoneNumber>,
                Map<Optional<Entity<Service>>,
                List<Optional<Entity<ServiceSubscription>>>>>
                personInfo = partitionMap(personsEntry.getValue());
            final List<Entity<PhoneNumber>>
                phoneNumbers = new ArrayList<>(personInfo.getMeal().keySet());
            final Map<Entity<Service>, List<Optional<Entity<ServiceSubscription>>>>
                servicesMap = partitionMap(
                    personInfo
                        .getLeftovers()
                        .orElseGet(() -> new HashMap<>())).getMeal();
            final ArrayList<SubscribedService> subscribed = new ArrayList<>();
            for (Map.Entry<Entity<Service>,
                    List<Optional<Entity<ServiceSubscription>>>>
                    servicesEntry : servicesMap.entrySet()) {
                subscribed.add(new SubscribedService(
                    servicesEntry.getKey(),
                    catMaybes(servicesEntry.getValue())));
            }
            result.add(new Membership(person, phoneNumbers, subscribed));
        }
        return result;
    }
}
