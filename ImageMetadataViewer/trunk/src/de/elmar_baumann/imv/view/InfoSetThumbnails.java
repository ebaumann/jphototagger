package de.elmar_baumann.imv.view;

import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import javax.swing.JProgressBar;

/**
 * Shows an information: Setting thumbnails.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/14
 */
public final class InfoSetThumbnails {

    private final JProgressBar progressBar = GUI.INSTANCE.getAppPanel().
            getProgressBarCreateMetadataOfCurrentThumbnails();
    private final String oldString;

    /**
     * Shows the information.
     */
    public InfoSetThumbnails() {
        oldString = progressBar.getString();
        progressBar.setStringPainted(true);
        progressBar.setString(Bundle.getString("InfoSetThumbnails.Text"));
        progressBar.setIndeterminate(true);
    }

    /**
     * Hides the information.
     */
    public void hide() {
        progressBar.setIndeterminate(false);
        progressBar.setString(oldString);
        progressBar.setStringPainted(false);
    }
}
