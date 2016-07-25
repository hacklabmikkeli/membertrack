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
import org.apache.wicket.authroles.authentication.panel.SignInPanel;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class MtSignInPage extends WebPage {
    private static final long serialVersionUID = 0l;
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new SignInPanel("signInPanel"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        ResourceReference signinCss =
            new PackageResourceReference(
                ResourceRoot.class,
                "ui/css/MtSignInPage.css");
        response.render(CssHeaderItem.forReference(signinCss));
    }
}
