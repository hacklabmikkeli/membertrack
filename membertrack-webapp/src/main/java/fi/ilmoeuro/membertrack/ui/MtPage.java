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
package fi.ilmoeuro.membertrack.ui;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import fi.ilmoeuro.membertrack.util.PageParamsSaveLoad;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.Nullable;

public class MtPage extends WebPage {
    private static final long serialVersionUID = 0l;
    private @Nullable IModel<? extends PageParamsSaveLoad> model = null;

    @Override
    protected void onConfigure() {
        super.onConfigure();

        if (MtSession.get().isTemporary() ||
            !MtSession.get().isSignedIn()) {
            MtApplication.get().restartResponseAtSignInPage();
        }
    }

    protected final void setModel(IModel<? extends PageParamsSaveLoad> model) {
        this.model = model;
    }

    protected final void loadPageParameters(
        @UnderInitialization(MtPage.class) MtPage this,
        PageParameters params
    ) {
        if (model != null) {
            model.getObject().loadState(k -> params.get(k).toOptionalString());
        }
    }

    @Override
    public PageParameters getPageParameters() {
        final PageParameters params = super.getPageParameters();
        if (params != null && model != null) {
            model.getObject().saveState(
                (String k, @Nullable String v) -> params.set(k, v));
        }
        return params;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new MtActionButton("logoutButton", () -> {
            MtSession.get().invalidate();
            setResponsePage(MtApplication.get().getHomePage());
        }));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        ResourceReference pureRef
            = new PackageResourceReference(MtPage.class, "pure.css");
        response.render(CssHeaderItem.forReference(pureRef));

        PackageResourceReference pureThemeRef = 
            new PackageResourceReference(MtPage.class, "pure-theme.css");
        response.render(CssHeaderItem.forReference(pureThemeRef));

        PackageResourceReference cssRef = 
            new PackageResourceReference(MtPage.class, "MtPage.css");
        response.render(CssHeaderItem.forReference(cssRef));

        ResourceReference jquery =
            new WebjarsJavaScriptResourceReference("jquery/1.11.1/jquery.js");
        ResourceReference jqueryUi =
            new WebjarsJavaScriptResourceReference("jquery-ui/1.11.4/jquery-ui.js");
        ResourceReference jqueryUiCss =
            new WebjarsCssResourceReference("jquery-ui/1.11.4/jquery-ui.min.css");
        ResourceReference jqueryUiThemeCss =
            new PackageResourceReference(MtPage.class, "jquery-ui.theme.css");

        response.render(CssHeaderItem.forReference(jqueryUiCss));
        response.render(CssHeaderItem.forReference(jqueryUiThemeCss));
        response.render(JavaScriptHeaderItem.forReference(jquery));
        response.render(JavaScriptHeaderItem.forReference(jqueryUi));

        PackageResourceReference jsRef =
            new PackageResourceReference(MtPage.class, "MtPage.js");
        response.render(JavaScriptHeaderItem.forReference(jsRef));
    }
}
