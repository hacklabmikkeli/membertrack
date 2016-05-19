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
package fi.ilmoeuro.membertrack.plumbing;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.IConverter;

/**
 *
 * @author Ilmo Euro <ilmo.euro@gmail.com>
 */
public class WicketDateConverter implements IConverter<LocalDate> {

    @Override
    public LocalDate convertToObject(String val, Locale loc) throws ConversionException {
        try {
            return DateTimeFormatter.ISO_LOCAL_DATE.parse(val, LocalDate::from);
        } catch (RuntimeException e) {
            String message = String.format(
                "Couldn't convert \"%s\" to date",
                val);
            if (e.getMessage() != null) {
                message = e.getMessage();
            }
            throw new ConversionException(message, e);
        }
    }

    @Override
    public String convertToString(LocalDate date, Locale loc) {
        return DateTimeFormatter.ISO_LOCAL_DATE.format(date);
    }
}
