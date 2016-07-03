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

import fi.ilmoeuro.membertrack.membership.Membership;
import fi.ilmoeuro.membertrack.membership.MembershipBrowser;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.ui.MtHighlighter;
import fi.ilmoeuro.membertrack.ui.MtListView;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.jooq.DSLContext;

public final class MembershipBrowserPanel extends Panel {
    private static final long serialVersionUID = 1l;
    private final MtListView<Membership> memberships;
    private final IModel<MembershipBrowser<DSLContext>> model;

    @SuppressWarnings("initialization")
    public MembershipBrowserPanel(
        String id,
        IModel<MembershipBrowser<DSLContext>> model
    ) {
        super(id, new CompoundPropertyModel<>(model));
        this.model = model;

        memberships = new MtListView<>(
            "memberships",
            model,
            (ListItem<Membership> item) -> {
                item.add(new MtHighlighter(() -> isCurrentPerson(item)));
                item.add(new PersonInfoPanel(
                    "personInfo",
                    item.getModel(),
                    this::selectMembership));
                item.add(new MtListView<>(
                    "subscriptions",
                    item,
                    (ListItem<Subscription> subItem) -> {
                        subItem.add(
                            new SubscriptionPanel(
                                "subscription",
                                subItem.getModel()));}));});
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(memberships);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        PackageResourceReference cssRef = 
            new PackageResourceReference(
                MembershipBrowserPanel.class,
                "MembershipBrowserPanel.css");
        CssHeaderItem pageCss = CssHeaderItem.forReference(cssRef);
        response.render(pageCss);
    }

    private void selectMembership(Membership membership) {
        model.getObject().setSelectedMembership(membership);
    }

    private boolean isCurrentPerson(ListItem<Membership> item) {
        return model.getObject().checkIfMembershipSelected(item.getModelObject());
    }
}
