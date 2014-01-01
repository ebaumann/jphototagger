package org.jphototagger.program.module.thumbnails;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.api.component.Selectable;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.thumbnails.ThumbnailFlag;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
final class DisplaySidecarFlagAction extends AbstractAction implements Selectable {

    private static final long serialVersionUID = 1L;
    private static final String PREF_KEY_SELECTED = "DisplaySidecarFlagAction.Selected";
    private boolean selected = getSelectedFromThumbnailsDisplayer();

    DisplaySidecarFlagAction() {
        super(Bundle.getString(DisplaySidecarFlagAction.class, "DisplaySidecarFlagAction.Name"));
        setSelectedKey();
        restore();
    }

    private void setSelectedKey() {
        putValue(SELECTED_KEY, selected ? Boolean.TRUE : null);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        toggleMetaDataOverlay();
        persist();
    }

    private void toggleMetaDataOverlay() {
        ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
        boolean isDisplay = !thumbnailsDisplayer.isDisplayFlag(ThumbnailFlag.HAS_SIDECAR_FILE);
        thumbnailsDisplayer.setDisplayFlag(ThumbnailFlag.HAS_SIDECAR_FILE, isDisplay);
        selected = isDisplay;
        setSelectedKey();
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        setSelectedKey();
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    private boolean getSelectedFromThumbnailsDisplayer() {
        ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
        return thumbnailsDisplayer == null
                ? false
                : thumbnailsDisplayer.isDisplayFlag(ThumbnailFlag.HAS_SIDECAR_FILE);
    }

    private void persist() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setBoolean(PREF_KEY_SELECTED, isSelected());
    }

    private void restore() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs.containsKey(PREF_KEY_SELECTED)) {
            ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
            boolean sel = prefs.getBoolean(PREF_KEY_SELECTED);
            setSelected(sel);
            thumbnailsDisplayer.setDisplayFlag(ThumbnailFlag.HAS_SIDECAR_FILE, sel);
        }
    }
}
