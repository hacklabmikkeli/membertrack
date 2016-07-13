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
package fi.ilmoeuro.membertrack.person.db;

import fi.ilmoeuro.membertrack.person.Account;
import fi.ilmoeuro.membertrack.person.Accounts;
import fi.ilmoeuro.membertrack.person.Person;
import org.jooq.DSLContext;
import static fi.ilmoeuro.membertrack.schema.Tables.*;
import lombok.RequiredArgsConstructor;
import fi.ilmoeuro.membertrack.session.SessionToken;

@RequiredArgsConstructor
public final class
    DbAccounts
implements
    Accounts
{
    public static final class Factory implements Accounts.Factory<DSLContext> {
        private static final long serialVersionUID = 0L;

        @Override
        public Accounts create(SessionToken<DSLContext> token) {
            return new DbAccounts(token.getValue());
        }
    }

    private final DSLContext jooq;

    @Override
    public Account findByPerson(Person person) {
        return jooq
            .select(ACCOUNT.fields())
            .from(ACCOUNT)
            .where(ACCOUNT.PERSON_ID.eq(person.getId()))
            .fetchAnyInto(Account.class);
    }
}