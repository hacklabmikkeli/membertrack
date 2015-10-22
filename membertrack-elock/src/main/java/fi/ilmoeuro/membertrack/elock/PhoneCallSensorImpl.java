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
import lombok.extern.java.Log;

@Log
public final class PhoneCallSensorImpl implements PhoneCallSensor, AutoCloseable {

    private static final String CALL_PATTERN = "+CLIP: \"";
    private static final int CALL_PATTERN_LENGTH = CALL_PATTERN.length();

    private final ArrayList<PhoneCallListener> listeners;
    private final TemporalFilter<String> temporalFilter;
    private final ModemAdapter modemAdapter;

    @SuppressWarnings("methodref.receiver.bound.invalid")
    public PhoneCallSensorImpl(
        ModemAdapter modemAdapter,
        TemporalFilter<String> temporalFilter
    ) throws InitializationException {
        this.temporalFilter = temporalFilter;
        this.listeners = new ArrayList<>();
        this.modemAdapter = modemAdapter;
        modemAdapter.setMessageListener(this::onMessage);
    }

    private void onMessage(String rawInput) {
        final String input = rawInput.trim();
        if (input.startsWith(CALL_PATTERN)) {
            final String number = input.substring(
                CALL_PATTERN_LENGTH,
                input.indexOf("\"", CALL_PATTERN_LENGTH));
            if (!temporalFilter.accessAndCheckIfAlive(number)) {
                for (PhoneCallListener listener : listeners) {
                    listener.onCall(number);
                }
            }
        }
    }

    @Override
    public void addPhoneCallListener(PhoneCallListener phoneCallListener) {
        listeners.add(phoneCallListener);
    }

    @Override
    public void close() throws Exception {
        listeners.clear();
        modemAdapter.setMessageListener(null);
    }
}
