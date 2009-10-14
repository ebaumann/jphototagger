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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.GUI;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Inserts a list of Strings into the hierarchical keywords root.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-03
 */
public final class InsertHierarchicalKeywords extends Thread {

    private final List<String> keywords;

    public InsertHierarchicalKeywords(List<String> keywords) {
        this.keywords = new ArrayList<String>(keywords);
        setName("Inserting string list into hierarchical keywords @ " + // NOI18N
                getClass().getName());
    }

    @Override
    public void run() {
        copy();
    }

    private void copy() {
        TreeModel tm = GUI.INSTANCE.getAppPanel().getTreeHierarchicalKeywords().
                getModel();
        assert tm instanceof TreeModelHierarchicalKeywords :
                "Invalid model: " + tm; // NOI18N
        if (tm instanceof TreeModelHierarchicalKeywords) {
            TreeModelHierarchicalKeywords model =
                    (TreeModelHierarchicalKeywords) tm;
            Object root = model.getRoot();
            assert root instanceof DefaultMutableTreeNode;
            if (root instanceof DefaultMutableTreeNode) {
                insertKeywords((DefaultMutableTreeNode) root, model);
            }
        }
    }

    private void insertKeywords(
            DefaultMutableTreeNode rootHk,
            TreeModelHierarchicalKeywords modelHk) {

        DatabaseHierarchicalKeywords db = DatabaseHierarchicalKeywords.INSTANCE;
        boolean inserted = false;

        for (String keyword : keywords) {
            if (!db.existsRootKeyword(keyword)) {
                modelHk.addKeyword(rootHk, keyword, true);
                inserted = true;
            }
        }

        if (inserted) {
            expandRoot();
        }
    }

    private void expandRoot() {
        JTree tree = GUI.INSTANCE.getAppPanel().getTreeSelHierarchicalKeywords();
        Object root = tree.getModel().getRoot();
        tree.expandPath(new TreePath(((DefaultMutableTreeNode) root).getPath()));
    }
}
