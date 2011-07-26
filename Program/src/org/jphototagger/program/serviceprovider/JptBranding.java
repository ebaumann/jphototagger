package org.jphototagger.program.serviceprovider;

import java.awt.Image;
import java.util.List;
import org.jphototagger.lib.image.util.IconUtil;
import org.jphototagger.program.app.AppLookAndFeel;
import org.jphototagger.services.core.Branding;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2011-07-26
 */
public final class JptBranding implements Branding {

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
