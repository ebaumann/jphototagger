package org.jphototagger.program.app;

import java.awt.Image;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.branding.Branding;
import org.jphototagger.lib.swing.IconUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Branding.class)
public final class BrandingImpl implements Branding {

    @Override
    public Image getSmallAppIcon() {
        return IconUtil.getIconImage(AppLookAndFeel.SMALL_APP_ICON_PATH);
    }

    @Override
    public Image getMediumAppIcon() {
        return IconUtil.getIconImage(AppLookAndFeel.SMALL_APP_ICON_PATH);
    }

    @Override
    public List<? extends Image> getAppIcons() {
        return AppLookAndFeel.getAppIcons();
    }
}
