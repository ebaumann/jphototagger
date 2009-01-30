package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.List;

/**
 * Reacts to databse changes of thumbnails
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/15
 */
public final class ControllerThumbnailsDatabaseChanges extends Controller
    implements DatabaseListener {
    
    private final DatabaseImageFiles db = DatabaseImageFiles.getInstance();
    private final ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerThumbnailsDatabaseChanges() {
        db.addDatabaseListener(this);
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        DatabaseAction.Type type = action.getType();
        if (type.equals(DatabaseAction.Type.ThumbnailUpdated)) {
            thumbnailsPanel.repaint(new File(action.getFilename()));
        } else if (type.equals(DatabaseAction.Type.ImageFilesDeleted)) {
            removeThumbnails(action);
        }
    }

    private void removeThumbnails(DatabaseAction action) {
        List<File> deleted = FileUtil.getAsFiles(action.getFilenames());
        thumbnailsPanel.remove(deleted);
    }
}
