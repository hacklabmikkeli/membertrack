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

import java.util.logging.Level;
import lombok.extern.java.Log;

@Log
public final class Main {
    private Main() {
        // not meant to be instantiated
    }

    public static void main(String... args) {
        final ElockDaemon daemon = new ElockDaemon();
        try {
            daemon.init(args);
            daemon.start();
            System.in.read();
            daemon.stop();
        } catch (Exception ex) {
            log.log(
                Level.SEVERE,
                "Error while running membertrack-elock",
                ex
            );
        } finally {
            daemon.destroy();
        }
    }
}
