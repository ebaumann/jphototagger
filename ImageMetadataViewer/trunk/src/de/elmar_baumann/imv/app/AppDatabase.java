package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.database.DatabaseTables;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Initializes the application's database.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-11
 */
public final class AppDatabase {

    private static boolean init;

    public synchronized static void init() {
        assert !init;
        if (!init) {
            init = true;
            informationMessageInitDatabase();
            DatabaseTables.INSTANCE.createTables();
        }
    }

    private static void informationMessageInitDatabase() {
        SplashScreen.setMessageToSplashScreen(
                Bundle.getString(
                "AppDatabase.Info.SplashScreen.ConnectToDatabase")); // NOI18N
    }

    private AppDatabase() {
    }
}
