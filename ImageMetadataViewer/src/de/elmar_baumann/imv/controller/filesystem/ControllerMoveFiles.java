package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.event.FileSystemAction;
import de.elmar_baumann.imv.event.FileSystemActionListener;
import de.elmar_baumann.imv.event.FileSystemError;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.MoveToDirectoryDialog;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuPanelThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Renames files in the file system.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
 */
public class ControllerMoveFiles extends Controller
    implements ActionListener, FileSystemActionListener {

    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();
    private Database db = Database.getInstance();

    public ControllerMoveFiles() {
        PopupMenuPanelThumbnails.getInstance().addActionListenerFileSystemMoveFiles(this);
        Panels.getInstance().getAppFrame().getMenuItemFileSystemRename().addActionListener(this);
        ListenerProvider.getInstance().addFileSystemActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isStarted()) {
            moveFiles();
        }
    }

    private void moveFiles() {
        List<File> files = thumbnailsPanel.getSelectedFiles();
        if (files.size() > 0) {
            MoveToDirectoryDialog dialog = new MoveToDirectoryDialog();
            dialog.setSourceFiles(files);
            dialog.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(FileSystemAction action, File src, File target) {
        if (!src.getName().toLowerCase().endsWith(".xmp")) {
            db.updateRenameImageFilename(src.getAbsolutePath(), target.getAbsolutePath());
        }
    }

    @Override
    public void actionFailed(FileSystemAction action, FileSystemError error, File src, File target) {
    }
}
