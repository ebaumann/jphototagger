package org.jphototagger.program.app.ui;

import javax.swing.ImageIcon;
import org.jphototagger.lib.api.AppIconProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = AppIconProvider.class)
public final class AppIconProviderImpl implements AppIconProvider {

    @Override
    public ImageIcon getIcon(String name) {
        return AppLookAndFeel.getIcon(name);
    }
}
