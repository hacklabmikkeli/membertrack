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
import fi.ilmoeuro.membertrack.ui.MtForm;
import fi.ilmoeuro.membertrack.ui.MtLink;
import fi.ilmoeuro.membertrack.ui.MtListView;
import fi.ilmoeuro.membertrack.ui.MtTextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.jooq.DSLContext;

@Slf4j
public class PersonInfoEditor extends Panel {
    private static final long serialVersionUID = 1l;
    private final MtForm personEditor; 
    private final IModel<Membership> model;
    private final IModel<MembershipsPageModel<DSLContext>> rootModel;
    private final MtLink closeLink;
    private final FeedbackPanel feedbackPanel;

    @SuppressWarnings("methodref.receiver.bound.invalid")
    public PersonInfoEditor(
        String id,
        IModel<Membership> model,
        IModel<MembershipsPageModel<DSLContext>> rootModel
    ) {
        super(id, model);
        this.model = model;
        this.rootModel = rootModel;
        // this.rootModel is already set, it's OK to register this::save
        this.personEditor = new MtForm("personEditor", this::save);
        this.closeLink = new MtLink("closeLink", () -> {
            rootModel.getObject().setCurrentMembership(null);
        });
        this.feedbackPanel = new FeedbackPanel("feedbackPanel");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        personEditor.add(new MtTextField<String>("person.fullName", model));
        personEditor.add(new MtTextField<String>("person.email", model));
        personEditor.add(new MtListView<>(
            "phoneNumbers",
            model,
            (ListItem<PhoneNumber> item) -> {
                item.add(new MtTextField<String>("phoneNumber", item.getModel()));
            }));
        add(personEditor);
        add(closeLink);
        add(feedbackPanel);
    }

    @Override
    public void onConfigure() {
        super.onConfigure();

        setVisible(rootModel.getObject().getCurrentMembership() != null);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        PackageResourceReference cssRef = 
            new PackageResourceReference(PersonInfoEditor.class, "PersonInfoEditor.css");
        CssHeaderItem pageCss = CssHeaderItem.forReference(cssRef);
        response.render(pageCss);
    }

    private void save() {
        try {
            this.rootModel.getObject().saveCurrent();
        } catch (MembershipsPageModel.NonUniqueEmailException ex) {
            error("Email is already in use");
        }
    }
}
