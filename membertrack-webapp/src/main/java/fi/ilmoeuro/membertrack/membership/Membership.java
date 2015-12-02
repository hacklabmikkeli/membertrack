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

import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.service.Service;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import lombok.Value;

public @Value class Membership implements Serializable {

    private static final long serialVersionUID = 0l;

    Person person;
    Set<PhoneNumber> phoneNumbers;
    Map<Service, Set<SubscriptionPeriod>> subscriptionPeriods;
}
