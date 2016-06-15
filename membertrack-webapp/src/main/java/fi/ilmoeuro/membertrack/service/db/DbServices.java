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
package fi.ilmoeuro.membertrack.service.db;

import org.jooq.DSLContext;
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import fi.ilmoeuro.membertrack.service.Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import fi.ilmoeuro.membertrack.service.Services;
import fi.ilmoeuro.membertrack.session.SessionToken;
import java.util.UUID;

@RequiredArgsConstructor
public final class
    DbServices
implements
    Services
{
    private final DSLContext jooq;

    @Override
    public List<Service> listServices() {
        return jooq
            .select(SERVICE.fields())
            .from(SERVICE)
            .orderBy(
                SERVICE.TITLE.asc())
            .fetchInto(Service.class);
    }

    @Override
    public Service findById(UUID serviceUUID) {
        return jooq
            .select(SERVICE.fields())
            .from(SERVICE)
            .where(SERVICE.ID.eq(serviceUUID))
            .fetchAnyInto(Service.class);
    }

    public static final class Factory implements Services.Factory<DSLContext> {
        private static final long serialVersionUID = 0L;

        @Override
        public Services create(SessionToken<DSLContext> token) {
            return new DbServices(token.getValue());
        }
    }
}
