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
package fi.ilmoeuro.membertrack;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class TestBase {
    private static final String SCHEMA_LIST_FILE = "setup_list_prod.txt";
    private static final String CONN_STRING = "jdbc:h2:mem:;CREATE=TRUE";

    private @Nullable Connection conn;
    private @Nullable DSLContext jooq;

    private void initSchema() {
        try (final InputStream schemaFileStream =
                ResourceRoot.class.getResourceAsStream(SCHEMA_LIST_FILE)) {
            if (schemaFileStream == null) {
                throw new RuntimeException("Schema list not found");
            } else {
                for (final String schemaFile : IOUtils.readLines(
                    schemaFileStream, 
                    Charsets.UTF_8
                )) {
                    try (final InputStream sqlStream
                        = ResourceRoot.class.getResourceAsStream(schemaFile)) {
                        if (sqlStream == null) {
                            throw new RuntimeException(
                                String.format(
                                    "Schema file '%s' not found",
                                    schemaFile
                                )
                            );
                        } else {
                            final String sql
                                = IOUtils.toString(sqlStream, Charsets.US_ASCII);
                            for (String part : sql.split(";")) {
                                if (jooq == null) {
                                    throw new IllegalStateException(
                                        "jooq must be initialized"
                                    );
                                } else {
                                    jooq.execute(part);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException("Couldn't retrieve schema file", ex);
        }
    }
    
    @BeforeMethod
    public void jooqSetUp() throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection(CONN_STRING);
        jooq = DSL.using(conn, SQLDialect.H2);
        initSchema();
    }
    
    @AfterMethod
    public void jooqTearDown() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }
    
    protected DSLContext jooq() {
        if (jooq == null) {
            throw new IllegalStateException("`jooq' not initialized");
        }
        return jooq;
    }
}
