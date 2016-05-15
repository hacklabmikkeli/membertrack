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
package fi.ilmoeuro.membertrack.membership.ui;

import fi.ilmoeuro.membertrack.membership.Membership;
import fi.ilmoeuro.membertrack.membership.MembershipsPageModel;
import fi.ilmoeuro.membertrack.person.PhoneNumber;
import fi.ilmoeuro.membertrack.service.PeriodTimeUnit;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.ui.MtButton;
import fi.ilmoeuro.membertrack.ui.MtLabel;
import fi.ilmoeuro.membertrack.ui.MtLink;
import fi.ilmoeuro.membertrack.ui.MtListView;
import fi.ilmoeuro.membertrack.ui.MtTextField;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.jooq.DSLContext;

@Slf4j
public class MembershipEditor extends Panel {
    private static final long serialVersionUID = 2l;

    private final IModel<Membership> model;
    private final IModel<MembershipsPageModel<DSLContext>> rootModel;

    private final MtLink closeLink;
    private final FeedbackPanel feedbackPanel;

    private final Form<Object> personEditor; 
    private final MtTextField<String> personFullNameField;
    private final MtTextField<String> personEmailField;

    private final MtListView<PhoneNumber> numbersSection;
    private final MtButton addNumber;

    private final MtListView<Subscription> subscriptionsSection;

    private final MtButton saveButton;



    // OK to register callbacks on methods, they're not called right away
    @SuppressWarnings("initialization")
    public MembershipEditor(
        String id,
        IModel<Membership> model,
        IModel<MembershipsPageModel<DSLContext>> rootModel
    ) {
        super(id, model);

        this.model = model;
        this.rootModel = rootModel;

        this.feedbackPanel = new FeedbackPanel("feedbackPanel");
        this.closeLink = new MtLink("closeLink", this::close);

        this.personEditor = new Form<>("personEditor");
        this.personFullNameField = new MtTextField<>("person.fullName", model);
        this.personEmailField = new MtTextField<>("person.email", model);

        this.numbersSection = new MtListView<>(
            "phoneNumbers",
            model,
            (ListItem<PhoneNumber> item) -> {
                MtTextField<String> numberField
                    = new MtTextField<>("phoneNumber", item);
                MtButton deleteNumber = new MtButton("deleteNumber", () ->
                    deletePhoneNumber(item));
                if (item.getModelObject().getDeleted()) {
                    item.setVisible(false);
                }
                item.add(numberField);
                item.add(deleteNumber);
            });
        this.addNumber = new MtButton("addNumber", this::newPhoneNumber);

        this.subscriptionsSection = new MtListView<>(
            "subscriptions",
            model,
            (ListItem<Subscription> subItem) -> {
                MtLabel legend = new MtLabel("service.title", subItem);
                MtListView<SubscriptionPeriod> periodsSection = new MtListView<>(
                    "periods",
                    subItem,
                    (ListItem<SubscriptionPeriod> prdItem) -> {
                        if (prdItem.getModelObject().getDeleted()) {
                            prdItem.setVisible(false);
                        }
                        /*
                        MtTextField<LocalDate> startDateField =
                            new MtTextField<>("startDate", prdItem);
                        */
                        MtTextField<Integer> lengthField =
                            new MtTextField<>("length", prdItem);
                        MtTextField<Integer> paymentField =
                            new MtTextField<>("payment", prdItem);
                        MtButton deleteButton =
                            new MtButton(
                                "deletePeriod", 
                                () -> deleteSubscriptionPeriod(prdItem));

                        //prdItem.add(startDateField);
                        prdItem.add(lengthField);
                        prdItem.add(paymentField);
                        prdItem.add(deleteButton);
                    });
                MtButton addPeriod = new MtButton(
                    "addPeriod",
                    () -> newSubscriptionPeriod(subItem));
                subItem.add(legend);
                subItem.add(periodsSection);
                subItem.add(addPeriod);
            });

        this.saveButton = new MtButton("saveButton", this::save);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        personEditor.add(personFullNameField);
        personEditor.add(personEmailField);

        personEditor.add(numbersSection);
        personEditor.add(addNumber);

        personEditor.add(subscriptionsSection);

        personEditor.add(saveButton);
        personEditor.setDefaultButton(saveButton);

        add(personEditor);
        add(closeLink);
        add(feedbackPanel);
    }

    @Override
    public void onConfigure() {
        super.onConfigure();

        setVisible(rootModel.getObject().getCurrentMembership() != null);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        PackageResourceReference cssRef = 
            new PackageResourceReference(MembershipEditor.class, "MembershipEditor.css");
        CssHeaderItem pageCss = CssHeaderItem.forReference(cssRef);
        response.render(pageCss);
    }

    private void save() {
        try {
            this.rootModel.getObject().saveCurrent();
        } catch (MembershipsPageModel.NonUniqueEmailException ex) {
            error("Email is already in use");
        }
    }

    private void newPhoneNumber() {
        model.getObject().addPhoneNumber();
    }

    private void newSubscriptionPeriod(ListItem<Subscription> li) {
        Subscription sub = li.getModelObject();
        sub.getPeriods().add(
            new SubscriptionPeriod(
                sub.getService().getId(),
                model.getObject().getPerson().getId(),
                LocalDate.now(),
                PeriodTimeUnit.DAY,
                0,
                0,
                false));
    }

    private void deleteSubscriptionPeriod(ListItem<SubscriptionPeriod> li) {
        li.getModelObject().setDeleted(true);
    }

    private void close() {
        rootModel.getObject().setCurrentMembership(null);
    }

    private void deletePhoneNumber(ListItem<PhoneNumber> li) {
        li.getModelObject().setDeleted(true);
    }
}
