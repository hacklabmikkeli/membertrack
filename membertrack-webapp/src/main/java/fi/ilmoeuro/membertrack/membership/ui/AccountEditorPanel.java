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

import fi.ilmoeuro.membertrack.membership.AccountEditor;
import fi.ilmoeuro.membertrack.membership.Membership;
import fi.ilmoeuro.membertrack.ui.MtButton;
import fi.ilmoeuro.membertrack.ui.MtForm;
import fi.ilmoeuro.membertrack.ui.MtPasswordField;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

@Slf4j
public final class AccountEditorPanel extends Panel {
    private static final long serialVersionUID = 3l;

    private final IModel<AccountEditor<?>> model;

    private final FeedbackPanel feedbackPanel;

    private final MtPasswordField newPassword;
    private final MtPasswordField newPasswordAgain;
    private final MtForm<Membership> newPasswordForm; 
    private final MtButton confirmButton; 
    private final FormComponentLabel newPasswordLabel;
    private final FormComponentLabel newPasswordAgainLabel;

    // OK to register callbacks on methods, they're not called right away
    @SuppressWarnings("initialization")
    public AccountEditorPanel(
        String id,
        IModel<AccountEditor<?>> model
    ) {
        super(id, model);

        this.model = model;

        feedbackPanel = new FeedbackPanel(
            "feedbackPanel");
        newPassword = new MtPasswordField(
            "newPassword",
            model);
        newPasswordLabel = new FormComponentLabel(
            "newPasswordLabel",
            newPassword);
        newPasswordAgain = new MtPasswordField(
            "newPasswordAgain",
            model);
        newPasswordAgainLabel = new FormComponentLabel(
            "newPasswordAgainLabel",
            newPasswordAgain);
        confirmButton = new MtButton(
            "confirmButton",
            this::createAccount);
        newPasswordForm = new MtForm<>("newPasswordForm");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        newPasswordForm.add(newPassword);
        newPasswordForm.add(newPasswordLabel);
        newPasswordForm.add(newPasswordAgain);
        newPasswordForm.add(newPasswordAgainLabel);
        newPasswordForm.add(confirmButton);

        add(feedbackPanel);
        add(newPasswordForm);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        AccountEditor<?> editor = model.getObject();
        newPasswordForm.setVisible(!editor.isAccountExists());
    }

    private void createAccount() {
        try {
            model.getObject().createAccount();
        } catch (AccountEditor.PasswordsDontMatchException ex) {
            error("Passwords don't match");
        }
    }
}
