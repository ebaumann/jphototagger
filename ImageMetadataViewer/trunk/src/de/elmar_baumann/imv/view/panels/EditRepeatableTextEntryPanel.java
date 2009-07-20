package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.AutoCompleteData;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.data.TextEntryContent;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.event.listener.TextEntryListener;
import de.elmar_baumann.imv.event.listener.impl.TextEntryListenerSupport;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.TextModifyer;
import de.elmar_baumann.imv.view.renderer.ListCellRendererKeywordsEdit;
import de.elmar_baumann.lib.componentutil.InputVerifierMaxLength;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.componentutil.ListUtil;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
public final class EditRepeatableTextEntryPanel extends javax.swing.JPanel
        implements TextEntry, ActionListener, DocumentListener {

    /**
     * Delimiter separating words in a single string
     */
    private static final String DELIMITER = XmpMetadata.getXmpTokenDelimiter();
    /**
     * Replaces delimiter in a single word to avoid considering it as multiple
     * words.
     *
     * Have to be different from {@link XmpMetadata#getXmpTokenDelimiter()}!
     */
    private static final String DELIMITER_REPLACEMENT = "?"; // NOI18N
    private final DefaultListModel model = new DefaultListModel();
    private Column column;
    private AutoCompleteData autoCompleteData;
    private boolean editable = true;
    private boolean dirty = false;
    private TextModifyer textModifier;
    /**
     * Contains the words not to modify by the text modifier
     */
    private List<String> ignoreModifyWords = new ArrayList<String>();
    private TextEntryListenerSupport textEntryListenerSupport =
            new TextEntryListenerSupport();

    public EditRepeatableTextEntryPanel(Column column) {
        this.column = column;
        initComponents();
        postInitComponents();
        assert !DELIMITER_REPLACEMENT.equals(DELIMITER) :
                "Delimiter replacement is equals to delimiter! " + DELIMITER; // NOI18N
    }

    private void postInitComponents() {
        textFieldInput.setInputVerifier(
                new InputVerifierMaxLength(column.getLength()));
        textFieldInput.getDocument().addDocumentListener(this);
        setPropmt();
    }

    private void setPropmt() {
        labelPrompt.setText(column.getDescription());
    }

    @Override
    public void setAutocomplete() {
        autoCompleteData = new AutoCompleteData(column);
        AutoCompleteDecorator.decorate(
                textFieldInput,
                autoCompleteData.getList(),
                false);
    }

    public AutoCompleteData getAutoCompleteData() {
        return autoCompleteData;
    }

    /**
     * Returns the text: All elements in the list <strong>plus</strong> the
     * text of the input field if it's not contained in the list.
     *
     * @return words delimited by {@link XmpMetadata#getXmpTokenDelimiter()}
     */
    @Override
    public String getText() {
        emptyTextfieldIfListContainsText();
        String listItemText = ListUtil.getTokenString(model, DELIMITER);
        String textfieldText = textFieldInput.getText().trim();
        listItemText += textfieldText.isEmpty()
                        ? "" // NOI18N
                        : DELIMITER + textfieldText;
        if (!textfieldText.isEmpty() &&
                !ListUtil.containsString(model, textfieldText)) {
            notifyTextAdded(column, textfieldText);
        }
        return listItemText;
    }

    /**
     * Empties the input text field if the word in the text field is also in
     * the list. This is true if the user hits ENTER or the ADD button; then
     * the text in the input field will be added to the list if it is not
     * already there.
     */
    private void emptyTextfieldIfListContainsText() {
        String input = textFieldInput.getText();
        if (ListUtil.containsString(list.getModel(), input)) {
            textFieldInput.setText(""); // NOI18N
        }
    }

    /**
     * Sets a text modifier. If the user presses <strong>Ctrl+K</strong> or
     * pushes the <strong>K</strong> button, the text modifier will be invoked
     * through
     * {@link TextModifyer#modify(java.lang.String, java.util.Collection)}.
     *
     * If the modified text differs from the text in the input field after
     * invoking, the modified text will be added to the list. If the text is
     * delimited by {@link XmpMetadata#getXmpTokenDelimiter()}, all words not
     * contained in the list will be added to the list.
     *
     * @param textModifier text modifier
     */
    public void setTextModifier(TextModifyer textModifier) {
        this.textModifier = textModifier;
        buttonTextModifier.setEnabled(editable && textModifier != null);
        buttonTextModifier.setToolTipText(textModifier == null
                                          ? "" // NOI18N
                                          : textModifier.getDescription());
    }

    /**
     * Sets and replaces the words in the list. A word is a token delimited by
     * {@link XmpMetadata#getXmpTokenDelimiter()}. Does <em>not</em> check
     * for duplicates.
     *
     * Removes also the dirty flag.
     *
     * @param text text delimited by {@link XmpMetadata#getXmpTokenDelimiter()}
     */
    @Override
    public void setText(String text) {
        ListUtil.setToken(text, DELIMITER, model);
        setIgnoreModifyWords(text);
        textFieldInput.setText(""); // NOI18N
        dirty = false;
        setEnabledButtons();
    }

    /**
     * Adds text <em>whithout</em> replacing existing words in the list. If the
     * text is delimited by {@link XmpMetadata#getXmpTokenDelimiter()}, every
     * word not contained in the list will be added to the list.
     *
     * @param text text delimited by {@link XmpMetadata#getXmpTokenDelimiter()}
     */
    public void addText(String text) {
        assert editable : "Edit is not enabled!"; // NOI18N
        if (!editable) return;
        addTokensToList(text);
        dirty = true;
    }

    /**
     * Sets and replaces all words that will <em>not</em> be modified through
     * the text modifier.
     *
     * @param text text delimited by {@link XmpMetadata#getXmpTokenDelimiter()}
     */
    private void setIgnoreModifyWords(String text) {
        ignoreModifyWords.clear();
        StringTokenizer st = new StringTokenizer(text, DELIMITER);
        while (st.hasMoreTokens()) {
            ignoreModifyWords.add(st.nextToken());
        }
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
            addOneWordToList(getInputWithoutDelimiter());
        } else {
            setEnabledButtons();
        }
    }

    /**
     * Adds a word to the list if the list doesn't contain that word.
     *
     * @param word word
     */
    private void addOneWordToList(String word) {
        if (!word.isEmpty() && !model.contains(word)) {
            model.addElement(word);
            textFieldInput.setText(""); // NOI18N
            notifyTextAdded(column, word);
            ComponentUtil.forceRepaint(getParent().getParent());
        }
    }

    /**
     * Returns the text in the input text field where all occurences of
     * {@link XmpMetadata#getXmpTokenDelimiter()} will be replaced with
     * {@link #DELIMITER_REPLACEMENT}.
     *
     * This is necessary to avoid words in the list that will be considered as
     * two or more words when returned by {@link #getText()}.
     *
     * @return text where the delimiter is replaced with
     *         <code>DELIMITER_REPLACEMENT</code>
     */
    private String getInputWithoutDelimiter() {
        String input = textFieldInput.getText().trim();
        return input.replace(DELIMITER, DELIMITER_REPLACEMENT).trim();
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
                ignoreModifyWords.remove(value.toString());
                notifyTextRemoved(column, value.toString());
                dirty = true;
            }
            ComponentUtil.forceRepaint(getParent().getParent());
        }
    }

    private MessageDisplayer.ConfirmAction confirmRemoveSelectedItems() {
        return MessageDisplayer.confirm(
                "EditRepeatableTextEntryPanel.Confirm.RemoveSelItems",
                MessageDisplayer.CancelButton.HIDE, column.getDescription());
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        textFieldInput.setEditable(editable);
        buttonAddInput.setEnabled(editable);
        buttonRemoveSelection.setEnabled(editable);
        buttonTextModifier.setEnabled(editable && textModifier != null);
        list.setBackground(editable
                           ? textFieldInput.getBackground()
                           : getBackground());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    /**
     * Adds the input as one word to the list if the list doesn't contain the
     * input.
     */
    private void handleButtonAddInputActionPerformed() {
        addOneWordToList(getInputWithoutDelimiter());
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
        dirty = true;
    }

    /**
     * The input text field's document was changed: Sets the dirty flag.
     *
     * @param e document event
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        dirty = true;
    }

    /**
     * The input text field's document was changed: Sets the dirty flag.
     *
     * @param e document event
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        dirty = true;
    }

    /**
     * Invokes the text modifier if the <code>Ctrl+K</code> was pressed.
     *
     * @param evt key event
     */
    private void modifyText(java.awt.event.KeyEvent evt) {
        if (textModifier != null && KeyEventUtil.isControl(evt, KeyEvent.VK_K)) {
            modifyText();
        }
    }

    /**
     * Invokes the text modifier and after returning sets the words of the
     * modified text to the list if not contained there. Does nothing if no
     * text was modified.
     */
    private void modifyText() {
        String prevText = textFieldInput.getText();
        String modifiedText =
                textModifier.modify(prevText, new ArrayList<String>());
        if (!modifiedText.isEmpty() &&
                !prevText.equalsIgnoreCase(modifiedText)) {
            addTokensToList(modifiedText);
            textFieldInput.setText(""); // NOI18N
        }
    }

    /**
     * Adds all tokens delimited by {@link XmpMetadata#getXmpTokenDelimiter()}
     * not contained in the list to the list.
     *
     * @param tokenText text delimited by
     *                  {@link XmpMetadata#getXmpTokenDelimiter()}
     */
    private void addTokensToList(String tokenText) {
        StringTokenizer st = new StringTokenizer(tokenText, DELIMITER);
        int countAdded = 0;
        while (st.hasMoreTokens()) {
            String token = st.nextToken().trim();
            if (!token.isEmpty() && !model.contains(token)) {
                model.addElement(token);
                ignoreModifyWords.add(token);
                countAdded++;
                notifyTextAdded(column, token);
            }
        }
        if (countAdded > 0) {
            ComponentUtil.forceRepaint(getParent().getParent());
        }
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
                "Invalid model index: " + index +
                ". Valid: 0.." + (model.size() - 1);
        boolean ready = false;
        String oldName = model.getElementAt(index).toString();
        String newName = null;
        do {
            newName = JOptionPane.showInputDialog(Bundle.getString(
                    "EditRepeatableTextEntryPanel.Input.RenameListItem"),
                    oldName);
            ready = newName == null;
            if (newName != null && newName.trim().equalsIgnoreCase(oldName)) {
                ready = confirm("EditRepeatableTextEntryPanel.Confirm.SameNames").
                        equals(MessageDisplayer.ConfirmAction.NO);
                newName = null;
            } else if (newName != null &&
                    ListUtil.containsString(list.getModel(), newName.trim())) {
                ready = confirm(
                        "EditRepeatableTextEntryPanel.Confirm.NameExists",
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
        return MessageDisplayer.confirm(key, MessageDisplayer.CancelButton.HIDE,
                params);
    }

    private boolean checkSelected(int selCount) {
        if (selCount <= 0) {
            MessageDisplayer.error("EditRepeatableTextEntryPanel.Error.Select");
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
        buttonTextModifier = new javax.swing.JButton();

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

        buttonTextModifier.setMnemonic('k');
        buttonTextModifier.setText(Bundle.getString("EditRepeatableTextEntryPanel.buttonTextModifier.text")); // NOI18N
        buttonTextModifier.setEnabled(false);
        buttonTextModifier.setMargin(new java.awt.Insets(0, 0, 0, 0));
        buttonTextModifier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonTextModifierActionPerformed(evt);
            }
        });
        panelButtons.add(buttonTextModifier);

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
    modifyText(evt);
}//GEN-LAST:event_textFieldInputKeyPressed

private void buttonTextModifierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonTextModifierActionPerformed
    modifyText();
}//GEN-LAST:event_buttonTextModifierActionPerformed

private void menuItemRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRenameActionPerformed
    renameSelectedListItems();
}//GEN-LAST:event_menuItemRenameActionPerformed

private void menuItemRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRemoveActionPerformed
    removeSelectedElements();
}//GEN-LAST:event_menuItemRemoveActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddInput;
    private javax.swing.JButton buttonRemoveSelection;
    private javax.swing.JButton buttonTextModifier;
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
