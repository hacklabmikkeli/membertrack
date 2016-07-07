/*
 * Copyright (C) 2016 Ilmo Euro <ilmo.euro@gmail.com>
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

import fi.ilmoeuro.membertrack.ResourceRoot;
import fi.ilmoeuro.membertrack.schema.tables.pojos.MigrationBase;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.DSLContext;
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import org.jooq.exception.DataAccessException;

@Slf4j
public final class Migration extends MigrationBase {
    private static final long serialVersionUID = 0l;
    
    @SuppressWarnings("nullness") // Interface with autogen code
    @Deprecated
    private Migration(
        @Nullable Integer pk,
        UUID id,
        String fileName
    ) {
        super(pk, id, fileName);
    }

    private Migration(String fileName) {
        this(null, UUID.randomUUID(), fileName);
    }

    public static void bootstrap(DSLContext jooq, String bootstrapFile) {
        if (!jooq.meta().getTables().stream().anyMatch(t -> t.equals(MIGRATION))) {
            try {
                runSqlFile(jooq, bootstrapFile);
            } catch (IOException e) {
                throw new RuntimeException("Error while bootstrapping", e);
            }
        }
    } 

    public static void runMigration(DSLContext jooq, String fileName) {
        if (jooq.fetchExists(MIGRATION, MIGRATION.FILE_NAME.eq(fileName))) {
            return;
        }
        
        try {
            runSqlFile(jooq, fileName);

            Migration migration = new Migration(fileName);
            jooq.newRecord(MIGRATION, migration).insert();
        } catch (IOException e) {
            throw new RuntimeException("Error executing sql file", e);
        }
    }

    private static void runSqlFile(DSLContext jooq, String fileName)
        throws RuntimeException, IOException, DataAccessException
    {
        InputStream sqlStream
                = ResourceRoot.class.getResourceAsStream(fileName);
        if (sqlStream == null) {
            throw new RuntimeException(
                    String.format(
                            "SQL file '%s'  not found",
                            fileName
                    ));
        }
        String sql = IOUtils.toString(sqlStream, Charsets.UTF_8);
        log.info("Executing: {}", sql);
        for (String part : sql.split(";")) {
            jooq.execute(part);
        }
    }
}
