/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.controller.hierarchicalkeywords;

import de.elmar_baumann.jpt.helper.InsertHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/**
 * Inserts the (flat) keywords and categories into the hierarchical keywords
 * root.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-02
 */
public final class ControllerInsertIntoHierarchicalKeywords
        implements ActionListener {

    private final JMenuItem menuItemCategories = GUI.INSTANCE.getAppFrame().
            getMenuItemCopyCategoriesToHierarchicalKeywords();
    private final JMenuItem menuItemKeywords = GUI.INSTANCE.getAppFrame().
            getMenuItemCopyKeywordsToHierarchicalKeywords();

    public ControllerInsertIntoHierarchicalKeywords() {
        listen();
    }

    private void listen() {
        menuItemCategories.addActionListener(this);
        menuItemKeywords.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<String> keywords = getKeywords(e.getSource());
        if (keywords.size() > 0) {
            SwingUtilities.invokeLater(new InsertHierarchicalKeywords(keywords));
        }
    }

    private List<String> getKeywords(Object source) {
        if (source == menuItemCategories) {
            return ListUtil.toStringList(
                    GUI.INSTANCE.getAppPanel().getListCategories().getModel());
        } else if (source == menuItemKeywords) {
            return ListUtil.toStringList(
                    GUI.INSTANCE.getAppPanel().getListKeywords().getModel());
        } else {
            assert false : "Invalid source: " + source; // NOI18N
            return new ArrayList<String>();
        }
    }
}
