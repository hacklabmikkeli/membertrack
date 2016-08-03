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

import fi.ilmoeuro.membertrack.TestBase;
import fi.ilmoeuro.membertrack.session.db.DbUnitOfWork;
import fi.ilmoeuro.membertrack.membership.db.DbMemberships;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.service.PeriodTimeUnit;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;
import org.testng.annotations.Test;

@Slf4j
public class MembershipRepositoryNGTest extends TestBase {
    
    public MembershipRepositoryNGTest() {
    }

    @Test
    public void testListMemberships() {
        Person p = new Person("John", "Doe", "john.doe@example.com");
        PhoneNumber pn = new PhoneNumber(p, "+1234567890");
        Service s = new Service("Tilankäyttö", "Tilankäyttömaksut");
        SubscriptionPeriod pr1 = new SubscriptionPeriod(
            s,
            p,
            LocalDate.of(2000, Month.MARCH, 1),
            PeriodTimeUnit.DAY,
            30,
            2000,
            true
        );
        SubscriptionPeriod pr2 = new SubscriptionPeriod(
            s,
            p,
            LocalDate.of(2000, Month.APRIL, 1),
            PeriodTimeUnit.DAY,
            30,
            2000,
            true
        );

        UnitOfWork uw = new DbUnitOfWork(jooq());
        uw.addEntity(p);
        uw.addEntity(pn);
        uw.addEntity(s);
        uw.addEntity(pr1);
        uw.addEntity(pr2);
        uw.execute();

        Memberships repo = new DbMemberships(jooq());
        List<Membership> memberships = repo.listMembershipsPage(1);

        assertThat(memberships, hasSize(1));
        Membership membership = memberships.get(0);
        assertThat(membership.getPerson(), equalTo(p));
        assertThat(membership.getPhoneNumbers(), contains(pn));
        assertThat(membership.getSubscriptions(), hasSize(1));
        Subscription sub = membership.getSubscriptions().get(0);
        assertThat(sub.getService(), equalTo(s));
        assertThat(sub.getPeriods(), containsInAnyOrder(pr1, pr2));
    }
}
