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

import java.util.Arrays;
import lombok.extern.java.Log;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

@Log
public final class Main {

    private Main() {
        // not meant to be instantiated
    }

    public static void main(String... args) {
        CommandLineOptions options = new CommandLineOptions();
        CmdLineParser argParser = new CmdLineParser(options);
        try {
            argParser.parseArgument(args);
        } catch (CmdLineException ex) {
            System.err.println(ex.getMessage());
            System.err.println("membertrack-elock [options]");
            argParser.printUsage(System.err);
            System.err.println();
            return;
        }

        try (LockActuatorImpl lockActuator = 
                new LockActuatorImpl(options.getPinName());
             PhoneCallSensorImpl phoneCallSensor =
                new PhoneCallSensorImpl(options.getSerialDevice())) {
            DoorOpenMechanism doorOpenMechanism =
                new DoorOpenMechanism(
                        options.getOpenDelay(),
                        lockActuator,
                        phoneCallSensor,
                        new CollectionBasedMemberLookup(
                                Arrays.asList(/* phone numbers here */)));
        } catch (Exception ex) {
            System.err.println("membertrack-elock: " + ex.getMessage());
        }
    }
}
