/*
 * Copyright (C) 2016 Ilmo Euro <ilmo.euro@gmail.com>
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

import fi.ilmoeuro.membertrack.db.ErrorCode;
import fi.ilmoeuro.membertrack.membership.MembershipBrowser.NonUniqueEmailException;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.ServiceRepository;
import fi.ilmoeuro.membertrack.service.ServiceRepositoryFactory;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import fi.ilmoeuro.membertrack.session.UnitOfWorkFactory;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.exception.DataAccessException;

@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class
    MembershipEditor<SessionTokenType>
implements
    Serializable
{
    private final ServiceRepositoryFactory<SessionTokenType> srf;
    private final UnitOfWorkFactory<SessionTokenType> uowFactory;
    private final SessionRunner<SessionTokenType> sessionRunner;

    @Getter
    @Setter
    private @Nullable Membership membership;
    
    public void
        saveCurrent()
    throws
        NonUniqueEmailException
    {
        final Membership ms = membership;
        if (ms != null) {
            try {
                sessionRunner.exec(token -> {
                    UnitOfWork uow = uowFactory.create(token);
                    uow.addEntity(ms.getPerson());
                    uow.execute();
                    for (PhoneNumber pn : ms.getPhoneNumbers()) {
                        uow.addEntity(pn);
                    }
                    for (Subscription sub : ms.getSubscriptions()) {
                        for (SubscriptionPeriod period : sub.getPeriods()) {
                            uow.addEntity(period);
                        }
                    }
                    uow.execute();
                });
                refreshOthers();
            } catch (DataAccessException ex) {
                @Nullable ErrorCode ec = ErrorCode.fromThrowable(ex);

                if (ec == ErrorCode.DUPLICATE_KEY) {
                    throw new NonUniqueEmailException();
                }
            }
        }
    }

    public void deleteCurrent() {
        Membership m = membership;
        if (m != null) {
            m.delete();
        }
    }

    public void unDeleteCurrent() {
        Membership m = membership;
        if (m != null) {
            m.unDelete();
        }
    }

    public void addPhoneNumber() {
        Membership m = membership;
        if (m != null) {
            m.addPhoneNumber();
        }
    }

    public boolean isCurrentDeleted() {
        Membership m = membership;
        if (m != null) {
            return m.isDeleted();
        } else {
            return false;
        }
    }

    public void initNew() {
        sessionRunner.exec(token -> {
            final ServiceRepository sr = srf.create(token);
            final Person person = new Person("", "");
            final List<Service> services = sr.listServices();
            final List<Subscription> subs = services
                .stream()
                .map(serv -> new Subscription(person, serv, new ArrayList<>()))
                .collect(Collectors.<@NonNull Subscription>toList());

            membership = new Membership(person, new ArrayList<>(), subs);
        });
    }

    public void close() {
        membership = null;
    }

    protected abstract void refreshOthers();
}