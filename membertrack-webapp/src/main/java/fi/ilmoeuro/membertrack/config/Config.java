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
package fi.ilmoeuro.membertrack.config;

import fi.ilmoeuro.membertrack.ResourceRoot;
import fi.ilmoeuro.membertrack.auth.db.DbPasswordResetManager;
import fi.ilmoeuro.membertrack.db.DataSourceInitializer;
import fi.ilmoeuro.membertrack.db.DatabaseInitializer;
import fi.ilmoeuro.membertrack.db.DebugServer;
import fi.ilmoeuro.membertrack.holvi.HolviSynchronizer;
import fi.ilmoeuro.membertrack.session.db.DbSessionRunner;
import fi.ilmoeuro.membertrack.ui.MtApplication;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import jodd.bean.BeanCopy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Slf4j
public @Data class Config implements Serializable {
    private static final long serialVersionUID = 0l;
    private static final String CONF_FILE = "membertrack.yaml";
    private static final String CONF_PROPERTY = "membertrack.config";
    
    DataSourceInitializer.Config dataSourceInitializer;
    DatabaseInitializer.Config databaseInitializer;
    DebugServer.Config debugServer;
    HolviSynchronizer.Config holviSynchronizer;
    DbSessionRunner.Config sessionRunner;
    DbPasswordResetManager.Config passwordResetManager;
    MtApplication.Config application;

    public static Config load() throws FileNotFoundException, IOException {
        Yaml yaml = new Yaml(new Constructor(Config.class));

        try (InputStream stream = ResourceRoot.class.getResourceAsStream(CONF_FILE)) {
            if (stream == null) {
                throw new FileNotFoundException(CONF_FILE + " not found");
            }
            Config result = yaml.loadAs(stream, Config.class);

            @Nullable String configFileLocation = System.getProperty(CONF_PROPERTY);
            if (configFileLocation != null) {
                File configFile = new File(configFileLocation);
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    Config userConfig = yaml.loadAs(fis, Config.class);
                    BeanCopy.beans(userConfig, result).ignoreNulls(true).copy();
                }
            }

            return result;
        }
    }
}
