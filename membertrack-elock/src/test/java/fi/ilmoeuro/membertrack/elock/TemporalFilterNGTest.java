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
import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class TemporalFilterNGTest {

    @Test
    public void testDeadObject() throws InterruptedException {
        TemporalFilter<String> filter =
            new TemporalFilter<>(Duration.ofMillis(5));

        assertFalse(
            filter.accessAndCheckIfAlive("test"),
            "not alive initially"
        );

        Thread.sleep(20);

        assertFalse(
            filter.accessAndCheckIfAlive("test"),
            "not alive finally"
        );
    }

    @Test
    public void testAliveObject() throws InterruptedException {
        TemporalFilter<String> filter =
            new TemporalFilter<>(Duration.ofMillis(20));

        assertFalse(
            filter.accessAndCheckIfAlive("test"),
            "not alive initially"
        );

        Thread.sleep(5);

        assertTrue(
            filter.accessAndCheckIfAlive("test"),
            "alive finally"
        );
    }
    
}
