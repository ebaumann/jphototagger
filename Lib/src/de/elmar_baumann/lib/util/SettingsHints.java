/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.lib.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Hints for the class {@link Settings}.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class SettingsHints {

    private final List<String> excludedMembers = new ArrayList<String>();
    private final EnumSet<Option> options;

    public enum Option {

        NONE,
        /**
         * The content of {@link javax.swing.JComboBox}es shall be written to
         * the properties
         */
        SET_COMBOBOX_CONTENT,
        /**
         * The content of {@link javax.swing.JComboBox}es shall <em>not</em> be
         * written to the properties
         */
        DONT_SET_COMBOBOX_CONTENT,
        /**
         * The content of {@link javax.swing.JList}s shall be written to the
         * properties
         */
        SET_LIST_CONTENT,
        /**
         * The content of {@link javax.swing.JList}s shall <em>not</em> be
         * written to the properties
         */
        DONT_SET_LIST_CONTENT,
        /**
         * The tabbed pane contents shall be written to the properties
         */
        SET_TABBED_PANE_CONTENT,
        /**
         * The tabbed pane contents shall <em>not</em> be written to the
         * properties
         */
        DONT_SET_TABBED_PANE_CONTENT,
    }

    /**
     * Creates a new instance with {@link Option#NONE}.
     */
    public SettingsHints() {
        this.options = EnumSet.of(Option.NONE);
    }

    /**
     * Creates a new instance with specific options.
     *
     * @param options options
     */
    public SettingsHints(EnumSet<Option> options) {
        this.options = options;
    }

    /**
     * Fügt ein Attribut hinzu, dessen Inhalt <em>nicht</em> persistent gespeichert
     * werden soll.
     *
     * @param member Attribut, genauer Pfad, z.B.
     *               <code>de.elmar_baumann.jpt.view.AppPanel.tableXmp</code>
     *               Default: Kein Attribut ist ausgeschlossen.
     */
    public void addExclude(String member) {
        if (member == null)
            throw new NullPointerException("member == null");

        excludedMembers.add(member);
    }

    /**
     * Liefert, ob der Inhalt eines Attributs gespeichert werden soll. Diese
     * Operation ist invers zu
     * {@link PersistentSettingsHints#isExcludedMember(java.lang.String)}.
     *
     * @param member Attribut, genauer Pfad, z.B.
     *               <code>de.elmar_baumann.jpt.view.AppPanel.tableXmp</code>
     * @return       true, wenn der Inhalt des Attributs gespeichert werden soll
     */
    boolean isSet(String member) {
        if (member == null)
            throw new NullPointerException("member == null");

        return !isExclude(member);
    }

    boolean isOption(Option option) {
        return options.contains(option);
    }

    private boolean isExclude(String member) {
        assert member != null : member;

        return excludedMembers.contains(member);
    }
}
