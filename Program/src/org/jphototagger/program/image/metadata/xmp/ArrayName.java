/*
 * @(#)ArrayName.java    Created on 2010-01-27
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

package org.jphototagger.program.image.metadata.xmp;

import com.adobe.xmp.options.PropertyOptions;

import org.jphototagger.program.database.metadata.mapping
    .XmpColumnXmpDataTypeMapping.XmpValueType;

/**
 *
 *
 * @author Elmar Baumann
 */
public enum ArrayName {
    LR_HIERARCHICAL_SUBJECTS("lr:hierarchicalSubject", Namespace.LIGHTROOM,
                             XmpValueType.BAG_TEXT),
    ;

    private final String       name;
    private final Namespace    namesapce;
    private final XmpValueType valueType;

    private ArrayName(String name, Namespace namesapce,
                      XmpValueType valueType) {
        this.name      = name;
        this.namesapce = namesapce;
        this.valueType = valueType;
    }

    public Namespace getNamesapce() {
        return namesapce;
    }

    public String getName() {
        return name;
    }

    public XmpValueType getValueType() {
        return valueType;
    }

    public PropertyOptions getArrayPropertyOptions() {
        if (valueType.equals(XmpValueType.BAG_TEXT)) {
            return new PropertyOptions().setArray(true);
        } else if (valueType.equals(XmpValueType.SEQ_PROPER_NAME)) {
            return new PropertyOptions().setArrayOrdered(true);
        } else if (valueType.equals(XmpValueType.LANG_ALT)) {
            return new PropertyOptions().setArrayAlternate(true);
        } else {
            assert false : valueType;

            return null;
        }
    }
}
