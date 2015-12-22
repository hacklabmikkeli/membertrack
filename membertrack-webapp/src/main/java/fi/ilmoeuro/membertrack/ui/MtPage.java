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

import java.util.Arrays;
import java.util.List;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.PackageResourceReference;

public class MtPage extends WebPage {
    private static final long serialVersionUID = 0l;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        List<String> cssFiles = Arrays.asList(
            "pure.css",
            "pure-theme.css",
            "membertrack.css"
        );

        for (String cssFile : cssFiles) {
            PackageResourceReference cssRef = 
                new PackageResourceReference(MtPage.class, cssFile);
            CssHeaderItem pageCss = CssHeaderItem.forReference(cssRef);
            response.render(pageCss);
        }
    }
}
