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
package fi.ilmoeuro.membertrack.membership.db;

import fi.ilmoeuro.membertrack.membership.MembershipRepository;
import fi.ilmoeuro.membertrack.membership.MembershipRepositoryFactory;
import fi.ilmoeuro.membertrack.session.SessionToken;
import org.jooq.DSLContext;

public final class DbMembershipRepositoryFactory
implements
    MembershipRepositoryFactory<DSLContext>
{
    private static final long serialVersionUID = 0l;

    @Override
    public MembershipRepository create(SessionToken<DSLContext> token) {
        return new DbMembershipRepository(token.getValue());
    }
}