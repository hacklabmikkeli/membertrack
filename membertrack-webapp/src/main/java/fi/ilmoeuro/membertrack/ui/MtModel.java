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
import org.apache.wicket.model.CompoundPropertyModel;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jooq.DSLContext;

public class
    MtModel<T extends SessionJoinable<DSLContext>>
extends
    CompoundPropertyModel<T>
{
    private static final long serialVersionUID = 0l;

    private boolean dirty;

    public MtModel(@NonNull T model) {
        super(model);
        this.dirty = true;
    }

    @Override
    public T getObject() {
        if (dirty) {
            T object = super.getObject();
            if (object != null) {
                MtApplication
                    .get()
                    .getSessionRunner()
                    .exec(object::join);
                dirty = false;
            }
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
}
