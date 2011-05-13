package org.jphototagger.program.event.listener.impl;

import org.jphototagger.lib.event.util.MouseEventUtil;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import org.jdesktop.swingx.JXList;

/**
 * Do not use this class! Instead extend a popup menu from
 * {@link org.jphototagger.lib.event.listener.PopupMenuList}.
 *
 * @author Elmar Baumann
 */
public abstract class MouseListenerList extends MouseAdapter {
    private int index;
    private boolean popupAlways;

    @Override
    public void mousePressed(MouseEvent evt) {
        if (MouseEventUtil.isPopupTrigger(evt)) {
            JXList list = (JXList) evt.getSource();

            index = list.locationToIndex(new Point(evt.getX(), evt.getY()));

            if (popupAlways || (index >= 0)) {
                showPopup(list, evt.getX(), evt.getY());
            }
        }
    }

    public void setPopupAlways(boolean popupAlways) {
        this.popupAlways = popupAlways;
    }

    public int getIndex() {
        return index;
    }

    protected abstract void showPopup(JXList list, int x, int y);
}
