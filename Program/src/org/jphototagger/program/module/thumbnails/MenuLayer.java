package org.jphototagger.program.module.thumbnails;

import java.util.Arrays;
import java.util.Collection;
import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.lib.api.MainWindowMenuProviderAdapter;
import org.jphototagger.lib.api.MenuItemProviderImpl;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = MainWindowMenuProvider.class)
public final class MenuLayer extends MainWindowMenuProviderAdapter {

    @Override
    public Collection<? extends MenuItemProvider> getViewMenuItems() {
        MenuItemProviderImpl itemToggleMetaDataOverlay = new MenuItemProviderImpl(new ToggleMetaDataOverlayAction(), 100, false);
        MenuItemProviderImpl itemDisplaySidecarFileFlag = new MenuItemProviderImpl(new DisplaySidecarFlagAction(), 200, false);
        MenuItemProviderImpl itemDisplayDcSubjects = new MenuItemProviderImpl(new DisplayDcSubjectsAction(), 300, false);
        return Arrays.asList(
                itemToggleMetaDataOverlay,
                itemDisplaySidecarFileFlag,
                itemDisplayDcSubjects);
    }
}
