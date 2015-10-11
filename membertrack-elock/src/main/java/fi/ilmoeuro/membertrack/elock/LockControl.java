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

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.SoftPwm;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LockControl {

    private static final int OPEN_PWM_VALUE = 8;
    private static final int CLOSED_PWM_VALUE = 0;
    private static final int MIN_PWM_VALUE = 0;
    private static final int MAX_PWM_VALUE = 10;
    private static final int PWM_DIVISOR = 19; // 19.2e6 / 19 / 100 = 1kHz;

    @Getter
    private boolean lockOpen = false;
    private boolean initialized = false;
    private final int softPwmPinNumber;

    public void init() {
        int errno = Gpio.wiringPiSetup();
        if (errno > 0) {
            // TODO exception
            throw new RuntimeException(
                    String.format("Error during wiringPi setup: %d", errno)
            );
        }
        Gpio.pwmSetMode(Gpio.PWM_MODE_MS);
        Gpio.pwmSetClock(PWM_DIVISOR);
        errno = SoftPwm.softPwmCreate(
                softPwmPinNumber,
                MIN_PWM_VALUE,
                MAX_PWM_VALUE);
        if (errno > 0) {
            // TODO exception
            throw new RuntimeException(
                    String.format("Error during soft pwm create: %d", errno)
            );
        }
        initialized = true;
    }

    public void setLockOpen(boolean lockOpen) {
        if (!initialized) {
            throw new IllegalStateException("Uninitialized lock control");
        }
        
        if (lockOpen == this.lockOpen) {
            return;
        }

        if (lockOpen) {
            SoftPwm.softPwmWrite(softPwmPinNumber, OPEN_PWM_VALUE);
        } else {
            SoftPwm.softPwmWrite(softPwmPinNumber, CLOSED_PWM_VALUE);
        }

        this.lockOpen = lockOpen;
    }
}
