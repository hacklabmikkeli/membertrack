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
package fi.ilmoeuro.membertrack.member;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import lombok.Value;

@Value
public final class ServiceSubscription {
    Instant start;
    long length;
    int payment;

    private static String format_fi_FI(Instant instant) {
        return instant
            .atZone(ZoneId.of("Europe/Helsinki"))
            .format(DateTimeFormatter.ofPattern("d. M. uuuu"));
    }

    public Instant getEnd() {
        return start.plusMillis(length);
    }

    public String getStart_fi_FI() {
        return format_fi_FI(getStart());
    }

    public String getEnd_fi_FI() {
        return format_fi_FI(getEnd());
    }

    public String getPaymentFormatted() {
        return String.format("%d,%02d", getPayment() / 100, getPayment() % 100);
    }
}
