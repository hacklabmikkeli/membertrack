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
package fi.ilmoeuro.membertrack.ui;

import java.io.Serializable;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;

/**
 *
 * @author Ilmo Euro <ilmo.euro@gmail.com>
 */
public class MtHighlighter extends AttributeAppender {
    private static final long serialVersionUID = 0l;

    @FunctionalInterface
    public interface HighlightPredicate extends Serializable {
        public boolean isHighlighted();
    }
    
    public MtHighlighter(HighlightPredicate highlightPredicate) {
        super("class", new IModel<String>() {
            @Override
            public String getObject() {
                return highlightPredicate.isHighlighted() ? "highlighted" : "";
            }

            @Override
            public void setObject(String object) {
            }

            @Override
            public void detach() {
            }
        });
    }
}
