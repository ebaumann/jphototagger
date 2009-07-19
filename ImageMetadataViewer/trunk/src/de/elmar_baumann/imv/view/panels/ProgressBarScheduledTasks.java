package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.resource.MutualExcludedResource;

/**
 * Synchronized access to
 * {@link AppPanel#getProgressBarScheduledTasks()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-17
 */
public final class ProgressBarScheduledTasks extends MutualExcludedResource {

    public static final ProgressBarScheduledTasks INSTANCE =
            new ProgressBarScheduledTasks();

    private ProgressBarScheduledTasks() {
        setResource(GUI.INSTANCE.getAppPanel().getProgressBarScheduledTasks());
    }
}
