package org.jphototagger.domain.thumbnails;

import java.util.Collection;
import java.util.Collections;

import org.jphototagger.api.windows.MenuItemProvider;

/**
 * @author Elmar Baumann
 */
public class ThumbnailsPopupMenuItemProviderAdapter implements ThumbnailsPopupMenuItemProvider {

    @Override
    public Collection<? extends MenuItemProvider> getRootMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getRefreshMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getMetaDataMenuItems() {
        return Collections.emptyList();
    }

    @Override
    public Collection<? extends MenuItemProvider> getFileOperationsMenuItems() {
        return Collections.emptyList();
    }
}
