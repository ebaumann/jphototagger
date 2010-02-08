/*
 * JavaStandardLibrary JSL - subproject of JPhotoTagger
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
package de.elmar_baumann.lib.resource;

import de.elmar_baumann.lib.image.util.IconUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Icons symbolizing a <code>java.util.logging.Level</code> object.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-06
 */
public final class LogLevelIcons {

    private static final Map<Level, ImageIcon> ICON_OF_LEVEL =
            new HashMap<Level, ImageIcon>();

    static {
        ICON_OF_LEVEL.put(Level.CONFIG,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_config.png"));
        ICON_OF_LEVEL.put(Level.FINE,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_fine.png"));
        ICON_OF_LEVEL.put(Level.FINER,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_finer.png"));
        ICON_OF_LEVEL.put(Level.FINEST,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_finest.png"));
        ICON_OF_LEVEL.put(Level.INFO,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_info.png"));
        ICON_OF_LEVEL.put(Level.SEVERE,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_severe.png"));
        ICON_OF_LEVEL.put(Level.WARNING,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_warning.png"));
    }

    /**
     * Returns an icon associated with a log level.
     *
     * @param level log level
     * @return      Icon or null, if the icon couldn't be constructed or if the
     *              level ist not associated with an icon. Levels whitout icons
     *              are <code>Level.ALL</code> and <code>Level.OFF</code>.
     */
    public static Icon getIcon(Level level) {
        if (level == null)
            throw new NullPointerException("level == null");

        return ICON_OF_LEVEL.get(level);
    }

    private LogLevelIcons() {
    }
}
