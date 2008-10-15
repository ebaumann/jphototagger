package de.elmar_baumann.imv.event;

/**
 * Listen to exiting the VM.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/15
 */
public interface AppExitListener {

    /**
     * Tells, that the application will be existed. Listeners can release
     * resources, write persistent, ...
     */
    public void appWillExit();
}
