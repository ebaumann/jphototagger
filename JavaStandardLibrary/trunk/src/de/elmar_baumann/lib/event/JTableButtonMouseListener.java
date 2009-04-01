package de.elmar_baumann.lib.event;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

/**
 * Ursprünglicher Code von http://www.devx.com/getHelpOn/10MinuteSolution/20425
 *
 * @version 2009/04/01
 */
public final class JTableButtonMouseListener implements MouseListener {

    private final JTable table;

    private void forwardEventToButton(MouseEvent e) {
        JButton button = getButton(e);

        if (button != null) {
            MouseEvent mouseEvent = SwingUtilities.convertMouseEvent(table, e, button);
            button.dispatchEvent(mouseEvent);
            // This is necessary so that when a button is pressed and released
            // it gets rendered properly.  Otherwise, the button may still appear
            // pressed down when it has been released.
            table.repaint();
        }
    }

    private JButton getButton(MouseEvent e) {
        int row = e.getY() / table.getRowHeight();
        int column = table.getColumnModel().getColumnIndexAtX(e.getX());

        if (row >= table.getRowCount() || row < 0 ||
                column >= table.getColumnCount() || column < 0)
            return null;

        Object value = table.getValueAt(row, column);

        if (value instanceof JButton)
            return (JButton) value;

        return null;
    }

    public JTableButtonMouseListener(JTable table) {
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        JButton button = getButton(e);
        if (button != null) {
            button.doClick();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        forwardEventToButton(e);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        forwardEventToButton(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        forwardEventToButton(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        forwardEventToButton(e);
    }
}
