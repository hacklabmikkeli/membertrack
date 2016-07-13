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

import fi.ilmoeuro.membertrack.auth.PasswordResetManager;
import fi.ilmoeuro.membertrack.person.Account;
import fi.ilmoeuro.membertrack.person.Accounts;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import java.io.Serializable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import fi.ilmoeuro.membertrack.util.Refreshable;
import java.util.Objects;
import lombok.Setter;

@Slf4j
@RequiredArgsConstructor
public final class
    AccountEditor<SessionTokenType>
implements
    Serializable,
    Refreshable
{
    public static final class
        PasswordsDontMatchException
    extends
        RuntimeException
    {
        public PasswordsDontMatchException() {
            super("Passwords don't match");
        }
    }
    
    private static final long serialVersionUID = 0l;
    
    private final Accounts.Factory<SessionTokenType> af;
    private final UnitOfWork.Factory<SessionTokenType> uowFactory;
    private final SessionRunner<SessionTokenType> sessionRunner;
    private final PasswordResetManager pwResetManager;

    @Getter
    @Setter
    private String newPassword = "";

    @Getter
    @Setter
    private String newPasswordAgain = "";

    @Getter
    private @Nullable Person person;

    @Getter
    private @Nullable Account account;

    public void setPerson(Person person) {
        this.person = person;
        refresh();
    }
    
    public boolean isAccountExists() {
        return account != null;
    }

    @Override
    public void refresh() {
        sessionRunner.exec(token -> {
            if (person != null) {
                Person p = person;
                Accounts accounts = af.create(token);
                account = accounts.findByPerson(p);
            } else {
                account = null;
            }
        });
    }

    public void requestPasswordReset() {
        if (person != null && account != null) {
            pwResetManager.requestPasswordReset(person.getEmail());
        }
    }

    public void createAccount() {
        sessionRunner.exec(token -> {
            if (!Objects.equals(
                newPassword,
                newPasswordAgain)) {
                throw new PasswordsDontMatchException();
            }
            
            if (person != null && account == null) {
                Account newAccount = Account.create(person, newPassword);
                UnitOfWork uow = uowFactory.create(token);
                uow.addEntity(newAccount);
                uow.execute();
                account = newAccount;
            }
        });
    }
}
