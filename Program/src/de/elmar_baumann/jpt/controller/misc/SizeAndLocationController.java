package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.UserSettings;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-14
 */
public final class SizeAndLocationController extends WindowAdapter {

    @Override
    public void windowOpened(WindowEvent e) {
        UserSettings.INSTANCE.getSettings().applySizeAndLocation(e.getComponent());
    }

    @Override
    public void windowClosing(WindowEvent e) {
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(e.getComponent());
        UserSettings.INSTANCE.writeToFile();
    }
}
