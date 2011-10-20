package org.jphototagger.exifmodule;

import java.util.Arrays;
import java.util.Collection;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.domain.thumbnails.ThumbnailsPopupMenuItemProvider;
import org.jphototagger.domain.thumbnails.ThumbnailsPopupMenuItemProviderAdapter;
import org.jphototagger.lib.api.MenuItemProviderImpl;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailsPopupMenuItemProvider.class)
public final class ActionsToMenus extends ThumbnailsPopupMenuItemProviderAdapter {

    @Override
    public Collection<? extends MenuItemProvider> getMetaDataMenuItems() {
        return Arrays.asList(new MenuItemProviderImpl(new SetExifToXmpAction(), 300, false));
    }
}
