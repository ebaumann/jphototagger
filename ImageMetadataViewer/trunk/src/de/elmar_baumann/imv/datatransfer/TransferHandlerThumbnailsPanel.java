package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.database.DatabaseImageCollections;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.ViewUtil;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

/**
 * Handler for <strong>copying</strong> or <strong>moving</strong> a list of
 * thumbnails. The filenames of the thumbnails will be transferred as
 * <code>DataFlavor.stringFlavor</code>, each filename is separated by 
 * {@link #delimiter}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/24
 */
public final class TransferHandlerThumbnailsPanel extends TransferHandler {

    /**
     * Delimiter between the filenames in the transfered string.
     */
    static final String delimiter = "\n";
    private static final List<Content> contentIsAFilesystemDirectory =
            new ArrayList<Content>();


    static {
        contentIsAFilesystemDirectory.add(Content.DIRECTORY);
        contentIsAFilesystemDirectory.add(Content.FAVORITE_DIRECTORY);
    }

    @Override
    public boolean canImport(TransferSupport transferSupport) {
        ImageFileThumbnailsPanel panel =
                (ImageFileThumbnailsPanel) transferSupport.getComponent();
        if (panel.getContent().equals(Content.IMAGE_COLLECTION)) return true;
        return canPanelAddImageFiles(panel) &&
                TransferUtil.maybeContainFileData(transferSupport.
                getTransferable());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        List<String> filenames = FileUtil.getAsFilenames(
                ((ImageFileThumbnailsPanel) c).getSelectedFiles());
        return TransferUtil.getStringListTransferable(filenames, delimiter);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.COPY_OR_MOVE;
    }

    @Override
    public boolean importData(final TransferSupport transferSupport) {
        final ImageFileThumbnailsPanel panel =
                (ImageFileThumbnailsPanel) transferSupport.getComponent();
        boolean imagesSelected = panel.getSelectionCount() > 0;
        if (!canImport(transferSupport)) return false;
        if (imagesSelected &&
                panel.getContent().equals(Content.IMAGE_COLLECTION)) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    moveSelectedImages(transferSupport, panel);
                }
            });
            return true;
        }
        copyOrMoveSelectedImages(transferSupport);
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    private boolean canPanelAddImageFiles(
            ImageFileThumbnailsPanel thumbnailsPanel) {
        return contentIsAFilesystemDirectory.contains(
                thumbnailsPanel.getContent());
    }

    private void copyOrMoveSelectedImages(TransferSupport transferSupport) {
        List<File> sourceFiles =
                TransferUtil.getFiles(transferSupport.getTransferable(),
                delimiter);
        File targetDirectory = null;
        if (isContent(Content.DIRECTORY)) {
            JTree treeDirectories =
                    GUI.INSTANCE.getAppPanel().getTreeDirectories();
            targetDirectory =
                    ViewUtil.getSelectedDirectory(treeDirectories);
        } else if (isContent(Content.FAVORITE_DIRECTORY)) {
            targetDirectory =
                    ViewUtil.getSelectedDirectoryFromFavoriteDirectories();
        }
        if (targetDirectory != null & sourceFiles.size() > 0) {
            TransferHandlerTreeDirectories.handleDroppedFiles(transferSupport.
                    getDropAction(),
                    sourceFiles, targetDirectory);
        }
    }

    private boolean isContent(Content content) {
        ImageFileThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.getAppPanel().
                getPanelThumbnails();
        return thumbnailsPanel.getContent().equals(content);
    }

    private void moveSelectedImages(TransferSupport transferSupport,
            ImageFileThumbnailsPanel panel) {
        Point dropPoint = transferSupport.getDropLocation().getDropPoint();
        panel.moveSelectedToIndex(panel.getDropIndex(dropPoint.x, dropPoint.y));
        String imageCollectionName = getImageCollectionName();
        if (imageCollectionName != null) {
            DatabaseImageCollections.INSTANCE.insertImageCollection(
                    imageCollectionName,
                    FileUtil.getAsFilenames(panel.getFiles()));
        }
    }

    private String getImageCollectionName() {
        JList listImageCollections = GUI.INSTANCE.getAppPanel().
                getListImageCollections();
        Object element = null;
        int index = listImageCollections.getSelectedIndex();
        if (index >= 0) {
            element = listImageCollections.getModel().getElementAt(index);
        }
        return element == null
               ? null
               : element.toString();
    }
}
