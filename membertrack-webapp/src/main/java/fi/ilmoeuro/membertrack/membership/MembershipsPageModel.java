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
package fi.ilmoeuro.membertrack.membership;

import fi.ilmoeuro.membertrack.auth.PasswordResetManager;
import fi.ilmoeuro.membertrack.person.Accounts;
import fi.ilmoeuro.membertrack.person.Person;
import fi.ilmoeuro.membertrack.service.Service;
import fi.ilmoeuro.membertrack.service.Services;
import fi.ilmoeuro.membertrack.service.Subscription;
import fi.ilmoeuro.membertrack.util.Refreshable;
import fi.ilmoeuro.membertrack.session.SessionRunner;
import fi.ilmoeuro.membertrack.session.UnitOfWork;
import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import fi.ilmoeuro.membertrack.util.PageParamsSaveLoad;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Slf4j
public final class
    MembershipsPageModel<SessionTokenType>
implements
    Refreshable,
    Serializable,
    PageParamsSaveLoad
{
    private static final long serialVersionUID = 1l;

    public static enum Editor {
        NONE,
        MEMBERSHIP,
        ACCOUNT
    }

    private final SessionRunner<SessionTokenType> sessionRunner;
    private final Services.Factory<SessionTokenType> srf;

    @Getter
    private @Nullable Membership membership;

    @Getter
    private final MembershipBrowser<SessionTokenType> membershipBrowser;

    @Getter
    private final MembershipEditor<SessionTokenType> membershipEditor;

    @Getter
    private final AccountEditor<SessionTokenType> accountEditor;

    @Getter
    @Setter
    private Editor currentEditor = Editor.NONE;

    // OK to register callbacks not called until later
    @SuppressWarnings("initialization")
    public MembershipsPageModel(
        PasswordResetManager pwResetManager,
        Memberships.Factory<SessionTokenType> mrf,
        Services.Factory<SessionTokenType> srf,
        Accounts.Factory<SessionTokenType> af,
        UnitOfWork.Factory<SessionTokenType> uowFactory,
        SessionRunner<SessionTokenType> sessionRunner
    ) {
        this.sessionRunner = sessionRunner;
        this.srf = srf;
        
        this.membershipEditor = new MembershipEditor<>(
            srf,
            uowFactory,
            sessionRunner,
            this::refresh,
            this::close);

        this.accountEditor = new AccountEditor<>(
            af,
            uowFactory,
            sessionRunner,
            pwResetManager);

        this.membershipBrowser = new MembershipBrowser<>(
            mrf,
            sessionRunner,
            this::setMembership,
            this::getMembership);
    }

    public void setMembership(@Nullable Membership membership) {
        this.membership = membership;
        membershipEditor.setMembership(membership);
        if (membership != null) {
            accountEditor.setPerson(membership.getPerson());
        }

        if (membership == null) {
            currentEditor = Editor.NONE;
        } else {
            currentEditor = Editor.MEMBERSHIP;
        }
    }

    @Override
    public void refresh() {
        membershipBrowser.refresh();
        accountEditor.refresh();
    }

    public void close() {
        setMembership(null);
    }

    private int getCurrentPage() {
        return membershipBrowser.getCurrentPage();
    }

    private void setCurrentPage(int pageNum) {
        membershipBrowser.setCurrentPage(pageNum);
        setMembership(null);
    }

    public void createNewMembership() {
        sessionRunner.exec(token -> {
            final Services sr = srf.create(token);
            final Person person = Person.empty();
            final List<Service> services = sr.listServices();
            final List<Subscription> subs = services
                .stream()
                .map(serv -> new Subscription(person, serv, new ArrayList<>()))
                .collect(Collectors.<@NonNull Subscription>toList());

            setMembership(
                new Membership(
                    person,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    subs));
        });
    }

    @Override
    public void saveState(BiConsumer<String, @Nullable String> pairConsumer) {
        pairConsumer.accept("page", String.valueOf(getCurrentPage()));
        Membership selected = membershipEditor.getMembership();
        if (selected != null) {
            pairConsumer.accept("selected", selected.getPerson().getEmail());
        } else {
            pairConsumer.accept("selected", null);
        }
        if (currentEditor != Editor.NONE) {
            pairConsumer.accept(
                "editor",
                currentEditor
                    .name()
                    .toLowerCase(Locale.ROOT));
        } else {
            pairConsumer.accept("editor", null);
        }
        String searchString = membershipBrowser.getSearchString();
        if (StringUtils.isBlank(searchString)) {
            pairConsumer.accept("search", null);
        } else {
            pairConsumer.accept("search", searchString);
        }
    }

    @Override
    public void loadState(Function<String, @Nullable String> getValue) {
        setCurrentPage(NumberUtils.toInt(getValue.apply("page"), 1));
        refresh();
        String selected = getValue.apply("selected");
        String editor = getValue.apply("editor");
        String search = getValue.apply("search");
        if (selected != null) {
            for (Membership ms : membershipBrowser.getMemberships()) {
                if (ms.getPerson().getEmail().equals(selected)) {
                    setMembership(ms);
                }
            }
        }
        if (editor != null) {
            try {
                Editor enumValue = Editor.valueOf(editor.toUpperCase(Locale.ROOT));
                setCurrentEditor(enumValue);
            } catch (IllegalArgumentException ex) {
                log.error("Invalid Editor enum value: {}", editor);
            }
        }
        if (search != null) {
            membershipBrowser.setSearchString(search);
            membershipBrowser.refresh();
        }
    }
}
