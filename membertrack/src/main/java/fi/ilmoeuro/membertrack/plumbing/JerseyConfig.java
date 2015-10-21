/* 
 * Copyright (C) 2015 Ilmo Euro
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
package fi.ilmoeuro.membertrack.plumbing;

import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.linking.DeclarativeLinkingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.mvc.MvcFeature;
import org.glassfish.jersey.server.mvc.jsp.JspMvcFeature;

@ApplicationPath("/app")
public final class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        super();
        super.register(MvcFeature.class);
        super.register(JspMvcFeature.class);
        super.register(DeclarativeLinkingFeature.class);
        super.property(JspMvcFeature.TEMPLATE_BASE_PATH, "/WEB-INF/templates");
        super.packages("fi.ilmoeuro.membertrack");
    }
}
