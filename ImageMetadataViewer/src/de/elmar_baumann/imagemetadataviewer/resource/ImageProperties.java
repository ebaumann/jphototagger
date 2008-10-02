package de.elmar_baumann.imagemetadataviewer.resource;

import com.imagero.util.R3;

/**
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/17
 */
public final class ImageProperties implements R3 {

    @Override
    public final String[] get() {
        String[] s = new String[res.length];
        for (int i = 0; i < s.length; i++) {
            s[i] = res[i];
        }

        return s;
    }
    private final String[] res = {
        "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAABurO0ABXQADUVsbWFyIEJhdW1hbm50", // NOI18N
        "AApYTVAtVmlld2VydAAAdAAEbWV0YXQAF25vbi1jb21tZXJjaWFsIHVzZSBvbmx5", // NOI18N
        "dAACbm9xAH4AAnQACDA1LjExLjA3dAAKMTAuMTAuMjEwNnQABDIuOTl1cQB+AAAA", // NOI18N
        "AAAuMCwCFG0cderl/m4EJh5x2zEqI5nORpE/AhR3YHmZhUHoTfndNxTnrXQuQWve", // NOI18N
        "0Q==" // NOI18N
    ,}
;

}
