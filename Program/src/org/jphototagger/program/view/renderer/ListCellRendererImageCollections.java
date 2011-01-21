package org.jphototagger.program.view.renderer;

import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.model.ListModelImageCollections;

import java.awt.Color;
import java.awt.Component;

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ListCellRendererImageCollections
        extends DefaultListCellRenderer {
    private static final Icon ICON_DEFAULT =
        AppLookAndFeel.getIcon("icon_imagecollection.png");
    private static final Color             SPECIAL_COLLECTION_FOREGROUND =
        Color.BLUE;
    private static final Map<Object, Icon> ICON_OF_VALUE                 =
        new HashMap<Object, Icon>();
    private static final long serialVersionUID = -431048760716078334L;
    private int               tempSelRow       = -1;

    {
        ICON_OF_VALUE.put(
            ListModelImageCollections.NAME_IMAGE_COLLECTION_PREV_IMPORT,
            AppLookAndFeel.getIcon("icon_card.png"));
        ICON_OF_VALUE.put(
            ListModelImageCollections.NAME_IMAGE_COLLECTION_PICKED,
            AppLookAndFeel.getIcon("icon_picked.png"));
        ICON_OF_VALUE.put(
            ListModelImageCollections.NAME_IMAGE_COLLECTION_REJECTED,
            AppLookAndFeel.getIcon("icon_rejected.png"));
    }

    public ListCellRendererImageCollections() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value,
                           index, isSelected, cellHasFocus);
        boolean specialCollection =
            ListModelImageCollections.isSpecialCollection(value.toString());
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow  = index == tempSelRow;

        label.setForeground((isTempSelRow || (isSelected &&!tempSelExists))
                            ? AppLookAndFeel.getListSelectionForeground()
                            : specialCollection
                              ? SPECIAL_COLLECTION_FOREGROUND
                              : AppLookAndFeel.getListForeground());
        label.setBackground((isTempSelRow || (isSelected &&!tempSelExists))
                            ? AppLookAndFeel.getListSelectionBackground()
                            : AppLookAndFeel.getListBackground());
        label.setIcon(getIconOfValue(value));

        return label;
    }

    private Icon getIconOfValue(Object value) {
        Icon icon = ICON_OF_VALUE.get(value);

        return (icon == null)
               ? ICON_DEFAULT
               : icon;
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
