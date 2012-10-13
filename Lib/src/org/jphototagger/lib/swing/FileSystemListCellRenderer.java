package org.jphototagger.lib.swing;

import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;

/**
 * Renders an file specific icon for cell values that are an instance of
 * {@code java.io.File}. Uses
 * {@code javax.swing.filechooser.FileSystemView#getSystemIcon(java.io.File)}.
 *
 * @author Elmar Baumann
 */
public final class FileSystemListCellRenderer extends DefaultListCellRenderer {

    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private static final long serialVersionUID = 1L;
    private final boolean absolutePathName;

    /**
     * Constructor.
     *
     * @param absolutePathName true, if the absolute path shall be displayed and
     *                     false, if only the file name shall be displayed.
     *                     Default: false (only the file name shall be displayed).
     */
    public FileSystemListCellRenderer(boolean absolutePathName) {
        this.absolutePathName = absolutePathName;
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof File) {
            File file = (File) value;

            if (file.exists()) {
                synchronized (FILE_SYSTEM_VIEW) {
                    try {
                        label.setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
                    } catch (Exception ex) {
                        Logger.getLogger(FileSystemListCellRenderer.class.getName()).log(Level.WARNING, null, ex);
                    }
                }
            }

            label.setText(absolutePathName
                    ? file.getAbsolutePath()
                    : file.getName());
        }

        return label;
    }
}
