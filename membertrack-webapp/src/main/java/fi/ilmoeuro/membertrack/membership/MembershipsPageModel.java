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

import fi.ilmoeuro.membertrack.paging.Pageable;
import fi.ilmoeuro.membertrack.service.ServiceRepositoryFactory;
import fi.ilmoeuro.membertrack.session.Refreshable;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWorkFactory;
import java.io.Serializable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class
    MembershipsPageModel<SessionTokenType>
implements
    Refreshable,
    Pageable,
    Serializable
{
    @Getter
    private final MembershipBrowser<SessionTokenType> membershipBrowser;

    @Getter
    private final MembershipEditor<SessionTokenType> membershipEditor;

    // OK to register callbacks not called until later
    @SuppressWarnings("initialization")
    public MembershipsPageModel(
        MembershipRepositoryFactory<SessionTokenType> mrf,
        ServiceRepositoryFactory<SessionTokenType> srf,
        UnitOfWorkFactory<SessionTokenType> uowFactory,
        SessionRunner<SessionTokenType> sessionRunner
    ) {
        membershipEditor = new MembershipEditor<SessionTokenType>(
            srf,
            uowFactory,
            sessionRunner
        ) {
            @Override
            protected void refreshOthers() {
                MembershipsPageModel.this.refresh();
            }
        };

        membershipBrowser = new MembershipBrowser<SessionTokenType>(
            mrf,
            sessionRunner
        ) {
            @Override
            public Membership getSelectedMembership() {
                return membershipEditor.getMembership();
            }

            @Override
            public void setSelectedMembership(Membership membership) {
                membershipEditor.setMembership(membership);
            }
        };
    }

    @Override
    public void refresh() {
        membershipBrowser.refresh();
    }

    @Override
    public int getNumPages() {
        return membershipBrowser.getNumPages();
    }

    @Override
    public int getCurrentPage() {
        return membershipBrowser.getCurrentPage();
    }

    @Override
    public void setCurrentPage(int pageNum) {
        membershipBrowser.setCurrentPage(pageNum);
    }

    public void createNewMembership() {
        membershipEditor.initNew();
    }
}
