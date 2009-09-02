package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.AutoCompleteDataOfColumn;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.data.TextEntryContent;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.datatransfer.TransferHandlerDropEdit;
import de.elmar_baumann.imv.datatransfer.TransferHandlerDropList;
import de.elmar_baumann.imv.event.listener.TextEntryListener;
import de.elmar_baumann.imv.event.listener.impl.TextEntryListenerSupport;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Suggest;
import de.elmar_baumann.imv.view.renderer.ListCellRendererKeywordsEdit;
import de.elmar_baumann.lib.componentutil.InputVerifierMaxLength;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * Panel with an input text field an a list. The list contains multiple words,
 * the input field one word.
 *
 * Text in the input field will be added to the list on hitting the ENTER key
 * or pushing the ADD button.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-18
 */
public final class EditRepeatableTextEntryPanel
        extends javax.swing.JPanel
        implements TextEntry,
                   ActionListener,
                   DocumentListener,
                   ListDataListener {

    private final DefaultListModel model = new DefaultListModel();
    private Column column;
    private boolean editable = true;
    private boolean dirty = false;
    private Suggest suggest;
    private boolean ignoreIntervalAdded;
    private TextEntryListenerSupport textEntryListenerSupport =
            new TextEntryListenerSupport();

    public EditRepeatableTextEntryPanel(Column column) {
        this.column = column;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        textFieldInput.setInputVerifier(
                new InputVerifierMaxLength(column.getLength()));
        textFieldInput.getDocument().addDocumentListener(this);
        model.addListDataListener(this);
        setPropmt();
    }

    private void setPropmt() {
        labelPrompt.setText(column.getDescription());
    }

    @Override
    public void setAutocomplete() {
        AutoCompleteDecorator.decorate(
                textFieldInput,
                AutoCompleteDataOfColumn.INSTANCE.get(column).getData(),
                false);
    }

    /**
     * Returns the text from the input field.
     *
     * @return trimmed text
     */
    @Override
    public String getText() {
        return textFieldInput.getText().trim();
    }

    /**
     * Returns the list item texts.
     *
     * @return list item texts
     */
    public Collection<String> getRepeatableText() {
        List<String> texts = new ArrayList<String>(model.size());
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            texts.add(model.get(i).toString());
        }
        return texts;
    }

    /**
     * Sets a suggester for the keyword in the input text field.
     *
     * @param suggest suggest for keywords
     */
    public void setSuggest(Suggest suggest) {
        this.suggest = suggest;
        buttonSuggestion.setEnabled(editable && suggest != null);
        buttonSuggestion.setToolTipText(suggest == null
                                        ? "" // NOI18N
                                        : suggest.getDescription());
    }

    /**
     * Does nothing but removing the dirty flag. Don't call this!
     *
     * @param text ignroed
     * @see        #addText(java.lang.String)
     */
    @Override
    public void setText(String text) {
        assert false : "Don't call this (Called with text: '" + text + "')"; // NOI18N
        // ignore
    }

    /**
     * Sets text to lists (replaces existing text) and sets the dirty to false.
     *
     * @param texts text to set, every text is a list item
     */
    public void setText(Collection<String> texts) {
        textFieldInput.setText(""); // NOI18N
        model.removeAllElements();
        addToList(texts);
        setEnabledButtons();
        dirty = false;
    }

    @Override
    public void empty(boolean dirty) {
        textFieldInput.setText(""); // NOI18N
        model.removeAllElements();
        this.dirty = dirty;
    }

    public void removeText(String text) {
        assert editable : "Edit is not enabled!"; // NOI18N
        if (!editable) return;
        model.removeElement(text);
        notifyTextRemoved(column, text);
        dirty = true;
    }

    /**
     * Adds text to the list <em>whithout</em> replacing existing words in the
     * list if editing is allowed and sets the dirty flag to true.
     *
     * @param text text
     */
    public void addText(String text) {
        assert editable : "Edit is not enabled!"; // NOI18N
        if (!editable) return;
        addToList(Collections.singleton(text));
        dirty = true;
    }

    @Override
    public Column getColumn() {
        return column;
    }

    /**
     * Adds the text in the input text field to the list if the ENTER key
     * was pressed.
     *
     * @param evt key event
     */
    private void handleTextFieldKeyReleased(KeyEvent evt) {
        JComponent component = (JComponent) evt.getSource();
        if (evt.getKeyCode() == KeyEvent.VK_ENTER &&
                component.getInputVerifier().verify(component)) {
            addInputToList();
        } else {
            setEnabledButtons();
        }
    }

    private void addInputToList() {
        if (addToList(Collections.singleton(textFieldInput.getText())) > 0) {
            textFieldInput.setText(""); // NOI18N
        }
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean isEmpty() {
        return model.isEmpty();
    }

    /**
     * Focusses the input text field
     */
    @Override
    public void focus() {
        textFieldInput.requestFocus();
    }

    /**
     * Removes from the list all selected elements.
     */
    private void removeSelectedElements() {
        if (list.getSelectedIndex() >= 0 && confirmRemoveSelectedItems().equals(
                MessageDisplayer.ConfirmAction.YES)) {
            Object[] values = list.getSelectedValues();
            for (Object value : values) {
                model.removeElement(value);
                notifyTextRemoved(column, value.toString());
                dirty = true;
            }
            ComponentUtil.forceRepaint(getParent().getParent());
        }
    }

    private MessageDisplayer.ConfirmAction confirmRemoveSelectedItems() {
        return MessageDisplayer.confirm(this,
                "EditRepeatableTextEntryPanel.Confirm.RemoveSelItems", // NOI18N
                MessageDisplayer.CancelButton.HIDE, column.getDescription());
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        textFieldInput.setEditable(editable);
        buttonAddInput.setEnabled(editable);
        buttonRemoveSelection.setEnabled(editable);
        buttonSuggestion.setEnabled(editable && suggest != null);
        list.setBackground(editable
                           ? textFieldInput.getBackground()
                           : getBackground());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    private void handleButtonAddInputActionPerformed() {
        addInputToList();
    }

    /**
     * Removes the list's selected elements from the list.
     */
    private void handleButtonRemoveInputActionPerformed() {
        removeSelectedElements();
    }

    private void handleListKeyPressed(KeyEvent evt) {
        // won't be called but avoids that other components's key listeners
        // triggered
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSelectedElements();
        }
    }

    private void handleListValueChanged(ListSelectionEvent evt) {
        setEnabledButtons();
    }

    private void setEnabledButtons() {
        buttonAddInput.setEnabled(editable &&
                !textFieldInput.getText().isEmpty());
        buttonRemoveSelection.setEnabled(
                editable && list.getSelectedIndex() >= 0);
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public TextEntry clone() {
        return new TextEntryContent(getText(), column);
    }

    /**
     * Removes the list's selected elements from the list.
     *
     * @param e action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        removeSelectedElements();
    }

    /**
     * The input text field's document was changed: Sets the dirty flag.
     *
     * @param e document event
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        // Don't notify TextEntryListener listeners because the model doesn't
        // change
        dirty = true;
    }

    /**
     * The input text field's document was changed: Sets the dirty flag.
     *
     * @param e document event
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        // Don't notify TextEntryListener listeners because the model doesn't
        // change
        dirty = true;
    }

    /**
     * The input text field's document was changed: Sets the dirty flag.
     *
     * @param e document event
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        // Don't notify TextEntryListener listeners because the model doesn't
        // change
        dirty = true;
    }

    /**
     * Invokes the text modifier if the <code>Ctrl+K</code> was pressed.
     *
     * @param evt key event
     */
    private void suggestText(java.awt.event.KeyEvent evt) {
        if (KeyEventUtil.isControl(evt, KeyEvent.VK_K)) {
            suggestText();
        }
    }

    private void suggestText() {
        String trimmedInput = textFieldInput.getText().trim();
        if (suggest != null && !trimmedInput.isEmpty()) {
            addToList(suggest.suggest(trimmedInput));
        }
    }

    private int addToList(Collection<String> texts) {
        ignoreIntervalAdded = true;
        int countAdded = 0;
        for (String text : texts) {
            String trimmedText = text.trim();
            if (!trimmedText.isEmpty() && !model.contains(trimmedText)) {
                model.addElement(trimmedText);
                countAdded++;
                notifyTextAdded(column, trimmedText);
            }
        }
        if (countAdded > 0) {
            if (getParent() != null) {
                ComponentUtil.forceRepaint(getParent());
                if (getParent().getParent() != null) {
                    ComponentUtil.forceRepaint(getParent().getParent());
                }
            }
        }
        ignoreIntervalAdded = false;
        return countAdded;
    }

    private void renameSelectedListItems() {
        int[] selIndices = list.getSelectedIndices();
        if (!checkSelected(selIndices.length)) return;
        for (int selIndex : selIndices) {
            renameListItem(selIndex);
        }
    }

    private void renameListItem(int index) {
        assert model.getElementAt(index) != null :
                "Invalid model index: " + index + // NOI18N
                ". Valid: 0.." + (model.size() - 1); // NOI18N
        boolean ready = false;
        String oldName = model.getElementAt(index).toString();
        String newName = null;
        do {
            newName = JOptionPane.showInputDialog(this, Bundle.getString(
                    "EditRepeatableTextEntryPanel.Input.RenameListItem"), // NOI18N
                    oldName);
            ready = newName == null;
            if (newName != null && newName.trim().equalsIgnoreCase(oldName)) {
                ready = confirm("EditRepeatableTextEntryPanel.Confirm.SameNames"). // NOI18N
                        equals(MessageDisplayer.ConfirmAction.NO);
                newName = null;
            } else if (newName != null &&
                    ListUtil.containsString(list.getModel(), newName.trim())) {
                ready = confirm(
                        "EditRepeatableTextEntryPanel.Confirm.NameExists", // NOI18N
                        newName).equals(MessageDisplayer.ConfirmAction.NO);
                newName = null;
            } else if (newName != null && !newName.trim().isEmpty()) {
                ready = true;
                newName = newName.trim();
            }
        } while (!ready);
        if (newName != null) {
            model.set(index, newName);
            dirty = true;
            notifyTextChanged(column, oldName, newName);
        }
    }

    private MessageDisplayer.ConfirmAction confirm(String key, Object... params) {
        return MessageDisplayer.confirm(
                this, key, MessageDisplayer.CancelButton.HIDE, params);
    }

    private boolean checkSelected(int selCount) {
        if (selCount <= 0) {
            MessageDisplayer.error(this,
                    "EditRepeatableTextEntryPanel.Error.Select"); // NOI18N
            return false;
        }
        return true;
    }

    public void addTextEntryListener(TextEntryListener listener) {
        textEntryListenerSupport.addTextEntryListener(listener);
    }

    public void removeTextEntryListener(TextEntryListener listener) {
        textEntryListenerSupport.removeTextEntryListener(listener);
    }

    private void notifyTextRemoved(Column column, String removedText) {
        textEntryListenerSupport.notifyTextRemoved(column, removedText);
    }

    private void notifyTextAdded(Column column, String addedText) {
        textEntryListenerSupport.notifyTextAdded(column, addedText);
    }

    private void notifyTextChanged(Column column, String oldText, String newText) {
        textEntryListenerSupport.notifyTextChanged(column, oldText, newText);
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        if (ignoreIntervalAdded) return;
        // drop
        int index0 = e.getIndex0();
        int index1 = e.getIndex1();
        for (int i = index0; i <= index1; i++) {
            notifyTextAdded(column, model.get(i).toString());
            dirty = true;
        }
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        // ignore
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        // ignore
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        popupMenuList = new javax.swing.JPopupMenu();
        menuItemRename = new javax.swing.JMenuItem();
        menuItemRemove = new javax.swing.JMenuItem();
        labelPrompt = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        textFieldInput = new javax.swing.JTextField();
        panelButtons = new javax.swing.JPanel();
        buttonRemoveSelection = new javax.swing.JButton();
        buttonAddInput = new javax.swing.JButton();
        buttonSuggestion = new javax.swing.JButton();

        menuItemRename.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_rename.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/imv/resource/properties/Bundle"); // NOI18N
        menuItemRename.setText(bundle.getString("EditRepeatableTextEntryPanel.menuItemRename.text")); // NOI18N
        menuItemRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRenameActionPerformed(evt);
            }
        });
        popupMenuList.add(menuItemRename);

        menuItemRemove.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de/elmar_baumann/imv/resource/icons/icon_remove.png"))); // NOI18N
        menuItemRemove.setText(bundle.getString("EditRepeatableTextEntryPanel.menuItemRemove.text")); // NOI18N
        menuItemRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRemoveActionPerformed(evt);
            }
        });
        popupMenuList.add(menuItemRemove);

        setLayout(new java.awt.GridBagLayout());

        labelPrompt.setText(Bundle.getString("EditRepeatableTextEntryPanel.labelPrompt.text")); // NOI18N
        labelPrompt.setToolTipText(column.getLongerDescription());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(labelPrompt, gridBagConstraints);

        scrollPane.setMinimumSize(new java.awt.Dimension(22, 44));

        list.setModel(model);
        list.setToolTipText(Bundle.getString("EditRepeatableTextEntryPanel.list.toolTipText")); // NOI18N
        list.setCellRenderer(new ListCellRendererKeywordsEdit());
        list.setComponentPopupMenu(popupMenuList);
        list.setDragEnabled(true);
        list.setDropMode(javax.swing.DropMode.INSERT);
        list.setFocusable(false);
        list.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        scrollPane.setViewportView(list);
        list.setTransferHandler(new TransferHandlerDropList());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);

        textFieldInput.setToolTipText(Bundle.getString("EditRepeatableTextEntryPanel.textFieldInput.toolTipText")); // NOI18N
        textFieldInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldInputKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldInputKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(textFieldInput, gridBagConstraints);
        textFieldInput.setTransferHandler(new TransferHandlerDropEdit());

        panelButtons.setLayout(new java.awt.GridLayout(3, 1));

        buttonRemoveSelection.setText(Bundle.getString("EditRepeatableTextEntryPanel.buttonRemoveSelection.text")); // NOI18N
        buttonRemoveSelection.setToolTipText(Bundle.getString("EditRepeatableTextEntryPanel.buttonRemoveSelection.toolTipText")); // NOI18N
        buttonRemoveSelection.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonRemoveSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveSelectionActionPerformed(evt);
            }
        });
        panelButtons.add(buttonRemoveSelection);

        buttonAddInput.setText(Bundle.getString("EditRepeatableTextEntryPanel.buttonAddInput.text")); // NOI18N
        buttonAddInput.setToolTipText(Bundle.getString("EditRepeatableTextEntryPanel.buttonAddInput.toolTipText")); // NOI18N
        buttonAddInput.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonAddInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddInputActionPerformed(evt);
            }
        });
        panelButtons.add(buttonAddInput);

        buttonSuggestion.setMnemonic('k');
        buttonSuggestion.setText(Bundle.getString("EditRepeatableTextEntryPanel.buttonSuggestion.text")); // NOI18N
        buttonSuggestion.setEnabled(false);
        buttonSuggestion.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonSuggestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSuggestionActionPerformed(evt);
            }
        });
        panelButtons.add(buttonSuggestion);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 1, 2);
        add(panelButtons, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void textFieldInputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldInputKeyReleased
    handleTextFieldKeyReleased(evt);
}//GEN-LAST:event_textFieldInputKeyReleased

