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

import fi.ilmoeuro.membertrack.config.ConfigProvider;
import fi.ilmoeuro.membertrack.config.TypesafeConfigProvider;
import fi.ilmoeuro.membertrack.membership.ui.MembershipsPage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

@Slf4j
public class MembertrackApplication extends WebApplication {

    @Getter
    private final ConfigProvider configProvider;

    public MembertrackApplication() {
        configProvider = new TypesafeConfigProvider();
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return MembershipsPage.class;
    }

    public static MembertrackApplication get() {
        try {
            return (MembertrackApplication) Application.get();
        } catch (ClassCastException ex) {
            String error = "Called get() from wrong app";
            log.error(error, ex);
            throw new RuntimeException(error, ex);
        }
    }
}
