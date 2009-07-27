package de.elmar_baumann.lib.componentutil;

import de.elmar_baumann.lib.event.util.MouseEventUtil;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Listens in a {@link JList} for popup triggers and sets to the list cell
 * renderer of that list the row index below the mouse location.
 *
 * If the popup menu becomes invisible, the row index will be set to -1.
 *
 * The cell renderer has to implement the method
 * <strong>setHighlightIndexForPopup</strong> with an <strong>int</strong> as
 * parameter for the index.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-27
 */
public final class ListItemPopupHighlighter
        implements MouseListener, PopupMenuListener {

    private final JList list;
    private static final String METHOD_NAME_HIGHLIGHT_INDEX =
            "setHighlightIndexForPopup";

    /**
     * Creates a new instance.
     *
     * @param list      list
     * @param popupMenu the list's popup menu
     */
    public ListItemPopupHighlighter(JList list, JPopupMenu popupMenu) {
        this.list = list;
        list.addMouseListener(this);
        popupMenu.addPopupMenuListener(this);
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        setHighlightIndex(-1);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (MouseEventUtil.isPopupTrigger(e)) {
            int index = list.locationToIndex(new Point(e.getX(), e.getY()));
            if (index < 0) return;
            setHighlightIndex(index);
        }
    }

    private void setHighlightIndex(int index) {
        ListCellRenderer renderer = list.getCellRenderer();
        if (hasHighlightMethod(renderer)) {
            try {
                Method m = renderer.getClass().getMethod(
                        METHOD_NAME_HIGHLIGHT_INDEX, int.class);
                m.invoke(renderer, index);
                list.repaint();
            } catch (Exception ex) {
                Logger.getLogger(ListItemPopupHighlighter.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }
    }

    private boolean hasHighlightMethod(ListCellRenderer renderer) {
        for (Method method : renderer.getClass().getDeclaredMethods()) {
            if (method.getName().equals(METHOD_NAME_HIGHLIGHT_INDEX)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 &&
                        parameterTypes[0].equals(int.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // ignore
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // ignore
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        // ignore
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        // ignore
    }
}
