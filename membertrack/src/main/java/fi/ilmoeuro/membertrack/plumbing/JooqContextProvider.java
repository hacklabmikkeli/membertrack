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
package fi.ilmoeuro.membertrack.plumbing;

import fi.ilmoeuro.membertrack.config.ConfigProvider;
import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import javax.inject.Inject;
import lombok.Data;

@ApplicationScoped
@RequiredArgsConstructor
public class JooqContextProvider {

    @Data public static class Config {
        @Nullable
        private String sqlDialect;
    }
    
    @Resource(lookup = "jdbc/membertrack")
    @Nullable
    private DataSource dataSource;

    private final Config config;

    @Inject
    public JooqContextProvider(ConfigProvider configProvider) {
        config = configProvider.getConfig("jooq", Config.class);
    }

    @Produces
    public DSLContext getDSLContext() {
        return DSL.using(dataSource,
                         SQLDialect.valueOf(config.getSqlDialect()));
    }
}
