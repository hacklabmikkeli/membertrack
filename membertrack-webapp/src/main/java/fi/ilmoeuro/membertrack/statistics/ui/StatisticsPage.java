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
package fi.ilmoeuro.membertrack.statistics.ui;

import fi.ilmoeuro.membertrack.membership.ui.*;
import fi.ilmoeuro.membertrack.membership.Memberships;
import org.jooq.DSLContext;
import fi.ilmoeuro.membertrack.membership.MembershipsPageModel;
import fi.ilmoeuro.membertrack.paging.ui.Pager;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.Services;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriods;
import fi.ilmoeuro.membertrack.service.db.DbServices;
import fi.ilmoeuro.membertrack.service.db.DbSubscriptionPeriods;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.statistics.StatisticsPageModel;
import fi.ilmoeuro.membertrack.ui.MtActionButton;
import fi.ilmoeuro.membertrack.ui.MtApplication;
import fi.ilmoeuro.membertrack.ui.MtLabel;
import fi.ilmoeuro.membertrack.ui.MtListView;
import fi.ilmoeuro.membertrack.ui.MtModel;
import fi.ilmoeuro.membertrack.ui.MtPage;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public final class StatisticsPage extends MtPage {
    private static final long serialVersionUID = 1l;
    private final IModel<StatisticsPageModel<?>> model;
    private final Component header;
    private final Component dataRows;

    // OK to register callbacks on methods, they're not called right away
    @SuppressWarnings("initialization")
    public StatisticsPage(
    ) {
        super();

        final SubscriptionPeriods.Factory<DSLContext> sp
            = new DbSubscriptionPeriods.Factory();
        final Services.Factory<DSLContext> srf
            = new DbServices.Factory();
        final SessionRunner<DSLContext> sr
            = MtApplication.get().getSessionRunner();
        model = new MtModel<>(
            new StatisticsPageModel<DSLContext>(sr, srf, sp));
        header = new MtListView<Service> (
            "dataSet.header",
            model,
            (ListItem<Service> item) -> {
                Component title = new MtLabel("title", item);
                item.add(title);
            });
        dataRows = new MtListView<StatisticsPageModel.ServiceStatisticsDataRow> (
            "dataSet.dataRows",
            model,
            (ListItem<StatisticsPageModel.ServiceStatisticsDataRow> row) -> {
            Component year = new MtLabel("year", row);
            Component month = new MtLabel("month", row);
            Component dataPoints = new MtListView<StatisticsPageModel.ServiceStatisticsDataPoint>(
                "dataPoints",
                row,
                (ListItem<StatisticsPageModel.ServiceStatisticsDataPoint> point) -> {
                    Component numSubscribers = new MtLabel("numSubscribers", point);
                    point.add(numSubscribers);
                });
            row.add(year);
            row.add(month);
            row.add(dataPoints);
            });
    }

    @Override
    public PageParameters getPageParameters() {
        final PageParameters params = super.getPageParameters();
        if (params != null) {
            model.getObject().saveState(
                (String k, String v) -> params.set(k, v));
        }
        return params;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(header);
        add(dataRows);
    }
}
