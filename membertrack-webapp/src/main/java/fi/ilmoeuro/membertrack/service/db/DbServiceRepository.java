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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jooq.SelectField;
import fi.ilmoeuro.membertrack.service.ServiceRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class
    DbServiceRepository
implements
    ServiceRepository
{
    private final DSLContext jooq;

    @Override
    public List<Service> listServices() {
        List<SelectField<?>> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(SERVICE.fields()));
        return jooq
            .select(fields)
            .from(SERVICE)
            .orderBy(
                SERVICE.TITLE.asc())
            .fetchInto(Service.class);
    }
}
