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

import de.elmar_baumann.jpt.importer.HierarchicalKeywordsImporter;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.HierarchicalKeywordsImportDialog;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.ProgressBar;
import de.elmar_baumann.lib.generics.Pair;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemImportKeywords()} and
 * on action performed imports hierarchical keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-01
 */
public final class ControllerImportHierarchicalKeywords implements ActionListener {

    private static final String PROGRESSBAR_STRING = Bundle.getString("ControllerImportHierarchicalKeywords.ProgressBar.String");

    public ControllerImportHierarchicalKeywords() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemImportKeywords().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        importKeywords();
    }

    private void importKeywords() {
        HierarchicalKeywordsImportDialog dlg = new HierarchicalKeywordsImportDialog();
        dlg.setVisible(true);
        if (dlg.isAccepted()) {
            HierarchicalKeywordsImporter importer = dlg.getImporter();
            assert importer != null : "Importer is null!";
            if (importer != null) {
                Collection<List<Pair<String, Boolean>>> paths = importer.getPaths(dlg.getFile());
                if (paths != null) {
                    new ImportTask(paths).start();
                }
            }
        }
    }

    private static class ImportTask extends Thread {

        private final Collection<List<Pair<String, Boolean>>> paths;
        private final TreeModel                               treeModel  = GUI.INSTANCE.getAppPanel().getTreeHierarchicalKeywords().getModel();
        private       JProgressBar                            progressBar;

        public ImportTask(Collection<List<Pair<String, Boolean>>> paths) {
            this.paths = paths;
            setName("Importing keywords @ " + getClass().getName());
        }

        private void getProgressBar() {
            if (progressBar != null) return;
            progressBar = ProgressBar.INSTANCE.getResource(this);
        }

        @Override
        public void run() {
            assert treeModel instanceof TreeModelHierarchicalKeywords : treeModel;
            if (treeModel instanceof TreeModelHierarchicalKeywords) {
                TreeModelHierarchicalKeywords model = (TreeModelHierarchicalKeywords) treeModel;
                updateProgressBar(0);
                int progressValue = 0;
                for (List<Pair<String, Boolean>> path : paths) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) model.getRoot();
                    for (Pair<String, Boolean> keyword : path) {
                        DefaultMutableTreeNode existingNode = model.findChildByName(node, keyword.getFirst());
                        if (existingNode == null) {
                            model.addKeyword(node, keyword.getFirst(), keyword.getSecond());
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
            JTree  tree = GUI.INSTANCE.getAppPanel().getTreeSelHierarchicalKeywords();
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
