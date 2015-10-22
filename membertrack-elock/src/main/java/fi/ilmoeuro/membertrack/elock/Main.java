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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Duration;
import java.util.Arrays;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public final class Main {

    private static final Duration CLIP_TTL = Duration.ofSeconds(10);

    private Main() {
        // not meant to be instantiated
    }

    @SuppressFBWarnings("UW_UNCOND_WAIT")
    private static void waitForever() throws InterruptedException {
        final Object lock = new Object();
        synchronized(lock) {
            for (;;) {
                lock.wait();
            }
        }
    }

    public static void main(String... args) {
        CommandLineOptions options = new CommandLineOptions();
        CmdLineParser argParser = new CmdLineParser(options);
        try {
            argParser.parseArgument(args);
        } catch (CmdLineException ex) {
            System.err.println(ex.getMessage());
            System.err.println("Usage: membertrack-elock [options]");
            argParser.printUsage(System.err);
            System.err.println();
            return;
        }

        TemporalFilter temporalFilter = new TemporalFilter(CLIP_TTL);
        try (LockActuatorImpl lockActuator = 
                new LockActuatorImpl(options.getPinName());
             JsscModemAdapter modemAdapter =
                new JsscModemAdapter(options.getSerialDevice());
             PhoneCallSensorImpl phoneCallSensor =
                new PhoneCallSensorImpl(
                    modemAdapter,
                    temporalFilter
                )
        ) {
            DoorOpenMechanism doorOpenMechanism =
                new DoorOpenMechanism(
                        options.getOpenTime(),
                        options.getCloseTime(),
                        lockActuator,
                        phoneCallSensor,
                        new CollectionBasedMemberLookup(
                                Arrays.asList(/* phone numbers here */)));

            try {
                doorOpenMechanism.start();
                waitForever();
            } catch (InterruptedException ex) {
            } finally {
                doorOpenMechanism.stop();
            }
        } catch (Exception ex) {
            System.err.println("membertrack-elock: " + ex.getMessage());
        }
    }
}
