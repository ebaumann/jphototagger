package de.elmar_baumann.jpt.controller.keywords.tree;

import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.database.DatabaseKeywords;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.event.DatabaseImageEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import de.elmar_baumann.jpt.resource.GUI;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to database updates and adds not existing keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-17
 */
public final class ControllerKeywordsDbUpdates implements DatabaseImageFilesListener {

    public ControllerKeywordsDbUpdates() {
        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addDatabaseImageFilesListener(this);
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
                   if (!DatabaseKeywords.INSTANCE.exists(keyword)) {
                       addKeyword(keyword);
                   }
                }
            }
        }
    }

    private void addKeyword(String keyword) {
        TreeModelKeywords model =
                (TreeModelKeywords) GUI.INSTANCE.getAppPanel().getTreeEditKeywords().getModel();
        model.addKeyword((DefaultMutableTreeNode)model.getRoot(), keyword, true);
    }
}
