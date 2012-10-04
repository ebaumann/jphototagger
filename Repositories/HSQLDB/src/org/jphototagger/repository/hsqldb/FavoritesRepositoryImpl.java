package org.jphototagger.repository.hsqldb;

import java.util.List;
import org.jphototagger.domain.favorites.Favorite;
import org.jphototagger.domain.repository.FavoritesRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = FavoritesRepository.class)
public final class FavoritesRepositoryImpl implements FavoritesRepository {

    @Override
    public boolean deleteFavorite(String favoriteName) {
        return FavoritesDatabase.INSTANCE.deleteFavorite(favoriteName);
    }

    @Override
    public boolean existsFavorite(String favoriteName) {
        return FavoritesDatabase.INSTANCE.existsFavorite(favoriteName);
    }

    @Override
    public List<Favorite> findAllFavorites() {
        return FavoritesDatabase.INSTANCE.getAllFavorites();
    }

    @Override
    public boolean saveOrUpdateFavorite(Favorite favorite) {
        return FavoritesDatabase.INSTANCE.insertOrUpdateFavorite(favorite);
    }

    @Override
    public boolean updateFavorite(Favorite favorite) {
        return FavoritesDatabase.INSTANCE.updateFavorite(favorite);
    }

    @Override
    public boolean updateRenameFavorite(String fromFavoriteName, String toFavoriteName) {
        return FavoritesDatabase.INSTANCE.updateRenameFavorite(fromFavoriteName, toFavoriteName);
    }
}
