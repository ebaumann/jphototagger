package org.jphototagger.program.module.keywords.list;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.CommonPreferences;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.domain.repository.DcSubjectsStatistics;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.program.app.ui.ListCellRendererExt;
import org.jphototagger.resources.Icons;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class KeywordsListCellRenderer extends ListCellRendererExt {

    private static final long serialVersionUID = 1L;
    private static final DcSubjectsStatistics STATISTICS = Lookup.getDefault().lookup(DcSubjectsStatistics.class);
    private static final Icon ICON = Icons.getIcon("icon_keyword.png");
    private boolean isDisplayCount = Lookup.getDefault().lookup(Preferences.class).getBoolean(CommonPreferences.KEY_DISPLAY_DC_SUBJECT_COUNT);
    private Component component;

    public KeywordsListCellRenderer() {
        AnnotationProcessor.process(this);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        component = list;

        JLabel label = (JLabel) super.getListCellRendererComponent(list, getValue(value), index, isSelected, cellHasFocus);
        int tempSelRow = getTempSelectionRow();
        boolean tempSelRowIsSelected = tempSelRow < 0 ? false : list.isSelectedIndex(tempSelRow);

        setColors(index, isSelected, tempSelRowIsSelected, label);
        label.setIcon(ICON);

        return label;
    }

    private Object getValue(Object value) {
        if (isDisplayCount && value instanceof String) {
            String keyword = (String) value;
            return keyword + "   [" + STATISTICS.getImageCountOfDcSubject(keyword) + "]";
        }
        return value;
    }

    // ListItemTempSelectionRowSetter calls this reflective not if only in super class defined
    @Override
    public void setTempSelectionRow(int index) {
        super.setTempSelectionRow(index);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (evt.isKey(CommonPreferences.KEY_DISPLAY_DC_SUBJECT_COUNT)) {
            isDisplayCount = (boolean) evt.getNewValue();
            if (component != null) {
                ComponentUtil.forceRepaint(component);
            }
        }
    }
}
