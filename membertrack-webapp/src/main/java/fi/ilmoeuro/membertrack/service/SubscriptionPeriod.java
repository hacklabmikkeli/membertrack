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
package fi.ilmoeuro.membertrack.service;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import lombok.Value;

public final @Value class SubscriptionPeriod {
    LocalDate startDate;
    PeriodTimeUnit lengthUnit;
    long length;
    int payment;
    boolean approved;

    private static String format_fi_FI(LocalDate localDate) {
        return localDate
            .format(DateTimeFormatter.ofPattern("d. M. uuuu"));
    }

    public LocalDate getEndDate() {
        switch (getLengthUnit()) {
            case DAY: {
                return getStartDate().plusDays(getLength());
            }
            case YEAR: {
                int nextYear = getStartDate().getYear() + 1;
                return LocalDate.of(nextYear, Month.JANUARY, 1).minusDays(1);
            }
        }
        throw new IllegalStateException("Invalid time unit");
    }

    public String getStart_fi_FI() {
        return format_fi_FI(getStartDate());
    }

    public String getEnd_fi_FI() {
        return format_fi_FI(getEndDate());
    }

    public String getPaymentFormatted() {
        return String.format("%d,%02d", getPayment() / 100, getPayment() % 100);
    }
}
