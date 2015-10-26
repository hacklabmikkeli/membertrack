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

public class ElockSystem implements AutoCloseable {
    
    private final LockActuatorImpl lockActuator;
    private final JsscModemAdapter modemAdapter;
    private final PhoneCallSensorImpl phoneCallSensor;
    private final TemporalFilter<String> temporalFilter;
    private final DatabaseMemberLookup memberLookup;
    private final DoorOpenMechanism doorOpenMechanism;

    public ElockSystem(
        ElockSystemParameters params
    ) throws InitializationException {
        final Duration ringDelay = Duration.ofMillis(params.getRingDelay());
        lockActuator = new LockActuatorImpl(params.getPinNumber());
        modemAdapter = new JsscModemAdapter(params.getSerialDevice());
        temporalFilter = new TemporalFilter<>(ringDelay);
        phoneCallSensor = new PhoneCallSensorImpl(
            modemAdapter,
            temporalFilter
        );
        memberLookup = new DatabaseMemberLookup(
            params.getH2URL(),
            params.getH2UserName(),
            params.getH2Password()
        );
        doorOpenMechanism = new DoorOpenMechanism(
            params.getOpenTime(),
            params.getCloseTime(),
            lockActuator,
            phoneCallSensor,
            memberLookup
        );
    }

    public void start() {
        doorOpenMechanism.start();
    }

    public void stop() {
        doorOpenMechanism.stop();
    }

    @Override
    public void close() throws Exception {
        memberLookup.close();
        phoneCallSensor.close();
        modemAdapter.close();
        lockActuator.close();
    }
}
