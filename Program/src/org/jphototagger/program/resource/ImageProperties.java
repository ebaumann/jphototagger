package org.jphototagger.program.resource;

import com.imagero.util.R3;

/**
 *
 * @author Elmar Baumann
 */
public final class ImageProperties implements R3 {
    @Override
    public String[] get() {
        String[] s = new String[res.length];
        System.arraycopy(res, 0, s, 0, s.length);

        return s;
    }

    private final String[] res = { "rO0ABXVyAAJbQqzzF/gGCFTgAgAAeHAAAABurO0ABXQADUVsbWFyIEJhdW1hbm50",
                                   "AApYTVAtVmlld2VydAAAdAAEbWV0YXQAF25vbi1jb21tZXJjaWFsIHVzZSBvbmx5",
                                   "dAACbm9xAH4AAnQACDA1LjExLjA3dAAKMTAuMTAuMjEwNnQABDIuOTl1cQB+AAAA",
                                   "AAAuMCwCFG0cderl/m4EJh5x2zEqI5nORpE/AhR3YHmZhUHoTfndNxTnrXQuQWve",
                                   "0Q==", }
    ;
}
