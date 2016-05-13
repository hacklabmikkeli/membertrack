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
import fi.ilmoeuro.membertrack.ui.MtForm;
import fi.ilmoeuro.membertrack.ui.MtTextField;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.jooq.DSLContext;

@Slf4j
public class PersonInfoEditor extends Panel {
    private static final long serialVersionUID = 1l;
    private final MtForm personEditor; 
    private final IModel<Membership> model;
    private final IModel<MembershipsPageModel<DSLContext>> rootModel;

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
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        this.personEditor.add(new MtTextField<String>("person.fullName", model));
        this.personEditor.add(new MtTextField<String>("person.email", model));
        this.add(this.personEditor);
    }

    @Override
    public boolean isVisible() {
        return this.rootModel.getObject().getCurrentMembership() != null;
    }

    private void save() {
        this.rootModel.getObject().saveCurrent();
    }
}
