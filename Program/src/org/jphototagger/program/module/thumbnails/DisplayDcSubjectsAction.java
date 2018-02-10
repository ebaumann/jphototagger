package org.jphototagger.program.module.thumbnails;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.api.component.Selectable;
import org.jphototagger.api.preferences.CommonPreferences;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class DisplayDcSubjectsAction extends AbstractAction implements Selectable {

    private static final long serialVersionUID = 1L;
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private boolean isDisplay = prefs.getBoolean(CommonPreferences.KEY_DISPLAY_DC_SUBJECT_COUNT);

    public DisplayDcSubjectsAction() {
        super(Bundle.getString(DisplayDcSubjectsAction.class, "DisplayDcSubjectsAction.Name"));
        putValue(SHORT_DESCRIPTION, Bundle.getString(DisplayDcSubjectsAction.class, "DisplayDcSubjectsAction.Description"));
        putValue(SELECTED_KEY, isDisplay);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setSelected(!isDisplay);
    }

    @Override
    public void setSelected(boolean selected) {
        if (selected != isDisplay) {
            isDisplay = selected;
            prefs.setBoolean(CommonPreferences.KEY_DISPLAY_DC_SUBJECT_COUNT, isDisplay);
        }
    }

    @Override
    public boolean isSelected() {
        return isDisplay;
    }
}
