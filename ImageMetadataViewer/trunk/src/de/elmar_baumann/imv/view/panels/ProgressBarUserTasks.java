package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.resource.MutualExcludedResource;
import javax.swing.JProgressBar;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public final class ProgressBarUserTasks extends MutualExcludedResource {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JProgressBar progressBar = appPanel.getProgressBarUserTasks();
    public static final ProgressBarUserTasks INSTANCE =
            new ProgressBarUserTasks();

    private ProgressBarUserTasks() {
        setResource(progressBar);
    }
}
