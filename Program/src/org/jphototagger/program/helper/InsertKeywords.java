/*
 * @(#)InsertKeywords.java    Created on 2009-09-03
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

package org.jphototagger.program.helper;

import org.jphototagger.program.database.DatabaseKeywords;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;
import org.jphototagger.program.resource.GUI;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Inserts a list of Strings into the keywords root.
 *
 * @author  Elmar Baumann
 */
public final class InsertKeywords extends Thread {
    private final List<String> keywords;

    public InsertKeywords(List<String> keywords) {
        super("JPhotoTagger: Inserting string list into keywords");

        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        this.keywords = new ArrayList<String>(keywords);
    }

    @Override
    public void run() {
        copy();
    }

    private void copy() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TreeModelKeywords model =
                    ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);
                Object root = model.getRoot();

                if (root instanceof DefaultMutableTreeNode) {
                    insertKeywords((DefaultMutableTreeNode) root, model);
                }
            }
        });
    }

    private void insertKeywords(DefaultMutableTreeNode rootHk,
                                TreeModelKeywords modelHk) {
        DatabaseKeywords db       = DatabaseKeywords.INSTANCE;
        boolean          inserted = false;

        for (String keyword : keywords) {
            if (!db.existsRootKeyword(keyword)) {
                modelHk.insert(rootHk, keyword, true, true);
                inserted = true;
            }
        }

        if (inserted) {
            expandRoot();
        }
    }

    private void expandRoot() {
        JTree  tree = GUI.INSTANCE.getAppPanel().getTreeSelKeywords();
        Object root = tree.getModel().getRoot();

        tree.expandPath(
            new TreePath(((DefaultMutableTreeNode) root).getPath()));
    }
}
