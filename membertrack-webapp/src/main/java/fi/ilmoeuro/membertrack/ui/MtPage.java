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
import fi.ilmoeuro.membertrack.ResourceRoot;
import fi.ilmoeuro.membertrack.util.PageParamsSaveLoad;
import java.util.Arrays;
import java.util.List;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
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
    private final List<String> cssFiles = Arrays.asList(
        "ui/css/pure-theme.css",
        "ui/css/pure.css",
        "ui/css/AccountEditorPanel.css",
        "ui/css/MembershipBrowserPanel.css",
        "ui/css/MembershipEditorPanel.css",
        "ui/css/MembershipsPage.css",
        "ui/css/MtPage.css",
        "ui/css/Pager.css",
        "ui/css/PersonInfoPanel.css");

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
        add(new HeaderResponseContainer("js", "js"));

        super.onInitialize();

        add(new MtActionButton("logoutButton", () -> {
            MtSession.get().invalidate();
            setResponsePage(MtApplication.get().getHomePage());
        }));

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        ResourceReference jqueryUiCss =
            new WebjarsCssResourceReference("jquery-ui/1.11.4/jquery-ui.min.css");
        response.render(CssHeaderItem.forReference(jqueryUiCss));

        ResourceReference jqueryUiThemeCss =
            new PackageResourceReference(ResourceRoot.class, "ui/css/jquery-ui-theme.css");
        response.render(CssHeaderItem.forReference(jqueryUiThemeCss));

        for (String file : cssFiles) {
            ResourceReference cssRef
                = new PackageResourceReference(ResourceRoot.class, file);
            response.render(CssHeaderItem.forReference(cssRef));
        }

        ResourceReference jquery =
            new WebjarsJavaScriptResourceReference("jquery/1.11.1/jquery.js");
        ResourceReference jqueryUi =
            new WebjarsJavaScriptResourceReference("jquery-ui/1.11.4/jquery-ui.js");
        response.render(JavaScriptHeaderItem.forReference(jquery));
        response.render(JavaScriptHeaderItem.forReference(jqueryUi));

        PackageResourceReference instantClickJsRef =
            new PackageResourceReference(ResourceRoot.class, "ui/instantclick.js");
        response.render(JavaScriptHeaderItem.forReference(instantClickJsRef));
        // IE doesn't support XHR.responseURL, so instantclick would mess up url bar
        response.render(JavaScriptHeaderItem.forScript(
            "if (!window.document.documentMode) InstantClick.init();",
            "instantClickInit"));

        PackageResourceReference mtPageJsRef =
            new PackageResourceReference(MtPage.class, "MtPage.js");
        response.render(JavaScriptHeaderItem.forReference(mtPageJsRef));
    }
}
