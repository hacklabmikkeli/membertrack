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

import fi.ilmoeuro.membertrack.data.Entity;
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import java.util.Optional;
import org.jooq.Record;

public class RecordEntities {
    
    public static Optional<Entity<Person>> person(
        Record r
    ) {
        if (r.getValue(PERSON.ID) == null ||
            r.getValue(PERSON.EMAIL) == null) {
            return Optional.empty();
        } else {
            return Optional.of(
                new Entity<>(
                    r.getValue(PERSON.ID),
                    new Person(
                        r.getValue(PERSON.EMAIL)
                    )
                )
            );
        }
    }

    public static Optional<Entity<Service>> service(
        Record r
    ) {
        if (r.getValue(SERVICE.ID) == null ||
            r.getValue(SERVICE.TITLE) == null ||
            r.getValue(SERVICE.DESCRIPTION) == null) {
            return Optional.empty();
        } else {
            return Optional.of(
                new Entity<>(
                    r.getValue(SERVICE.ID),
                    new Service(
                        r.getValue(SERVICE.TITLE),
                        r.getValue(SERVICE.DESCRIPTION)
                    )
                )
            );
        }
    }

    public static Optional<Entity<ServiceSubscription>> subscription(
        Record r
    ) {
        if (r.getValue(SERVICE_SUBSCRIPTION.ID) == null ||
            r.getValue(SERVICE_SUBSCRIPTION.START_TIME) == null ||
            r.getValue(SERVICE_SUBSCRIPTION.LENGTH) == null) {
            return Optional.empty();
        } else {
            return Optional.of(
                new Entity<>(
                    r.getValue(SERVICE_SUBSCRIPTION.ID),
                    new ServiceSubscription(
                        r.getValue(SERVICE_SUBSCRIPTION.START_TIME),
                        r.getValue(SERVICE_SUBSCRIPTION.LENGTH)
                    )
                )
            );
        }
    }

    public static Optional<Entity<PhoneNumber>> phoneNumber(
        Record r
    ) {
        if (r.getValue(PHONE_NUMBER.ID) == null ||
            r.getValue(PHONE_NUMBER.PHONE_NUMBER_) == null) {
            return Optional.empty();
        } else {
            return Optional.of(
                new Entity<>(
                    r.getValue(PHONE_NUMBER.ID),
                    new PhoneNumber(
                        r.getValue(PHONE_NUMBER.PHONE_NUMBER_)
                    )
                )
            );
        }
    }
}
