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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

public class PhoneCallSensorImplNGTest {

    private static final Duration CLIP_TTL = Duration.ofMillis(20);

    @Test
    @SuppressWarnings("methodref.inference.unimplemented")
    public void testCorrectCall()
        throws InitializationException, InterruptedException
    {
        final long timeoutMs = 1000;
        final String phoneNumber = "+123456789";
        final BlockingQueue<String> phoneCalls =
            new ArrayBlockingQueue<>(1);
        final FakeModemAdapter modemAdapter =
            new FakeModemAdapter();
        final TemporalFilter<String> temporalFilter =
            new TemporalFilter<>(CLIP_TTL);
        final PhoneCallSensorImpl phoneCallSensor =
            new PhoneCallSensorImpl(modemAdapter, temporalFilter);
        phoneCallSensor.addPhoneCallListener(phoneCalls::add);

        modemAdapter.sendMessage("+CLIP: \"" + phoneNumber + "\"");
        
        @Nullable String actual =
            phoneCalls.poll(timeoutMs, TimeUnit.MILLISECONDS);
        if (actual == null) {
            fail("couldn't get a phone number in time");
        } else {
            assertEquals(
                actual,
                phoneNumber,
                "decoded phone number"
            );
        }
    }

    @Test
    public void testIncorrectCall()
        throws InitializationException, InterruptedException
    {
        final long waitTimeMs = 50;
        final String phoneNumber = "+123456789";
        final BlockingQueue<String> phoneCalls =
            new ArrayBlockingQueue<>(1);
        final FakeModemAdapter modemAdapter =
            new FakeModemAdapter();
        final TemporalFilter<String> temporalFilter =
            new TemporalFilter<>(CLIP_TTL);
        final PhoneCallSensorImpl phoneCallSensor =
            new PhoneCallSensorImpl(modemAdapter, temporalFilter);
        phoneCallSensor.addPhoneCallListener(phoneCalls::add);

        modemAdapter.sendMessage("+CIP \"" + phoneNumber + "\"");

        Thread.sleep(waitTimeMs);

        assertTrue(phoneCalls.isEmpty(), "no decoded phone numbers");
    }
}
