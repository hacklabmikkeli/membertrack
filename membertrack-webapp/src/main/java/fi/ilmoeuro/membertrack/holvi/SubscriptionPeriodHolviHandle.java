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

import fi.ilmoeuro.membertrack.db.Persistable;
import fi.ilmoeuro.membertrack.schema.tables.pojos.SubscriptionPeriodHolviHandleBase;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 *
 * @author Ilmo Euro <ilmo.euro@gmail.com>
 */
public final class
    SubscriptionPeriodHolviHandle
extends
    SubscriptionPeriodHolviHandleBase
implements
    Persistable
{
    private static final long serialVersionUID = 0l;
    
    @SuppressWarnings("nullness") // Interface with autogen code
    @Deprecated
    public SubscriptionPeriodHolviHandle(
        @Nullable Integer pk,
        UUID id,
        UUID periodId,
        String poolHandle,
        String orderHandle,
        int itemNumber
    ) {
        super(pk, id, periodId, poolHandle, orderHandle, itemNumber);
    }

    @SuppressWarnings("deprecation")
    public SubscriptionPeriodHolviHandle(
        SubscriptionPeriod period,
        String poolHandle,
        String orderHandle,
        int itemNumber
    ) {
        this(null, UUID.randomUUID(), period.getId(), poolHandle, orderHandle, itemNumber);
    }

    public String getHolviURL() {
        return String.format(
            "https://holvi.com/group/%s/store/orders/%s/",
            getPoolHandle(),
            getOrderHandle());
    }
}
