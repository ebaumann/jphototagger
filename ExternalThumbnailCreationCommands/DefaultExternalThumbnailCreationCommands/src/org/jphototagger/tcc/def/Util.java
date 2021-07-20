package org.jphototagger.tcc.def;

import org.jphototagger.lib.awt.DesktopUtil;

/**
 * @author Elmar Baumann
 */
public final class Util {

    public static void browse(String uri) {
        if (uri == null) {
            throw new NullPointerException("uri == null");
        }

        DesktopUtil.browse(uri, "JPhotoTagger");
    }

    private Util() {
    }
}
