/*
 * Copyright (C) 2016 Ilmo Euro <ilmo.euro@gmail.com>
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
package fi.ilmoeuro.membertrack.holvi;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.ilmoeuro.membertrack.membership.MembershipPeriodDeOverlapper;
import fi.ilmoeuro.membertrack.membership.Memberships;
import fi.ilmoeuro.membertrack.person.Persons;
import fi.ilmoeuro.membertrack.service.Services;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import java.io.Serializable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HolviSynchronizer<SessionTokenType> {

    public static final @Data class Config implements Serializable {
        private static final long serialVersionUID = 0l;
        boolean enabled;
        int interval;
        TimeUnit intervalUnit;
        HolviPopulator.Config populator;
    }

    public HolviSynchronizer(
        ObjectMapper objectMapper,
        Config config,
        SessionRunner<SessionTokenType> sessionRunner,
        UnitOfWork.Factory<SessionTokenType> uowFactory,
        Persons.Factory<SessionTokenType> personsFactory,
        Services.Factory<SessionTokenType> servicesFactory,
        Memberships.Factory<SessionTokenType> membershipsFactory
    ) {
        this.config = config;
        this.holviPopulator = new HolviPopulator<SessionTokenType>(
            config.getPopulator(),
            sessionRunner,
            uowFactory,
            personsFactory,
            servicesFactory,
            objectMapper
        );
        this.deOverlapper = new MembershipPeriodDeOverlapper<SessionTokenType>(
            sessionRunner,
            uowFactory,
            membershipsFactory
        );
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    
    private final Config config;
    private final HolviPopulator<SessionTokenType> holviPopulator;
    private final MembershipPeriodDeOverlapper<SessionTokenType> deOverlapper;
    private final ScheduledExecutorService scheduler;
    private boolean running = false;

    private void synchronize() {
        if (!config.isEnabled()) {
            return;
        }

        try {
            log.info("Synchronizing with Holvi");
            holviPopulator.runPopulator();
            deOverlapper.runFullDeOverlap();
        } catch (Exception ex) {
            log.error("Error while synchronizing with Holvi: ", ex);
        }
    }

    public void start() {
        if (config.isEnabled() && !running) {
            scheduler.scheduleAtFixedRate(
                this::synchronize,
                0,
                config.getInterval(),
                config.getIntervalUnit());
            running = true;
        }
    }

    public void stop() {
        scheduler.shutdownNow();
        running = false;
    }
}
