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
package fi.ilmoeuro.membertrack.service;

import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.util.ClockHolder;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import lombok.Value;

public final @Value class
    Subscription
implements
    Serializable 
{
    private static final long serialVersionUID = 0l;

    Person person;
    Service service;
    List<SubscriptionPeriod> periods;

    public void addPeriod() {
        periods.add(
            new SubscriptionPeriod(
                service,
                person,
                LocalDate.now(ClockHolder.get()),
                LocalDate.now(ClockHolder.get()).plusDays(1),
                0,
                false));
    }
}
