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

import java.util.logging.Level;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;
import lombok.extern.java.Log;
import org.checkerframework.checker.nullness.qual.Nullable;

@Log
public class JsscModemAdapter implements ModemAdapter, AutoCloseable {

    private @Nullable ModemMessageListener listener;
    private final SerialPort serialPort;
    private static final int BAUD_RATE = SerialPort.BAUDRATE_115200;
    private static final int DATA_BITS = SerialPort.DATABITS_8;
    private static final int STOP_BITS = SerialPort.STOPBITS_1;
    private static final int PARITY = SerialPort.PARITY_NONE;

    @SuppressWarnings("methodref.receiver.bound.invalid")
    public JsscModemAdapter(
            String serialPortName
    ) throws InitializationException {
        this.serialPort = new SerialPort(serialPortName);
        this.listener = null;
        log.info("Initializing serial port");

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
            log.info("Serial port initialized");
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
                    if (listener != null) {
                        listener.onMessage(rawInput);
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
    public void setMessageListener(@Nullable ModemMessageListener messageListener) {
        this.listener = messageListener;
    }

    @Override
    public void close() throws Exception {
        serialPort.removeEventListener();
        serialPort.closePort();
    }
}
