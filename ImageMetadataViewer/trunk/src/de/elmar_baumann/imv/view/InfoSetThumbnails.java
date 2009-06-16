package de.elmar_baumann.imv.view;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.view.panels.ProgressBarCreateMetadataOfCurrentThumbnails;
import javax.swing.JProgressBar;

/**
 * Shows an information: Setting thumbnails.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/14
 */
public final class InfoSetThumbnails {

    private final ProgressBarCreateMetadataOfCurrentThumbnails progressBarProvider =
            ProgressBarCreateMetadataOfCurrentThumbnails.INSTANCE;
    private final JProgressBar progressBar;

    /**
     * Shows the information.
     */
    public InfoSetThumbnails() {
        progressBar = (JProgressBar) progressBarProvider.getResource(this);
        if (progressBar != null) {
            progressBar.setStringPainted(true);
            progressBar.setString(Bundle.getString("InfoSetThumbnails.Text"));
            progressBar.setIndeterminate(true);
        }
    }

    /**
     * Hides the information.
     */
    public void hide() {
        if (progressBar != null) {
            progressBar.setIndeterminate(false);
            progressBar.setStringPainted(false);
            progressBarProvider.releaseResource(this);
        }
    }
}
