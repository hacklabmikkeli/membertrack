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
import fi.ilmoeuro.membertrack.paging.ui.Pager;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.ui.MtButton;
import fi.ilmoeuro.membertrack.ui.MtForm;
import fi.ilmoeuro.membertrack.ui.MtHighlighter;
import fi.ilmoeuro.membertrack.ui.MtListView;
import fi.ilmoeuro.membertrack.ui.MtTextField;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jooq.DSLContext;

public final class MembershipBrowserPanel extends Panel {
    private static final long serialVersionUID = 1l;
    private final Component pager;
    private final MtTextField<String> searchString;
    private final MtButton searchButton;
    private final MtForm searchForm;
    private final MtListView<Membership> memberships;
    private final IModel<MembershipBrowser<DSLContext>> model;

    @SuppressWarnings("initialization")
    public MembershipBrowserPanel(
        String id,
        IModel<MembershipBrowser<DSLContext>> model
    ) {
        super(id, new CompoundPropertyModel<>(model));
        this.model = model;

        pager = new Pager<>("pager", model);

        searchString = new MtTextField<>("searchString", model);
        searchButton = new MtButton("searchButton", this::search);
        searchForm = new MtForm("searchForm", model);

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

        searchForm.add(searchString);
        searchForm.add(searchButton);
        add(searchForm);
        add(memberships);
        add(pager);
    }

    private void selectMembership(Membership membership) {
        model.getObject().setSelectedMembership(membership);
    }

    private boolean isCurrentPerson(ListItem<Membership> item) {
        return model.getObject().checkIfMembershipSelected(item.getModelObject());
    }

    private void search() {
        model.getObject().search();
    }
}
