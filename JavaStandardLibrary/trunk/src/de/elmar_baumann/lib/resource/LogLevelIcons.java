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
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_config.png")); // NOI18N
        ICON_OF_LEVEL.put(Level.FINE,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_fine.png")); // NOI18N
        ICON_OF_LEVEL.put(Level.FINER,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_finer.png")); // NOI18N
        ICON_OF_LEVEL.put(Level.FINEST,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_finest.png")); // NOI18N
        ICON_OF_LEVEL.put(Level.INFO,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_info.png")); // NOI18N
        ICON_OF_LEVEL.put(Level.SEVERE,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_severe.png")); // NOI18N
        ICON_OF_LEVEL.put(Level.WARNING,
                IconUtil.getImageIcon(
                "/de/elmar_baumann/lib/resource/icons/icon_logfiledialog_warning.png")); // NOI18N
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
            throw new NullPointerException("level == null"); // NOI18N

        return ICON_OF_LEVEL.get(level);
    }

    private LogLevelIcons() {
    }
}
