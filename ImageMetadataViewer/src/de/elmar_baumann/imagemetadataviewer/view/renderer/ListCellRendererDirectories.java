package de.elmar_baumann.imagemetadataviewer.view.renderer;

import de.elmar_baumann.imagemetadataviewer.io.DirectoryInfo;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.awt.Component;
import java.text.MessageFormat;
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
 * @see     de.elmar_baumann.imagemetadataviewer.view.dialogs.UpdateMetaDataOfDirectoriesDialog
 */
public class ListCellRendererDirectories extends DefaultListCellRenderer {

    private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private static final MessageFormat message = new MessageFormat(Bundle.getString("ListCellRendererDirectories.LabelText"));

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
            index, isSelected, cellHasFocus);
        DirectoryInfo directoryInfo = (DirectoryInfo) value;
        label.setIcon(fileSystemView.getSystemIcon(directoryInfo.getDirectory()));
        label.setText(getLabelText(directoryInfo));
        return label;
    }

    private static String getLabelText(DirectoryInfo directoryInfo) {
        Object[] params = {directoryInfo.getDirectory().getAbsolutePath(),
            directoryInfo.getImageFileCount()
        };
        return message.format(params);
    }
}
