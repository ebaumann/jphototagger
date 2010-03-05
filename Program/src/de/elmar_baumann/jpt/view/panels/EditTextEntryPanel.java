/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.database.metadata.selections.AutoCompleteDataOfColumn;
import de.elmar_baumann.jpt.data.TextEntry;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.event.listener.TextEntryListener;
import de.elmar_baumann.jpt.event.listener.impl.TextEntryListenerSupport;
import de.elmar_baumann.jpt.helper.AutocompleteHelper;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.componentutil.Autocomplete;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Panel zum Eingeben einzeiliger Texte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-18
 */
public final class EditTextEntryPanel
        extends    JPanel
        implements TextEntry,
                   DocumentListener,
                   DatabaseImageFilesListener
    {

    private static final Color                    EDITABLE_COLOR           = Color.WHITE;
    private static final long                     serialVersionUID         = -6455550547873630461L;
    private transient    Column                   column;
    private              boolean                  dirty                    = false;
    private              boolean                  editable;
    private transient    TextEntryListenerSupport textEntryListenerSupport = new TextEntryListenerSupport();
    private              Autocomplete             autocomplete;

    public EditTextEntryPanel() {
        column = ColumnXmpDcTitle.INSTANCE;
        initComponents();
    }

    public EditTextEntryPanel(Column column) {
        this.column = column;
        initComponents();
        postSetColumn();
    }

    private void postSetColumn() {
        setPropmt();
        textAreaEdit.setInputVerifier(column.getInputVerifier());
        textAreaEdit.getDocument().addDocumentListener(this);
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    public void setColumn(Column column) {
        this.column = column;
        postSetColumn();
    }

    private void setPropmt() {
        labelPrompt.setText(column.getDescription());
        labelPrompt.setLabelFor(textAreaEdit);
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
        textAreaEdit.setText("");
        this.dirty = dirty;
    }

    @Override
    public Column getColumn() {
        return column;
    }

    @Override
    public void setAutocomplete() {
        synchronized (this) {
            if (autocomplete != null) return;
        }
        autocomplete = new Autocomplete();
        autocomplete.decorate(
                textAreaEdit,
                AutoCompleteDataOfColumn.INSTANCE.get(column).get());
    }

    @Override
    public void actionPerformed(DatabaseImageFilesEvent event) {
        if (autocomplete == null) return;
        if (event.getType().equals(DatabaseImageFilesEvent.Type.IMAGEFILE_DELETED)) return; // Do not remove autocomplete data

        ImageFile imageFile = event.getImageFile();

        if (imageFile != null && imageFile.getXmp() != null) {
            AutocompleteHelper.addAutocompleteData(
                    column, autocomplete, imageFile.getXmp());
        }
    }

    @Override
    public boolean isEmpty() {
        return textAreaEdit.getText().isEmpty();
    }

    @Override
    public void focus() {
        textAreaEdit.requestFocus();
        textAreaEdit.selectAll();
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
    public void insertUpdate(DocumentEvent e) {
        notifyTextChanged(column, "", textAreaEdit.getText());
        dirty = true;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        notifyTextChanged(column, "", textAreaEdit.getText());
        dirty = true;
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        notifyTextChanged(column, "", textAreaEdit.getText());
        dirty = true;
    }

    public void addTextEntryListener(TextEntryListener listener) {
        textEntryListenerSupport.add(listener);
    }

    public void removeTextEntryListener(TextEntryListener listener) {
        textEntryListenerSupport.remove(listener);
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
        textAreaEdit = new javax.swing.JTextArea();

        setLayout(new java.awt.GridBagLayout());

        labelPrompt.setText(JptBundle.INSTANCE.getString("EditTextEntryPanel.labelPrompt.text")); // NOI18N
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
        textAreaEdit.setWrapStyleWord(true);
        scrollPane.setViewportView(textAreaEdit);
        textAreaEdit.setTransferHandler(new de.elmar_baumann.jpt.datatransfer.TransferHandlerDropEdit());

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
