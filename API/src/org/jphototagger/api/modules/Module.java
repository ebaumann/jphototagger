package org.jphototagger.api.modules;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface Module {

    /**
     * Called from the Framework, initializes a module
     */
    void init();

    /**
     * Called from the Framework before the application exits or removes the module
     */
    void close();
}
