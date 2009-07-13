package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Factorie's utils.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/02/17
 */
final class Util {

    /**
     * Checks whether a factory is initialized more than one times. Logs an
     * error message if a factory was previously initialized.
     *
     * @param  c    Factory class
     * @param  init true if the factory is already initialized
     */
    static void checkInit(Class c, boolean init) {
        if (init) {
            AppLog.logWarning(MetaFactory.class, Bundle.getString(
                    "FactoryMessages.ErrorMessage.InitCalledMoreThanOneTimes", // NOI18N
                    c.getName()));
        }
    }

    private Util() {
    }
}
