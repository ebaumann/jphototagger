package org.jphototagger.api.modules;

import org.jphototagger.api.plugin.Plugin;

/**
 * A module will be automatically initialized by the application
 * (through calling {@link #start()}) in opposite to a
 * {@link Plugin} which will be invoked only on demand.
 * <p>
 * Modules will be collected through the Java Service Provider
 * Interface.
 *
 * @author Elmar Baumann
 */
public interface Module {

    /**
     * Called from the Framework, initializes a module
     */
    void start();

    /**
     * Called from the Framework before the application exits or removes the module
     */
    void close();
}
