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
public class ProgressBarCurrentTasks extends MutualExcludedResource {

    private AppPanel appPanel = Panels.getInstance().getAppPanel();
    private JProgressBar progressBar = appPanel.getProgressBarCurrentTasks();
    private static ProgressBarCurrentTasks instance = new ProgressBarCurrentTasks();

    public static ProgressBarCurrentTasks getInstance() {
        return instance;
    }

    private ProgressBarCurrentTasks() {
        setResource(progressBar);
    }
}
