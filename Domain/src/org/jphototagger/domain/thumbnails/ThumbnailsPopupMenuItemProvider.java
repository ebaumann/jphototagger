package org.jphototagger.domain.thumbnails;

import java.util.Collection;
import org.jphototagger.api.windows.MenuItemProvider;

/**
 * @author Elmar Baumann
 */
public interface ThumbnailsPopupMenuItemProvider {

    Collection<? extends MenuItemProvider> getRootMenuItems();

    Collection<? extends MenuItemProvider> getRefreshMenuItems();

    Collection<? extends MenuItemProvider> getMetaDataMenuItems();

    Collection<? extends MenuItemProvider> getFileOperationsMenuItems();
}
