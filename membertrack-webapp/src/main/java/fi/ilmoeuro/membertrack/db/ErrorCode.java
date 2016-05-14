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
package fi.ilmoeuro.membertrack.db;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.h2.jdbc.JdbcSQLException;

@AllArgsConstructor
public enum ErrorCode {
    DUPLICATE_KEY (
        String.valueOf(org.h2.api.ErrorCode.DUPLICATE_KEY_1)
    ),
    CHECK_VIOLATION (
        String.valueOf(org.h2.api.ErrorCode.CHECK_CONSTRAINT_VIOLATED_1)
    ),
    NOT_NULL_VIOLATION (
        String.valueOf(org.h2.api.ErrorCode.NULL_NOT_ALLOWED)
    );

    final String h2Code;

    public static @Nullable ErrorCode fromThrowable(Throwable throwable) {
        Throwable rootCause = ExceptionUtils.getRootCause(throwable);

        if (rootCause instanceof JdbcSQLException) {
            JdbcSQLException jsqle = (JdbcSQLException) rootCause;

            for (ErrorCode errorCode : ErrorCode.values()) {
                if (errorCode.h2Code.equals(jsqle.getSQLState())) {
                    return errorCode;
                }
            }
        }

        return null;
    }
}
