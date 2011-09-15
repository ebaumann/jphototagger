package org.jphototagger.program.controller.misc;

import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.jphototagger.api.storage.Storage;
import org.openide.util.Lookup;

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
        Storage storage = Lookup.getDefault().lookup(Storage.class);
        Component component = evt.getComponent();
        String key = component.getClass().getName();

        storage.applySize(key, component);
        storage.applyLocation(key, component);
    }

    @Override
    public void windowClosing(WindowEvent evt) {
        Storage storage = Lookup.getDefault().lookup(Storage.class);
        Component component = evt.getComponent();
        String key = component.getClass().getName();

        storage.setSize(key, component);
        storage.setLocation(key, component);
    }
}
