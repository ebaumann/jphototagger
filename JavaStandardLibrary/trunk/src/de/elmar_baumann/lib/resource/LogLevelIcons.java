package de.elmar_baumann.lib.resource;

import de.elmar_baumann.lib.image.icon.IconUtil;
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
 * @version 2008/09/06
 */
public final class LogLevelIcons {

    private static final Map<Level, ImageIcon> iconOfLevel = new HashMap<Level, ImageIcon>();


    static {
        iconOfLevel.put(Level.CONFIG, IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/logfiledialog_config.png")); // NOI18N
        iconOfLevel.put(Level.FINE, IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/logfiledialog_fine.png")); // NOI18N
        iconOfLevel.put(Level.FINER, IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/logfiledialog_finer.png")); // NOI18N
        iconOfLevel.put(Level.FINEST, IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/logfiledialog_finest.png")); // NOI18N
        iconOfLevel.put(Level.INFO, IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/logfiledialog_info.png")); // NOI18N
        iconOfLevel.put(Level.SEVERE, IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/logfiledialog_severe.png")); // NOI18N
        iconOfLevel.put(Level.WARNING, IconUtil.getImageIcon(
            "/de/elmar_baumann/lib/resource/logfiledialog_warning.png")); // NOI18N
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

        return iconOfLevel.get(level);
    }

    private LogLevelIcons() {
    }
}
