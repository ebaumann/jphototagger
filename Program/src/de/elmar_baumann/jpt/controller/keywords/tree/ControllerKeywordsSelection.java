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

package de.elmar_baumann.jpt.controller.keywords.tree;

import de.elmar_baumann.jpt.controller.keywords.list
    .ShowThumbnailsContainingAllKeywords2;
import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to {@link AppPanel#getTreeSelKeywords()} and on selection
 * shows thumbnails of the selected keyword and all it's
 *
 * @author  Elmar Baumann
 * @version 2009-09-02
 */
public final class ControllerKeywordsSelection
        implements TreeSelectionListener {
    private final JTree tree = GUI.INSTANCE.getAppPanel().getTreeSelKeywords();

    public ControllerKeywordsSelection() {
        listen();
    }

    private void listen() {
        tree.getSelectionModel().addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        if (e.isAddedPath()) {
            showThumbnailsOfSelKeywords();
        }
    }

    private void showThumbnailsOfSelKeywords() {
        SwingUtilities.invokeLater(
            new ShowThumbnailsContainingAllKeywords2(getKeywordStringPaths()));
    }

    private List<List<String>> getKeywordStringPaths() {
        List<List<String>>  keywordPaths = new ArrayList<List<String>>();
        List<List<Keyword>> hkwp         = getKeywordPaths();

        for (List<Keyword> kws : hkwp) {
            List<String> stringKeywords = new ArrayList<String>();

            for (Keyword kw : kws) {
                stringKeywords.add(kw.getName());
            }

            keywordPaths.add(stringKeywords);
        }

        return keywordPaths;
    }

    private List<List<Keyword>> getKeywordPaths() {
        TreePath[]          selPaths = tree.getSelectionPaths();
        List<List<Keyword>> paths    = new ArrayList<List<Keyword>>();

        for (TreePath selPath : selPaths) {
            DefaultMutableTreeNode selNode =
                (DefaultMutableTreeNode) selPath.getLastPathComponent();
            List<Keyword> kwPath = new ArrayList<Keyword>();

            for (Object userObject : selNode.getUserObjectPath()) {
                if (userObject instanceof Keyword) {
                    Keyword kw = (Keyword) userObject;

                    if (kw.isReal()) {
                        kwPath.add(kw);
                    }
                }
            }

            paths.add(kwPath);
        }

        return paths;
    }
}
