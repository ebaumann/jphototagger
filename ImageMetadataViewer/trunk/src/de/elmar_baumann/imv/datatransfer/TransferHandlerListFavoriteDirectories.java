package de.elmar_baumann.imv.datatransfer;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.data.FavoriteDirectory;
import de.elmar_baumann.imv.model.ListModelFavoriteDirectories;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.dialogs.FavoriteDirectoryPropertiesDialog;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.datatransfer.TransferUtil;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.TransferHandler;

/**
 * Transfer handler for the list with favorite directories. Handles file drops
 * and moving items.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/24
 */
public final class TransferHandlerListFavoriteDirectories extends TransferHandler {

    static final String itemDelimiter = "\n";

    @Override
    public boolean canImport(TransferHandler.TransferSupport transferSupport) {
        return TransferUtil.maybeContainFileData(transferSupport.getTransferable());
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        return TransferUtil.getSelectedItemStringsTransferable(
            (JList) c, itemDelimiter);
    }

    @Override
    public int getSourceActions(JComponent c) {
        return TransferHandler.MOVE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean importData(TransferHandler.TransferSupport transferSupport) {
        if (!transferSupport.isDrop()) {
            return false;
        }
        if (transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            insertDirectories(TransferUtil.getFilesFromJavaFileList(transferSupport.getTransferable()));
        } else if (transferSupport.isDataFlavorSupported(TransferUtil.getUriListFlavor())) {
            insertDirectories(TransferUtil.getFilesFromUriList(transferSupport.getTransferable()));
        } else if (transferSupport.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            handleDroppedString(transferSupport);
        }
        return true;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    private ListModelFavoriteDirectories getModel() {
        return (ListModelFavoriteDirectories) Panels.getInstance().getAppPanel().
            getListFavoriteDirectories().getModel();
    }

    private void insertDirectories(List<File> files) {
        List<File> directories = FileUtil.getDirectories(files);
        int size = directories.size();
        ListModelFavoriteDirectories model = getModel();
        if (size > 0 && confirmAddDirectories(size)) {
            for (File directory : directories) {
                FavoriteDirectoryPropertiesDialog dialog = new FavoriteDirectoryPropertiesDialog();
                dialog.setDirectoryName(directory.getAbsolutePath());
                dialog.setEnabledButtonChooseDirectory(false);
                dialog.setVisible(true);
                if (dialog.isAccepted()) {
                    model.insertFavorite(new FavoriteDirectory(
                        dialog.getFavoriteName(), dialog.getDirectoryName(), -1));
                }
            }
        }
    }

    private boolean confirmAddDirectories(int size) {
        MessageFormat msg = new MessageFormat("{0} neue Verzeichnis(se) zu den Favoriten hinzufügen?");
        Object[] params = {size};
        return JOptionPane.showConfirmDialog(
            null,
            msg.format(params),
            "Frage",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppIcons.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private void handleDroppedString(TransferSupport transferSupport) {
        JList list = (JList) transferSupport.getComponent();
        ListModelFavoriteDirectories model =
            (ListModelFavoriteDirectories) list.getModel();
        int dropIndex =
            ((JList.DropLocation) transferSupport.getDropLocation()).getIndex();
        try {
            String itemName = (String) transferSupport.getTransferable().
                getTransferData(DataFlavor.stringFlavor);
            Object item = ListUtil.getFirstItemWithText(itemName, model);
            if (dropIndex >= 0 && item != null) {
                model.swapFavorites(dropIndex, model.indexOf(item));
            }
        } catch (Exception ex) {
            de.elmar_baumann.imv.app.AppLog.logWarning(getClass(), ex);
        }
    }
}
