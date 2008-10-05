package de.elmar_baumann.lib.component;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
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
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class CheckList extends JList {

    private static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    private List<ActionListener> actionListeners = new ArrayList<ActionListener>();

    public CheckList() {
        setCellRenderer(new CellRenderer());

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                int index = locationToIndex(e.getPoint());

                if (index != -1) {
                    JCheckBox checkbox = (JCheckBox) getModel().getElementAt(index);
                    checkbox.setSelected(!checkbox.isSelected());
                    repaint();
                    notifyActionListener(index);
                }
            }
        });

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    /**
     * Fügt einen Beobachter hinzu. Wird eine Checkbox an- oder abgekreuzt,
     * wird dieser benachrichtigt. Der Index der Checkbox wird geliefert mit
     * {@link java.awt.event.ActionEvent#getID()}.
     * 
     * @param listener Beobachter
     */
    public void addActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    /**
     * Entfernt einen Beobachter.
     * 
     * @param listener Beobachter
     */
    public void removeActionListener(ActionListener listener) {
        actionListeners.add(listener);
    }

    private void notifyActionListener(int index) {
        for (ActionListener listener : actionListeners) {
            listener.actionPerformed(new ActionEvent(this, index, "")); // NOI18N
        }
    }

    /**
     * Liefert, ob mindestens ein Item selektiert ist.
     * 
     * @return true, wenn mindestens eines selektiert ist
     */
    public boolean hasSelectedItems() {
        return getSelectionCount() > 0;
    }

    /**
     * Liefert die Anzahl selektierter Items.
     * 
     * @return Anzahl
     */
    public int getSelectionCount() {
        return getSelectedItemIndices().size();
    }

    /**
     * Liefert alle selektierten Items.
     * 
     * @return Items
     */
    public List<JCheckBox> getSelectedItems() {
        List<JCheckBox> items = new ArrayList<JCheckBox>();
        ListModel model = getModel();
        int count = model.getSize();
        for (int index = 0; index < count; index++) {
            JCheckBox checkBox = (JCheckBox) model.getElementAt(index);
            if (checkBox.isSelected()) {
                items.add(checkBox);
            }

        }
        return items;
    }

    /**
     * Liefert die Indexe aller selektierten Items.
     * 
     * @return Indizes
     */
    public List<Integer> getSelectedItemIndices() {
        List<Integer> indices = new ArrayList<Integer>();
        ListModel model = getModel();
        int count = model.getSize();
        for (int index = 0; index < count; index++) {
            if (((JCheckBox) model.getElementAt(index)).isSelected()) {
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
    public List<String> getSelectedItemTexts() {
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
    public String getSelectedItemTexts(String delimiter) {
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
    public void setSelectedItemsWithText(List<String> texts, boolean select) {
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
     * @see #setSelectedItemsWithText(java.util.ArrayList, boolean)
     */
    public void setSelectedItemsWithText(String text, boolean select) {
        ListModel model = getModel();
        int count = model.getSize();
        for (int index = 0; index < count; index++) {
            JCheckBox checkBox = (JCheckBox) model.getElementAt(index);
            if (checkBox.getText().equals(text)) {
                checkBox.setSelected(select);
            }
        }
    }

    private class CellRenderer implements ListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
            JList list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {
            JCheckBox checkbox = (JCheckBox) value;
            checkbox.setBackground(
                isSelected ? getSelectionBackground() : getBackground());
            checkbox.setForeground(
                isSelected ? getSelectionForeground() : getForeground());
            checkbox.setEnabled(isEnabled());
            checkbox.setFont(getFont());
            checkbox.setFocusPainted(false);
            checkbox.setBorderPainted(true);
            checkbox.setBorder(
                isSelected
                ? UIManager.getBorder("List.focusCellHighlightBorder") // NOI18N
                : noFocusBorder);
            return checkbox;
        }
    }
}
