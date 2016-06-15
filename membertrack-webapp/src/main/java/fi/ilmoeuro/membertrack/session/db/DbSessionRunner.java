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

import fi.ilmoeuro.membertrack.db.DataIntegrityException;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.SessionToken;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

@RequiredArgsConstructor
public final class DbSessionRunner implements SessionRunner<DSLContext> {
    private final static long serialVersionUID = 0l;
    
    public static final @Data class Config implements Serializable {
        private final static long serialVersionUID = 0l;
        private String dataSourceJndiName = "jdbc/membertrack";
        private String sqlDialect = "H2";
    }

    private final Config config;

    @Override
    @SuppressWarnings("method.invocation.invalid")
    public <R> @NonNull R eval(Function<SessionToken<DSLContext>,@NonNull R> func) {
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(config.getDataSourceJndiName());
            DSLContext jooq = DSL.using(ds, SQLDialect.valueOf(config.getSqlDialect()));
            final @NonNull AtomicReference<@NonNull R>
                resultRef = new AtomicReference<>();
            jooq.transaction((Configuration conf) -> {
                resultRef.set(func.apply(new SessionToken<>(DSL.using(conf))));
            });
            R result = resultRef.get();
            if (result != null) {
                return result;
            } else {
                throw new RuntimeException("Atomic reference not set");
            }
        } catch (DataAccessException ex) {
            DataIntegrityException err = DataIntegrityException.fromThrowable(ex);
            if (err != null) {
                throw err;
            } else {
                throw ex;
            }
        } catch (NamingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @SuppressWarnings("all")
    public <R> @Nullable R nullableEval(Function<SessionToken<DSLContext>, @Nullable R> func) {
        Optional<R> optionalValue = this.<Optional<R>>eval(token -> {
            @Nullable R value = func.apply(token);
            return Optional.ofNullable(value);
        });
        return optionalValue.orElse(null);
    }

    @Override
    public void exec(Consumer<SessionToken<DSLContext>> func) {
        this.<Object>eval((SessionToken<DSLContext> c) -> {
            func.accept(c);
            return new Object(); // Hack to satisfy null safety
        });
    }
}
