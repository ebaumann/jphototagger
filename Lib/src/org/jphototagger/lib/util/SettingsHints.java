/*
 * @(#)SettingsHints.java    Created on 2008-10-05
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

package org.jphototagger.lib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Hints for the class {@link Settings}.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public final class SettingsHints {
    private final List<String> excludedKeys = new ArrayList<String>();
    private final Set<Option>  options;

    public enum Option {

        /**
         * All components of the tabbed pane shall be set recursively.
         * Default: false.
         */
        SET_TABBED_PANE_CONTENT,
    }

    public SettingsHints() {
        options = EnumSet.noneOf(Option.class);
    }

    public SettingsHints(Option... options) {
        if (options == null) {
            throw new NullPointerException("options == null");
        }

        this.options = (options.length == 0)
                       ? EnumSet.noneOf(Option.class)
                       : EnumSet.copyOf(Arrays.asList(options));
    }

    public void addKeyToExclude(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        excludedKeys.add(key);
    }

    public void removeKeyFromExclude(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        excludedKeys.remove(key);
    }

    boolean isSet(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        return !excludedKeys.contains(key);
    }

    boolean isOption(Option option) {
        if (option == null) {
            throw new NullPointerException("option == null");
        }

        return options.contains(option);
    }
}
