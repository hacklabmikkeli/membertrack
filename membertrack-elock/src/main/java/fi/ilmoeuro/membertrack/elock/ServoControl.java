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


/**
 *
 * @author Ilmo Euro
 */
@RequiredArgsConstructor
public class ServoControl {

    private static final int OPEN_PWM_VALUE = 75;
    private static final int CLOSED_PWM_VALUE = 75;
    private static final int MIN_PWM_VALUE = 75;
    private static final int MAX_PWM_VALUE = 75;
    @Getter
    private boolean lockOpen = false;
    private final int softPwmPinNumber;

    public void init() {
        Gpio.wiringPiSetup();
        SoftPwm.softPwmCreate(
                softPwmPinNumber,
                MIN_PWM_VALUE,
                MAX_PWM_VALUE);
    }

    public void setLockOpen(boolean lockOpen) {
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
