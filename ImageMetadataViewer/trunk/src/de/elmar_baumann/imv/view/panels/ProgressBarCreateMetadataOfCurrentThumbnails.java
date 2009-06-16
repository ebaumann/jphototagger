package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.resource.MutualExcludedResource;
import javax.swing.JProgressBar;

/**
 * Synchronized access to
 * {@link AppPanel#getProgressBarCreateMetadataOfCurrentThumbnails()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/16
 */
public final class ProgressBarCreateMetadataOfCurrentThumbnails extends MutualExcludedResource {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JProgressBar progressBar =
            appPanel.getProgressBarCreateMetadataOfCurrentThumbnails();
    public static final ProgressBarCreateMetadataOfCurrentThumbnails INSTANCE =
            new ProgressBarCreateMetadataOfCurrentThumbnails();

    private ProgressBarCreateMetadataOfCurrentThumbnails() {
        setResource(progressBar);
    }
}
