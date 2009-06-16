package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.io.DirectoryInfo;
import de.elmar_baumann.imv.resource.Bundle;
import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;

/**
 * Benutzt vom ScanDirectoriesDialog, zeigt Systemordnericons vor
 * den Verzeichnisnamen an.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/25
 * @see     de.elmar_baumann.imv.view.dialogs.UpdateMetadataOfDirectoriesDialog
 */
public final class ListCellRendererDirectories extends DefaultListCellRenderer {

    private static final FileSystemView fileSystemView = FileSystemView.
            getFileSystemView();

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                index, isSelected, cellHasFocus);
        DirectoryInfo directoryInfo = (DirectoryInfo) value;
        File dir = directoryInfo.getDirectory();
        if (dir.exists()) {
            synchronized (fileSystemView) {
                try {
                    label.setIcon(fileSystemView.getSystemIcon(dir));
                } catch (Exception ex) {
                    AppLog.logWarning(ListCellRendererDirectories.class, ex);
                }
            }
        }
        label.setText(getLabelText(directoryInfo));
        return label;
    }

    private static String getLabelText(DirectoryInfo directoryInfo) {
        return Bundle.getString("ListCellRendererDirectories.LabelText",
                directoryInfo.getDirectory().getAbsolutePath(),
                directoryInfo.getImageFileCount());
    }
}
