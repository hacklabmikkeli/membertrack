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
import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class DoorOpenMechanismNGTest {

    @Test
    public void testAuthorizedPhoneNumber()
        throws InitializationException, InterruptedException 
    {
        final String number = "+123456789";
        final long openTime = 50;
        final long closedTime = 50;
        final long timeMargin = 10;
        final FakeLockActuator lockActuator =
            new FakeLockActuator();
        final FakePhoneCallSensor phoneCallSensor =
            new FakePhoneCallSensor();
        final CollectionBasedMemberLookup memberLookup =
            new CollectionBasedMemberLookup(
                Arrays.asList(number)
            );
        final DoorOpenMechanism mechanism =
            new DoorOpenMechanism(
                openTime,
                closedTime,
                lockActuator,
                phoneCallSensor,
                memberLookup
            );

        mechanism.start();
        assertFalse(lockActuator.isLockOpen(), "lock closed initially");
        phoneCallSensor.call(number);
        Thread.sleep(timeMargin);
        assertTrue(lockActuator.isLockOpen(), "lock open after call");
        Thread.sleep(openTime + timeMargin);
        assertFalse(lockActuator.isLockOpen(), "lock closed after waiting");
        mechanism.stop();
    }

    @Test
    public void testUnauthorizedPhoneNumber()
        throws InitializationException, InterruptedException 
    {
        final String number = "+123456789";
        final String wrongNumber = "+987654321";
        final long openTime = 20;
        final long closedTime = 20;
        final long timeMargin = 5;
        final FakeLockActuator lockActuator =
            new FakeLockActuator();
        final FakePhoneCallSensor phoneCallSensor =
            new FakePhoneCallSensor();
        final CollectionBasedMemberLookup memberLookup =
            new CollectionBasedMemberLookup(
                Arrays.asList(number)
            );
        final DoorOpenMechanism mechanism =
            new DoorOpenMechanism(
                openTime,
                closedTime,
                lockActuator,
                phoneCallSensor,
                memberLookup
            );

        mechanism.start();
        assertFalse(lockActuator.isLockOpen(), "lock closed initially");
        phoneCallSensor.call(wrongNumber);
        Thread.sleep(timeMargin);
        assertFalse(lockActuator.isLockOpen(), "lock closed after call");
        Thread.sleep(openTime + timeMargin);
        assertFalse(lockActuator.isLockOpen(), "lock closed after waiting");
        mechanism.stop();
    }
}
