package de.elmar_baumann.lib.renderer;

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
 * {@link java.io.File}. Uses
 * {@link javax.swing.filechooser.FileSystemView#getSystemIcon(java.io.File)}.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public final class ListCellRendererFileSystem extends DefaultListCellRenderer {

    private static final FileSystemView FILE_SYSTEM_VIEW =
            FileSystemView.getFileSystemView();
    private final boolean absolutePathName;

    /**
     * Constructor.
     * 
     * @param absolutePathName true, if the absolute path shall be displayed and
     *                     false, if only the file name shall be displayed.
     *                     Default: false (only the file name shall be displayed).
     */
    public ListCellRendererFileSystem(boolean absolutePathName) {
        this.absolutePathName = absolutePathName;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        if (value instanceof File) {
            File file = (File) value;
            if (file.exists()) {
                synchronized (FILE_SYSTEM_VIEW) {
                    try {
                        label.setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
                    } catch (Exception ex) {
                        Logger.getLogger(ListCellRendererFileSystem.class.
                                getName()).log(Level.WARNING, null, ex);
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
