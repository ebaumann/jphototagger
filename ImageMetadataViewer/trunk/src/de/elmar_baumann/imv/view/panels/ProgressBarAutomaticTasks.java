package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.resource.MutualExcludedResource;
import javax.swing.JProgressBar;

/**
 * Synchronized access to
 * {@link AppPanel#getProgressBarAutomaticTasks()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/16
 */
public final class ProgressBarAutomaticTasks extends MutualExcludedResource {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JProgressBar progressBar =
            appPanel.getProgressBarAutomaticTasks();
    public static final ProgressBarAutomaticTasks INSTANCE =
            new ProgressBarAutomaticTasks();

    private ProgressBarAutomaticTasks() {
        setResource(progressBar);
    }
}
