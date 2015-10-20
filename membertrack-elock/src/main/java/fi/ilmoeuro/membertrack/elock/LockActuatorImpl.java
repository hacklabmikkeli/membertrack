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

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class LockActuatorImpl implements LockActuator, AutoCloseable {

    private final GpioController gpioController;
    private final GpioPinDigitalOutput outputPin;

    public LockActuatorImpl(String pinName) throws InitializationException {
        gpioController = GpioFactory.getInstance();
        outputPin = gpioController.provisionDigitalOutputPin(
                RaspiPin.getPinByName(pinName)
        );
        outputPin.setShutdownOptions(false);
    }

    @Override
    public void setLockOpen(boolean lockOpen) {
        outputPin.setState(lockOpen);
    }

    @Override
    public boolean isLockOpen() {
        return outputPin.getState() == PinState.HIGH;
    }

    @Override
    public void close() throws Exception {
        gpioController.shutdown();
    }
}
