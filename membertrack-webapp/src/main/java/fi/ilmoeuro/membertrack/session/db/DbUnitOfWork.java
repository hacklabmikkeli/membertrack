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
package fi.ilmoeuro.membertrack.session.db;

import fi.ilmoeuro.membertrack.auth.db.PasswordResetToken;
import fi.ilmoeuro.membertrack.db.Persistable;
import fi.ilmoeuro.membertrack.holvi.SubscriptionPeriodHolviHandle;
import fi.ilmoeuro.membertrack.person.Account;
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.person.SecondaryEmail;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.session.SessionToken;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import lombok.Value;

@RequiredArgsConstructor
public final class DbUnitOfWork implements UnitOfWork {

    public static class Factory implements UnitOfWork.Factory<DSLContext> {
        private static final long serialVersionUID = 0L;

        @Override
        public UnitOfWork create(SessionToken<DSLContext> token) {
            return new DbUnitOfWork(token.getValue());
        }
    }

    private static @Value class PersistableRecord {
        UpdatableRecord updatableRecord;
        Persistable persistable;
    }
    
    private final DSLContext jooq;
    private final List<PersistableRecord> records = new ArrayList<>();

    @Override
    public void addEntity(Persistable o) {
        if (o instanceof Person) {
            addEntity(PERSON, o);
        } else if (o instanceof SecondaryEmail) {
            addEntity(SECONDARY_EMAIL, o);
        } else if (o instanceof PhoneNumber) {
            addEntity(PHONE_NUMBER, o);
        } else if (o instanceof Account) {
            addEntity(ACCOUNT, o);
        } else if (o instanceof Service) {
            addEntity(SERVICE, o);
        } else if (o instanceof SubscriptionPeriod) {
            addEntity(SUBSCRIPTION_PERIOD, o);
        } else if (o instanceof SubscriptionPeriodHolviHandle) {
            addEntity(SUBSCRIPTION_PERIOD_HOLVI_HANDLE, o);
        } else if (o instanceof PasswordResetToken) {
            addEntity(PASSWORD_RESET_TOKEN, o);
        } else {
            throw new IllegalArgumentException(
                String.format(
                    "Argument %s is not a persistable entity",
                    o.toString()
                )
            );
        }
    }

    @Override
    public void execute() {
        for (PersistableRecord record : records) {
            if (record.getPersistable().isDeleted()) {
                if (record.getPersistable().getPk() != null) {
                    record.getUpdatableRecord().delete();
                }
            } else {
                if (record.getPersistable().getPk() == null) {
                    record.getUpdatableRecord().store();
                    record.getPersistable().setPk(
                        record.getUpdatableRecord().getValue("pk", Integer.class));
                } else {
                    record.getUpdatableRecord().update();
                }
            }
        }
    }

    private <R extends UpdatableRecord> void addEntity(
        Table<R> table,
        Persistable val
    ) {
        R record = jooq.newRecord(table, val);
        records.add(new PersistableRecord(record, val));
    }
}
