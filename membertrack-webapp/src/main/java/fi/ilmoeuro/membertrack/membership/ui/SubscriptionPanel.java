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

import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.ui.MtLabel;
import fi.ilmoeuro.membertrack.ui.MtListView;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class SubscriptionPanel extends Panel {
    private static final long serialVersionUID = 0l;
    private final IModel<Subscription> model;

    public SubscriptionPanel(String id, IModel<Subscription> model) {
        super(id, new CompoundPropertyModel<>(model));
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new MtLabel("service.title", model));
        add(new MtListView<>(
            "periods",
            model,
            (ListItem<SubscriptionPeriod> item) -> {
                item.add(new MtLabel("startDate", item));
                item.add(new MtLabel("endDate", item));
                item.add(new MtLabel("paymentFormatted", item));
                item.add(new MarkupContainer("approved") {
                    @Override
                    protected void onConfigure() {
                        super.onConfigure();

                        setVisible(item.getModelObject().getApproved());
                    }
                });
            }));
    }
}
