package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.importer.HierarchicalKeywordsImporter;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsImportDialog;
import de.elmar_baumann.imv.view.frames.AppFrame;
import de.elmar_baumann.imv.view.panels.ProgressBarUserTasks;
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
                new ImportTask(importer.getPaths(dlg.getFile())).start();
            }
        }
    }

    private class ImportTask extends Thread {

        private final Collection<List<String>> paths;
        private final TreeModel treeModel = GUI.INSTANCE.getAppPanel().
                getTreeHierarchicalKeywords().getModel();
        private final JProgressBar progressBar;

        public ImportTask(Collection<List<String>> paths) {
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
                for (List<String> path : paths) {
                    DefaultMutableTreeNode node =
                            (DefaultMutableTreeNode) model.getRoot();
                    for (String keyword : path) {
                        DefaultMutableTreeNode existingNode =
                                model.findChildByName(node, keyword);
                        if (existingNode == null) {
                            model.addKeyword(node, keyword);
                            node = model.findChildByName(node, keyword);
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
            }
        }

        private void setProgressBarNextValue() {
            if (progressBar != null) {
                progressBar.setValue(progressBar.getValue() + 1);
            }
        }

        private void releaseProgressBar() {
            if (progressBar != null) {
                progressBar.setValue(paths.size());
                ProgressBarUserTasks.INSTANCE.releaseResource(this);
            }
        }
    }
}
