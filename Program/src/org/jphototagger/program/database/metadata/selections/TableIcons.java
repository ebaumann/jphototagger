package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.program.app.AppLookAndFeel;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

/**
 * Liefert Icons für Tabellen.
 *
 * @author Elmar Baumann
 */
public final class TableIcons {
    private static final Icon ICON_UNDEFINED =
        AppLookAndFeel.getIcon("icon_table_undefined.png");
    private static final Icon ICON_FILES =
        AppLookAndFeel.getIcon("icon_file.png");
    private static final Icon ICON_EXIF =
        AppLookAndFeel.getIcon("icon_exif.png");
    private static final Icon ICON_XMP          =
        AppLookAndFeel.getIcon("icon_xmp.png");
    private static final Map<String, Icon> ICON_OF_TABLENAME =
        new HashMap<String, Icon>();

    static {
        ICON_OF_TABLENAME.put("exif", ICON_EXIF);
        ICON_OF_TABLENAME.put("exif_recording_equipment", ICON_EXIF);
        ICON_OF_TABLENAME.put("exif_lenses", ICON_EXIF);
        ICON_OF_TABLENAME.put("files", ICON_FILES);
        ICON_OF_TABLENAME.put("xmp", ICON_XMP);
        ICON_OF_TABLENAME.put("dc_creators", ICON_XMP);
        ICON_OF_TABLENAME.put("dc_rights", ICON_XMP);
        ICON_OF_TABLENAME.put("iptc4xmpcore_locations", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_authorspositions", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_captionwriters", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_cities", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_countries", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_credits", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_sources", ICON_XMP);
        ICON_OF_TABLENAME.put("photoshop_states", ICON_XMP);
        ICON_OF_TABLENAME.put("dc_subjects", ICON_XMP);
    }

    private TableIcons() {}

    public static Icon getIcon(String tablename) {
        if (tablename == null) {
            throw new NullPointerException("tablename == null");
        }

        Icon icon = ICON_OF_TABLENAME.get(tablename);

        return (icon == null)
               ? ICON_UNDEFINED
               : icon;
    }
}
