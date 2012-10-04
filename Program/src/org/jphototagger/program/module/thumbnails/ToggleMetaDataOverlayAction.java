package org.jphototagger.program.module.thumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import org.jphototagger.api.component.Selectable;
import org.jphototagger.domain.thumbnails.ThumbnailsDisplayer;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author  Martin Pohlack, Elmar Baumann
 */
final class ToggleMetaDataOverlayAction extends AbstractAction implements Selectable {

    private static final long serialVersionUID = 1L;
    private boolean selected = getSelectedFromThumbnailsDisplayer();

    ToggleMetaDataOverlayAction() {
        super(Bundle.getString(ToggleMetaDataOverlayAction.class, "ToggleMetaDataOverlayAction.Name"));
        putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_O));
        setSelectedKey();
    }

    private void setSelectedKey() {
        putValue(SELECTED_KEY, selected ? Boolean.TRUE : null);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        toggleMetaDataOverlay();
    }

    private void toggleMetaDataOverlay() {
        ThumbnailsDisplayer thumbnailsDisplayer = Lookup.getDefault().lookup(ThumbnailsDisplayer.class);
        boolean isKeywordsOverlay = !thumbnailsDisplayer.isMetaDataOverlay();
        thumbnailsDisplayer.setMetaDataOverlay(isKeywordsOverlay);
        selected = isKeywordsOverlay;
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
                : thumbnailsDisplayer.isMetaDataOverlay();
    }
}
