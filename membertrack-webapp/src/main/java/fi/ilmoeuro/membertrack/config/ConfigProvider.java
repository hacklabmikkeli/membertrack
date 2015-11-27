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
package fi.ilmoeuro.membertrack.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;
import fi.ilmoeuro.membertrack.ResourceRoot;
import java.io.File;
import java.net.URL;
import javax.inject.Singleton;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Singleton
public class ConfigProvider {

    private static final String CONF_FILE = "membertrack.properties";
    private static final String CONF_PROPERTY = "membertrack.config";

    private final Config config;

    public ConfigProvider() {
        final @Nullable URL url
            = ResourceRoot.class.getResource(CONF_FILE);
        if (url == null) {
            throw new RuntimeException(CONF_FILE + " not found");
        }
        final @Nullable String configFileLocation
            = System.getProperty(CONF_PROPERTY);
        final @Nullable File configFile
            = configFileLocation == null ? null : new File(configFileLocation);
        final Config userConfig;
        if (configFile != null) {
            userConfig = ConfigFactory.parseFile(configFile);
        } else {
            userConfig = ConfigFactory.empty();
        }
        config = userConfig.withFallback(ConfigFactory.parseURL(url));
    }

    public <T extends @NonNull Object> T getConfig(String path, Class<T> clazz) {
        return ConfigBeanFactory.create(config.getConfig(path), clazz);
    }
}
