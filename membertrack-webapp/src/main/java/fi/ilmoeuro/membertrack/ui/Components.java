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

import fi.ilmoeuro.membertrack.session.SessionJoinable;
import java.io.Serializable;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jooq.DSLContext;

public final class Components {
    private Components() {
        // not meant to be instantiated
    }

    @FunctionalInterface
    public interface ListViewPopulator<T> extends Serializable {
        void populateItem(ListItem<@NonNull T> item);
    }

    @FunctionalInterface
    public interface LinkAction {
        void onClick();
    }

    public static <T extends SessionJoinable<DSLContext>>
        IModel<T> model(@NonNull T model)
    {
        return new CompoundPropertyModel<T>(model) {
            private boolean dirty = true;

            @Override
            public T getObject() {
                if (dirty) {
                    T object = super.getObject();
                    MembertrackApplication
                        .get()
                        .getSessionRunner()
                        .exec(object::join);
                    dirty = false;
                }
                return super.getObject();
            }

            @Override
            public void setObject(T object) {
                super.setObject(object);
            }

            @Override
            public void detach() {
                super.detach();
                dirty = true;
            }
        };
    }

    public static Label label(String id, IModel<?> baseModel) {
        return new Label(id, new PropertyModel(baseModel, id));
    }

    public static Label label(String id, ListItem<?> item) {
        return label(id, item.getModel());
    }

    public static <T> ListView<@NonNull T> listView(
        String id,
        IModel<?> baseModel,
        ListViewPopulator<T> populate
    ) {
        return new ListView<@NonNull T>(
            id,
            new PropertyModel(baseModel, id)
        ) {
            @Override
            protected void populateItem(ListItem<@NonNull T> li) {
                populate.populateItem(li);
            }
        };
    }

    public static <T> ListView<@NonNull T> listView(
        String id,
        ListItem<?> item,
        ListViewPopulator<T> populate
    ) {
        return new ListView<@NonNull T>(
            id,
            new PropertyModel<>(item.getModel(), id
        )) {
            @Override
            protected void populateItem(ListItem<@NonNull T> li) {
                populate.populateItem(li);
            }
        };
    }

    public static Link link(
        String id,
        LinkAction action
    ) {
        return new Link(id, Model.of()) {
            @Override
            public void onClick() {
                action.onClick();
            }
        };
    }
}
