package org.jphototagger.api.modules;

/**
 * A module will be automatically initialized by the application
 * (through calling {@code #init()}) in opposite to a
 * {@code Plugin} which will be invoked only on demand.
 * <p>
 * Modules will be picked up from the application through the
 * Java Service Provider Interface (SPI).
 *
 * @author Elmar Baumann
 */
public interface Module {

    /**
     * Called from the Framework, the module shall initialize itself.
     */
    void init();

    /**
     * Called from the Framework if the module shall not longer be available,
     * at least called before the application quits.
     */
    void remove();
}
