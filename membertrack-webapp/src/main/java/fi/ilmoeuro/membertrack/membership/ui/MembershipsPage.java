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
import fi.ilmoeuro.membertrack.schema.tables.PhoneNumber;
import fi.ilmoeuro.membertrack.schema.tables.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.ui.Components;
import fi.ilmoeuro.membertrack.ui.MembertrackPage;

public final class MembershipsPage extends MembertrackPage {
    private static final long serialVersionUID = 0l;

    private final MembershipsModel<DSLContext> model;

    public MembershipsPage() {
        model = new MembershipsModel<>(
            new DbMembershipRepositoryFactory()
        );
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        setDefaultModel(Components.model(model));
        add(Components.<Membership>listView(
            "memberships",
            (ListItem<Membership> item) -> {
                item.add(Components.label("person.fullName", item));
                item.add(Components.label("person.email", item));
                item.add(Components.<PhoneNumber>listView(
                    "phoneNumbers",
                    item,
                    (ListItem<PhoneNumber> pnItem) -> {
                        pnItem.add(Components.label("phoneNumber", pnItem));
                    }
                ));
                item.add(Components.<Subscription>listView(
                    "subscriptions",
                    item,
                    (ListItem<Subscription> subItem) -> {
                        subItem.add(Components.label("service.title", subItem));
                        subItem.add(Components.<SubscriptionPeriod>listView(
                            "periods",
                            subItem,
                            (ListItem<SubscriptionPeriod> prdItem) -> {
                                prdItem.add(Components.label("startDate", prdItem));
                                prdItem.add(Components.label("endDate", prdItem));
                                prdItem.add(Components.label("paymentFormatted", prdItem));
                            }
                        ));
                    }
                ));
        }));
    }
}
