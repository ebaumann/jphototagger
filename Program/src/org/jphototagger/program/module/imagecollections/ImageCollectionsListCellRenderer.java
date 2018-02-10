package org.jphototagger.program.module.imagecollections;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class ImageCollectionsListCellRenderer extends DefaultListCellRenderer {

    private static final Icon ICON_DEFAULT = Icons.getIcon("icon_imagecollection.png");
    private static final Color SPECIAL_COLLECTION_FOREGROUND = Color.BLUE;
    private static final Map<Object, Icon> ICON_OF_SPECIAL_COLLECTION = new HashMap<>();
    private static final long serialVersionUID = 1L;
    private int tempSelRow = -1;

    {
        ICON_OF_SPECIAL_COLLECTION.put(ImageCollection.PREVIOUS_IMPORT_NAME, Icons.getIcon("icon_card.png"));
        ICON_OF_SPECIAL_COLLECTION.put(ImageCollection.PICKED_NAME, Icons.getIcon("icon_picked.png"));
        ICON_OF_SPECIAL_COLLECTION.put(ImageCollection.REJECTED_NAME, Icons.getIcon("icon_rejected.png"));
    }

    public ImageCollectionsListCellRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        boolean specialCollection = ImageCollection.isSpecialCollection(value.toString());
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow = index == tempSelRow;
        label.setForeground((isTempSelRow || (isSelected && !tempSelExists))
                ? AppLookAndFeel.getListSelectionForeground()
                : specialCollection
                ? SPECIAL_COLLECTION_FOREGROUND
                : AppLookAndFeel.getListForeground());
        label.setBackground((isTempSelRow || (isSelected && !tempSelExists))
                ? AppLookAndFeel.getListSelectionBackground()
                : AppLookAndFeel.getListBackground());
        label.setIcon(getIconOfValue(value));
        if (value instanceof String) {
            label.setText(ImageCollection.getLocalizedName((String) value));
        }
        return label;
    }

    private Icon getIconOfValue(Object value) {
        Icon icon = ICON_OF_SPECIAL_COLLECTION.get(value);
        return icon == null
                ? ICON_DEFAULT
                : icon;
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }
}
