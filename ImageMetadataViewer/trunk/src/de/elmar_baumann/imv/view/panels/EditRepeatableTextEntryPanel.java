package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.data.AutoCompleteData;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.data.TextEntryContent;
import de.elmar_baumann.imv.database.metadata.Column;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * Panel zum Eingeben mehrzeiliger Texte.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public final class EditRepeatableTextEntryPanel extends javax.swing.JPanel
        implements TextEntry, ActionListener, DocumentListener {

    private static final String DELIMITER = XmpMetadata.getXmpTokenDelimiter();
    private static final String DELIMITER_REPLACEMENT = "?";
    private final DefaultListModel model = new DefaultListModel();
    private Column column;
    private AutoCompleteData autoCompleteData;
    private boolean editable = true;
    private boolean dirty = false;
    private TextModifyer textModifier;
    private List<String> ignoreModifyWords = new ArrayList<String>();

    public EditRepeatableTextEntryPanel(Column column) {
        this.column = column;
        initComponents();
        postInitComponents();
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

    @Override
    public String getText() {
        emptyTextfieldIfListContainsText();
        String listItemText = ListUtil.getTokenString(model, DELIMITER);
        String textfieldText = textFieldInput.getText().trim();
        listItemText += textfieldText.isEmpty()
                        ? ""
                        : DELIMITER + textfieldText;
        return listItemText;
    }

    private void emptyTextfieldIfListContainsText() {
        String input = textFieldInput.getText();
        if (ListUtil.containsString(list.getModel(), input)) {
            textFieldInput.setText("");
        }
    }

    public void setTextModifier(TextModifyer textModifier) {
        this.textModifier = textModifier;
    }

    @Override
    public void setText(String text) {
        ListUtil.setToken(text, DELIMITER, model);
        setIgnoreModifyWords(text);
        textFieldInput.setText("");
        dirty = false;
        setEnabledButtons();
    }

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
        String input = getInputWithoutDelimiter();
        if (!input.isEmpty() && !model.contains(input)) {
            model.addElement(input);
            textFieldInput.setText("");
            ComponentUtil.forceRepaint(getParent().getParent());
        }
    }

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

    @Override
    public void focus() {
        textFieldInput.requestFocus();
    }

    private void removeSelectedElements() {
        if (list.getSelectedIndex() >= 0) {
            Object[] values = list.getSelectedValues();
            for (Object value : values) {
                model.removeElement(value);
                dirty = true;
            }
            ComponentUtil.forceRepaint(getParent().getParent());
        }
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        textFieldInput.setEditable(editable);
        buttonAddInput.setEnabled(editable);
        buttonRemoveSelection.setEnabled(editable);
        list.setBackground(editable
                           ? textFieldInput.getBackground()
                           : getBackground());
    }

    private void handleButtonAddInputActionPerformed() {
        addInputToList();
    }

    private void handleButtonRemoveInputActionPerformed() {
        removeSelectedElements();
    }

    private void handleListValueChanged(ListSelectionEvent evt) {
        setEnabledButtons();
    }

    private void setEnabledButtons() {
        buttonAddInput.setEnabled(editable &&
                !textFieldInput.getText().isEmpty());
        buttonRemoveSelection.setEnabled(editable && list.getSelectedIndex() >=
                0);
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public TextEntry clone() {
        return new TextEntryContent(getText(), column);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        removeSelectedElements();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        dirty = true;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        dirty = true;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        dirty = true;
    }

    private void modifyText(java.awt.event.KeyEvent evt) {
        if (textModifier != null && KeyEventUtil.isControl(evt, KeyEvent.VK_K)) {
            String prevText = textFieldInput.getText();
            String modifiedText =
                    textModifier.modify(prevText, new ArrayList<String>());
            if (!prevText.equalsIgnoreCase(modifiedText)) {
                addTokenToInput(modifiedText);
            }
        }
    }

    private void addTokenToInput(String text) {
        StringTokenizer st = new StringTokenizer(text, DELIMITER);
        int countAdded = 0;
        while (st.hasMoreTokens()) {
            String input = st.nextToken().trim();
            if (!input.isEmpty() && !model.contains(input)) {
                model.addElement(input);
                countAdded++;
            }
        }
        textFieldInput.setText("");
        if (countAdded > 0) {
            ComponentUtil.forceRepaint(getParent().getParent());
        }
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

        labelPrompt = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        textFieldInput = new javax.swing.JTextField();
        panelButtons = new javax.swing.JPanel();
        buttonRemoveSelection = new javax.swing.JButton();
        buttonAddInput = new javax.swing.JButton();

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

        panelButtons.setLayout(new java.awt.GridLayout(2, 1));

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
    // won't be called but avoids that other components's key listeners
    // triggered
    if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
        removeSelectedElements();
    }
}//GEN-LAST:event_listKeyPressed

private void textFieldInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldInputKeyPressed
    modifyText(evt);
}//GEN-LAST:event_textFieldInputKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddInput;
    private javax.swing.JButton buttonRemoveSelection;
    private javax.swing.JLabel labelPrompt;
    private javax.swing.JList list;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JScrollPane scrollPane;
    public javax.swing.JTextField textFieldInput;
    // End of variables declaration//GEN-END:variables
}
