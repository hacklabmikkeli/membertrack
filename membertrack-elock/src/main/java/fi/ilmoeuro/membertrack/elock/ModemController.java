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

import jssc.SerialPort;
import jssc.SerialPortException;
import lombok.extern.java.Log;

@Log
public class ModemController {

    private static final int BAUD_RATE = SerialPort.BAUDRATE_115200;
    private static final int DATA_BITS = SerialPort.DATABITS_8;
    private static final int STOP_BITS = SerialPort.STOPBITS_1;
    private static final int PARITY = SerialPort.PARITY_NONE;

    private final SerialPort serialPort;

    public ModemController(String serialPortName) {
        this.serialPort = new SerialPort(serialPortName);
        init();
    }

    private void init() {
        try {
            serialPort.openPort();
            serialPort.setParams(
                    BAUD_RATE,
                    DATA_BITS,
                    STOP_BITS,
                    PARITY);
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
        } catch (SerialPortException ex) {
            // TODO exception
            log.severe(String.format("Serial port error: %s", ex));
        }
    }

    public void addPhoneCallListener(PhoneCallListener phoneCallListener) {
        try {
            serialPort.addEventListener((event) -> {
                // TODO modem data format
            });
        } catch (SerialPortException ex) {
            log.severe(
                    String.format("Error adding phone call listener: %s", ex));
        }
    }
}
