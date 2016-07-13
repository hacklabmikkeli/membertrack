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
package fi.ilmoeuro.membertrack.auth;

import fi.ilmoeuro.membertrack.util.Refreshable;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import fi.ilmoeuro.membertrack.util.PageParamsSaveLoad;
import org.checkerframework.checker.nullness.qual.Nullable;

@RequiredArgsConstructor
public final class
    PasswordResetPageModel
implements
    Refreshable,
    PageParamsSaveLoad,
    Serializable
{
    private static final long serialVersionUID = 0l;
    private final PasswordResetManager passwordResetManager;

    @Getter
    @Setter
    private String email = "";
    
    @Getter
    @Setter
    private String password = "";
    
    @Getter
    @Setter
    private String passwordAgain = "";
    
    @Getter
    @Setter
    private String token = "";

    @Getter
    private String errorMessage = "";

    private void clearFields() {
        email = "";
        password = "";
        passwordAgain = "";
        token = "";
    }

    public void doReset() {
        if (!Objects.equals(password, passwordAgain)) {
            errorMessage = "Passwords don't match";
            return;
        }
        switch (passwordResetManager.resetPassword(email, password, token)) {
            case FAILURE_NO_PERSON:
                errorMessage = "No person for given email";
                return;
            case FAILURE_NO_ACCOUNT:
                errorMessage = "No account for given email";
                return;
            case FAILURE_NO_TOKEN:
                errorMessage = "No password reset token requested";
                return;
            case SUCCESS:
                errorMessage = "Reset successful!";
                clearFields();
                break;
        }
    }

    @Override
    public void refresh() {
    }

    @Override
    public void saveState(BiConsumer<String, @Nullable String> pairConsumer) {
    }

    @Override
    public void loadState(Function<String, @Nullable String> getValue) {
    }
}
