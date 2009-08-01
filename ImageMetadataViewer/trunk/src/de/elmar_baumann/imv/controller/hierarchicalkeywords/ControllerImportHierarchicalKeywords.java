package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.importer.HierarchicalKeywordsImporter;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.HierarchicalKeywordsImportDialog;
import de.elmar_baumann.imv.view.frames.AppFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

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
            assert importer != null : "Importer is null!";
            if (importer != null) {
                Collection<List<String>> paths =
                        importer.getPaths(dlg.getFile());
                for (List<String> path : paths) {
                    addKeywordPath(path);
                }
            }
        }
    }

    private void addKeywordPath(List<String> path) {
        TreeModel m = GUI.INSTANCE.getAppPanel().getTreeHierarchicalKeywords().
                getModel();
        assert m instanceof TreeModelHierarchicalKeywords : "Model is not a " +
                TreeModelHierarchicalKeywords.class + " but a " + m.getClass();

        if (m instanceof TreeModelHierarchicalKeywords) {
            TreeModelHierarchicalKeywords model =
                    (TreeModelHierarchicalKeywords) m;
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
        }
    }
}
