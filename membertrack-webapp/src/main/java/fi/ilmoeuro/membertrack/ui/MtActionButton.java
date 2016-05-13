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
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.markup.html.form.Form;

/**
 *
 * @author Ilmo Euro <ilmo.euro@gmail.com>
 */
@Slf4j
public class MtActionButton extends Form<Object> {
    private static final long serialVersionUID = 0l;

    public interface ButtonAction extends Serializable {
        void onSubmit();
    }

    private final ButtonAction action;
    
    public MtActionButton(String id, ButtonAction action) {
        super(id);
        this.action = action;
    }

    @Override
    public void onSubmit() {
        action.onSubmit();
    }

    @Override
    protected void onError() {
        log.debug("onError() @" + getId());
    }
}
