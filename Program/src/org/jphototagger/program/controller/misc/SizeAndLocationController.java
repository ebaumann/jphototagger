package org.jphototagger.program.controller.misc;

import org.jphototagger.program.UserSettings;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Listens to <code>windowOpend()</code> and <code>windowClosing()</code> and
 * reads and writes the size and location of a component to {@link UserSettings}.
 * <p>
 * Usage: Bevor setting a component visible, call
 * <code>Component#addWindowListener(new SizeAndLocationController())</code> or
 * use a singleton instance rather than creation a new.
 *
 * @author Elmar Baumann
 */
public final class SizeAndLocationController extends WindowAdapter {
    @Override
    public void windowOpened(WindowEvent evt) {
        UserSettings.INSTANCE.getSettings().applySizeAndLocation(
            evt.getComponent());
    }

    @Override
    public void windowClosing(WindowEvent evt) {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(
                evt.getComponent());
        UserSettings.INSTANCE.writeToFile();
    }
}
