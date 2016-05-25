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

import fi.ilmoeuro.membertrack.config.ConfigProvider;
import java.sql.SQLException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.h2.tools.Server;

@Slf4j
public class DebugServer {
    public static final @Data class Config {
        private boolean enabled = true;
    }

    private final Config config;
    private @Nullable Server server;

    public DebugServer(
        ConfigProvider configProvider
    ) {
        config = configProvider.getConfig(
            "debugServer",
            Config.class);
        server = null;
    }

    public void start() {
        if (config.isEnabled()) {
            try {
                log.debug("Starting debug server...");
                Server s = Server.createWebServer("-webPort", "9000");
                s.start();
                server = s;
            } catch (SQLException ex) {
                log.debug("Couldn't start debug server: " + ex.getMessage());
            }
        }
    }

    public void stop() {
        if (config.isEnabled()) {
            log.debug("Stopping debug server...");
            Server s = server;
            if (s != null) {
                s.stop();
            }
        }
    }
}
