package org.jphototagger.program.module.imagecollections;

import java.awt.Color;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.domain.imagecollections.ImageCollection;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ImageCollectionsListCellRenderer extends DefaultListCellRenderer {

    private static final Icon ICON_DEFAULT = Icons.getIcon("icon_imagecollection.png");
    private static final Icon ICON_TARGET_COLLECTION = Icons.getIcon("icon_imagecollection_target.png");
    private static final Color SPECIAL_COLLECTION_FOREGROUND = Color.BLUE;
    private static final Color TARGET_COLLECTION_FOREGROUND = new Color(0, 125, 0);
    private static final Map<Object, Icon> ICON_OF_SPECIAL_COLLECTION = new HashMap<>();
    private static final long serialVersionUID = 1L;
    private String targetCollectionName;
    private int tempSelRow = -1;

    static {
        ICON_OF_SPECIAL_COLLECTION.put(ImageCollection.PREVIOUS_IMPORT_NAME, Icons.getIcon("icon_card.png"));
        ICON_OF_SPECIAL_COLLECTION.put(ImageCollection.PICKED_NAME, Icons.getIcon("icon_picked.png"));
        ICON_OF_SPECIAL_COLLECTION.put(ImageCollection.REJECTED_NAME, Icons.getIcon("icon_rejected.png"));
    }

    public ImageCollectionsListCellRenderer() {
        init();
    }

    private void init() {
        setOpaque(true);
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        targetCollectionName = prefs.getString(TargetCollectionController.KEY_TARGET_COLLECTION_NAME);
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass=PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent e) {
        if (TargetCollectionController.KEY_TARGET_COLLECTION_NAME.equals(e.getKey())) {
            targetCollectionName = (String) e.getNewValue();
        }
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        boolean specialCollection = ImageCollection.isSpecialCollection(value.toString());
        boolean tempSelExists = tempSelRow >= 0;
        boolean isTempSelRow = index == tempSelRow;
        boolean targetCollection = isTargetCollection(value);
        label.setForeground((isTempSelRow || (isSelected && !tempSelExists))
                ? AppLookAndFeel.getListSelectionForeground()
                : specialCollection
                ? SPECIAL_COLLECTION_FOREGROUND
                : targetCollection
                ? TARGET_COLLECTION_FOREGROUND
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
        Icon iconSpecialCollection = ICON_OF_SPECIAL_COLLECTION.get(value);
        return isTargetCollection(value)
                ? ICON_TARGET_COLLECTION
                : iconSpecialCollection == null
                ? ICON_DEFAULT
                : iconSpecialCollection;
    }

    public void setTempSelectionRow(int index) {
        tempSelRow = index;
    }

    private boolean isTargetCollection(Object value) {
        return StringUtil.hasContent(targetCollectionName)
                && Objects.equals(targetCollectionName, value);
    }
}
