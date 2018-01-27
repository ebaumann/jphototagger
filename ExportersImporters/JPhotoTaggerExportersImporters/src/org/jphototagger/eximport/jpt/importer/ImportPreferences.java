package org.jphototagger.eximport.jpt.importer;

import javax.swing.ImageIcon;
import org.jphototagger.lib.api.AppIconProvider;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ImportPreferences {

    public static final ImageIcon ICON = Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_import.png");

    private ImportPreferences() {
    }
}
