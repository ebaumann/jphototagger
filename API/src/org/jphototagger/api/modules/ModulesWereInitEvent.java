package org.jphototagger.api.modules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ModulesWereInitEvent {

    private final Object source;
    private final Collection<? extends Module> loadedModules;

    public ModulesWereInitEvent(Object source, Collection<? extends Module> loadedModules) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (loadedModules == null) {
            throw new NullPointerException("loadedModules == null");
        }

        this.source = source;
        this.loadedModules = new ArrayList<Module>(loadedModules);
    }

    public Collection<? extends Module> getLoadedModules() {
        return Collections.unmodifiableCollection(loadedModules);
    }

    public Object getSource() {
        return source;
    }
}
