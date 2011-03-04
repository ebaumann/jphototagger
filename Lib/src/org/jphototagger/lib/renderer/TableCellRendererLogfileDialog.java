package org.jphototagger.lib.renderer;

import org.jphototagger.lib.image.util.IconUtil;
import org.jphototagger.lib.resource.JslBundle;

import java.awt.Color;
import java.awt.Component;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * Renders the {@link java.util.logging.Level} icons displayed in the GUI of
 * {@link org.jphototagger.lib.dialog.LogfileDialog}. Also formats dates and
 * selected table rows.
 *
 * @author Elmar Baumann
 */
public final class TableCellRendererLogfileDialog implements TableCellRenderer {
    private static final Color SEL_BACKGROUND_COLOR = new Color(251, 225, 146);
    private static final SimpleDateFormat DATE_FORMAT =
        new SimpleDateFormat(JslBundle.INSTANCE.getString("TableCellRendererLogfileDialog.DateFormat"));
    private static final Map<Level, ImageIcon> ICON_OF_LEVEL = new HashMap<Level, ImageIcon>();

    static {
        ICON_OF_LEVEL.put(Level.CONFIG,
                          IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_logfiledialog_config.png"));
        ICON_OF_LEVEL.put(Level.FINE,
                          IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_logfiledialog_fine.png"));
        ICON_OF_LEVEL.put(Level.FINER,
                          IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_logfiledialog_finer.png"));
        ICON_OF_LEVEL.put(Level.FINEST,
                          IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_logfiledialog_finest.png"));
        ICON_OF_LEVEL.put(Level.INFO,
                          IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_logfiledialog_info.png"));
        ICON_OF_LEVEL.put(Level.SEVERE,
                          IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_logfiledialog_severe.png"));
        ICON_OF_LEVEL.put(Level.WARNING,
                          IconUtil.getImageIcon("/org/jphototagger/lib/resource/icons/icon_logfiledialog_warning.png"));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        JLabel cellLabel = new JLabel();

        if (value instanceof Level) {
            cellLabel.setIcon(getIcon((Level) value));
        } else if (value instanceof Date) {
            renderDate(cellLabel, (Date) value);
        } else {
            cellLabel.setText(value.toString());
        }

        renderSelection(cellLabel, isSelected);

        return cellLabel;
    }

    private void renderSelection(JLabel cellLabel, boolean isSelected) {
        cellLabel.setForeground(Color.BLACK);
        cellLabel.setBackground(isSelected
                                ? SEL_BACKGROUND_COLOR
                                : Color.WHITE);
        cellLabel.setOpaque(true);
    }

    private void renderDate(JLabel cellLabel, Date date) {
        cellLabel.setText(DATE_FORMAT.format(date));
    }

    private static Icon getIcon(Level level) {
        return ICON_OF_LEVEL.get(level);
    }
}
