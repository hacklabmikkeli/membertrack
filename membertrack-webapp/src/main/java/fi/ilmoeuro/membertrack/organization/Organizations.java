/*
 * Copyright (C) 2015 Ilmo Euro
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
package fi.ilmoeuro.membertrack.organization;

import fi.ilmoeuro.membertrack.data.Entity;
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import static fi.ilmoeuro.membertrack.data.Queries.*;
import java.util.List;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import org.jooq.DSLContext;

@Dependent
public final class Organizations {

    private final DSLContext jooq;

    @Inject
    public Organizations(
        DSLContext jooq
    ) {
        this.jooq = jooq;
    }
    
    public void save(Organization organization) {
        jooq.executeInsert(
            jooq.newRecord(ORGANIZATION, organization)
        );
    }

    public void save(Entity<Organization> organization) {
        jooq.executeUpdate(
            jooq.newRecord(ORGANIZATION, organization.getValue()),
            ORGANIZATION.ID.eq(organization.getId())
        );
    }

    public List<Entity<Organization>> getAll() {
        return findAll(
            jooq,
            ORGANIZATION,
            Organization.class,
            ORGANIZATION.ID);
    }

    public void delete(Entity<Organization> organization) {
        jooq.executeDelete(
            ORGANIZATION.newRecord(), 
            ORGANIZATION.ID.eq(organization.getId())
        );
    }

    public void deleteNamed(String name) {
        jooq.executeDelete(
            ORGANIZATION.newRecord(), 
            ORGANIZATION.NAME.eq(name)
        );
    }
}
