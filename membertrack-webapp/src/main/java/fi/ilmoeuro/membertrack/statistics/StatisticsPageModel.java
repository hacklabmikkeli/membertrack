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
package fi.ilmoeuro.membertrack.statistics;

import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.Services;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriods;
import fi.ilmoeuro.membertrack.util.Refreshable;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.util.StateExternalizable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.tuple.Tuple3;

@Slf4j
@RequiredArgsConstructor
public final class
    StatisticsPageModel<SessionTokenType>
implements
    Refreshable,
    Serializable,
    StateExternalizable
{
    private static final long serialVersionUID = 1l;

    public static @Value class ServiceStatisticsDataPoint implements Serializable {
        private static final long serialVersionUID = 0l;
        Service service;
        int numSubscribers;
    }

    public static @Value class ServiceStatisticsDataRow implements Serializable {
        private static final long serialVersionUID = 0l;
        int year;
        int month;
        List<ServiceStatisticsDataPoint> dataPoints;
    }

    public static @Value class ServiceStatisticsDataSet implements Serializable {
        private static final long serialVersionUID = 0l;
        List<Service> header;
        List<ServiceStatisticsDataRow> dataRows;
    }

    @Getter
    private ServiceStatisticsDataSet dataSet
        = new ServiceStatisticsDataSet(
            Collections.emptyList(),
            Collections.emptyList()
    );

    private final SessionRunner<SessionTokenType> sessionRunner;
    private final Services.Factory<SessionTokenType> sFactory;
    private final SubscriptionPeriods.Factory<SessionTokenType> spsFactory;

    @Override
    public void refresh() {
        Map<Tuple3<Integer, Integer, UUID>, Integer> dataPoints
            = new HashMap<>();
        LocalDate startDate
            = LocalDate.now()
                       .minusMonths(11)
                       .withDayOfMonth(1);
        List<Service> ss = sessionRunner.eval(token -> 
            sFactory
                .create(token)
                .listServices()
        );
        List<SubscriptionPeriod> sps = sessionRunner.eval(token ->
            spsFactory
                .create(token)
                .listSubscriptionPeriodsAfter(startDate));
        for (SubscriptionPeriod sp: sps) {
            for (int i = 0; i < 12; i++) {
                LocalDate probeDate = startDate.plusMonths(i);
                if (sp.containsDate(probeDate)) {
                    dataPoints.merge(
                        new Tuple3<>(
                            probeDate.getYear(),
                            probeDate.getMonthValue(),
                            sp.getServiceId()),
                        1,
                        (v1, v2) -> v1 + v2);
                }
            }
        }
        List<ServiceStatisticsDataRow> rows = new ArrayList<>(12);

        for (int i = 0; i < 12; i++) {
            LocalDate probeDate = startDate.plusMonths(i);
            List<ServiceStatisticsDataPoint> points = new ArrayList<>();
            for (Service s: ss) {
                points.add(new ServiceStatisticsDataPoint(
                    s,
                    dataPoints.getOrDefault(
                        new Tuple3<>(
                            probeDate.getYear(),
                            probeDate.getMonthValue(),
                            s.getId()
                        ),
                        0)));
            }
            rows.add(new ServiceStatisticsDataRow(
                probeDate.getYear(), 
                probeDate.getMonthValue(),
                points));
        }

        dataSet = new ServiceStatisticsDataSet(ss, rows);
    }

    @Override
    public void saveState(BiConsumer<String, String> pairConsumer) {
    }

    @Override
    public void loadState(Function<String, String> getValue) {
    }
}
