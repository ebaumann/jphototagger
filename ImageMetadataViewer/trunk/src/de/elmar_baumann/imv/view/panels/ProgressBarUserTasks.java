package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.resource.MutualExcludedResource;
import javax.swing.JProgressBar;

/**
 * Synchronized access to
 * {@link AppPanel#getProgressBarUserTasks()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class ProgressBarUserTasks
        extends MutualExcludedResource<JProgressBar> {

    public static final ProgressBarUserTasks INSTANCE =
            new ProgressBarUserTasks();

    private ProgressBarUserTasks() {
        setResource(GUI.INSTANCE.getAppPanel().getProgressBarUserTasks());
    }
}
