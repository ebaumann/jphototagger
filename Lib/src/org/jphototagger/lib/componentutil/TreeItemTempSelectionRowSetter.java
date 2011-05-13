package org.jphototagger.lib.componentutil;

import org.jphototagger.lib.event.util.MouseEventUtil;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;
import org.jdesktop.swingx.JXTree;

/**
 * Listens in a {@link JTree} for popup triggers and sets to the tree cell
 * renderer of that tree the row index below the mouse location.
 *
 * If the popup menu becomes invisible, the row index will be set to -1.
 *
 * The cell renderer has to implement the method
 * <strong>setTempSelectionRow</strong> with an <strong>int</strong> as
 * parameter for the index.
 *
 * @author Elmar Baumann
 */
public final class TreeItemTempSelectionRowSetter implements MouseListener, PopupMenuListener {
    private final JTree tree;
    private static final String TEMP_SEL_ROW_METHOD_NAME = "setTempSelectionRow";

    /**
     * Creates a new instance.
     *
     * @param tree      tree
     * @param popupMenu the tree's popup menu
     */
    public TreeItemTempSelectionRowSetter(JTree tree, JPopupMenu popupMenu) {
        this.tree = tree;

        if (tree == null) {
            throw new NullPointerException("tree == null");
        }

        if (popupMenu == null) {
            throw new NullPointerException("popupMenu == null");
        }

        tree.addMouseListener(this);
        popupMenu.addPopupMenuListener(this);
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
        setRowIndex(-1);
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        if (MouseEventUtil.isPopupTrigger(evt)) {
            int index = tree.getRowForLocation(evt.getX(), evt.getY());

            if (index < 0) {
                return;
            }

            setRowIndex(index);
        }
    }

    private void setRowIndex(int index) {
        TreeCellRenderer renderer = tree.getCellRenderer();

        if (renderer instanceof JXTree.DelegatingRenderer) {
            renderer = ((JXTree.DelegatingRenderer) renderer).getDelegateRenderer();
        }

        if (hasMethod(renderer)) {
            try {
                Method m = renderer.getClass().getMethod(TEMP_SEL_ROW_METHOD_NAME, int.class);

                m.invoke(renderer, index);
                tree.repaint();
            } catch (Exception ex) {
                Logger.getLogger(TreeItemTempSelectionRowSetter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private boolean hasMethod(TreeCellRenderer renderer) {
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
