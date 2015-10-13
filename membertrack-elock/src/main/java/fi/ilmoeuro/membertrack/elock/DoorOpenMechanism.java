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

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import lombok.Value;
import lombok.extern.java.Log;

@Log
public class DoorOpenMechanism {

    private static final @Value class PhoneCallEvent {
        private final String phoneNumber;
    }

    private final static int MAX_CALLS_IN_QUEUE = 10_000;
    private final long lockOpenTime;
    private final LockActuator lockActuator;
    private final ModemController modemController;
    private final CollectionBasedMemberLookup memberLookup;
    private final Thread thread;
    private final AtomicBoolean running;
    private final BlockingQueue<PhoneCallEvent> eventQueue;

    public DoorOpenMechanism(
            long lockOpenTime,
            LockActuator lockActuator,
            ModemController modemController,
            CollectionBasedMemberLookup memberLookup
    ) {
        this.lockOpenTime = lockOpenTime;
        this.lockActuator = lockActuator;
        this.modemController = modemController;
        this.memberLookup = memberLookup;
        this.thread = new Thread(this::run);
        this.running = new AtomicBoolean(false);
        this.eventQueue = new ArrayBlockingQueue<>(MAX_CALLS_IN_QUEUE);
    }

    public void init() throws InitializationException {
        lockActuator.init();
        modemController.init();
        modemController.addPhoneCallListener((phoneNumber) -> {
            if (!eventQueue.offer(new PhoneCallEvent(phoneNumber))) {
                log.log(Level.SEVERE,
                        "Phone call from {0} rejected due full queue",
                        phoneNumber);
            }
        });
    }

    public void start() {
        log.info("Lock mechanism starting");
        thread.start();
    }

    @SuppressWarnings("SleepWhileInLoop")
    private void run() {
        running.set(true);
        log.info("Lock mechanism started");
        while (running.get()) {
            try {
                PhoneCallEvent event = eventQueue.take();
                if (memberLookup.isAuthorizedMember(event.getPhoneNumber())) {
                    lockActuator.setLockOpen(true);
                    Thread.sleep(lockOpenTime);
                }
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }
            } catch (InterruptedException ex) {
                log.info("Lock mechanism interrupted");
            } finally {
                lockActuator.setLockOpen(false);
            }
        }
        log.info("Lock mechanism stopped");
    }

    public synchronized void stop() {
        log.info("Lock mechanism stopping");
        running.set(false);
        thread.interrupt();
    }
}
