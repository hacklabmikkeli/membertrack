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
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.service.SubscriptionPeriod;
import fi.ilmoeuro.membertrack.ui.MtButton;
import fi.ilmoeuro.membertrack.ui.MtForm;
import fi.ilmoeuro.membertrack.ui.MtLabel;
import fi.ilmoeuro.membertrack.ui.MtLink;
import fi.ilmoeuro.membertrack.ui.MtListView;
import fi.ilmoeuro.membertrack.ui.MtTextField;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jooq.DSLContext;

@Slf4j
public class MembershipEditor extends Panel {
    private static final long serialVersionUID = 3l;

    private final IModel<Membership> model;
    private final IModel<MembershipsPageModel<DSLContext>> rootModel;

    private final MtLink closeLink;
    private final FeedbackPanel feedbackPanel;

    private final MtForm<Membership> personEditor; 
    private final MtTextField<String> personFullNameField;
    private final FormComponentLabel personFullNameLabel;
    private final MtTextField<String> personEmailField;
    private final FormComponentLabel personEmailLabel;

    private final MtListView<PhoneNumber> numbersSection;
    private final MtButton addNumber;

    private final MtListView<Subscription> subscriptionsSection;

    private final MtButton saveButton;
    private final MtButton deleteButton;

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

        feedbackPanel = new FeedbackPanel("feedbackPanel");
        closeLink = new MtLink("closeLink", this::close);

        personEditor = new MtForm<>("personEditor", model);
        personFullNameField = new MtTextField<>("person.fullName", model);
        personFullNameField.setRequired(true);
        personFullNameLabel = new FormComponentLabel("person.fullName.label",
                                                     personFullNameField);
        personEmailField = new MtTextField<>("person.email", model);
        personEmailField.setRequired(true);
        personEmailLabel = new FormComponentLabel("person.email.label",
                                                  personEmailField);

        numbersSection = new MtListView<>(
            "phoneNumbers",
            model,
            (ListItem<PhoneNumber> item) -> {
                MtTextField<String> numberField
                    = new MtTextField<>("phoneNumber", item);
                numberField.setRequired(true);
                MtButton deleteNumber = new MtButton("deleteNumber", () ->
                    deletePhoneNumber(item));
                if (item.getModelObject().getDeleted()) {
                    item.setVisible(false);
                }
                item.add(numberField);
                item.add(deleteNumber);
            });
        addNumber = new MtButton("addNumber", this::newPhoneNumber);

        subscriptionsSection = new MtListView<>(
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
                        MtTextField<LocalDate> startDateField =
                            new MtTextField<>("startDate", prdItem);
                        MtTextField<Integer> lengthField =
                            new MtTextField<>("length", prdItem);
                        MtTextField<Double> paymentField =
                            new MtTextField<>("paymentFormatted", prdItem);
                        DropDownChoice<PeriodTimeUnit> lengthUnitField =
                            new DropDownChoice<PeriodTimeUnit>(
                                "lengthUnit",
                                new PropertyModel<PeriodTimeUnit>(
                                    prdItem.getModel(),
                                    "lengthUnit"),
                                new PropertyModel<List<PeriodTimeUnit>>(
                                    prdItem.getModel(),
                                    "possibleLengthUnits"));
                        CheckBox approvedField = new CheckBox(
                                "approved",
                                new PropertyModel<>(
                                    prdItem.getModel(),
                                    "approved"));

                        MtButton deletePeriod =
                            new MtButton(
                                "deletePeriod", 
                                () -> deleteSubscriptionPeriod(prdItem));

                        startDateField.setRequired(true);
                        lengthField.setRequired(true);
                        paymentField.setRequired(true);
                        lengthUnitField.setRequired(true);
                        approvedField.setRequired(true);
                        prdItem.add(startDateField);
                        prdItem.add(lengthField);
                        prdItem.add(paymentField);
                        prdItem.add(lengthUnitField);
                        prdItem.add(approvedField);
                        prdItem.add(deletePeriod);
                    });
                MtButton addPeriod = new MtButton(
                    "addPeriod",
                    () -> newSubscriptionPeriod(subItem));
                subItem.add(legend);
                subItem.add(periodsSection);
                subItem.add(addPeriod);
            });

        saveButton = new MtButton("saveButton", this::save);
        deleteButton = new MtButton("deleteButton", this::delete);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        personEditor.add(personFullNameField);
        personEditor.add(personFullNameLabel);
        personEditor.add(personEmailField);
        personEditor.add(personEmailLabel);

        personEditor.add(numbersSection);
        personEditor.add(addNumber);

        personEditor.add(subscriptionsSection);

        personEditor.add(saveButton);
        personEditor.setDefaultButton(saveButton);

        personEditor.add(deleteButton);

        add(personEditor);
        add(closeLink);
        add(feedbackPanel);
    }

    @Override
    public void onConfigure() {
        super.onConfigure();

        if (model.getObject() != null
            && !model.getObject().isDeleted()) {
            setVisible(true);
        } else {
            setVisible(false);
        }
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

    private void delete() {
        model.getObject().delete();
    }

    private void newPhoneNumber() {
        model.getObject().addPhoneNumber();
    }

    private void newSubscriptionPeriod(ListItem<Subscription> li) {
        li.getModelObject().addPeriod();
    }

    private void deleteSubscriptionPeriod(ListItem<SubscriptionPeriod> li) {
        li.getModelObject().delete();
    }

    private void close() {
        model.setObject(null);
    }

    private void deletePhoneNumber(ListItem<PhoneNumber> li) {
        li.getModelObject().delete();
    }
}
