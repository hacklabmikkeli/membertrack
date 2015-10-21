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
package fi.ilmoeuro.membertrack.data;

import fi.ilmoeuro.membertrack.ResourceRoot;
import fi.ilmoeuro.membertrack.config.ConfigProvider;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import lombok.Data;
import lombok.extern.java.Log;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

@Singleton
@Startup
@Log
public class DatabaseInitializer {

    public static final @Data class Config {
        private List<String> setupFiles = Arrays.asList();
        private List<String> clearFiles = Arrays.asList();
        private boolean enabled;
    }

    private DSLContext jooq;
    private Config config;

    @Inject
    public DatabaseInitializer(
        DSLContext jooq,
        ConfigProvider configProvider
    ) {
        this.jooq = jooq;
        this.config = configProvider.getConfig(
            "databaseInitializer",
            Config.class);
    }

    public DatabaseInitializer() {
        throw new IllegalStateException("Please call the other constructor");
    }

    private void runSqlFiles(List<String> fileNames) {
        try {
            for (String fileName : fileNames) {
                InputStream sqlStream
                        = ResourceRoot.class.getResourceAsStream(fileName);
                if (sqlStream == null) {
                    // TODO proper exception
                    throw new RuntimeException("SQL file not found");
                }
                String sql = IOUtils.toString(sqlStream, Charsets.US_ASCII);
                log.log(Level.INFO, "Executing: {0}", sql);
                for (String part : sql.split(";")) {
                    jooq.execute(part);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void init() {
        if (config.isEnabled()) {
            try {
                runSqlFiles(config.getClearFiles());
            } catch (DataAccessException e) {
                // Database is clean
            }

            runSqlFiles(config.getSetupFiles());
        }
    }
}
