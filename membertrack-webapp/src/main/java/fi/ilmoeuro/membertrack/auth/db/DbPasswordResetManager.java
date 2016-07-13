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
package fi.ilmoeuro.membertrack.auth.db;

import static fi.ilmoeuro.membertrack.schema.Tables.*;
import fi.ilmoeuro.membertrack.auth.PasswordResetManager;
import fi.ilmoeuro.membertrack.auth.PasswordResetOutcome;
import fi.ilmoeuro.membertrack.person.Account;
import fi.ilmoeuro.membertrack.person.Accounts;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.Persons;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import fi.ilmoeuro.membertrack.util.ClockHolder;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;

@RequiredArgsConstructor
@Slf4j
public final class
    DbPasswordResetManager
implements
    PasswordResetManager,
    Serializable
{
    private static final long serialVersionUID = 0l;

    public static final @Data class Config implements Serializable {
        private static final long serialVersionUID = 0l;

        int resetTokenValiditySecs = 3600;
    }
    
    private final SessionRunner<DSLContext> sessionRunner;
    private final UnitOfWork.Factory<DSLContext> uowFactory;
    private final Accounts.Factory<DSLContext> accountsFactory;
    private final Persons.Factory<DSLContext> personsFactory;
    private final Config config;

    @Override
    public void requestPasswordReset(String email) {
        sessionRunner.exec(token -> {
            Persons persons = personsFactory.create(token);
            Person person = persons.findByPrimaryEmail(email);
            if (person == null) {
                return;
            }

            long validitySecs = config.getResetTokenValiditySecs();
            PasswordResetToken prt = PasswordResetToken.create(person, validitySecs);
            UnitOfWork uow = uowFactory.create(token);
            uow.addEntity(prt);
            uow.execute();
        });
    }

    @Override
    public PasswordResetOutcome resetPassword(
        String email,
        String password,
        String token
    ) {
        return sessionRunner.eval(t -> {
            DSLContext jooq = t.getValue();
            LocalDateTime now = LocalDateTime.now(ClockHolder.get());
            Persons persons = personsFactory.create(t);
            Person person = persons.findByPrimaryEmail(email);
            if (person == null) {
                return PasswordResetOutcome.FAILURE_NO_PERSON;
            }

            Accounts accounts = accountsFactory.create(t);
            Account account = accounts.findByPerson(person);
            if (account == null) {
                return PasswordResetOutcome.FAILURE_NO_ACCOUNT;
            }

            List<PasswordResetToken> prts = jooq
                .select(PASSWORD_RESET_TOKEN.fields())
                .from(PASSWORD_RESET_TOKEN)
                .where(PASSWORD_RESET_TOKEN.VALID_UNTIL.le(now))
                .fetchInto(PasswordResetToken.class);
            for (PasswordResetToken prt : prts) {
                if (prt.matches(person)) {
                    account.setPassword(password);
                    prt.setDeleted(true);
                    UnitOfWork uow = uowFactory.create(t);
                    uow.addEntity(account);
                    uow.addEntity(prt);
                    uow.execute();
                    return PasswordResetOutcome.SUCCESS;
                }
            }

            return PasswordResetOutcome.FAILURE_NO_TOKEN;
        });
    }
}
