package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.app.AppLog;
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
public final class ListCellRendererPrograms extends DefaultListCellRenderer {

    private static final FileSystemView FILE_SYSTEM_VIEW =
            FileSystemView.getFileSystemView();
    private static final Icon ICON_ERROR = AppIcons.getIcon("icon_error.png"); // NOI18N

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        Program program = (Program) value;
        label.setText(program.getAlias());
        File file = program.getFile();
        if (file.exists()) {
            if (file.exists()) {
                synchronized (FILE_SYSTEM_VIEW) {
                    try {
                        setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
                    } catch (Exception ex) {
                        AppLog.logWarning(ListCellRendererPrograms.class, ex);
                    }
                }
            }
        } else {
            label.setIcon(ICON_ERROR);
        }
        return label;
    }
}
