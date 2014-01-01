package org.jphototagger.program.module.programs;

import java.awt.Component;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * @author Elmar Baumann
 */
public final class ProgramsListCellRenderer extends DefaultListCellRenderer {

    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private static final Icon ICON_ERROR = AppLookAndFeel.getIcon("icon_error.png");
    private static final long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Program program = (Program) value;
        label.setText(program.getAlias());
        File file = program.getFile();
        if (file.exists()) {
            if (file.exists()) {
                synchronized (FILE_SYSTEM_VIEW) {
                    try {
                        setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
                    } catch (Throwable t) {
                        Logger.getLogger(ProgramsListCellRenderer.class.getName()).log(Level.SEVERE, null, t);
                    }
                }
            }
        } else {
            label.setIcon(ICON_ERROR);
        }
        return label;
    }
}
