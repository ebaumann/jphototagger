package de.elmar_baumann.imv.resource;

import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.lib.resource.MutualExcludedResource;
import javax.swing.JProgressBar;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public final class ProgressBarCurrentTasks extends MutualExcludedResource {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JProgressBar progressBar = appPanel.getProgressBarCurrentTasks();
    public static final ProgressBarCurrentTasks INSTANCE = new ProgressBarCurrentTasks();

    private ProgressBarCurrentTasks() {
        setResource(progressBar);
    }
}
