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
package fi.ilmoeuro.membertrack.paging.ui;

import fi.ilmoeuro.membertrack.paging.Pageable;
import fi.ilmoeuro.membertrack.ui.MtLabel;
import fi.ilmoeuro.membertrack.ui.MtLink;
import fi.ilmoeuro.membertrack.ui.MtRefreshingView;
import java.io.Serializable;
import java.util.Iterator;
import java.util.stream.IntStream;
import lombok.Value;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.resource.PackageResourceReference;

public class Pager extends Panel {
    private static final long serialVersionUID = 1l;

    private static @Value class Page implements Serializable {
        private static final long serialVersionUID = 0l;
        int pageNum;

        public int getUiPageNum() {
            return this.pageNum + 1;
        }
    }

    private final IModel<? extends Pageable> model;

    public Pager(
        String id,
        IModel<? extends Pageable> model
    ) {
        super(id, new PropertyModel<>(model, id));
        this.model = model;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(
            new MtRefreshingView<>(
                "items",
                this::populatePageItem,
                this::pageStream));
    }

    private void populatePageItem(Item<Page> item) {
        setSelectedClass(item);
        MtLink link = new MtLink("setPage", () -> {
            model.getObject().setCurrentPage(
                item.getModelObject().getPageNum());
        });
        link.add(new MtLabel("uiPageNum", item.getModel()));
        item.add(link);
    }

    private void setSelectedClass(Item<Page> item) {
        if (item.getModelObject().getPageNum() == model.getObject().getCurrentPage()) {
            item.add(
                AttributeModifier.append(
                    "class",
                    " pure-menu-selected"));
        }
    }

    private Iterator<IModel<Page>> pageStream() {
        return IntStream
            .range(0, model.getObject().getNumPages())
            .mapToObj(this::buildPageModel)
            .iterator();
    }

    private IModel<Page> buildPageModel(int pageNum) {
        return Model.<Page>of(new Page(pageNum));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        PackageResourceReference cssRef = 
            new PackageResourceReference(Pager.class, "Pager.css");
        CssHeaderItem pageCss = CssHeaderItem.forReference(cssRef);
        response.render(pageCss);
    }
}
