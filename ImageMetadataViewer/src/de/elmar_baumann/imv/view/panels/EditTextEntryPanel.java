package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.data.AutoCompleteData;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.data.TextEntryContent;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.component.InputVerifierMaxLength;
import de.elmar_baumann.lib.component.TabLeavingTextArea;
import java.awt.Color;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * Panel zum Eingeben einzeiliger Texte.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public class EditTextEntryPanel extends javax.swing.JPanel implements TextEntry {

    private Column column;
    private AutoCompleteData autoCompleteData;
    private String text = "";
    private static final Color editableColor = Color.WHITE;

    public EditTextEntryPanel(Column column) {
        this.column = column;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setPropmt();
        setInputVerifier();
    }

    private void setPropmt() {
        labelPrompt.setText(column.getDescription());
    }

    private void setInputVerifier() {
        textAreaEdit.setInputVerifier(new InputVerifierMaxLength(column.getLength()));
    }

    @Override
    public String getText() {
        return textAreaEdit.getText();
    }

    @Override
    public boolean isDirty() {
        return !text.equals(textAreaEdit.getText());
    }

    @Override
    public void setDirty(boolean dirty) {
        if (!dirty) {
            text = textAreaEdit.getText();
        }
    }

    @Override
    public void setText(String text) {
        this.text = text;
        textAreaEdit.setText(text.trim());
    }

    @Override
    public Column getColumn() {
        return column;
    }

    @Override
    public void setAutocomplete() {
        autoCompleteData = new AutoCompleteData(column);
        AutoCompleteDecorator.decorate(
            textAreaEdit,
            autoCompleteData.getList(),
            false);
    }

    public AutoCompleteData getAutoCompleteData() {
        return autoCompleteData;
    }

    @Override
    public boolean isEmpty() {
        return textAreaEdit.getText().isEmpty();
    }

    @Override
    public void focus() {
        textAreaEdit.requestFocus();
    }

    @Override
    public void setEditable(boolean editable) {
        textAreaEdit.setEditable(editable);
        textAreaEdit.setBackground(editable ? editableColor : getBackground());
    }

    @Override
    public TextEntry clone() {
        return new TextEntryContent(getText(), column);
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
        textAreaEdit = textAreaEdit = new TabLeavingTextArea();

        setLayout(new java.awt.GridBagLayout());

        labelPrompt.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        labelPrompt.setText(Bundle.getString("EditTextEntryPanel.labelPrompt.text")); // NOI18N
        labelPrompt.setToolTipText(column.getLongerDescription());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(labelPrompt, gridBagConstraints);

        textAreaEdit.setColumns(1);
        textAreaEdit.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        textAreaEdit.setRows(1);
        scrollPane.setViewportView(textAreaEdit);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.5;
        add(scrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelPrompt;
    private javax.swing.JScrollPane scrollPane;
    public javax.swing.JTextArea textAreaEdit;
    // End of variables declaration//GEN-END:variables
}
