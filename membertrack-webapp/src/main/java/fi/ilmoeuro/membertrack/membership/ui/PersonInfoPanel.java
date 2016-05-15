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
import fi.ilmoeuro.membertrack.membership.MembershipsPageModel;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.ui.MtLabel;
import fi.ilmoeuro.membertrack.ui.MtLink;
import fi.ilmoeuro.membertrack.ui.MtListView;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.jooq.DSLContext;

public class PersonInfoPanel extends Panel {
    private static final long serialVersionUID = 0l;
    private final IModel<Membership> model;
    private final IModel<MembershipsPageModel<DSLContext>> rootModel;

    public PersonInfoPanel(
        String id,
        IModel<Membership> model,
        IModel<MembershipsPageModel<DSLContext>> rootModel
    ) {
        super(id, new CompoundPropertyModel<>(model));
        this.model = model;
        this.rootModel = rootModel;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new ContextImage("avatar",
                             new PropertyModel<>(model, "person.gravatarUrl")));
        add(new MtLabel("person.fullName", model));
        add(new MtLabel("person.email", model));
        add(new MtListView<>(
            "phoneNumbers",
            model,
            (ListItem<PhoneNumber> item) -> {
                item.add(new MtLabel("phoneNumber", item));
            }));
        add(new MtLink("edit", this::selectForEditing));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        PackageResourceReference cssRef = 
            new PackageResourceReference(PersonInfoPanel.class, "PersonInfoPanel.css");
        CssHeaderItem pageCss = CssHeaderItem.forReference(cssRef);
        response.render(pageCss);
    }

    private void selectForEditing() {
        rootModel.getObject().setCurrentMembership(model.getObject());
    }
}
