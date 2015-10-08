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
import java.util.Optional;
import javax.ejb.Singleton;

@Singleton
public class ConfigProvider {

    private final Config config;

    public ConfigProvider() {
        URL url = ResourceRoot.class.getResource("membertrack.conf");
        config = Optional
                .ofNullable(System.getProperty("membertrack.config"))
                .map((fn) -> ConfigFactory.parseFile(new File(fn)))
                .orElse(ConfigFactory.empty())
                .withFallback(ConfigFactory.parseURL(url));
    }

    public <T> T getConfig(String path, Class<T> clazz) {
        return ConfigBeanFactory.create(config.getConfig(path), clazz);
    }
}