private void buttonAddInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddInputActionPerformed
    handleButtonAddInputActionPerformed();
}//GEN-LAST:event_buttonAddInputActionPerformed

private void buttonRemoveSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveSelectionActionPerformed
    handleButtonRemoveInputActionPerformed();
}//GEN-LAST:event_buttonRemoveSelectionActionPerformed

private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
    handleListValueChanged(evt);
}//GEN-LAST:event_listValueChanged

private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
    handleListKeyPressed(evt);
}//GEN-LAST:event_listKeyPressed

private void textFieldInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldInputKeyPressed
    suggestText(evt);
}//GEN-LAST:event_textFieldInputKeyPressed

private void buttonSuggestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSuggestionActionPerformed
    suggestText();
}//GEN-LAST:event_buttonSuggestionActionPerformed

private void menuItemRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRenameActionPerformed
    renameSelectedListItems();
}//GEN-LAST:event_menuItemRenameActionPerformed

private void menuItemRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRemoveActionPerformed
    removeSelectedElements();
}//GEN-LAST:event_menuItemRemoveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddInput;
    private javax.swing.JButton buttonRemoveSelection;
    private javax.swing.JButton buttonSuggestion;
    private javax.swing.JLabel labelPrompt;
    private javax.swing.JList list;
    private javax.swing.JMenuItem menuItemRemove;
    private javax.swing.JMenuItem menuItemRename;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPopupMenu popupMenuList;
    private javax.swing.JScrollPane scrollPane;
    public javax.swing.JTextField textFieldInput;
    // End of variables declaration//GEN-END:variables
}
