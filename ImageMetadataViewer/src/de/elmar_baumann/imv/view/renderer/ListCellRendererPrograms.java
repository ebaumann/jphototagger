package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.data.Program;
import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/04
 */
public class ListCellRendererPrograms extends DefaultListCellRenderer {

    private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
    private static final Icon iconError = AppSettings.getIcon("icon_error.png"); // NOI18N

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Program program = (Program) value;
        label.setText(program.getAlias());
        File file = program.getFile();
        if (file.exists()) {
            try {
                setIcon(fileSystemView.getSystemIcon(file));
            } catch (Exception ex) {
                de.elmar_baumann.imv.Log.logWarning(getClass(), ex);
            }
        } else {
            label.setIcon(iconError);
        }
        return label;
    }
}
