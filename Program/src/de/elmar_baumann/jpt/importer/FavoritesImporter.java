package de.elmar_baumann.jpt.importer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.AppLookAndFeel;
import de.elmar_baumann.jpt.data.Favorite;
import de.elmar_baumann.jpt.database.DatabaseFavorites;
import de.elmar_baumann.jpt.exporter.FavoritesExporter;
import de.elmar_baumann.jpt.exporter.FavoritesExporter.CollectionWrapper;
import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-03-02
 */
public final class FavoritesImporter implements Importer {

    public static final FavoritesImporter INSTANCE = new FavoritesImporter();

    @Override
    public void importFile(File file) {
        try {
            FavoritesExporter.CollectionWrapper wrapper = (CollectionWrapper)
                    XmlObjectImporter.importObject(
                          file, FavoritesExporter.CollectionWrapper.class);

            for (Favorite favorite : wrapper.getCollection()) {
                if (!DatabaseFavorites.INSTANCE.exists(favorite.getName())) {
                    DatabaseFavorites.INSTANCE.insertOrUpdate(favorite);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(FavoritesImporter.class, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FavoritesExporter.INSTANCE.getFileFilter();
    }

    @Override
    public String getDisplayName() {
        return FavoritesExporter.INSTANCE.getDisplayName();
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return FavoritesExporter.INSTANCE.getDefaultFilename();
    }

    private FavoritesImporter() {
    }
}
