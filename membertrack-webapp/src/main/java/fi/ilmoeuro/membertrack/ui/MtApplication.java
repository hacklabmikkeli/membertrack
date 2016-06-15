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
package fi.ilmoeuro.membertrack.ui;

import de.agilecoders.wicket.webjars.WicketWebjars;
import de.agilecoders.wicket.webjars.settings.WebjarsSettings;
import fi.ilmoeuro.membertrack.config.Config;
import fi.ilmoeuro.membertrack.db.DataSourceInitializer;
import fi.ilmoeuro.membertrack.db.DatabaseInitializer;
import fi.ilmoeuro.membertrack.db.DebugServer;
import fi.ilmoeuro.membertrack.db.exampledata.DefaultExampleData;
import fi.ilmoeuro.membertrack.membership.ui.MembershipsPage;
import fi.ilmoeuro.membertrack.plumbing.WicketDateConverter;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.SessionToken;
import fi.ilmoeuro.membertrack.session.UnitOfWorkFactory;
import fi.ilmoeuro.membertrack.session.db.DbSessionRunner;
import fi.ilmoeuro.membertrack.session.db.DbUnitOfWorkFactory;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.Application;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.markup.html.WebPage;
import org.jooq.DSLContext;

@Slf4j
public final class MtApplication extends AuthenticatedWebApplication {

    @Getter
    private final Config config;
    @Getter
    private final SessionRunner<DSLContext> sessionRunner;

    private final UnitOfWorkFactory<DSLContext> uowFactory;
    private final DataSourceInitializer dsInitializer;
    private final DatabaseInitializer<DSLContext> dbInitializer;
    private final DebugServer debugServer;

    public MtApplication() throws FileNotFoundException {
        config = Config.load();
        sessionRunner
            = new DbSessionRunner(config.getSessionRunner());
        uowFactory
            = new DbUnitOfWorkFactory();
        dsInitializer
            = new DataSourceInitializer(config.getDataSourceInitializer());
        dbInitializer
            = new DatabaseInitializer<>(
                config.getDatabaseInitializer(),
                new DefaultExampleData<>(
                    config.getDefaultExampleData(),
                    uowFactory));
        debugServer
            = new DebugServer(config.getDebugServer());
    }

    public MtApplication(
        Config config,
        SessionRunner<DSLContext> sessionRunner,
        UnitOfWorkFactory<DSLContext> uowFactory,
        DataSourceInitializer dsInitializer,
        DatabaseInitializer<DSLContext> dbInitializer,
        DebugServer debugServer
    ) {
        this.config = config;
        this.sessionRunner = sessionRunner;
        this.uowFactory = uowFactory;
        this.dsInitializer = dsInitializer;
        this.dbInitializer = dbInitializer;
        this.debugServer = debugServer;
    }

    @Override
    public void init() {
        super.init();
        dsInitializer.init();
        sessionRunner.exec((SessionToken<DSLContext> token) -> {
            dbInitializer.init(token);
        });
        debugServer.start();

        WebjarsSettings webjarsSettings = new WebjarsSettings();
        WicketWebjars.install(this, webjarsSettings);

        getMarkupSettings().setStripWicketTags(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        debugServer.stop();
    }

    @SuppressWarnings("unchecked")
    public static MtApplication get() {
        try {
            return (MtApplication) Application.get();
        } catch (ClassCastException ex) {
            String error = "Called get() from wrong app";
            log.error(error, ex);
            throw new RuntimeException(error, ex);
        }
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator locator = new ConverterLocator();
        locator.set(LocalDate.class, new WicketDateConverter());
        return locator;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return MembershipsPage.class;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return MtSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return MtSignInPage.class;
    }
}
