package org.jphototagger.program.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.database.DatabaseFavorites;
import org.jphototagger.program.exporter.FavoritesExporter;
import org.jphototagger.program.exporter.FavoritesExporter.CollectionWrapper;

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
            Logger.getLogger(FavoritesImporter.class.getName()).log(Level.SEVERE, null, ex);
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
