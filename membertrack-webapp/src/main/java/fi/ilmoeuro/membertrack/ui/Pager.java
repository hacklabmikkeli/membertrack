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

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class Pager extends Panel {
    private final IModel<?> model;
    private final String currentPageProperty;
    private final String numPagesProperty;

    public Pager(String id,
                 IModel<?> model,
                 String currentPageProperty,
                 String numPagesProperty) {
        super(id, new CompoundPropertyModel<>(model));
        this.model = model;
        this.currentPageProperty = currentPageProperty;
        this.numPagesProperty = numPagesProperty;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
    }
}
