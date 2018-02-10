package org.jphototagger.program.app;

import java.awt.Image;
import java.util.List;
import org.jphototagger.api.branding.Branding;
import org.jphototagger.resources.Icons;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = Branding.class)
public final class BrandingImpl implements Branding {

    @Override
    public Image getSmallAppIcon() {
        return Icons.getSmallAppIcon();
    }

    @Override
    public Image getMediumAppIcon() {
        return Icons.getMediumAppIcon();
    }

    @Override
    public List<? extends Image> getAppIcons() {
        return Icons.getAppIcons();
    }
}
