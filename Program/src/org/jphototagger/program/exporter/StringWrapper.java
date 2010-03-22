/*
 * @(#)StringWrapper.java    Created on 2010-03-04
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.exporter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 *
 * @author  Elmar Baumann
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class StringWrapper {
    private String string;

    public StringWrapper() {}

    public StringWrapper(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public static List<StringWrapper> getWrappedStrings(
            Collection<? extends String> strings) {
        List<StringWrapper> wrapped =
            new ArrayList<StringWrapper>(strings.size());

        for (String string : strings) {
            wrapped.add(new StringWrapper(string));
        }

        return wrapped;
    }
}
