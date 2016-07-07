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
package fi.ilmoeuro.membertrack.db;

import fi.ilmoeuro.membertrack.ResourceRoot;
import fi.ilmoeuro.membertrack.session.SessionToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.jooq.DSLContext;

@Slf4j
@RequiredArgsConstructor
public final class DatabaseInitializer {

    public static final @Data class Config implements Serializable {
        private static final long serialVersionUID = 0l;
        private String setupList = "";
        private boolean enabled = false;
    }

    private final Config config;

    private void runMigrations(DSLContext jooq, List<String> fileNames) {
        Iterator<String> iter = fileNames.iterator();
        if (iter.hasNext()) {
            log.info("Bootstrapping database");
            Migration.bootstrap(jooq, iter.next());
        }
        while (iter.hasNext()) {
            String fileName = iter.next();
            log.info("Running migration: {}", fileName);
            Migration.runMigration(jooq, fileName);
        }
    }

    public void init(SessionToken<DSLContext> sessionToken) {
        if (config.isEnabled()) {
            try (final InputStream setupStream
                = ResourceRoot.class.getResourceAsStream(config.getSetupList()
            )) {
                if (setupStream != null) {
                    final List<String> setupFiles =
                        IOUtils.readLines(setupStream, Charsets.UTF_8);
                    runMigrations(sessionToken.getValue(), setupFiles);
                }
            } catch (IOException ex) {
                throw new RuntimeException("Error loading setup list", ex);
            }
        }
    }
}
