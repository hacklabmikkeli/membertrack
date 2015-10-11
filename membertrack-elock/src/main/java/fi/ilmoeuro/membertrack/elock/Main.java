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

public class Main {

    private static final int LOCK_SERVO_PIN = 1;
    private static final String MODEM_SERIAL_PORT = "/dev/ttyUSB0";
    private static final long LOCK_TIMEOUT = 5_000;
    private static final Object lock = new Object();

    private Main() {
        // not meant to be instantiated
    }

    @SuppressWarnings("SleepWhileHoldingLock")
    public static void main(String... args) {
        MemberLookup memberLookup = new MemberLookup();
        LockController lockController = new LockController(LOCK_SERVO_PIN);
        ModemController modemController = new ModemController(MODEM_SERIAL_PORT);

        lockController.setLockOpen(false);

        modemController.addPhoneCallListener((number) -> {
            synchronized (lock) {
                if (!memberLookup.isAuthorizedMember(number)) {
                    return;
                }

                try {
                    lockController.setLockOpen(true);
                    Thread.sleep(LOCK_TIMEOUT);
                } catch (InterruptedException ex) {
                } finally {
                    lockController.setLockOpen(false);
                }
            }
        });
    }
}