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
public final class CommandLineOptions {

    @Option(
            name = "-D",
            usage = "Modem serial device",
            metaVar = "<dev>")
    private String serialDevice = "/dev/ttyUSB0";

    @Option(name = "-P",
            usage = "Lock pin name (pi4j)",
            metaVar = "<pin>")
    private String pinName = "GPIO_01";

    @Option(name = "-o",
            usage = "Lock open time (in ms)",
            metaVar = "<ms>")
    private long openTime = 3_000;
    
    @Option(name = "-c",
            usage = "Lock close minimum time (in ms)",
            metaVar = "<ms>")
    private long closeTime = 3_000;
    
}