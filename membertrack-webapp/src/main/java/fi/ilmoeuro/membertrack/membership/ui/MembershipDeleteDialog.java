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
import fi.ilmoeuro.membertrack.ui.MtButton;
import fi.ilmoeuro.membertrack.ui.MtLink;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.jooq.DSLContext;

@Slf4j
public class MembershipDeleteDialog extends Panel {
    private static final long serialVersionUID = 3l;

    private final IModel<Membership> model;
    private final IModel<MembershipsPageModel<DSLContext>> rootModel;

    private final MtLink closeLink;

    private final Form<Object> deleteForm;
    private final MtButton confirmButton;
    private final MtButton cancelButton;

    // OK to register callbacks on methods, they're not called right away
    @SuppressWarnings("initialization")
    public MembershipDeleteDialog(
        String id,
        IModel<Membership> model,
        IModel<MembershipsPageModel<DSLContext>> rootModel
    ) {
        super(id, model);

        this.model = model;
        this.rootModel = rootModel;
        this.closeLink = new MtLink("closeLink", this::close);

        this.deleteForm = new Form<>("deleteForm");
        this.confirmButton = new MtButton("confirmButton", this::confirm);
        this.cancelButton = new MtButton("cancelButton", this::cancel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        deleteForm.add(confirmButton);
        deleteForm.add(cancelButton);
        add(deleteForm);
        add(closeLink);
    }

    @Override
    public void onConfigure() {
        super.onConfigure();

        if (model.getObject() != null
            && model.getObject().isDeleted()) {
            setVisible(true);
        } else {
            setVisible(false);
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        PackageResourceReference cssRef = 
            new PackageResourceReference(MembershipDeleteDialog.class, "MembershipDeleteDialog.css");
        CssHeaderItem pageCss = CssHeaderItem.forReference(cssRef);
        response.render(pageCss);
    }

    private void confirm() {
        try {
            this.rootModel.getObject().saveCurrent();
            rootModel.getObject().setCurrentMembership(null);
        } catch (MembershipsPageModel.NonUniqueEmailException ex) {
        }
    }

    private void cancel() {
        model.getObject().unDelete();
    }

    private void close() {
        rootModel.getObject().setCurrentMembership(null);
    }
}
