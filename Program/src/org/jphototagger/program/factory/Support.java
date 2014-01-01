package org.jphototagger.program.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.messages.MessageType;
import org.jphototagger.api.windows.MainWindowManager;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
final class Support {

    private static final Logger LOGGER = Logger.getLogger(Support.class.getName());
    private final Map<Class<?>, List<Object>> classInstances = new HashMap<>();

    @SuppressWarnings("unchecked")
    synchronized <T> List<T> getAll(Class<T> type) {
        return Collections.unmodifiableList((List<T>) classInstances.get(type));
    }

    @SuppressWarnings("unchecked")
    synchronized <T> T getFirst(Class<T> type) {
        List<T> instances = (List<T>) classInstances.get(type);
        return (instances == null)
                ? null
                : instances.get(0);
    }

    synchronized void add(Object instance) {
        List<Object> instances = classInstances.get(instance.getClass());
        if (instances == null) {
            instances = new ArrayList<>();
            classInstances.put(instance.getClass(), instances);
        }
        instances.add(instance);
    }

    static void setStatusbarInfo(final String message) {
        MainWindowManager messageDisplayer = Lookup.getDefault().lookup(MainWindowManager.class);
        messageDisplayer.setMainWindowStatusbarText(message, MessageType.INFO, 2000);
    }

    /**
     * Checks whether a factory is initialized more than one times. Logs an
     * error message if a factory was previously initialized.
     *
     * @param  c    Factory class
     * @param  init true if the factory is already initialized
     * @return      true if not initialized
     */
    static boolean checkInitOnlyOneTimes(Class<?> c, boolean init) {
        if (init) {
            LOGGER.log(Level.WARNING, "{0}: Initalized Meta-Factory again!", c.getName());
            return false;
        }
        return true;
    }
}
