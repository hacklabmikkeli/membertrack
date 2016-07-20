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

import fi.ilmoeuro.membertrack.auth.PasswordResetManager;
import fi.ilmoeuro.membertrack.auth.db.DbPasswordResetManager;
import fi.ilmoeuro.membertrack.config.Config;
import fi.ilmoeuro.membertrack.membership.Memberships;
import org.jooq.DSLContext;
import fi.ilmoeuro.membertrack.membership.MembershipsPageModel;
import fi.ilmoeuro.membertrack.paging.ui.Pager;
import fi.ilmoeuro.membertrack.service.Services;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import fi.ilmoeuro.membertrack.session.db.DbUnitOfWork;
import fi.ilmoeuro.membertrack.ui.MtActionButton;
import fi.ilmoeuro.membertrack.ui.MtApplication;
import fi.ilmoeuro.membertrack.ui.MtLink;
import fi.ilmoeuro.membertrack.ui.MtModel;
import fi.ilmoeuro.membertrack.ui.MtPage;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import static fi.ilmoeuro.membertrack.membership.MembershipsPageModel.Editor.*;
import fi.ilmoeuro.membertrack.membership.db.DbMemberships;
import fi.ilmoeuro.membertrack.person.Accounts;
import fi.ilmoeuro.membertrack.person.Persons;
import fi.ilmoeuro.membertrack.person.db.DbAccounts;
import fi.ilmoeuro.membertrack.person.db.DbPersons;
import fi.ilmoeuro.membertrack.service.db.DbServices;

public final class MembershipsPage extends MtPage {
    private static final long serialVersionUID = 1l;
    private final IModel<MembershipsPageModel<?>> model;

    private final Component pager;
    private final Component membershipBrowser;
    private final Component newMembershipButton;

    private final WebMarkupContainer editorPane;
    private final MtLink membershipTab;
    private final MtLink accountTab;
    private final Component closeLink;
    private final Component membershipEditor;
    private final Component accountEditor;

    // OK to register callbacks on methods, they're not called right away
    @SuppressWarnings("initialization")
    public MembershipsPage() {
        super();

        final Config conf
            = MtApplication.get().getConfig();
        final Memberships.Factory<DSLContext> mrf
            = new DbMemberships.Factory();
        final Services.Factory<DSLContext> srf
            = new DbServices.Factory();
        final Accounts.Factory<DSLContext> af
            = new DbAccounts.Factory();
        final UnitOfWork.Factory<DSLContext> uof
            = new DbUnitOfWork.Factory();
        final Persons.Factory<DSLContext> pf
            = new DbPersons.Factory();
        final SessionRunner<DSLContext> sr
            = MtApplication.get().getSessionRunner();
        final DbPasswordResetManager.Config prmConf
            = conf.getPasswordResetManager();
        final PasswordResetManager prm
            = new DbPasswordResetManager(sr, uof, af, pf, prmConf);
        model = new MtModel<>(
            new MembershipsPageModel<DSLContext>(prm, mrf, srf, af, uof, sr));

        pager = new Pager<>("pager", model);
        newMembershipButton = new MtActionButton(
            "newMembershipButton",
            () -> model.getObject().createNewMembership());
        membershipBrowser = new MembershipBrowserPanel(
            "membershipBrowser",
            new PropertyModel<>(model, "membershipBrowser"));

        editorPane = new WebMarkupContainer("editorPane");
        membershipTab = new MtLink("membershipTab", this::openMembershipEditor);
        accountTab = new MtLink("accountTab", this::openAccountEditor);
        closeLink = new MtLink("closeLink", this::close);

        membershipEditor = new MembershipEditorPanel(
            "membershipEditor",
            new PropertyModel<>(model, "membershipEditor"));
        accountEditor = new AccountEditorPanel(
            "accountEditor",
            new PropertyModel<>(model, "accountEditor"));

        setModel(model);
    }

    public MembershipsPage(PageParameters params) {
        this();
        loadPageParameters(params);
    }

    private void close() {
        model.getObject().close();
    }

    private void openMembershipEditor() {
        model.getObject().setCurrentEditor(MEMBERSHIP);
    }

    private void openAccountEditor() {
        model.getObject().setCurrentEditor(ACCOUNT);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        
        MembershipsPageModel obj = model.getObject();
        editorPane.setVisible(obj.getMembership() != null);
        membershipTab.setEnabled(obj.getCurrentEditor() != MEMBERSHIP);
        accountTab.setEnabled(obj.getCurrentEditor() != ACCOUNT);
        membershipEditor.setVisible(obj.getCurrentEditor() == MEMBERSHIP);
        accountEditor.setVisible(obj.getCurrentEditor() == ACCOUNT);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(pager);
        add(newMembershipButton);
        add(membershipBrowser);

        editorPane.add(membershipTab);
        editorPane.add(accountTab);
        editorPane.add(closeLink);
        editorPane.add(membershipEditor);
        editorPane.add(accountEditor);
        add(editorPane);
    }
}
