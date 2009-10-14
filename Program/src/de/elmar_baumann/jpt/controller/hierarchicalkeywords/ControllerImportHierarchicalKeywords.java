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

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.importer.HierarchicalKeywordsImporter;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.HierarchicalKeywordsImportDialog;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.ProgressBarUserTasks;
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
public final class ControllerImportHierarchicalKeywords
        implements ActionListener {

    public ControllerImportHierarchicalKeywords() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemImportKeywords().addActionListener(
                this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        importKeywords();
    }

    private void importKeywords() {
        HierarchicalKeywordsImportDialog dlg =
                new HierarchicalKeywordsImportDialog();
        dlg.setVisible(true);
        if (dlg.isAccepted()) {
            HierarchicalKeywordsImporter importer = dlg.getImporter();
            assert importer != null : "Importer is null!"; // NOI18N
            if (importer != null) {
                Collection<List<Pair<String, Boolean>>> paths =
                        importer.getPaths(dlg.getFile());
                if (paths != null) {
                    new ImportTask(paths).start();
                }
            }
        }
    }

    private class ImportTask extends Thread {

        private final Collection<List<Pair<String, Boolean>>> paths;
        private final TreeModel treeModel = GUI.INSTANCE.getAppPanel().
                getTreeHierarchicalKeywords().getModel();
        private final JProgressBar progressBar;

        public ImportTask(Collection<List<Pair<String, Boolean>>> paths) {
            this.paths = paths;
            setName("Importing keywords @ " + getClass().getName()); // NOI18N
            progressBar = ProgressBarUserTasks.INSTANCE.getResource(this);
            if (progressBar == null) {
                AppLog.logInfo(getClass(), "ProgressBar.Locked", getClass(), // NOI18N
                        ProgressBarUserTasks.INSTANCE.getOwner());
            }
        }

        @Override
        public void run() {
            assert treeModel instanceof TreeModelHierarchicalKeywords :
                    "Not a TreeModelHierarchicalKeywords: " + treeModel; // NOI18N
            if (treeModel instanceof TreeModelHierarchicalKeywords) {
                TreeModelHierarchicalKeywords model =
                        (TreeModelHierarchicalKeywords) treeModel;
                initProgressBar();
                for (List<Pair<String, Boolean>> path : paths) {
                    DefaultMutableTreeNode node =
                            (DefaultMutableTreeNode) model.getRoot();
                    for (Pair<String, Boolean> keyword : path) {
                        DefaultMutableTreeNode existingNode =
                                model.findChildByName(node, keyword.getFirst());
                        if (existingNode == null) {
                            model.addKeyword(node, keyword.getFirst(),
                                    keyword.getSecond());
                            node = model.findChildByName(
                                    node, keyword.getFirst());
                        } else {
                            node = existingNode;
                        }
                    }
                    setProgressBarNextValue();
                }
                releaseProgressBar();
                expandRootSelHk();
            }
        }

        private void expandRootSelHk() {
            JTree tree =
                    GUI.INSTANCE.getAppPanel().getTreeSelHierarchicalKeywords();
            Object root = tree.getModel().getRoot();
            tree.expandPath(
                    new TreePath(((DefaultMutableTreeNode) root).getPath()));
        }

        private void initProgressBar() {
            if (progressBar != null) {
                progressBar.setMinimum(0);
                progressBar.setMaximum(paths.size());
                progressBar.setValue(0);
                progressBar.setStringPainted(true);
                progressBar.setString(
                        Bundle.getString(
                        "ControllerImportHierarchicalKeywords.ProgressBar.String")); // NOI18N
            }
        }

        private void setProgressBarNextValue() {
            if (progressBar != null) {
                progressBar.setValue(progressBar.getValue() + 1);
            }
        }

        private void releaseProgressBar() {
            if (progressBar != null) {
                progressBar.setValue(0);
                progressBar.setString(""); // NOI18N
                ProgressBarUserTasks.INSTANCE.releaseResource(this);
            }
        }
    }
}
