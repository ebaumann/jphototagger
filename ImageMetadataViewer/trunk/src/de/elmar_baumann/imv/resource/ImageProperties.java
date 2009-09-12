/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.imv.resource;

import com.imagero.util.R3;

/**
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-02-17
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
