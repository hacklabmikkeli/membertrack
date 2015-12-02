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
package fi.ilmoeuro.membertrack.db.exampledata;

import fi.ilmoeuro.membertrack.db.UnitOfWork;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import static fi.ilmoeuro.membertrack.schema.Tables.PERSON;
import static fi.ilmoeuro.membertrack.schema.Tables.PHONE_NUMBER;
import static fi.ilmoeuro.membertrack.schema.Tables.SERVICE;
import static fi.ilmoeuro.membertrack.schema.Tables.SUBSCRIPTION_PERIOD;
import fi.ilmoeuro.membertrack.service.PeriodTimeUnit;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import java.time.LocalDate;
import java.time.Month;
import org.jooq.DSLContext;

public final class DefaultExampleData {
    public void populate(DSLContext jooq) {
        Person p = new Person("John Doe", "john.doe@example.com");
        PhoneNumber pn = new PhoneNumber(p.getId(), "+1234567890");
        Service s = new Service("Tilankäyttö", "Tilankäyttömaksut");
        SubscriptionPeriod pr1 = new SubscriptionPeriod(
            s.getId(),
            p.getId(),
            LocalDate.of(2000, Month.MARCH, 1),
            PeriodTimeUnit.DAY,
            30,
            2000,
            true
        );
        SubscriptionPeriod pr2 = new SubscriptionPeriod(
            s.getId(),
            p.getId(),
            LocalDate.of(2000, Month.APRIL, 1),
            PeriodTimeUnit.DAY,
            30,
            2000,
            true
        );

        UnitOfWork uw = new UnitOfWork(jooq);
        uw.addEntity(PERSON, p);
        uw.addEntity(PHONE_NUMBER, pn);
        uw.addEntity(SERVICE, s);
        uw.addEntity(SUBSCRIPTION_PERIOD, pr1);
        uw.addEntity(SUBSCRIPTION_PERIOD, pr2);
        uw.execute();
    }
}
