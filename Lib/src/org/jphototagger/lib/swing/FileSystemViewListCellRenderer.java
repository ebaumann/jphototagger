package org.jphototagger.lib.swing;

import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileSystemView;

/**
 * @author Elmar Baumann
 */
public class FileSystemViewListCellRenderer implements ListCellRenderer<File> {

    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();
    private final boolean onlyFilename;

    public FileSystemViewListCellRenderer() {
        this(false);
    }

    /**
     * @param onlyFilename true, if only filename shall be displayed, false if absolute path name shall be displayed.
     * Default: false
     */
    public FileSystemViewListCellRenderer(boolean onlyFilename) {
        this.onlyFilename = onlyFilename;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends File> list, File file, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) delegate.getListCellRendererComponent(list, file, index, isSelected, cellHasFocus);
        label.setText(file == null
                ? ""
                : onlyFilename
                ? file.getName()
                : file.getAbsolutePath());
        if (file != null && file.exists()) {
            synchronized (FILE_SYSTEM_VIEW) {
                try {
                    label.setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
                } catch (Throwable t) {
                    Logger.getLogger(FileSystemViewListCellRenderer.class.getName()).log(Level.WARNING, null, t);
                }
            }

        }
        return label;
    }
}
