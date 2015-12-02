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
package fi.ilmoeuro.membertrack.db;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.UpdatableRecord;

public final class UnitOfWork {

    private final DSLContext jooq;
    private final List<UpdatableRecord> records = new ArrayList<>();

    @Inject
    public UnitOfWork(
        DSLContext jooq
    ) {
        this.jooq = jooq;
    }

    public <R extends UpdatableRecord> void addEntity(
        Table<R> table,
        Object val
    ) {
        R record = jooq.newRecord(table, val);
        records.add(record);
    }

    public void execute() {
        for (UpdatableRecord record : records) {
            record.store();
        }
    }
}
