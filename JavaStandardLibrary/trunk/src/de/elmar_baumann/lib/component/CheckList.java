package de.elmar_baumann.lib.component;

import de.elmar_baumann.lib.util.ArrayUtil;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * Liste mit Checkboxes. Als Inhalt des Models werden Objekte des Typs
 * <code>JCheckBox</code> erwartet!
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class CheckList extends JList {

    private static final Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();

    public CheckList() {
        setCellRenderer(new CellRenderer());
        listenToMouse();
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Fügt einen Beobachter hinzu. Wird eine Checkbox an- oder abgekreuzt,
     * wird dieser benachrichtigt. Der Index der Checkbox wird geliefert mit
     * {@link java.awt.event.ActionEvent#getID()}.
     * 
     * @param listener Beobachter
     */
    public synchronized void addActionListener(ActionListener listener) {
        if (listener == null)
            throw new NullPointerException("listener == null");

        actionListeners.add(listener);
    }

    /**
     * Entfernt einen Beobachter.
     * 
     * @param listener Beobachter
     */
    public synchronized void removeActionListener(ActionListener listener) {
        if (listener == null)
            throw new NullPointerException("listener == null");

        actionListeners.remove(listener);
    }

    private void listenToMouse() {
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());

                if (index != -1 && getModel().getElementAt(index) instanceof JCheckBox) {
                    JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                    checkbox.setSelected(!checkbox.isSelected());
                    repaint();
                    notifyActionListener(index);
                }
            }
        });
    }

    private synchronized void notifyActionListener(int index) {
        assert ArrayUtil.isValidIndex(actionListeners, index);

        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(new ActionEvent(this, index, "")); // NOI18N
        }
    }

    /**
     * Liefert, ob mindestens ein Item selektiert ist.
     * 
     * @return true, wenn mindestens eines selektiert ist
     */
    public synchronized boolean hasSelectedItems() {
        return getSelectionCount() > 0;
    }

    /**
     * Liefert die Anzahl selektierter Items.
     * 
     * @return Anzahl
     */
    public synchronized int getSelectionCount() {
        return getSelectedItemIndices().size();
    }

    /**
     * Liefert alle selektierten Items.
     * 
     * @return Items
     */
    public synchronized List<JCheckBox> getSelectedItems() {
        List<JCheckBox> items = new ArrayList<JCheckBox>();
        ListModel model = getModel();
        int count = model.getSize();
        for (int index = 0; index < count; index++) {
            Object o = model.getElementAt(index);
            if (o instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) o;
                if (checkBox.isSelected()) {
                    items.add(checkBox);
                }
            }
        }
        return items;
    }

    /**
     * Liefert die Indexe aller selektierten Items.
     * 
     * @return Indizes
     */
    public synchronized List<Integer> getSelectedItemIndices() {
        List<Integer> indices = new ArrayList<Integer>();
        ListModel model = getModel();
        int count = model.getSize();
        for (int index = 0; index < count; index++) {
            Object o = model.getElementAt(index);
            if (o instanceof JCheckBox && ((JCheckBox) o).isSelected()) {
                indices.add(index);
            }
        }
        return indices;
    }

    /**
     * Liefert die Texte aller selektierten Items.
     * 
     * @return Itemtexte
     */
    public synchronized List<String> getSelectedItemTexts() {
        List<JCheckBox> items = getSelectedItems();
        List<String> texts = new ArrayList<String>();
        for (JCheckBox checkBox : items) {
            texts.add(checkBox.getText());
        }
        return texts;
    }

    /**
     * Liefert die Texte aller selektierten Items als einen String.
     * 
     * @param delimiter Begrenzer zwischen den Itemtexten
     * @return          Texte
     */
    public synchronized String getSelectedItemTexts(String delimiter) {
        if (delimiter == null)
            throw new NullPointerException("delimiter == null");

        List<String> texts = getSelectedItemTexts();
        StringBuffer textBuffer = new StringBuffer();
        for (String text : texts) {
            textBuffer.append(text + delimiter);
        }
        return textBuffer.toString();
    }

    /**
     * Selektiert alle Items mit bestimmten Texten.
     * 
     * @param texts  Texte
     * @param select true, wenn selektiert werden soll, false, wenn deselektiert
     *               werden soll
     * @see          #setSelectedItemsWithText(java.lang.String, boolean)
     */
    public synchronized void setSelectedItemsWithText(List<String> texts, boolean select) {
        if (texts == null)
            throw new NullPointerException("texts == null");

        for (String text : texts) {
            setSelectedItemsWithText(text, select);
        }
    }

    /**
     * Selektiert alle Items mit bestimmtem Text.
     * 
     * @param text Text
     * @param select true, wenn selektiert werden soll, false, wenn deselektiert
     *     werden soll
     * @see #setSelectedItemsWithText(java.util.List, boolean)
     */
    public synchronized void setSelectedItemsWithText(String text, boolean select) {
        if (text == null)
            throw new NullPointerException("text == null");

        ListModel model = getModel();
        int count = model.getSize();
        for (int index = 0; index < count; index++) {
            Object o = model.getElementAt(index);
            if (o instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) o;
                if (checkBox.getText().equals(text)) {
                    checkBox.setSelected(select);
                }
            }
        }
    }

    private class CellRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

            if (value instanceof JCheckBox) {
                JCheckBox checkBox = (JCheckBox) value;
                checkBox.setBackground(
                    isSelected ? getSelectionBackground() : getBackground());
                checkBox.setForeground(
                    isSelected ? getSelectionForeground() : getForeground());
                checkBox.setEnabled(isEnabled());
                checkBox.setFont(getFont());
                checkBox.setFocusPainted(false);
                checkBox.setBorderPainted(true);
                checkBox.setBorder(
                    isSelected
                    ? UIManager.getBorder("List.focusCellHighlightBorder") // NOI18N
                    : noFocusBorder);
                return checkBox;
            } else if (value instanceof String) {
                String string = (String) value;
                return new JLabel(string);
            }
            return new JLabel();
        }
    }
}
