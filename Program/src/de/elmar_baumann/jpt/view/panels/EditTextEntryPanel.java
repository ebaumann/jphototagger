/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.data.AutoCompleteDataOfColumn;
import de.elmar_baumann.jpt.data.TextEntry;
import de.elmar_baumann.jpt.data.TextEntryContent;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.datatransfer.TransferHandlerDropEdit;
import de.elmar_baumann.jpt.event.listener.TextEntryListener;
import de.elmar_baumann.jpt.event.listener.impl.TextEntryListenerSupport;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.lib.componentutil.InputVerifierMaxLength;
import de.elmar_baumann.lib.component.TabOrEnterLeavingTextArea;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 * Panel zum Eingeben einzeiliger Texte.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-18
 */
public final class EditTextEntryPanel
        extends    JPanel
        implements TextEntry,
                   DocumentListener {

    private static final   Color                    EDITABLE_COLOR = Color.WHITE;
    private                Column                   column;
    private                boolean                  dirty           = false;
    private                boolean                  editable;
    private                TextEntryListenerSupport textEntryListenerSupport = new TextEntryListenerSupport();

    public EditTextEntryPanel(Column column) {
        this.column = column;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setPropmt();
        textAreaEdit.setInputVerifier(new InputVerifierMaxLength(column.getLength()));
        textAreaEdit.getDocument().addDocumentListener(this);
    }

    private void setPropmt() {
        labelPrompt.setText(column.getDescription());
    }

    @Override
    public String getText() {
        return textAreaEdit.getText().trim();
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public void setText(String text) {
        textAreaEdit.setText(text.trim());
        dirty = false;
    }

    @Override
    public void empty(boolean dirty) {
        textAreaEdit.setText(""); //
        this.dirty = dirty;
    }

    @Override
    public Column getColumn() {
        return column;
    }

    @Override
    public void setAutocomplete() {
        AutoCompleteDecorator.decorate(
                textAreaEdit,
                AutoCompleteDataOfColumn.INSTANCE.get(column).getData(),
                false);
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
        this.editable = editable;
        textAreaEdit.setEditable(editable);
        textAreaEdit.setBackground(editable? EDITABLE_COLOR : getBackground());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public TextEntry clone() {
        return new TextEntryContent(getText(), column);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        notifyTextChanged(column, "", textAreaEdit.getText()); //
        dirty = true;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        notifyTextChanged(column, "", textAreaEdit.getText()); //
        dirty = true;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        notifyTextChanged(column, "", textAreaEdit.getText()); //
        dirty = true;
    }

    public void addTextEntryListener(TextEntryListener listener) {
        textEntryListenerSupport.addTextEntryListener(listener);
    }

    public void removeTextEntryListener(TextEntryListener listener) {
        textEntryListenerSupport.removeTextEntryListener(listener);
    }

    private void notifyTextChanged(Column column, String oldText, String newText) {
        textEntryListenerSupport.notifyTextChanged(column, oldText, newText);
    }

    @Override
    public List<Component> getInputComponents() {
        return Arrays.asList((Component)textAreaEdit);
    }

    @Override
    public synchronized void addMouseListenerToInputComponents(MouseListener l) {
        List<Component> inputComponents = getInputComponents();
        for (Component component : inputComponents) {
            component.addMouseListener(l);
        }
    }

    @Override
    public synchronized void removeMouseListenerFromInputComponents(MouseListener l) {
        List<Component> inputComponents = getInputComponents();
        for (Component component : inputComponents) {
            component.removeMouseListener(l);
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
        textAreaEdit = textAreaEdit = new TabOrEnterLeavingTextArea();

        setLayout(new java.awt.GridBagLayout());

        labelPrompt.setText(Bundle.getString("EditTextEntryPanel.labelPrompt.text")); //
        labelPrompt.setToolTipText(column.getLongerDescription());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(labelPrompt, gridBagConstraints);

        textAreaEdit.setColumns(1);
        textAreaEdit.setLineWrap(true);
        textAreaEdit.setRows(1);
        scrollPane.setViewportView(textAreaEdit);
        textAreaEdit.setTransferHandler(new TransferHandlerDropEdit());

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
