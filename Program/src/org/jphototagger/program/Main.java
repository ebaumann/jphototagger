package org.jphototagger.program;

import org.jphototagger.program.app.AppInit;

/**
 *
 * @author Elmar Baumann
 */
public final class Main {
    public static void main(String[] args) {
        AppInit.INSTANCE.init(args);
    }

    private Main() {}
}
