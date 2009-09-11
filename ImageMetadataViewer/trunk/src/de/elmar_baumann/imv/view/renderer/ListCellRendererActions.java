package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.app.AppLookAndFeel;
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.Program;
import java.awt.Component;
import java.io.File;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-06
 */
public final class ListCellRendererActions extends DefaultListCellRenderer {

    private static final Icon ICON_ACTION = AppLookAndFeel.getIcon("icon_action.png"); // NOI18N
    private static final Icon ICON_ERROR = AppLookAndFeel.getIcon("icon_error.png"); // NOI18N

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);
        Program program = (Program) value;
        label.setText(program.getAlias());
        File file = program.getFile();
        if (file.exists()) {
            try {
                setIcon(ICON_ACTION);
            } catch (Exception ex) {
                AppLog.logSevere(ListCellRendererActions.class, ex);
            }
        } else {
            label.setIcon(ICON_ERROR);
        }
        return label;
    }
}
