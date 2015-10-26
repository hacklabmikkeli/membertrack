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
package fi.ilmoeuro.membertrack.elock;

import static fi.ilmoeuro.membertrack.elock.schema.Tables.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.sql.DataSource;
import lombok.extern.java.Log;
import org.h2.jdbcx.JdbcDataSource;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

@Log
public class DatabaseMemberLookup implements MemberLookup, AutoCloseable {

    private final DataSource dataSource;
    private final Connection keepAliveConnection;

    public DatabaseMemberLookup(
        String url,
        String username,
        String password
    ) throws InitializationException {
        final JdbcDataSource h2DataSource = new JdbcDataSource();
        h2DataSource.setURL(url);
        h2DataSource.setUser(username);
        h2DataSource.setPassword(password);
        dataSource = h2DataSource;

        try {
            keepAliveConnection = dataSource.getConnection();
        } catch (SQLException ex) {
            throw new InitializationException(
                String.format(
                    "Couldn't init database connection: %s", ex.getMessage()),
                ex
            );
        }
    }

    @Override
    public boolean isAuthorizedMember(String phoneNumber) {
        try (Connection conn = dataSource.getConnection()) {
            final DSLContext db = DSL.using(
                conn,
                SQLDialect.H2
            );
            return db.fetchExists(
                APPROVED_PHONE_NUMBER,
                APPROVED_PHONE_NUMBER.NUMBER.eq(phoneNumber)
            );
        } catch (SQLException ex) {
            log.log(
                Level.SEVERE,
                "Error while connecting to database",
                ex);
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        keepAliveConnection.close();
    }
}
