package de.elmar_baumann.lib.component;

import de.elmar_baumann.lib.image.icon.IconUtil;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/08
 */
public final class CheckListBeanInfo extends java.beans.SimpleBeanInfo {

    @Override
    public java.awt.Image getIcon(int iconKind) {
        return IconUtil.getIconImage(
            "/de/elmar_baumann/lib/resource/icon_checklist.png");
    }
}
