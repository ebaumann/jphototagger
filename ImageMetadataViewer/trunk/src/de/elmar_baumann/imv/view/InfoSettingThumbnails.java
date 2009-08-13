package de.elmar_baumann.imv.view;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.dialog.ProgressDialog;

/**
 * Shows an information: Setting thumbnails.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-14
 */
public final class InfoSettingThumbnails {

    private final ProgressDialog dlg = new ProgressDialog(null);

    /**
     * Shows the information.
     */
    public InfoSettingThumbnails() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                dlg.setIndeterminate(true);
                dlg.setInfoText(Bundle.getString("InfoSettingThumbnails.Info"));
                dlg.setVisible(true);
            }
        }).start();
    }

    /**
     * Hides the information.
     */
    public void hide() {
        dlg.setVisible(false);
    }
}
