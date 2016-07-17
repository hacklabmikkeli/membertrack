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

import fi.ilmoeuro.membertrack.util.SerializableAction;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.markup.html.form.Button;

@Slf4j
public final class MtButton extends Button {
    private static final long serialVersionUID = 0l;
    private final SerializableAction action;
    private final boolean defaultFormProcessing;

    public MtButton(
        String id,
        SerializableAction action,
        boolean defaultFormProcessing
    ) {
        super(id);
        this.action = action;
        this.defaultFormProcessing = defaultFormProcessing;
    }

    public MtButton(String id, SerializableAction action) {
        this(id, action, true);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        setDefaultFormProcessing(defaultFormProcessing);
    }

    @Override
    public void onSubmit() {
        try {
            action.execute();
        } catch (Exception ex) {
            String message = ex.getMessage();
            if (message != null) {
                error(message);
            } else {
                error(ex.getClass().getName());
            }
        }
    }

    @Override
    public void onError() {
        error("Error!");
    }
}
