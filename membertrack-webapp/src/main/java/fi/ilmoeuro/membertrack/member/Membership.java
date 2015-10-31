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

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import fi.ilmoeuro.membertrack.data.Entity;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jooq.lambda.tuple.Tuple2;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Membership {
    private final ListBuilder builder;
    @Getter
    private final Entity<Person> person;

    public Collection<Entity<PhoneNumber>> getPhoneNumbers() {
        return builder.phoneNumbers.get(person);
    }

    public Map<Entity<Service>,
               Collection<Entity<ServiceSubscription>>> getSubscriptions() {
        final LinkedHashMap<
            Entity<Service>,
            Collection<Entity<ServiceSubscription>>>
            result = new LinkedHashMap<>();

        for (Entity<Service> service : builder.services.get(person)) {
            result.put(
                service,
                builder.subscriptions.get(new Tuple2<>(person, service)));
        }

        return result;
    }

    public static final class ListBuilder {
        private final Set<Entity<Person>>
            people = new LinkedHashSet<>();
        private final Multimap<Entity<Person>, Entity<PhoneNumber>>
            phoneNumbers = LinkedHashMultimap.create();
        private final Multimap<Entity<Person>, Entity<Service>>
            services = LinkedHashMultimap.create();
        private final Multimap<Tuple2<Entity<Person>,Entity<Service>>,
            Entity<ServiceSubscription>>
            subscriptions = LinkedHashMultimap.create();

        public void putPerson(Entity<Person> p) {
            people.add(p);
        }

        public void putPhoneNumber(Entity<Person> p, Entity<PhoneNumber> pn) {
            phoneNumbers.put(p, pn);
        }

        public void putService(Entity<Person> p, Entity<Service> s) {
            services.put(p, s);
        }
        
        public void putSubscription(
            Entity<Person> p,
            Entity<Service> s,
            Entity<ServiceSubscription> sn
        ) {
            subscriptions.put(new Tuple2<>(p, s), sn);
        }

        public List<Membership> build() {
            return people
                .stream()
                .map(p -> new Membership(this, p))
                .collect(Collectors.toList());
        }
    }
}
