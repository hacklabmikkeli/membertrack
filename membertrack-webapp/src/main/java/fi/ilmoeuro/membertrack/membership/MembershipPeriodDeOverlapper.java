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
package fi.ilmoeuro.membertrack.membership;

import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public final class MembershipPeriodDeOverlapper<SessionTokenType> {

    private final SessionRunner<SessionTokenType> sessionRunner;
    private final UnitOfWork.Factory<SessionTokenType> uowFactory;
    private final Memberships.Factory<SessionTokenType> membershipsFactory;

    public void runFullDeOverlap() {
        log.info("Running full deoverlap");
        sessionRunner.exec(token -> {
            Memberships memberships = membershipsFactory.create(token);
            UnitOfWork uow = uowFactory.create(token);

            for (Membership membership : memberships.listAllMemberships()) {
                for (Subscription sub : membership.getSubscriptions()) {
                    List<SubscriptionPeriod> periods = sub.getPeriods();
                    Collections.reverse(periods);
                    deoverlap(periods, true);
                    periods.forEach(uow::addEntity);
                }
            }

            uow.execute();
        });
    }

    public static void deoverlap(
        List<SubscriptionPeriod> periods,
        boolean alreadySortedByStartDate
    ) {
        if (!alreadySortedByStartDate) {
            periods.sort((l, r) -> l.getStartDate().compareTo(r.getStartDate()));
        }
        for (int i = 1; i < periods.size(); i++) {
            SubscriptionPeriod lastPeriod = periods.get(i - 1);
            SubscriptionPeriod period = periods.get(i);
            if (period != null && lastPeriod != null) {
                if (lastPeriod.getEndDate().isAfter(period.getStartDate())) {
                    long numDaysToPush = ChronoUnit.DAYS.between(
                        period.getStartDate(),
                        lastPeriod.getEndDate())
                        + 1;
                    pushOverlapping(periods, i, numDaysToPush);
                }
            }
        }
    }

    private static void pushOverlapping(
        List<SubscriptionPeriod> periods,
        int startingIndex,
        long numDaysToPush
    ) {
        for (int i = startingIndex; i < periods.size(); i++) {
            SubscriptionPeriod lastPeriod = i > 0 ? periods.get(i - 1) : null;
            SubscriptionPeriod period = periods.get(i);
            if (period != null && lastPeriod != null) {
                if (lastPeriod.getEndDate().isAfter(period.getStartDate())) {
                    period.setStartDate(period.getStartDate().plusDays(numDaysToPush));
                    period.setEndDate(period.getEndDate().plusDays(numDaysToPush));
                } else {
                    return;
                }
            }
        }
    }
}
