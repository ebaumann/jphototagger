package de.elmar_baumann.jpt.controller.hierarchicalkeywords;

import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.database.DatabaseHierarchicalKeywords;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.DatabaseImageCollectionEvent;
import de.elmar_baumann.jpt.event.DatabaseImageEvent;
import de.elmar_baumann.jpt.event.DatabaseProgramEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseListener;
import de.elmar_baumann.jpt.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.jpt.resource.GUI;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to database updates and adds not existing keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-17
 */
public final class ControllerHierarchicalKeywordsDbUpdates implements DatabaseListener {

    public ControllerHierarchicalKeywordsDbUpdates() {
        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addDatabaseListener(this);
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
        if (event.getType().equals(DatabaseImageEvent.Type.IMAGEFILE_INSERTED)
                || event.getType().equals(DatabaseImageEvent.Type.IMAGEFILE_UPDATED)) {
            addNotExistingKeywords(event.getImageFile());
        }
    }

    private void addNotExistingKeywords(ImageFile imageFile) {
        if (imageFile != null && imageFile.getXmp() != null) {
             List<String> keywords = imageFile.getXmp().getDcSubjects();
             if (keywords != null) {
                for (String keyword : keywords) {
                   if (!DatabaseHierarchicalKeywords.INSTANCE.existsKeyword(keyword)) {
                       addKeyword(keyword);
                   }
                }
            }
        }
    }

    private void addKeyword(String keyword) {
        TreeModelHierarchicalKeywords model =
                (TreeModelHierarchicalKeywords) GUI.INSTANCE.getAppPanel().getTreeHierarchicalKeywords().getModel();
        model.addKeyword((DefaultMutableTreeNode)model.getRoot(), keyword, true);
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // ignore
    }

    @Override
    public void actionPerformed(DatabaseImageCollectionEvent event) {
        // ignore
    }
}
