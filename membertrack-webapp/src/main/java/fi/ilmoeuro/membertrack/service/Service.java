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
package fi.ilmoeuro.membertrack.service;

import fi.ilmoeuro.membertrack.schema.tables.pojos.ServiceBase;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Service extends ServiceBase {
    private static final long serialVersionUID = 0l;
    
    @SuppressWarnings("nullness") // Interface with autogen code
    @Deprecated
    public Service(
        @Nullable Integer pk,
        UUID id,
        boolean deleted,
        String title,
        String description
    ) {
        super(pk, id, deleted, title, description);
    }

    @SuppressWarnings("deprecation")
    public Service(
        String title,
        String description
    ) {
        this(null, UUID.randomUUID(), false, title, description);
    }
}
