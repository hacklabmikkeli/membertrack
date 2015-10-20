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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import lombok.extern.java.Log;
import org.checkerframework.checker.nullness.qual.Nullable;

@Log
public class PhoneCallSensorImpl implements PhoneCallSensor, AutoCloseable {

    private static final int BAUD_RATE = SerialPort.BAUDRATE_115200;
    private static final int DATA_BITS = SerialPort.DATABITS_8;
    private static final int STOP_BITS = SerialPort.STOPBITS_1;
    private static final int PARITY = SerialPort.PARITY_NONE;
    private static final Pattern CALL_PATTERN =
            Pattern.compile("^\\+CLIP: \"\\+(\\d+)\"");

    private final SerialPort serialPort;
    private final ArrayList<PhoneCallListener> listeners;

    @SuppressWarnings("methodref.receiver.bound.invalid")
    public PhoneCallSensorImpl(String serialPortName) throws InitializationException {
        serialPort = new SerialPort(serialPortName);
        listeners = new ArrayList<>();

        try {
            serialPort.openPort();
            serialPort.setParams(
                    BAUD_RATE,
                    DATA_BITS,
                    STOP_BITS,
                    PARITY);
            serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
            serialPort.writeString("AT+CLIP=1\r\n");
            serialPort.addEventListener(this::serialEventListener);
        } catch (SerialPortException ex) {
            throw new InitializationException("Unable to initialize modem", ex);
        }
    }

    private void serialEventListener(SerialPortEvent event) {
        try {
            if (event.getEventType() == SerialPortEvent.RXCHAR) {
                String[] inputs = serialPort
                        .readString(event.getEventValue())
                        .split("\r\n");
                for (String rawInput : inputs) {
                    final String input = rawInput.trim();

                    if ("".equals(input)) {
                        continue;
                    }

                    log.info(input);
                    final Matcher matcher = CALL_PATTERN.matcher(input);
                    if (matcher.matches()) {
                        final @Nullable String group = matcher.group(1);
                        if (group == null) {
                            continue;
                        }
                        for (PhoneCallListener listener : listeners) {
                            listener.onCall(group);
                        }
                    }
                }
            }
        } catch (SerialPortException ex) {
            log.log(
                Level.SEVERE,
                "Listening to serial port event failed",
                ex
            );
        }
    }

    @Override
    public void addPhoneCallListener(PhoneCallListener phoneCallListener) {
        listeners.add(phoneCallListener);
    }

    @Override
    public void close() throws Exception {
        listeners.clear();
        serialPort.removeEventListener();
        serialPort.closePort();
    }
}
