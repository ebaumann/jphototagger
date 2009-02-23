package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.datatransfer.TransferHandlerTreeDirectories;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.clipboard.ClipboardUtil;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.TransferHandler;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/27
 */
public final class ControllerPasteFilesFromClipboard implements ActionListener {

    private final ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerPasteFilesFromClipboard() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemInsert().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (thumbnailsPanel.getContent().equals(Content.DIRECTORY)) {
            insertFiles();
        }
    }

    private void insertFiles() {
        File directory = ViewUtil.getSelectedDirectory(GUI.INSTANCE.getAppPanel().getTreeDirectories());
        if (directory != null) {
            List<File> files = ClipboardUtil.getFilesFromSystemClipboard("\n");
            TransferHandlerTreeDirectories.handleDroppedFiles(TransferHandler.COPY, files, directory);
            thumbnailsPanel.refresh();
            ComponentUtil.forceRepaint(thumbnailsPanel);
        }
    }
}
