package org.jphototagger.lib.componentutil;

import org.jphototagger.lib.event.util.MouseEventUtil;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;

import java.lang.reflect.Method;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;

/**
 * Listens in a {@link JList} for popup triggers and sets to the list cell
 * renderer of that list the row index below the mouse location.
 *
 * If the popup menu becomes invisible, the row index will be set to -1.
 *
 * The cell renderer has to implement the method
 * <strong>setTempSelectionRow</strong> with an <strong>int</strong> as
 * parameter for the index.
 *
 * @author Elmar Baumann
 */
public final class ListItemTempSelectionRowSetter implements MouseListener, PopupMenuListener {
    private final JList list;
    private static final String TEMP_SEL_ROW_METHOD_NAME = "setTempSelectionRow";

    /**
     * Creates a new instance.
     *
     * @param list      list
     * @param popupMenu the list's popup menu
     */
    public ListItemTempSelectionRowSetter(JList list, JPopupMenu popupMenu) {
        if (list == null) {
            throw new NullPointerException("list == null");
        }

        if (popupMenu == null) {
            throw new NullPointerException("popupMenu == null");
        }

        this.list = list;
        list.addMouseListener(this);
        popupMenu.addPopupMenuListener(this);
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
        setRowIndex(-1);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        if (MouseEventUtil.isPopupTrigger(evt)) {
            int index = list.locationToIndex(new Point(evt.getX(), evt.getY()));

            if (index < 0) {
                return;
            }

            setRowIndex(index);
        }
    }

    private void setRowIndex(int index) {
        ListCellRenderer renderer = list.getCellRenderer();

        if (hasMethod(renderer)) {
            try {
                Method m = renderer.getClass().getMethod(TEMP_SEL_ROW_METHOD_NAME, int.class);

                m.invoke(renderer, index);
                list.repaint();
            } catch (Exception ex) {
                Logger.getLogger(ListItemTempSelectionRowSetter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean hasMethod(ListCellRenderer renderer) {
        for (Method method : renderer.getClass().getDeclaredMethods()) {
            if (method.getName().equals(TEMP_SEL_ROW_METHOD_NAME)) {
                Class<?>[] parameterTypes = method.getParameterTypes();

                if ((parameterTypes.length == 1) && parameterTypes[0].equals(int.class)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void mouseClicked(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseReleased(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseEntered(MouseEvent evt) {

        // ignore
    }

    @Override
    public void mouseExited(MouseEvent evt) {

        // ignore
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {

        // ignore
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent evt) {

        // ignore
    }
}
