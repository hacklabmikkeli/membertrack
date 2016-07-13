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

import fi.ilmoeuro.membertrack.db.DataIntegrityException;
import fi.ilmoeuro.membertrack.membership.MembershipBrowser.NonUniqueEmailException;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.person.SecondaryEmail;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import fi.ilmoeuro.membertrack.util.SerializableAction;
import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import fi.ilmoeuro.membertrack.service.Services;

@Slf4j
@RequiredArgsConstructor
public final class
    MembershipEditor<SessionTokenType>
implements
    Serializable
{
    private static final long serialVersionUID = 0l;
    
    private final Services.Factory<SessionTokenType> srf;
    private final fi.ilmoeuro.membertrack.session.UnitOfWork.Factory<SessionTokenType> uowFactory;
    private final SessionRunner<SessionTokenType> sessionRunner;
    private final SerializableAction refreshOthers;
    private final SerializableAction close;

    @Getter
    @Setter
    private @Nullable Membership membership;
    
    public void
        saveCurrent()
    throws
        NonUniqueEmailException
    {
        try {
            sessionRunner.exec(token -> {
                final Membership ms = membership;
                if (ms != null) {
                    UnitOfWork uow = uowFactory.create(token);
                    uow.addEntity(ms.getPerson());
                    uow.execute();
                    for (PhoneNumber pn : ms.getPhoneNumbers()) {
                        uow.addEntity(pn);
                    }
                    for (SecondaryEmail se : ms.getSecondaryEmails()) {
                        uow.addEntity(se);
                    }
                    for (Subscription sub : ms.getSubscriptions()) {
                        for (SubscriptionPeriod period : sub.getPeriods()) {
                            uow.addEntity(period);
                        }
                    }
                    uow.execute();

                    if (ms.isDeleted()) {
                        close.execute();
                    }
                }
            });
            refreshOthers();
        } catch (DataIntegrityException ex) {
            if ("person_u_email".equals(ex.getIntegrityConstraint())) {
                throw new NonUniqueEmailException();
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

    public void addSecondaryEmail() {
        Membership m = membership;
        if (m != null) {
            m.addSecondaryEmail();
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

    private void refreshOthers() {
        refreshOthers.execute();
    }
}
