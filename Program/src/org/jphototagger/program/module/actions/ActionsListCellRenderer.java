package org.jphototagger.program.module.actions;

import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * @author Elmar Baumann
 */
public final class ActionsListCellRenderer extends DefaultListCellRenderer {

    private static final Icon ICON_ACTION = AppLookAndFeel.getIcon("icon_action.png");
    private static final Icon ICON_ERROR = AppLookAndFeel.getIcon("icon_error.png");
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Program program = (Program) value;

        label.setText(program.getAlias());

        File file = program.getFile();

        if (file.exists()) {
            try {
                setIcon(ICON_ACTION);
            } catch (Exception ex) {
                Logger.getLogger(ActionsListCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            label.setIcon(ICON_ERROR);
        }

        return label;
    }
}
