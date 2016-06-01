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
package fi.ilmoeuro.membertrack.plumbing;

import org.jooq.util.GeneratorStrategy;
import org.jooq.util.JavaGenerator;
import org.jooq.util.JavaWriter;
import org.jooq.util.TableDefinition;

public class JooqGenerator extends JavaGenerator {

    @Override
    protected void generatePojoClassFooter(
            TableDefinition table,
            JavaWriter out
    ) {
        String className = this.getStrategy().getJavaClassName(
                table,
                GeneratorStrategy.Mode.POJO);
        out.tab(1).println("@Override");
        out.tab(1).println("public boolean equals(Object obj) {");
        out.tab(2).println("return obj != null &&");
        out.tab(2).println("obj instanceof %s &&",
                className);
        out.tab(2).println("this.getId().equals(((%s)obj).getId());",
                className);
        out.tab(1).println("}");
        out.println();
        out.tab(1).println("@Override");
        out.tab(1).println("public int hashCode() {");
        out.tab(2).println("return getId().hashCode();");
        out.tab(1).println("}");
        out.println();
        out.tab(1).println("@Override");
        out.tab(1).println("public String toString() {");
        out.tab(2).println("if (isDeleted()) {");
        out.tab(3).println("return \"<deleted>\";");
        out.tab(2).println("} else {");
        out.tab(3).println("return String.format(");
        out.tab(4).println("\"%s(id=%s)\",");
        out.tab(4).println("getClass().getSimpleName(),");
        out.tab(4).println("getId());");
        out.tab(2).println("}");
        out.tab(1).println("}");
        out.println();
        out.tab(1).println("private boolean deleted;");
        out.println();
        out.tab(1).println("public boolean isDeleted() {");
        out.tab(2).println("return deleted;");
        out.tab(1).println("}");
        out.println();
        out.tab(1).println("public void setDeleted(boolean deleted) {");
        out.tab(2).println("this.deleted = deleted;");
        out.tab(1).println("}");

    }
    
}
