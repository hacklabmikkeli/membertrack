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
import fi.ilmoeuro.membertrack.service.Services;
import fi.ilmoeuro.membertrack.util.Refreshable;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.util.StateExternalizable;
import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;

@Slf4j
public final class
    MembershipsPageModel<SessionTokenType>
implements
    Refreshable,
    Pageable,
    Serializable,
    StateExternalizable
{
    private static final long serialVersionUID = 0l;

    @Getter
    private final MembershipBrowser<SessionTokenType> membershipBrowser;

    @Getter
    private final MembershipEditor<SessionTokenType> membershipEditor;

    // OK to register callbacks not called until later
    @SuppressWarnings("initialization")
    public MembershipsPageModel(
        fi.ilmoeuro.membertrack.membership.Memberships.Factory<SessionTokenType> mrf,
        Services.Factory<SessionTokenType> srf,
        fi.ilmoeuro.membertrack.session.UnitOfWork.Factory<SessionTokenType> uowFactory,
        SessionRunner<SessionTokenType> sessionRunner
    ) {
        membershipEditor = new MembershipEditor<>(
            srf,
            uowFactory,
            sessionRunner,
            () -> this.refresh());

        membershipBrowser = new MembershipBrowser<>(
            mrf,
            sessionRunner,
            (Membership x) -> membershipEditor.setMembership(x),
            () -> membershipEditor.getMembership());
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
        membershipEditor.close();
    }

    public void createNewMembership() {
        membershipEditor.initNew();
    }

    @Override
    public void saveState(BiConsumer<String, String> pairConsumer) {
        pairConsumer.accept("page", String.valueOf(getCurrentPage()));
    }

    @Override
    public void loadState(Function<String, String> getValue) {
        log.info("Page num: {}", getValue.apply("page"));
        setCurrentPage(NumberUtils.toInt(getValue.apply("page"), 1));
    }
}
