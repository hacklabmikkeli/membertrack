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
import org.apache.wicket.markup.html.list.ListItem;
import fi.ilmoeuro.membertrack.membership.Membership;
import fi.ilmoeuro.membertrack.membership.MembershipsPageModel;
import fi.ilmoeuro.membertrack.membership.db.DbMembershipRepositoryFactory;
import fi.ilmoeuro.membertrack.paging.ui.Pager;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.session.db.DbUnitOfWorkFactory;
import fi.ilmoeuro.membertrack.ui.MtApplication;
import fi.ilmoeuro.membertrack.ui.MtListView;
import fi.ilmoeuro.membertrack.ui.MtModel;
import fi.ilmoeuro.membertrack.ui.MtPage;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;

public final class MembershipsPage extends MtPage {
    private static final long serialVersionUID = 1l;

    private final IModel<MembershipsPageModel<DSLContext>> model;
    private final Pager pager;
    private final MtListView<Membership> memberships;
    private final PersonInfoEditor personInfoEditor;

    public MembershipsPage() {
        model = new MtModel<>(
            new MembershipsPageModel<>(
                new DbMembershipRepositoryFactory(),
                new DbUnitOfWorkFactory(),
                MtApplication.get().getSessionRunner()));
        pager = new Pager(
            "pager",
            model);
        memberships = new MtListView<>(
            "memberships",
            model,
            (ListItem<Membership> item) -> {
                item.add(new PersonInfoPanel(
                    "personInfo",
                    item.getModel(),
                    model));
                item.add(new MtListView<>(
                    "subscriptions",
                    item,
                    (ListItem<Subscription> subItem) -> {
                        subItem.add(
                            new SubscriptionPanel(
                                "subscription",
                                subItem.getModel()));
                    }
                ));
        });
        personInfoEditor = new PersonInfoEditor(
            "personInfoEditor",
            new PropertyModel<>(model, "currentMembership"),
            model);
    }

    @Override
    protected void onInitialize() {
        setDefaultModel(model);
        super.onInitialize();
        add(pager);
        add(memberships);
        add(personInfoEditor);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        PackageResourceReference cssRef = 
            new PackageResourceReference(Pager.class, "Pager.css");
        CssHeaderItem pageCss = CssHeaderItem.forReference(cssRef);
        response.render(pageCss);
    }
}
