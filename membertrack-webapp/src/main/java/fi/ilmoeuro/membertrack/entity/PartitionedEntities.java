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
package fi.ilmoeuro.membertrack.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Value;

public final @Value class PartitionedEntities<T> {
    List<T> fresh;
    List<Entity<T>> existing;
    List<Integer> existingIds;

    public PartitionedEntities(Collection<Entity<T>> entities) {
        fresh = new ArrayList<>();
        existing = new ArrayList<>();
        existingIds = new ArrayList<>();

        for (Entity<T> entity : entities) {
            if (entity.isFresh()) {
                fresh.add(entity.getValue());
            } else {
                existing.add(entity);
                existingIds.add(entity.getId());
            }
        }
    }
}
