package org.jphototagger.eximport.jpt.exporter;

import javax.swing.ImageIcon;
import org.jphototagger.lib.api.AppIconProvider;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExportPreferences {

    public static final ImageIcon ICON = Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_export.png");

    private ExportPreferences() {
    }
}
