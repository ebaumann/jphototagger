package org.jphototagger.program.importer;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.domain.Favorite;
import org.jphototagger.program.database.DatabaseFavorites;
import org.jphototagger.program.exporter.FavoritesExporter;
import org.jphototagger.program.exporter.FavoritesExporter.CollectionWrapper;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.Icon;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FavoritesImporter implements Importer {
    public static final FavoritesImporter INSTANCE = new FavoritesImporter();

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            FavoritesExporter.CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                                                              FavoritesExporter.CollectionWrapper.class);

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

    private FavoritesImporter() {}
}
