package org.jphototagger.program.app.logging;

import java.awt.Component;
import java.util.logging.Level;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * @author Elmar Baumann
 */
public final class LogLevelComboBoxModel extends DefaultComboBoxModel {

    private static final long serialVersionUID = 1L;

    public LogLevelComboBoxModel() {
        addElements();
    }

    private void addElements() {
        addElement(Level.INFO);
        addElement(Level.FINER);
        addElement(Level.FINEST);
        addElement(Level.ALL);
    }

    public static ListCellRenderer createRenderer() {
        return new DefaultListCellRenderer() {

            private static final long serialVersionUID = 1L;

            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Level) {
                    Level level = (Level) value;
                    label.setText(level.getLocalizedName());
                }
                return label;
            }
        };
    }
}
