package org.jphototagger.program.view.renderer;

import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;
import org.jphototagger.lib.util.Bundle;

import org.jphototagger.program.io.ImageFileDirectory;

/**
 * Benutzt vom ScanDirectoriesDialog, zeigt Systemordnericons vor
 * den Verzeichnisnamen an.
 *
 * @author Elmar Baumann
 * @see     org.jphototagger.program.view.dialogs.UpdateMetadataOfDirectoriesDialog
 */
public final class ListCellRendererDirectories extends DefaultListCellRenderer {
    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private static final long serialVersionUID = 1443237617540897116L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        ImageFileDirectory directoryInfo = (ImageFileDirectory) value;
        File dir = directoryInfo.getDirectory();

        if (dir.exists()) {
            synchronized (FILE_SYSTEM_VIEW) {
                try {
                    label.setIcon(FILE_SYSTEM_VIEW.getSystemIcon(dir));
                } catch (Exception ex) {
                    Logger.getLogger(ListCellRendererDirectories.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        label.setText(getLabelText(directoryInfo));

        return label;
    }

    private static String getLabelText(ImageFileDirectory directoryInfo) {
        return Bundle.getString(ListCellRendererDirectories.class, "ListCellRendererDirectories.LabelText",
                directoryInfo.getDirectory().getAbsolutePath(), directoryInfo.getImageFileCount());
    }
}
