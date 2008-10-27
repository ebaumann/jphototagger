package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.datatransfer.TransferHandlerTreeDirectories;
import de.elmar_baumann.imv.resource.Panels;
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
import javax.swing.plaf.ComponentUI;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/27
 */
public class ControllerPasteFilesFromClipboard extends Controller implements ActionListener {

    private ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    public ControllerPasteFilesFromClipboard() {
        Panels.getInstance().getAppFrame().getMenuItemInsert().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl() && thumbnailsPanel.getContent().equals(Content.Directory)) {
            insertFiles();
        }
    }

    private void insertFiles() {
        File directory = ViewUtil.getTargetDirectory(Panels.getInstance().getAppPanel().getTreeDirectories());
        if (directory != null) {
            List<File> files = ClipboardUtil.getFilesFromSystemClipboard("\n");
            TransferHandlerTreeDirectories.handleDroppedFiles(TransferHandler.COPY, files, directory);
            thumbnailsPanel.refresh();
            ComponentUtil.forceRepaint(thumbnailsPanel);
        }
    }
}
