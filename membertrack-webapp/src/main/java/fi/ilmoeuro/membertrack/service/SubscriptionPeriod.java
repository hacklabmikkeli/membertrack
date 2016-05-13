/*
 * Copyright (C) 2015 Ilmo Euro <ilmo.euro@gmail.com>
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
package fi.ilmoeuro.membertrack.service;

import fi.ilmoeuro.membertrack.db.Persistable;
import fi.ilmoeuro.membertrack.schema.tables.pojos.SubscriptionPeriodBase;
import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class SubscriptionPeriod
        extends SubscriptionPeriodBase
        implements Persistable {
    private static final long serialVersionUID = 0l;
    
    @SuppressWarnings("nullness") // Interface with autogen code
    @Deprecated
    public SubscriptionPeriod(
        @Nullable Integer pk,
        UUID id,
        boolean deleted,
        UUID serviceId,
        UUID personId,
        LocalDate startDate,
        PeriodTimeUnit lengthUnit,
        long length,
        int payment,
        boolean approved
    ) {
        super(
            pk,
            id,
            deleted,
            serviceId,
            personId,
            startDate,
            lengthUnit,
            length,
            payment,
            approved
        );
    }

    @SuppressWarnings("deprecation")
    public SubscriptionPeriod(
        UUID serviceId,
        UUID personId,
        LocalDate startDate,
        PeriodTimeUnit lengthUnit,
        long length,
        int payment,
        boolean approved
    ) {
        this(
            null,
            UUID.randomUUID(),
            false,
            serviceId,
            personId,
            startDate,
            lengthUnit,
            length,
            payment,
            approved
        );
    }

    @Override
    public boolean isNew() {
        return getPk() == null;
    }

    public LocalDate getEndDate() {
        switch (getLengthUnit()) {
            case DAY:
                return getStartDate().plusDays(getLength());
            case YEAR:
                return LocalDate.of(
                        getStartDate().getYear() + 1,
                        Month.JANUARY,
                        1)
                    .minusDays(1);
        }

        throw new IllegalStateException("Invalid length unit");
    }

    public String getPaymentFormatted() {
        int payment = getPayment();
        return String.format("%d,%02d €", payment / 100, payment % 100);
    }
}