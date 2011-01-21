package org.jphototagger.program.event.listener;

/**
 * Listen for exits of the VM.
 *
 * @author Elmar Baumann
 */
public interface AppExitListener {

    /**
     * Will be called before the application exists. Listeners can release
     * resources, writing persistent content etc.
     */
    void appWillExit();
}
