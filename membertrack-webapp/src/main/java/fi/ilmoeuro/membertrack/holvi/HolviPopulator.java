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
package fi.ilmoeuro.membertrack.holvi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.ilmoeuro.membertrack.db.DataIntegrityException;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.person.Persons;
import fi.ilmoeuro.membertrack.service.PeriodTimeUnit;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.Services;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.fluent.Request;
import org.checkerframework.checker.nullness.qual.Nullable;

@RequiredArgsConstructor
@Slf4j
public final class
    HolviPopulator<SessionTokenType>
implements
    Serializable
{
    private static final long serialVersionUID = 0l;

    public static final class ServiceNotFoundException extends RuntimeException {
        public ServiceNotFoundException(UUID serviceUUID) {
            super(String.format("Service %s not found", serviceUUID));
        }
    }

    public static final class PersonNotFoundException extends RuntimeException {
        public PersonNotFoundException(String personEmail) {
            super(String.format("Person with email %s not found", personEmail));
        }
    }
    
    public static final @Data class ProductMapping implements Serializable {
        private static final long serialVersionUID = 1l;

        String productCode;
        UUID serviceUUID;
        int length;
        PeriodTimeUnit timeUnit;
        double payment;
    }
    
    public static final @Data class Config implements Serializable {
        private static final long serialVersionUID = 0l;
        boolean enabled;
        String authToken;
        String poolHandle;
        int interval;
        List<ProductMapping> productMappings;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final @Data class Order {
        String email;
        String firstname;
        String lastname;
        String code;
        ZonedDateTime paid_time;
        List<Purchase> purchases;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final @Data class Purchase {
        String product;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final @Data class OrdersResult {
        String next;
        List<Order> results;
    }
    
    private final Config config;
    private final SessionRunner<SessionTokenType> sessionRunner;
    private final UnitOfWork.Factory<SessionTokenType> uowFactory;
    private final Persons.Factory<SessionTokenType> personsFactory;
    private final Services.Factory<SessionTokenType> servicesFactory;
    private final ObjectMapper objectMapper;

    public void runPopulator() throws IOException {
        if (!config.isEnabled()) {
            return;
        }

        String url = String.format(
            "https://holvi.com/api/checkout/v2/pool/%s/order/",
            config.getPoolHandle());
        while (url != null) {
            try (InputStream data = Request.Get(url)
                .setHeader(
                    "Authorization",
                        String.format("Token %s", config.getAuthToken()))
                .execute()
                .returnContent()
                .asStream())
            {
                OrdersResult orders = objectMapper.readValue(data, OrdersResult.class);
                handleOrders(orders);
                url = orders.getNext();
            }
        }
    }

    private void handleOrders(OrdersResult orders) {
        for (Order order : orders.getResults()) {
            try {
                createPerson(order);
            } catch (DataIntegrityException ex) {
                if ("person_u_email".equals(ex.getIntegrityConstraint())) {
                    // person already exists
                } else {
                    throw ex;
                }
            }

            Person person = findPersonByEmail(order);
            int i = 0;
            for (Purchase purchase : order.getPurchases()) {
                ProductMapping mapping = findProductMapping(purchase.getProduct());
                if (mapping == null) {
                    // non-service product
                } else {
                    try {
                        createSubscriptionPeriod(mapping, i, person, order);
                    } catch (DataIntegrityException e) {
                        if ("subscription_period_holvi_handle_u_ph_oh_in"
                            .equals(e.getIntegrityConstraint())) {
                            // period already exists, skip
                        } else {
                            throw e;
                        }
                    }
                }
                i++;
            }
        }

    }

    private void createSubscriptionPeriod(
        ProductMapping mapping,
        int i,
        Person person,
        Order order
    ) {
        sessionRunner.exec(token -> {
            Services services = servicesFactory.create(token);
            Service service = services.findById(mapping.getServiceUUID());
            if (service == null) {
                throw new ServiceNotFoundException(mapping.getServiceUUID());
            }
            
            double payment = mapping.getPayment();
            int euros = (int)(Math.floor(payment));
            int cents = (int)((payment - euros) * 100);
            SubscriptionPeriod sp = new SubscriptionPeriod(
                    service,
                    person,
                    order
                            .getPaid_time()
                            .withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDate(),
                    mapping.getTimeUnit(),
                    mapping.getLength(),
                    euros * 100 + cents,
                    false);
            SubscriptionPeriodHolviHandle hh = new SubscriptionPeriodHolviHandle(
                    sp,
                    config.getPoolHandle(),
                    order.getCode(),
                    i);
            
            UnitOfWork uow = uowFactory.create(token);
            uow.addEntity(sp);
            uow.addEntity(hh);
            uow.execute();
        });
    }

    private Person findPersonByEmail(final Order order) {
        @Nullable Person person = sessionRunner.nullableEval(token -> {
            Persons persons = personsFactory.create(token);
            return persons.findByEmail(order.getEmail());
        });
        if (person != null) {
            return person;
        } else {
            throw new PersonNotFoundException(order.getEmail());
        }
    }

    private void createPerson(final Order order) {
        sessionRunner.exec(token -> {
            Person newPerson = new Person(
                    order.getFirstname() + " " + order.getLastname(),
                    order.getEmail());
            
            UnitOfWork uow = uowFactory.create(token);
            uow.addEntity(newPerson);
            uow.execute();
        });
    }
    
    private @Nullable ProductMapping findProductMapping(String productCode) {
        for (ProductMapping productMapping : config.getProductMappings()) {
            if (Objects.equals(productMapping.getProductCode(), productCode)) {
                return productMapping;
            }
        }

        return null;
    }
}
