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

import com.github.javafaker.Faker;
import fi.ilmoeuro.membertrack.db.ExampleData;
import fi.ilmoeuro.membertrack.person.Account;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.service.PeriodTimeUnit;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.session.SessionToken;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import fi.ilmoeuro.membertrack.session.UnitOfWorkFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.Locale;
import java.util.Random;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class
    DefaultExampleData<SessionTokenType>
implements
    ExampleData<SessionTokenType>
{
    private final UnitOfWorkFactory<SessionTokenType> uwf;
    
    @Override
    public void populate(SessionToken<SessionTokenType> session) {
        Faker faker = new Faker(Locale.forLanguageTag("fi"));
        UnitOfWork uw = uwf.create(session);
        Random random = new Random();

        Service s = new Service("Tilankäyttö", "Tilankäyttömaksut");
        uw.addEntity(s);

        Person admin = new Person("Mr. Admin", "admin@example.com");
        Account adminAccount = Account.create(admin, "admin");

        uw.addEntity(admin);
        uw.addEntity(adminAccount);

        uw.execute();
        for (int i = 0; i < 100; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            Person p = new Person(
                firstName + " " + lastName,
                String.format("%s.%s.%d@example.com",
                    firstName.toLowerCase(Locale.ROOT),
                    lastName.toLowerCase(Locale.ROOT),
                    random.nextInt(10000)));
            PhoneNumber pn = new PhoneNumber(
                p.getId(), faker.phoneNumber().phoneNumber());
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

            uw = uwf.create(session);
            uw.addEntity(p);
            uw.addEntity(pn);
            uw.addEntity(pr1);
            uw.addEntity(pr2);
            uw.execute();
        }
    }
}
