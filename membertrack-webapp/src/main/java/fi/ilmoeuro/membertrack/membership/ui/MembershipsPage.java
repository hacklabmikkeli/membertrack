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
package fi.ilmoeuro.membertrack.membership.ui;

import fi.ilmoeuro.membertrack.membership.Memberships;
import org.jooq.DSLContext;
import fi.ilmoeuro.membertrack.membership.MembershipsPageModel;
import fi.ilmoeuro.membertrack.paging.ui.Pager;
import fi.ilmoeuro.membertrack.service.Services;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.ui.MtActionButton;
import fi.ilmoeuro.membertrack.ui.MtApplication;
import fi.ilmoeuro.membertrack.ui.MtModel;
import fi.ilmoeuro.membertrack.ui.MtPage;
import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public final class MembershipsPage extends MtPage {
    private static final long serialVersionUID = 1l;
    private final IModel<MembershipsPageModel<?>> model;
    private final Component newMembershipButton;
    private final Component membershipEditor;
    private final Component membershipBrowser;
    private final Component pager;

    // OK to register callbacks on methods, they're not called right away
    @SuppressWarnings("initialization")
    public MembershipsPage(
    ) {
        super();

        final Memberships.Factory<DSLContext> mrf
            = new fi.ilmoeuro.membertrack.membership.db.DbMemberships.Factory();
        final Services.Factory<DSLContext> srf
            = new fi.ilmoeuro.membertrack.service.db.DbServices.Factory();
        final fi.ilmoeuro.membertrack.session.UnitOfWork.Factory<DSLContext> uof
            = new fi.ilmoeuro.membertrack.session.db.DbUnitOfWork.Factory();
        final SessionRunner<DSLContext> sr
            = MtApplication.get().getSessionRunner();
        model = new MtModel<>(
            new MembershipsPageModel<DSLContext>(mrf, srf, uof, sr));

        pager = new Pager<>("pager", model);
        newMembershipButton = new MtActionButton(
            "newMembershipButton",
            () -> model.getObject().createNewMembership());
        membershipBrowser = new MembershipBrowserPanel(
            "membershipBrowser",
            new PropertyModel<>(model, "membershipBrowser"));
        membershipEditor = new MembershipEditorPanel(
            "membershipEditor",
            new PropertyModel<>(model, "membershipEditor"));
    }

    public MembershipsPage(PageParameters params) {
        this();

        model.getObject().loadState(k -> params.get(k).toString(""));
    }

    @Override
    public PageParameters getPageParameters() {
        final PageParameters params = super.getPageParameters();
        if (params != null) {
            model.getObject().saveState(
                (String k, String v) -> params.set(k, v));
        }
        return params;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(pager);
        add(newMembershipButton);
        add(membershipBrowser);
        add(membershipEditor);
    }
}
