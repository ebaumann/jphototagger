/*
 * @(#)TreeModelHelpContents.java    2008-10-02
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

package de.elmar_baumann.lib.model;

import de.elmar_baumann.lib.util.help.HelpIndexParser;
import de.elmar_baumann.lib.util.help.HelpNode;
import de.elmar_baumann.lib.util.help.HelpPage;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Contains the help index of an application's help, the root node of an
 * {@link de.elmar_baumann.lib.util.help.HelpNode} object.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
 */
public final class TreeModelHelpContents implements TreeModel {
    private HelpNode root = new HelpNode();

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return ((HelpNode) parent).getChild(index);
    }

    @Override
    public int getChildCount(Object parent) {
        return ((HelpNode) parent).getChildCount();
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof HelpPage;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((HelpNode) parent).getIndexOfChild(child);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

        // Nothing to bei done
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

        // Nothing to bei done
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

        // Nothing to bei done
    }

    /**
     * Constructor.
     *
     * @param url  URL of the XML file for the class
     */
    public TreeModelHelpContents(String url) {
        if (url == null) {
            throw new NullPointerException("url == null");
        }

        parse(url);
    }

    private void parse(String url) {
        HelpNode rootNode =
            HelpIndexParser.parse(this.getClass().getResourceAsStream(url));

        if (rootNode != null) {
            root = rootNode;
        }
    }
}
