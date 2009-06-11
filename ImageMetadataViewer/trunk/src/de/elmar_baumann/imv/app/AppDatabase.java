package de.elmar_baumann.imv.app;

import de.elmar_baumann.imv.SplashScreen;
import de.elmar_baumann.imv.database.DatabaseTables;
import de.elmar_baumann.imv.resource.Bundle;

/**
 * Database of the application.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/11
 */
public final class AppDatabase {

    public static void init() {
        informationMessageInitDatabase();
        DatabaseTables.INSTANCE.createTables();
    }

    private static void informationMessageInitDatabase() {
        SplashScreen.setMessageToSplashScreen(
                Bundle.getString(
                "Main.Init.InformationMessage.SplashScreen.ConnectToDatabase"));
    }

    private AppDatabase() {
    }
}
