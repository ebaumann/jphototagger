package org.jphototagger.api.core;

import java.awt.Image;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann
 */
public interface Branding {

    Image getSmallAppIcon();

    Image getMediumAppIcon();

    List<? extends Image> getAppIcons();
}
