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

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.annotation.Nullable;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class TestBase {

    private @Nullable Connection conn;
    private @Nullable DSLContext jooq;
    
    @BeforeMethod
    public void jooqSetUp() throws ClassNotFoundException, SQLException {
        StringBuilder connString = new StringBuilder();
        URL schemaUrl = TestBase.class.getResource("schema.sql");
        connString.append("jdbc:h2:mem:;");
        connString.append("CREATE=TRUE;");
        connString.append("INIT=runscript from '");
        connString.append(new File(schemaUrl.getPath()).getAbsolutePath());
        connString.append("'");
        Class.forName("org.h2.Driver");
        conn = DriverManager.getConnection(connString.toString());
        jooq = DSL.using(conn, SQLDialect.H2);
    }
    
    @AfterMethod
    public void jooqTearDown() throws SQLException {
        conn.close();
    }
    
    protected DSLContext jooq() {
        return jooq;
    }
}
