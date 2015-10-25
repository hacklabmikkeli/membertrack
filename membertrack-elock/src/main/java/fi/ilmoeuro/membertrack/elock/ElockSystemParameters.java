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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kohsuke.args4j.Option;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public final class ElockSystemParameters {

    @Option(
            name = "-D",
            usage = "Modem serial device",
            metaVar = "<dev>")
    private String serialDevice = "/dev/ttyUSB0";

    @Option(name = "-P",
            usage = "Lock pin number (0-29)",
            metaVar = "<pin>")
    private int pinNumber = 0;

    @Option(name = "-o",
            usage = "Lock open time (in ms)",
            metaVar = "<ms>")
    private long openTime = 3_000;
    
    @Option(name = "-c",
            usage = "Lock close minimum time (in ms)",
            metaVar = "<ms>")
    private long closeTime = 3_000;

    @Option(name = "-d",
            usage = "Ring delay (in ms)",
            metaVar = "<ms>")
    private long ringDelay = 3_000;
    

    public void validate() throws InvalidArgumentsException {
        if (pinNumber < 0 || pinNumber > 29) {
            throw new InvalidArgumentsException(
                "Pin number must be 0-29"
            );
        }

        if (openTime > 10_000) {
            throw new InvalidArgumentsException(
                "Open time must be less than 10 000 to not damage the lock"
            );
        }

        if (closeTime < 1_000) {
            throw new InvalidArgumentsException(
                "Close time must be more than 1 000 to not damage the lock"
            );
        }
    }
}