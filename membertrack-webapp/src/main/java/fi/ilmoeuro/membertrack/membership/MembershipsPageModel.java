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

import fi.ilmoeuro.membertrack.paging.Pageable;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.session.Refreshable;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import fi.ilmoeuro.membertrack.session.UnitOfWorkFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;

@RequiredArgsConstructor
public final class
    MembershipsPageModel<SessionTokenType>
implements
    Serializable,
    Refreshable,
    Pageable
{
    private static final long serialVersionUID = 0l;
        
    private final MembershipRepositoryFactory<SessionTokenType> mrf;
    private final UnitOfWorkFactory<SessionTokenType> uowFactory;
    private final SessionRunner<SessionTokenType> sessionRunner;

    @Getter
    private transient List<Membership> memberships = Collections.emptyList();

    @Getter(onMethod = @__({@Override}))
    private transient int numPages = 0;

    @Getter(onMethod = @__({@Override}))
    private int currentPage = 0;

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        refresh();
    }

    @Getter
    @Setter
    private @Nullable Membership currentMembership = null;

    private void readObject(ObjectInputStream is)
        throws IOException, ClassNotFoundException
    {
        is.defaultReadObject();
        memberships = Collections.emptyList();
        numPages = 0;
    }

    @Override
    public void refresh() {
        sessionRunner.exec(token -> {
            MembershipRepository mr = mrf.create(token);
            memberships = mr.listMembershipsPage(getCurrentPage());
            numPages = mr.numMembershipsPages();
        });
    }

    public void saveCurrent() {
        if (currentMembership != null) {
            // Make sure `membership` can't be mutated
            final Membership membership = currentMembership;
            sessionRunner.exec(token -> {
                UnitOfWork uow = uowFactory.create(token);
                uow.addEntity(membership.getPerson());
                for (PhoneNumber pn : membership.getPhoneNumbers()) {
                    uow.addEntity(pn);
                }
                for (Subscription sub : membership.getSubscriptions()) {
                    for (SubscriptionPeriod period : sub.getPeriods()) {
                        uow.addEntity(period);
                    }
                }
                uow.execute();
            });
        }
        refresh();
    }
}
