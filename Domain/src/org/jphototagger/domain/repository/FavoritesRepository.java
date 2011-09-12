package org.jphototagger.domain.repository;

import java.util.List;

import org.jphototagger.domain.favorites.Favorite;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface FavoritesRepository {

    boolean deleteFavorite(String favoriteName);

    boolean existsFavorite(String favoriteName);

    List<Favorite> findAllFavorites();

    boolean saveOrUpdateFavorite(Favorite favorite);

    boolean updateFavorite(Favorite favorite);

    boolean updateRenameFavorite(String fromFavoriteName, String toFavoriteName);
}
