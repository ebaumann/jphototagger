package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.io.ImageFileDirectory;
import org.jphototagger.program.resource.JptBundle;

import java.awt.Component;

import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Benutzt vom ScanDirectoriesDialog, zeigt Systemordnericons vor
 * den Verzeichnisnamen an.
 *
 * @author Elmar Baumann
 * @see     org.jphototagger.program.view.dialogs.UpdateMetadataOfDirectoriesDialog
 */
public final class ListCellRendererDirectories extends DefaultListCellRenderer {
    private static final FileSystemView FILE_SYSTEM_VIEW =
        FileSystemView.getFileSystemView();
    private static final long serialVersionUID = 1443237617540897116L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                           index, isSelected, cellHasFocus);
        ImageFileDirectory directoryInfo = (ImageFileDirectory) value;
        File          dir           = directoryInfo.getDirectory();

        if (dir.exists()) {
            synchronized (FILE_SYSTEM_VIEW) {
                try {
                    label.setIcon(FILE_SYSTEM_VIEW.getSystemIcon(dir));
                } catch (Exception ex) {
                    AppLogger.logSevere(ListCellRendererDirectories.class, ex);
                }
            }
        }

        label.setText(getLabelText(directoryInfo));

        return label;
    }

    private static String getLabelText(ImageFileDirectory directoryInfo) {
        return JptBundle.INSTANCE.getString(
            "ListCellRendererDirectories.LabelText",
            directoryInfo.getDirectory().getAbsolutePath(),
            directoryInfo.getImageFileCount());
    }
}
