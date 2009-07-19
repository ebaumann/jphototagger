package de.elmar_baumann.imv;

import de.elmar_baumann.imv.app.AppInit;

/**
 * Calls {@link AppInit#init(java.lang.String[])}.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-02-17
 */
public final class Main {

    public static void main(String[] args) {
        AppInit.init(args);
    }

    private Main() {
    }
}
