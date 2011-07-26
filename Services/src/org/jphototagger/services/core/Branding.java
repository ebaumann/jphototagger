package org.jphototagger.services.core;

import java.awt.Image;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2011-07-26
 */
public interface Branding {

    Image getSmallAppIcon();

    Image getMediumAppIcon();
    
    List<? extends Image> getAppIcons();
}
