package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.resource.MutualExcludedResource;

/**
 * Synchronized access to
 * {@link AppPanel#getProgressBarAutomaticTasks()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-16
 */
public final class ProgressBarAutomaticTasks extends MutualExcludedResource {

    public static final ProgressBarAutomaticTasks INSTANCE =
            new ProgressBarAutomaticTasks();

    private ProgressBarAutomaticTasks() {
        setResource(GUI.INSTANCE.getAppPanel().getProgressBarAutomaticTasks());
    }
}
