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

package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;

import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JList;
import javax.swing.JTree;

/**
 * Kontrolliert eine Gruppe von Lists und Trees, von denen nur bei einem Tree
 * oder einer List Items selektiert sein dürfen. Die Items der anderen werden
 * deselektiert, sobald bei einem ein Item in der Gruppe selektiert wird.
 *
 * @author  Elmar Baumann
 * @version 2008-10-05
 */
public final class ControllerItemsMutualExcludeSelection
        implements TreeSelectionListener, ListSelectionListener {
    private final AppPanel    appPanel = GUI.INSTANCE.getAppPanel();
    private final List<JTree> trees    = appPanel.getSelectionTrees();
    private final List<JList> lists    = appPanel.getSelectionLists();
    private boolean           listen   = true;

    public ControllerItemsMutualExcludeSelection() {
        listen();
    }

    private void listen() {
        for (JTree tree : trees) {
            tree.addTreeSelectionListener(this);
        }

        for (JList list : lists) {
            list.addListSelectionListener(this);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Object o = e.getSource();

        if (listen && e.isAddedPath() && (o instanceof JTree)) {
            handleTreeSelected((JTree) o);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        Object o = e.getSource();

        if (listen &&!e.getValueIsAdjusting() && (o instanceof JList)) {
            JList list = (JList) o;

            if (list.getSelectedIndex() >= 0) {
                handleListSelected(list);
            }
        }
    }

    private void handleTreeSelected(JTree currentSelectedTree) {
        clearSelectionAllLists();
        clearSelectionOtherTrees(currentSelectedTree);
    }

    private void handleListSelected(JList currentSelectedList) {
        clearSelectionAllTrees();
        clearSelectionOtherLists(currentSelectedList);
    }

    private void clearSelectionOtherLists(JList list) {
        listen = false;

        for (JList aList : lists) {
            if ((aList != list) &&!aList.isSelectionEmpty()) {
                aList.clearSelection();
            }
        }

        listen = true;
    }

    private void clearSelectionOtherTrees(JTree tree) {
        listen = false;

        for (JTree aTree : trees) {
            if ((aTree != tree) && (aTree.getSelectionCount() > 0)) {
                aTree.clearSelection();
            }
        }

        listen = true;
    }

    private void clearSelectionAllTrees() {
        for (JTree tree : trees) {
            tree.clearSelection();
        }
    }

    private void clearSelectionAllLists() {
        for (JList list : lists) {
            list.clearSelection();
        }
    }
}
