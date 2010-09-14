/*
 * @(#)ControllerKeywordsSelection.java    Created on 2009-09-02
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.keywords.tree;

import java.awt.EventQueue;
import org.jphototagger.program.controller.keywords.list
    .ShowThumbnailsContainingAllKeywords2;
import org.jphototagger.program.data.Keyword;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to {@link AppPanel#getTreeSelKeywords()} and on selection
 * shows thumbnails of the selected keyword and all it's
 *
 * @author  Elmar Baumann
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
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            showThumbnailsOfSelKeywords();
        }
    }

    private void showThumbnailsOfSelKeywords() {
        EventQueue.invokeLater(
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
