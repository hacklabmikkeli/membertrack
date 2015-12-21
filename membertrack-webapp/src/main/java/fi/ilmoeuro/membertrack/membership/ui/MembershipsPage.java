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
import fi.ilmoeuro.membertrack.membership.MembershipsModel;
import fi.ilmoeuro.membertrack.membership.db.DbMembershipRepositoryFactory;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.ui.Components;
import fi.ilmoeuro.membertrack.ui.MembertrackPage;
import org.apache.wicket.model.IModel;

public final class MembershipsPage extends MembertrackPage {
    private static final long serialVersionUID = 0l;

    private final IModel<MembershipsModel<DSLContext>> model;

    public MembershipsPage() {
        model = Components.model(
            new MembershipsModel<>(
                new DbMembershipRepositoryFactory()));
        setDefaultModel(model);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(Components.label("currentMembership.person.fullName", model));
        add(Components.<Membership>listView(
            "memberships",
            model,
            (ListItem<Membership> item) -> {
                item.add(new PersonInfoPanel(
                    "personInfo",
                    item.getModel(),
                    model));
                item.add(Components.<Subscription>listView(
                    "subscriptions",
                    item,
                    (ListItem<Subscription> subItem) -> {
                        subItem.add(
                            new SubscriptionPanel(
                                "subscription",
                                subItem.getModel()));
                    }
                ));
        }));
    }
}
