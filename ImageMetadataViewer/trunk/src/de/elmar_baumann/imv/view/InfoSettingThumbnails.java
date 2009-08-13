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

    private final DialogDisplayer dlgDisplayer = new DialogDisplayer();

    /**
     * Shows the information.
     */
    public InfoSettingThumbnails() {
        dlgDisplayer.start();
    }

    /**
     * Hides the information.
     */
    public void hide() {
        dlgDisplayer.hide();
    }

    private class DialogDisplayer extends Thread {

        private final ProgressDialog dlg = new ProgressDialog(null);
        private boolean show = true;

        @Override
        public void run() {
            synchronized (dlg) {
                if (show) {
                    dlg.setIndeterminate(true);
                    dlg.setInfoText(
                            Bundle.getString("InfoSettingThumbnails.Info"));
                    dlg.setVisible(true);
                }
            }
        }

        public void hide() {
            synchronized (dlg) {
                show = false;
                dlg.setVisible(false);
            }
        }
    }
}
