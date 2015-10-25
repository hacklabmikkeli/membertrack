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

import java.io.StringWriter;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

@Log
public class ElockDaemon implements Daemon {

    private @Nullable ElockSystem system;

    @SuppressWarnings("argument.type.incompatible")
    private String getUsage(CmdLineParser argParser) {
        final StringWriter usage = new StringWriter();
        argParser.printUsage(usage, null);
        return usage.toString();
    }

    @Override
    public void init(DaemonContext dc) throws DaemonInitException, Exception {
        final String[] args = dc.getArguments();
        init(args);
    }

    public void init(String... args) throws DaemonInitException, Exception {
        final ElockSystemParameters params = new ElockSystemParameters();
        final CmdLineParser argParser = new CmdLineParser(params);
        try {
            argParser.parseArgument(args);
            params.validate();
            system = new ElockSystem(params);
        } catch ( CmdLineException
                | InvalidArgumentsException
                | InitializationException ex
        ) {
            throw new DaemonInitException(
                String.format(
                    "%s%nusage: membertrack-elock [options]%n%s",
                    ex.getMessage(),
                    getUsage(argParser)));
        }
    }

    @Override
    public void start() throws Exception {
        if (system != null) {
            system.start();
        }
    }

    @Override
    public void stop() throws Exception {
        if (system != null) {
            system.stop();
        }
    }

    @Override
    public void destroy() {
        if (system != null) {
            try {
                system.close();
            } catch (Exception ex) {
                log.log(
                    Level.SEVERE,
                    "Error while closing elock-daemon",
                    ex
                );
            }
        }
    }
}
