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

import static fi.ilmoeuro.membertrack.schema.Tables.*;
import static fi.ilmoeuro.membertrack.data.DataUtils.*;
import static java.util.stream.Collectors.*;
import static org.jooq.impl.DSL.*;
import fi.ilmoeuro.membertrack.data.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
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

    private List<Membership> buildMemberships(
        Map<Optional<Entity<Person>>, 
            Map<Optional<Entity<PhoneNumber>>,
                Map<Optional<Entity<Service>>, 
                    List<Optional<Entity<ServiceSubscription>>>>>> source
    
    ) {
        final ArrayList<Membership> result = new ArrayList<>();
        for (Map.Entry<Optional<Entity<Person>>,
                Map<Optional<Entity<PhoneNumber>>,
                    Map<Optional<Entity<Service>>,
                        List<Optional<Entity<ServiceSubscription>>>>>>
                            personEntry : source.entrySet()) {
            final Optional<Entity<Person>> optPerson =
                personEntry.getKey();
            final ArrayList<Entity<PhoneNumber>> phoneNumbers =
                new ArrayList<>();
            final ArrayList<MemberSubscribedServices> subscriptions =
                new ArrayList<>();
            optPerson.ifPresent((person) -> {
                if (personEntry.getValue() != null) {
                    for (Map.Entry<Optional<Entity<PhoneNumber>>,
                            Map<Optional<Entity<Service>>,
                                List<Optional<Entity<ServiceSubscription>>>>>
                        phoneNumberEntry : personEntry.getValue().entrySet()) {
                        if (phoneNumberEntry.getKey().isPresent()) {
                            phoneNumbers.add(
                                phoneNumberEntry.getKey().get()
                            );
                        } else {
                            subscriptions.addAll(
                                buildSubscriptions(
                                    phoneNumberEntry.getValue()));
                        }
                    }
                    result.add(
                        new Membership(
                            person,
                            phoneNumbers,
                            subscriptions));
                }
            });
        }

        return result;
    }

    private ArrayList<MemberSubscribedServices> buildSubscriptions(
        Map<Optional<Entity<Service>>, 
            List<Optional<Entity<ServiceSubscription>>>> serviceMap
    ) {
        final ArrayList<MemberSubscribedServices> subscriptions =
                new ArrayList<>();
        for (Map.Entry<Optional<Entity<Service>>,
            List<Optional<Entity<ServiceSubscription>>>>
                serviceEntry : serviceMap.entrySet()) {
            Optional<Entity<Service>> optService = serviceEntry.getKey();
            optService.ifPresent((service) -> {
                final ArrayList<Entity<ServiceSubscription>>
                    subscriptionList = new ArrayList<>();
                for (Optional<Entity<ServiceSubscription>>
                    sub : serviceEntry.getValue()) {
                    sub.ifPresent(subscriptionList::add);
                }
                subscriptions.add(
                    new MemberSubscribedServices(
                        service,
                        subscriptionList));
            });
        }
        return subscriptions;
    }

    private List<Membership> listByConditions(
        Condition... conditions
    ) {
        Map<Optional<Entity<Person>>,
            Map<Optional<Entity<PhoneNumber>>,
                Map<Optional<Entity<Service>>,
                    List<Optional<Entity<ServiceSubscription>>>>>> resultMap = 
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
                    orderedGroupingBy(RecordEntities::person,
                    orderedGroupingBy(RecordEntities::phoneNumber,
                    orderedGroupingBy(RecordEntities::service,
                    mapping(RecordEntities::subscription,
                    toList())))));
        return buildMemberships(resultMap);
    }
}
