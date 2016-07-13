/*
 * Copyright (C) 2016 Ilmo Euro <ilmo.euro@gmail.com>
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
package fi.ilmoeuro.membertrack.auth.ui;

import fi.ilmoeuro.membertrack.ResourceRoot;
import fi.ilmoeuro.membertrack.auth.PasswordResetManager;
import fi.ilmoeuro.membertrack.auth.PasswordResetPageModel;
import fi.ilmoeuro.membertrack.auth.db.DbPasswordResetManager;
import fi.ilmoeuro.membertrack.config.Config;
import fi.ilmoeuro.membertrack.person.Accounts;
import fi.ilmoeuro.membertrack.person.Persons;
import fi.ilmoeuro.membertrack.person.db.DbAccounts;
import fi.ilmoeuro.membertrack.person.db.DbPersons;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import fi.ilmoeuro.membertrack.session.db.DbUnitOfWork;
import fi.ilmoeuro.membertrack.ui.MtApplication;
import fi.ilmoeuro.membertrack.ui.MtButton;
import fi.ilmoeuro.membertrack.ui.MtForm;
import fi.ilmoeuro.membertrack.ui.MtLabel;
import fi.ilmoeuro.membertrack.ui.MtModel;
import fi.ilmoeuro.membertrack.ui.MtPasswordField;
import fi.ilmoeuro.membertrack.ui.MtTextField;
import fi.ilmoeuro.membertrack.util.SerializableAction;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.jooq.DSLContext;

public class PasswordResetPage extends WebPage {
    private static final long serialVersionUID = 0l;

    private final IModel<PasswordResetPageModel> model;
    private final Component errorMessage;
    private final Component emailField;
    private final Component passwordField;
    private final Component passwordAgainField;
    private final Component tokenField;
    private final MtButton resetButton;
    private final MtForm resetForm;

    public PasswordResetPage() {
        final Config config
            = MtApplication.get().getConfig();
        final Accounts.Factory<DSLContext> accountsFactory
            = new DbAccounts.Factory();
        final Persons.Factory<DSLContext> personsFactory
            = new DbPersons.Factory();
        final UnitOfWork.Factory<DSLContext> uof
            = new DbUnitOfWork.Factory();
        final SessionRunner<DSLContext> sr
            = MtApplication.get().getSessionRunner();

        PasswordResetManager pwrManager =
            new DbPasswordResetManager(
                sr,
                uof,
                accountsFactory,
                personsFactory,
                config.getPasswordResetManager());
        model = new MtModel<PasswordResetPageModel>(
            new PasswordResetPageModel(pwrManager));

        errorMessage = new MtLabel("errorMessage", model);
        emailField = new MtTextField("email", model);
        passwordField = new MtPasswordField("password", model);
        passwordAgainField = new MtPasswordField("passwordAgain", model);
        tokenField = new MtTextField("token", model);
        @SuppressWarnings("initialization") 
        SerializableAction doReset = this::doReset;
        resetButton = new MtButton("resetButton", doReset);
        resetForm = new MtForm("resetForm");
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();

        resetForm.add(errorMessage);
        resetForm.add(emailField);
        resetForm.add(passwordField);
        resetForm.add(passwordAgainField);
        resetForm.add(tokenField);
        resetForm.add(resetButton);
        resetForm.setDefaultButton(resetButton);
        add(resetForm);
    }

    private void doReset() {
        PasswordResetPageModel modelObject = model.getObject();
        modelObject.doReset();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        PackageResourceReference cssRef = 
            new PackageResourceReference(
                PasswordResetPage.class,
                "PasswordResetPage.css");
        CssHeaderItem pageCss = CssHeaderItem.forReference(cssRef);
        response.render(pageCss);

        PackageResourceReference pureRef =
            new PackageResourceReference(
                ResourceRoot.class,
                "ui/pure.css"
            );
        CssHeaderItem pureCss = CssHeaderItem.forReference(pureRef);
        response.render(pureCss);

        PackageResourceReference pureThemeRef =
            new PackageResourceReference(
                ResourceRoot.class,
                "ui/pure-theme.css"
            );
        CssHeaderItem pureThemeCss = CssHeaderItem.forReference(pureThemeRef);
        response.render(pureThemeCss);
    }
}
