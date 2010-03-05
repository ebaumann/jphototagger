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
package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.view.panels.ProgressBar;
import de.elmar_baumann.lib.generics.Pair;
import java.io.File;
import java.util.Collection;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Imports keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-01
 */
public abstract class KeywordsImporter implements Importer {

    private static final String PROGRESSBAR_STRING = JptBundle.INSTANCE.getString("KeywordImporter.ProgressBar.String");

    /**
     * Returns all keyword paths to the leaf nodes.
     * <p>
     * Every path is a list. The first string in a path - the first list element
     * - is the root keyword (no parent) and the following keywords - string
     * elements in the list - are children where a string following a string is
     * a child of the previous string.
     * <p>
     * E.g. the tree
     *
     * {@code
     * Landscape
     *     Tree
     *         Beech
     *         Birch
     * Building
     * }
     * <p>
     * has these lists, elements delimited by a comma:
     * {@code
     * Landscape, Tree
     * Landscape, Tree, Beech
     * Landscape, Tree, Birch
     * Building
     * }
     *
     * @param  file file with keywords to import
     * @return      keyword paths. The first element in the pair is the keyword,
     *              the second is true, if the keyword is a real keyword and
     *              false if it's a helper. Null on errors.
     */
    public abstract Collection<List<Pair<String, Boolean>>> getPaths(File file);

    @Override
    public void importFile(File file) {
        Collection<List<Pair<String, Boolean>>> paths = getPaths(file);
        if (paths != null) {
            new ImportTask(paths).start();
        }
    }

    private static class ImportTask extends Thread {

        private final Collection<List<Pair<String, Boolean>>> paths;
        private final TreeModel                               treeModel  = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);
        private       JProgressBar                            progressBar;

        public ImportTask(Collection<List<Pair<String, Boolean>>> paths) {
            this.paths = paths;
            setName("Importing keywords @ " + getClass().getSimpleName());
        }

        private void getProgressBar() {
            if (progressBar != null) return;
            progressBar = ProgressBar.INSTANCE.getResource(this);
        }

        @Override
        public void run() {
            assert treeModel instanceof TreeModelKeywords : treeModel;
            if (treeModel instanceof TreeModelKeywords) {
                TreeModelKeywords model = (TreeModelKeywords) treeModel;
                updateProgressBar(0);
                int progressValue = 0;
                for (List<Pair<String, Boolean>> path : paths) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
                    for (Pair<String, Boolean> keyword : path) {
                        DefaultMutableTreeNode existingNode = model.findChildByName(node, keyword.getFirst());
                        if (existingNode == null) {
                            model.insert(node, keyword.getFirst(), keyword.getSecond());
                            node = model.findChildByName(node, keyword.getFirst());
                        } else {
                            node = existingNode;
                        }
                    }
                    updateProgressBar(++progressValue);
                }
                releaseProgressBar();
                expandRootSelHk();
            }
        }

        private void expandRootSelHk() {
            JTree  tree = GUI.INSTANCE.getAppPanel().getTreeSelKeywords();
            Object root = tree.getModel().getRoot();

            tree.expandPath(new TreePath(((DefaultMutableTreeNode) root).getPath()));
        }

        private void updateProgressBar(int value) {
            getProgressBar();
            if (progressBar != null) {
                progressBar.setMinimum(0);
                progressBar.setMaximum(paths.size());
                progressBar.setValue(value);
                if (!progressBar.isStringPainted()) {
                    progressBar.setStringPainted(true);
                }
                if (!PROGRESSBAR_STRING.equals(progressBar.getString())) {
                    progressBar.setString(PROGRESSBAR_STRING);
                }
            }
        }

        private void releaseProgressBar() {
            if (progressBar != null) {
                if (progressBar.isStringPainted()) {
                    progressBar.setString("");
                }
                progressBar.setValue(0);
            }
            ProgressBar.INSTANCE.releaseResource(this);
        }
    }
}
