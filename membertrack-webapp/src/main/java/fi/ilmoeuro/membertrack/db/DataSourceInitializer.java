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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public final class DataSourceInitializer {
    public static final @Data class Config {
        private String connectionString = "jdbc:h2:mem:membertrack;CREATE=TRUE";
        private String username = "sa";
        private String password = "sa";
        private String rdbms = "H2";
        private String jndiName = "jdbc/membertrack";
        private boolean enabled = true;
    }

    private final Config config;

    public void init() {
        if (!config.isEnabled()) {
            return;
        }
        
        try {
            if (config.getRdbms().equals("H2")) {
                org.h2.jdbcx.JdbcDataSource ds = new org.h2.jdbcx.JdbcDataSource();
                ds.setURL(config.getConnectionString());
                ds.setUser(config.getUsername());
                ds.setPassword(config.getPassword());
                Context ctx = new InitialContext();
                ctx.bind(config.getJndiName(), ds);
            }
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
