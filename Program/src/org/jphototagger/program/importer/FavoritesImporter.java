package org.jphototagger.program.importer;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;

import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.domain.repository.FavoritesRepository;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.program.exporter.FavoritesExporter;
import org.jphototagger.program.exporter.FavoritesExporter.CollectionWrapper;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = RepositoryDataImporter.class)
public final class FavoritesImporter implements RepositoryDataImporter {

    private final FavoritesRepository repo = Lookup.getDefault().lookup(FavoritesRepository.class);

    @Override
    public void importFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        try {
            FavoritesExporter.CollectionWrapper wrapper = (CollectionWrapper) XmlObjectImporter.importObject(file,
                    FavoritesExporter.CollectionWrapper.class);

            for (Favorite favorite : wrapper.getCollection()) {
                if (!repo.existsFavorite(favorite.getName())) {
                    repo.saveOrUpdateFavorite(favorite);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(FavoritesImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public FileFilter getFileFilter() {
        return FavoritesExporter.FILE_FILTER;
    }

    @Override
    public String getDisplayName() {
        return FavoritesExporter.DISPLAY_NAME;
    }

    @Override
    public Icon getIcon() {
        return AppLookAndFeel.getIcon("icon_import.png");
    }

    @Override
    public String getDefaultFilename() {
        return FavoritesExporter.DEFAULT_FILENAME;
    }

    @Override
    public int getPosition() {
        return FavoritesExporter.POSITION;
    }

    @Override
    public boolean isJPhotoTaggerData() {
        return true;
    }
}
