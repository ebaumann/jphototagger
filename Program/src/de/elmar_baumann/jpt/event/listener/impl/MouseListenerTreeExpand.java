/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.event.listener.impl;

import de.elmar_baumann.jpt.resource.JptBundle;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-19
 */
public final class MouseListenerTreeExpand
        extends    MouseListenerTree
        implements ActionListener {

    private final JPopupMenu popupMenu    = new JPopupMenu();
    private final JMenuItem  itemExpand   = new JMenuItem(JptBundle.INSTANCE.getString("MouseListenerTreeExpand.ItemExpand"));
    private final JMenuItem  itemCollapse = new JMenuItem(JptBundle.INSTANCE.getString("MouseListenerTreeExpand.ItemCollapse"));

    public MouseListenerTreeExpand() {
        popupMenu.add(itemExpand);
        popupMenu.add(itemCollapse);
        listenExpandAllSubItems  (itemExpand  , true);
        listenCollapseAllSubItems(itemCollapse, true);
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    @Override
    protected void popupTrigger(JTree tree, TreePath path, int x, int y) {
        popupMenu.show(tree, x, y);
    }
}
