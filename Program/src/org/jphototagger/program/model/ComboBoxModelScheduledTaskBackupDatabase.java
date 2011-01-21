package org.jphototagger.program.model;

import org.jphototagger.program.tasks.ScheduledTaskBackupDatabase.Interval;

import javax.swing.DefaultComboBoxModel;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelScheduledTaskBackupDatabase
        extends DefaultComboBoxModel {
    private static final long serialVersionUID = -5248869581490789742L;

    public ComboBoxModelScheduledTaskBackupDatabase() {
        addElements();
    }

    private void addElements() {
        for (Interval interval : Interval.values()) {
            addElement(interval);
        }
    }
}
