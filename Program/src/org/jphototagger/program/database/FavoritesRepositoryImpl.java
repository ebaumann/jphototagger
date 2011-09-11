package org.jphototagger.program.database;

import java.util.List;
import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.domain.repository.FavoritesRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = FavoritesRepository.class)
public final class FavoritesRepositoryImpl implements FavoritesRepository {

    private final DatabaseFavorites db = DatabaseFavorites.INSTANCE;

    @Override
    public boolean deleteFavorite(String favoriteName) {
        return db.deleteFavorite(favoriteName);
    }

    @Override
    public boolean existsFavorite(String favoriteName) {
        return db.existsFavorite(favoriteName);
    }

    @Override
    public List<Favorite> getAllFavorites() {
        return db.getAllFavorites();
    }

    @Override
    public boolean insertOrUpdateFavorite(Favorite favorite) {
        return db.insertOrUpdateFavorite(favorite);
    }

    @Override
    public boolean updateFavorite(Favorite favorite) {
        return db.updateFavorite(favorite);
    }

    @Override
    public boolean updateRenameFavorite(String fromFavoriteName, String toFavoriteName) {
        return db.updateRenameFavorite(fromFavoriteName, toFavoriteName);
    }
}
