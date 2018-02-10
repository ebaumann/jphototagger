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
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class ActionsListCellRenderer extends DefaultListCellRenderer {

    private static final Icon ICON_ACTION = Icons.getIcon("icon_action.png");
    private static final Icon ICON_ERROR = Icons.getIcon("icon_error.png");
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
            } catch (Throwable t) {
                Logger.getLogger(ActionsListCellRenderer.class.getName()).log(Level.SEVERE, null, t);
            }
        } else {
            label.setIcon(ICON_ERROR);
        }

        return label;
    }
}
