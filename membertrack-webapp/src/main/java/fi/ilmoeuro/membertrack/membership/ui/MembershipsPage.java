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

import org.jooq.DSLContext;
import fi.ilmoeuro.membertrack.membership.MembershipRepositoryFactory;
import fi.ilmoeuro.membertrack.membership.MembershipsPageModel;
import fi.ilmoeuro.membertrack.membership.db.DbMembershipRepositoryFactory;
import fi.ilmoeuro.membertrack.paging.ui.Pager;
import fi.ilmoeuro.membertrack.service.ServiceRepositoryFactory;
import fi.ilmoeuro.membertrack.service.db.DbServiceRepositoryFactory;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWorkFactory;
import fi.ilmoeuro.membertrack.session.db.DbUnitOfWorkFactory;
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
    private final Component newMembershipButton;
    private final Component membershipEditor;
    private final Component membershipBrowser;
    private final Component pager;

    // OK to register callbacks on methods, they're not called right away
    @SuppressWarnings("initialization")
    public MembershipsPage(
        int pageNumber,
        PageParameters params
    ) {
        final MembershipRepositoryFactory<DSLContext> mrf
            = new DbMembershipRepositoryFactory();
        final ServiceRepositoryFactory<DSLContext> srf
            = new DbServiceRepositoryFactory();
        final UnitOfWorkFactory<DSLContext> uof
            = new DbUnitOfWorkFactory();
        final SessionRunner<DSLContext> sr
            = MtApplication.get().getSessionRunner();
        final IModel<MembershipsPageModel<?>> model
            = new MtModel<>(new MembershipsPageModel<>(mrf, srf, uof, sr));

        pager = new Pager(
            "pager",
            model,
            MembershipsPage.class,
            params,
            "page");
        newMembershipButton = new MtActionButton(
            "newMembershipButton",
            () -> model.getObject().createNewMembership());
        membershipBrowser = new MembershipBrowserPanel(
            "membershipBrowser",
            new PropertyModel<>(model, "membershipBrowser"));
        membershipEditor = new MembershipEditorPanel(
            "membershipEditor",
            new PropertyModel<>(model, "membershipEditor"));

        model.getObject().setCurrentPage(pageNumber);
    }

    public MembershipsPage() {
        this(1, new PageParameters());
    }

    public MembershipsPage(PageParameters params) {
        this(params.get("page").toInt(1), params);
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
