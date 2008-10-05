package de.elmar_baumann.imagemetadataviewer;

import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import de.elmar_baumann.lib.image.icon.IconUtil;
import de.elmar_baumann.lib.io.FileFilter;
import de.elmar_baumann.lib.persistence.PersistentSettings;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Anwendungseinstellungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/07/26
 */
public class AppSettings {

    /**
     * Dateifilter für alle Bilddateiformate, die verarbeitet werden können.
     */
    public static final FileFilter fileFilterAcceptedImageFileFormats = new FileFilter(
        ".*\\.[cC][rR][wW];" + // NOI18N
        ".*\\.[cC][rR]2;" + // NOI18N
        ".*\\.[dD][cC][rR];" + // NOI18N
        ".*\\.[dD][nN][gG];" + // NOI18N
        ".*\\.[jJ][pP][gG];" + // NOI18N
        ".*\\.[jJ][pP][eE][gG];" + // NOI18N
        ".*\\.[mM][rR][wW];" + // NOI18N
        ".*\\.[nN][eE][fF];" + // NOI18N
        ".*\\.[tT][hH][mM];" + // NOI18N
        ".*\\.[tT][iI][fF];" + // NOI18N
        ".*\\.[tT][iI][fF][fF];", // NOI18N
        ";");  // NOI18N
    /**
     * Pfad zum kleinen Anwendungssymbol.
     */
    public static final String pathAppIconSmall =
        "/de/elmar_baumann/imagemetadataviewer/resource/icon_app_small.png";  // NOI18N
    /**
     * Pfad zum mittelgroßen Anwendungssymbol.
     */
    public static final String pathAppIconMedium =
        "/de/elmar_baumann/imagemetadataviewer/resource/icon_app_medium.png";  // NOI18N
    public static final String tooltipTextProgressBarDirectory = Bundle.getString("ProgressBarDirectory.TooltipText");
    public static final String tooltipTextProgressBarCurrentTasks = Bundle.getString("ProgressBarCurrentTasks.TooltipText");
    public static final String tooltipTextProgressBarScheduledTasks = Bundle.getString("ProgressBarScheduledTasks.TooltipText");
    /**
     * Vordergrundfarbe für Tabellentext, der in der Datenbank gespeichert ist.
     */
    public static final Color colorForegroundTableTextStoredInDatabase = Color.BLACK;
    /**
     * Hintergrundfarbe für Tabellentext, der in der Datenbank gespeichert ist.
     */
    public static final Color colorBackgroundTableTextStoredInDatabase = new Color(251, 249, 241);
    /**
     * Vordergrundfarbe für selektierten Tabellentext.
     */
    public static final Color colorForegroundTableTextSelected = Color.BLACK;
    /**
     * Vordergrundfarbe für selektierten Tabellentext.
     */
    public static final Color colorBackgroundTableTextSelected = new Color(226, 226, 255);
    /**
     * Standard-Vordergrundfarbe für Text in Tabellen.
     */
    public static final Color colorForegroundTableTextDefault = Color.BLACK;
    /**
     * Standard-Hintergrundfarbe für Text in Tabellen.
     */
    public static final Color colorBackgroundTableTextDefault = Color.WHITE;
    /**
     * Icon für die Aktion: Speichern der Metadaten, Zustand: Deaktiviert
     */
    public static final ImageIcon iconSaveMetaDataDisabled =
        IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_save_metadata_disabled.png"); // NOI18N
    /**
     * Icon für die Aktion: Speichern der Metadaten, Zustand: Aktiviert
     */
    public static final ImageIcon iconSaveMetaDataEnabled =
        IconUtil.getImageIcon("/de/elmar_baumann/imagemetadataviewer/resource/icon_save_metadata_enabled.png"); // NOI18N
    private static ArrayList<String> appIconPaths = new ArrayList<String>();
    private static ArrayList<Image> appIcons = new ArrayList<Image>();
    private static final Icon smallAppIcon = IconUtil.getImageIcon(pathAppIconSmall);
    private static final Icon mediumAppIcon = IconUtil.getImageIcon(pathAppIconMedium);
    

    static {
        appIconPaths.add(pathAppIconSmall);
        appIconPaths.add(pathAppIconMedium);
    }
    

    static {
        appIcons.add(IconUtil.getIconImage(AppSettings.pathAppIconSmall));
        appIcons.add(IconUtil.getIconImage(AppSettings.pathAppIconMedium));
    }

    /**
     * Liefert die Anwendungssymbole.
     * 
     * @return Anwendungssymbole
     */
    public static ArrayList<Image> getAppIcons() {
        return appIcons;
    }

    /**
     * Liefert die Pfade der Anwendungssymbole.
     * 
     * @return Pfade
     */
    public static ArrayList<String> getAppIconPaths() {
        return appIconPaths;
    }

    /**
     * Liefert das kleine Anwendungssymbol.
     * 
     * @return Kleines Anwendungssymbol
     */
    public static Icon getSmallAppIcon() {
        return smallAppIcon;
    }

    /**
     * Liefert das mittelgroße Anwendungssymbol.
     * 
     * @return Kleines Anwendungssymbol
     */
    public static Icon getMediumAppIcon() {
        return mediumAppIcon;
    }

    /**
     * Liefert den Namen der Logdatei.
     * 
     * @return Logdatei
     */
    public static String getLogfileName() {
        return PersistentSettings.getInstance().getDirectoryName() +
            File.separator + "imagemetadataviewerlog.xml";  // NOI18N
    }
}
