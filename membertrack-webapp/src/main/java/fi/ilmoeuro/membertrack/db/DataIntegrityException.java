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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

@Slf4j
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public final @Value class DataIntegrityException extends RuntimeException {

    @AllArgsConstructor
    public static enum IntegrityViolation {
        DUPLICATE_KEY (
            String.valueOf(org.h2.api.ErrorCode.DUPLICATE_KEY_1)
        ),
        CHECK_VIOLATION (
            String.valueOf(org.h2.api.ErrorCode.CHECK_CONSTRAINT_VIOLATED_1)
        ),
        NOT_NULL_VIOLATION (
            String.valueOf(org.h2.api.ErrorCode.NULL_NOT_ALLOWED)
        ),
        PARENT_MISSING (
            String.valueOf(org.h2.api.ErrorCode.REFERENTIAL_INTEGRITY_VIOLATED_PARENT_MISSING_1)
        );

        private IntegrityViolation(String... errorCodes) {
            this.errorCodes = Arrays.asList(errorCodes);
        }

        final List<String> errorCodes;
    }

    IntegrityViolation integrityViolation;
    String integrityConstraint;

    @Getter(onMethod = @__({@Override}))
    Throwable cause;

    private static final Pattern CONSTRAINT_REGEX
        = Pattern.compile("\\$([a-z_]+)\\$");

    public static @Nullable DataIntegrityException fromThrowable(Throwable throwable) {
        Throwable rootCause = ExceptionUtils.getRootCause(throwable);

        if (rootCause instanceof SQLException) {
            SQLException sqle = (SQLException) rootCause;
            String constraint = "";
            String message = sqle.getMessage();
            if (message != null) {
                Matcher matcher = CONSTRAINT_REGEX.matcher(message);

                if (matcher.find()) {
                    String group = matcher.group(1);
                    if (group != null) {
                        constraint = group;
                    }
                }
            }

            for (IntegrityViolation errorType : IntegrityViolation.values()) {
                if (errorType.errorCodes.contains(sqle.getSQLState())) {
                    return new DataIntegrityException(errorType, constraint, throwable);
                }
            }
        }

        return null;
    }

    @Override
    public String getMessage() {
        return String.format("Integrity violation %s on %s",
            integrityViolation.toString(),
            integrityConstraint);
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage(); // TODO i18n
    }
}
