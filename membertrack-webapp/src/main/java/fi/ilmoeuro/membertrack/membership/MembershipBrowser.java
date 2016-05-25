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
import fi.ilmoeuro.membertrack.session.Refreshable;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public abstract class
    MembershipBrowser<SessionTokenType>
implements
    Serializable,
    Refreshable,
    Pageable
{
    private static final long serialVersionUID = 1l;

    public static class NonUniqueEmailException extends Exception {}
        
    private final MembershipRepositoryFactory<SessionTokenType> mrf;
    private final SessionRunner<SessionTokenType> sessionRunner;

    @Getter
    private transient List<Membership> memberships = Collections.emptyList();

    @Getter(onMethod = @__({@Override}))
    private transient int numPages = 0;

    @Getter(onMethod = @__({@Override}))
    private int currentPage = 1;

    @Override
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        refresh();
    }

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

    public boolean checkIfMembershipSelected(Membership membership) {
        @Nullable Membership selectedMembership = getSelectedMembership();
        if (selectedMembership != null) {
            return Objects.equals(
                selectedMembership.getPerson().getId(),
                membership.getPerson().getId());
        } else {
            return false;
        }
    }

    public abstract @Nullable Membership getSelectedMembership();
    public abstract void setSelectedMembership(@Nullable Membership membership);
}
