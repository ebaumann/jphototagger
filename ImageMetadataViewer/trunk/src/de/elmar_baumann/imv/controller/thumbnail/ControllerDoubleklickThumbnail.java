package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.io.IoUtil;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;

/**
 * Kontroller für die Aktion: Doppelklick auf ein Thumbnail ausgelöst von
 * {@link de.elmar_baumann.imv.view.panels.ThumbnailsPanel}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-10
 */
public final class ControllerDoubleklickThumbnail {

    private final ThumbnailsPanel panel;

    public ControllerDoubleklickThumbnail(ThumbnailsPanel panel) {
        this.panel = panel;
    }

    public void doubleClickAtIndex(int index) {
        openImage(index);
    }

    private void openImage(int index) {
        if (panel.isIndex(index)) {
            IoUtil.execute(IoUtil.quoteForCommandLine(
                    UserSettings.INSTANCE.getDefaultImageOpenApp()),
                    IoUtil.quoteForCommandLine(panel.getFile(index)));
        }
    }
}
