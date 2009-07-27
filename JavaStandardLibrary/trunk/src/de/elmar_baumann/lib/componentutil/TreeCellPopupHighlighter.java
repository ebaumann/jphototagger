package de.elmar_baumann.lib.componentutil;

import de.elmar_baumann.lib.event.util.MouseEventUtil;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.TreeCellRenderer;

/**
 * Listens in a {@link JTree} for popup triggers and sets to the tree cell
 * renderer of that tree the row index below the mouse location.
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
public final class TreeCellPopupHighlighter
        implements MouseListener, PopupMenuListener {

    private final JTree tree;
    private static final String METHOD_NAME_HIGHLIGHT_INDEX =
            "setHighlightIndexForPopup";

    /**
     * Creates a new instance.
     *
     * @param tree      tree
     * @param popupMenu the tree's popup menu
     */
    public TreeCellPopupHighlighter(JTree tree, JPopupMenu popupMenu) {
        this.tree = tree;
        tree.addMouseListener(this);
        popupMenu.addPopupMenuListener(this);
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        setHighlightIndex(-1);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (MouseEventUtil.isPopupTrigger(e)) {
            int index = tree.getRowForLocation(e.getX(), e.getY());
            if (index < 0) return;
            setHighlightIndex(index);
        }
    }

    private void setHighlightIndex(int index) {
        TreeCellRenderer renderer = tree.getCellRenderer();
        if (hasHighlightMethod(renderer)) {
            try {
                Method m = renderer.getClass().getMethod(
                        METHOD_NAME_HIGHLIGHT_INDEX, int.class);
                m.invoke(renderer, index);
                tree.repaint();
            } catch (Exception ex) {
                Logger.getLogger(TreeCellPopupHighlighter.class.getName()).log(
                        Level.SEVERE, null, ex);
            }
        }
    }

    private boolean hasHighlightMethod(TreeCellRenderer renderer) {
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
