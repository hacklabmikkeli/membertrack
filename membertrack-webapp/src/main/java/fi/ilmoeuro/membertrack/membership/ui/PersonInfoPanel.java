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
import fi.ilmoeuro.membertrack.membership.MembershipsModel;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.ui.Components;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.jooq.DSLContext;

public class PersonInfoPanel extends Panel {
    private final IModel<Membership> model;
    private final IModel<MembershipsModel<DSLContext>> rootModel;

    public PersonInfoPanel(
        String id,
        IModel<Membership> model,
        IModel<MembershipsModel<DSLContext>> rootModel
    ) {
        super(id, new CompoundPropertyModel<>(model));
        this.model = model;
        this.rootModel = rootModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(Components.label("person.fullName", model));
        add(Components.label("person.email", model));
        add(Components.<PhoneNumber>listView(
            "phoneNumbers",
            model,
            (ListItem<PhoneNumber> item) -> {
                item.add(Components.label("phoneNumber", item));
            }));
        add(Components.link("edit", () -> {
            rootModel.getObject().setCurrentMembership(model.getObject());
        }));
    }
}
