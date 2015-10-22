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

import java.time.Duration;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

@Log
public final class Main {

    private static final Duration CLIP_TTL = Duration.ofSeconds(10);
    private final static long MAX_LOCK_OPEN_TIME = 10_000;
    private final static long MIN_LOCK_CLOSED_TIME = 2_000;

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
            System.err.println("Usage: membertrack-elock [options]");
            argParser.printUsage(System.err);
            System.err.println();
            return;
        }

        TemporalFilter temporalFilter = new TemporalFilter(CLIP_TTL);
        try (LockActuatorImpl lockActuator = 
                new LockActuatorImpl(options.getPinNumber());
             JsscModemAdapter modemAdapter =
                new JsscModemAdapter(options.getSerialDevice());
             PhoneCallSensorImpl phoneCallSensor =
                new PhoneCallSensorImpl(
                    modemAdapter,
                    temporalFilter
                )
        ) {
            if (options.getOpenTime() > MAX_LOCK_OPEN_TIME) {
                throw new InitializationException(
                    "Too long lock open time; it may harm the lock"
                );
            }
            if (options.getCloseTime() < MIN_LOCK_CLOSED_TIME) {
                throw new InitializationException(
                    "Too short lock closed time; it may harm the lock"
                );
            }
            DoorOpenMechanism doorOpenMechanism =
                new DoorOpenMechanism(
                    options.getOpenTime(),
                    options.getCloseTime(),
                    lockActuator,
                    phoneCallSensor,
                    new AllowEveryoneMemberLookup());
            try {
                doorOpenMechanism.start();
                System.in.read();
            } finally {
                doorOpenMechanism.stop();
            }
        } catch (InitializationException ex) {
            System.err.printf("membertrack-elock: %s\n", ex.getMessage());
        } catch (Exception ex) {
            log.log(
                Level.SEVERE,
                "membertrack-elock error",
                ex);
        }
    }
}
