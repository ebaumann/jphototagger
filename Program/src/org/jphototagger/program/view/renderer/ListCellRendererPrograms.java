package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.data.Program;

import java.awt.Component;

import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.filechooser.FileSystemView;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererPrograms extends DefaultListCellRenderer {
    private static final FileSystemView FILE_SYSTEM_VIEW = FileSystemView.getFileSystemView();
    private static final Icon ICON_ERROR = AppLookAndFeel.getIcon("icon_error.png");
    private static final long serialVersionUID = 8523795184154878875L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Program program = (Program) value;

        label.setText(program.getAlias());

        File file = program.getFile();

        if (file.exists()) {
            if (file.exists()) {
                synchronized (FILE_SYSTEM_VIEW) {
                    try {
                        setIcon(FILE_SYSTEM_VIEW.getSystemIcon(file));
                    } catch (Exception ex) {
                        AppLogger.logSevere(ListCellRendererPrograms.class, ex);
                    }
                }
            }
        } else {
            label.setIcon(ICON_ERROR);
        }

        return label;
    }
}
