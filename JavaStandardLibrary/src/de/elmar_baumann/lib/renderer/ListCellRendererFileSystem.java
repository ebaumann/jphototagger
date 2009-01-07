package de.elmar_baumann.lib.renderer;

import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;

/**
 * Rendert ein passendes Dateisystem-Icon vor jedes Item.
 * 
 * <em>Es wird erwartet, dass die Items Instanzen der Klasse java.io.File sind!</em>
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public final class ListCellRendererFileSystem extends DefaultListCellRenderer {

    private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private boolean fullPathName;

    /**
     * Konstruktor.
     * 
     * @param fullPathName true, wenn der komplette Pfad angezeigt werden soll,
     *                     false, wenn nur der Name der letzten Pfadkomponente
     *                     angezeigt werden soll.
     *                     Default: false;
     */
    public ListCellRendererFileSystem(boolean fullPathName) {
        this.fullPathName = fullPathName;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
            index, isSelected, cellHasFocus);
        if (value instanceof File) {
            File file = (File) value;
            label.setIcon(fileSystemView.getSystemIcon(file));
            label.setText(fullPathName ? file.getAbsolutePath() : file.getName());
        }
        return label;
    }
}
