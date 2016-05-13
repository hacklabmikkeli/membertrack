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

import java.io.Serializable;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MtListView<T> extends ListView<@NonNull T>{
    private static final long serialVersionUID = 0l;

    @FunctionalInterface
    public interface ListViewPopulator<T> extends Serializable {
        void populateItem(ListItem<@NonNull T> item);
    }

    private final ListViewPopulator<@NonNull T> populate;

    public MtListView(
        String id,
        IModel<?> baseModel,
        ListViewPopulator<@NonNull T> populate
    ) {
        super(id, new PropertyModel<>(baseModel, id));
        this.populate = populate;
    }

    public MtListView(
        String id,
        ListItem<?> item,
        ListViewPopulator<@NonNull T> populate
    ) {
        super(id, new PropertyModel<>(item.getModel(), id));
        this.populate = populate;
    }

    @Override
    protected void populateItem(ListItem<@NonNull T> item) {
        populate.populateItem(item);
    }
}
