package de.elmar_baumann.lib.resource;

import de.elmar_baumann.lib.image.icon.IconUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Icons, die einen <code>java.util.logging.Level</code> symbolisieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/06
 */
public class LogLevelIcons {

    private static Map<Level, ImageIcon> iconOfLevel = new HashMap<Level, ImageIcon>();

    /**
     * Liefert das Icon für einen Loglevel.
     * 
     * @param level Level
     * @return      Icon oder null, wenn für den Level kein Icon existiert
     *              (das sind <code>Level.ALL</code> und <code>Level.OFF</code>)
     */
    public static Icon getIcon(Level level) {
        initMap();
        return iconOfLevel.get(level);
    }

    private static void initMap() {
        if (iconOfLevel.isEmpty()) {
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
    }
}
