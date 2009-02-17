package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.Log;
import de.elmar_baumann.imv.resource.Bundle;
import java.text.MessageFormat;

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
            MessageFormat msg = new MessageFormat(Bundle.getString("FactoryMessages.ErrorMessage.InitCalledMoreThanOneTimes"));
            Object[] params = {c.getName()};
            Log.logWarning(MetaFactory.class, msg.format(params));
        }
    }

    private Util() {
    }
}
