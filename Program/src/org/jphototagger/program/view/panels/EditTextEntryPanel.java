package org.jphototagger.program.view.panels;

import org.jphototagger.program.UserSettings;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.TextEntry;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections
    .AutoCompleteDataOfColumn;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcTitle;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.event.listener.impl.TextEntryListenerSupport;
import org.jphototagger.program.event.listener.TextEntryListener;
import org.jphototagger.program.helper.AutocompleteHelper;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.lib.componentutil.Autocomplete;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.io.File;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JPanel;

/**
 * Panel zum Eingeben einzeiliger Texte.
 *
 * @author Elmar Baumann
 */
public final class EditTextEntryPanel extends JPanel
        implements TextEntry, DocumentListener, DatabaseImageFilesListener {
    private static final Color                 EDITABLE_COLOR   = Color.WHITE;
    private static final long                  serialVersionUID =
        -6455550547873630461L;
    private transient Column                   column;
    private boolean                            dirty = false;
    private boolean                            editable;
    private transient TextEntryListenerSupport textEntryListenerSupport =
        new TextEntryListenerSupport();
    private Autocomplete autocomplete;

    public EditTextEntryPanel() {
        column = ColumnXmpDcTitle.INSTANCE;
        initComponents();
    }

    public EditTextEntryPanel(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

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
        if (column == null) {
            throw new NullPointerException("column == null");
        }

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
        if (text == null) {
            throw new NullPointerException("text == null");
        }

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
        if (UserSettings.INSTANCE.isAutocomplete()) {
            synchronized (this) {
                if (autocomplete != null) {
                    return;
                }
            }
            autocomplete = new Autocomplete();
            autocomplete.decorate(
                textAreaEdit, AutoCompleteDataOfColumn.INSTANCE.get(column).get(),
                    true);
        }
    }

    private boolean isAutocomplete() {
        return autocomplete != null && UserSettings.INSTANCE.isAutocomplete();
    }

    private void addToAutocomplete(Xmp xmp) {
        if (isAutocomplete()) {
            AutocompleteHelper.addAutocompleteData(column, autocomplete, xmp);
        }
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        addToAutocomplete(xmp);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        addToAutocomplete(updatedXmp);
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {

        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void imageFileDeleted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileInserted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {

        // ignore
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {

        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {

        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {
        if (isAutocomplete()) {
            AutocompleteHelper.addAutocompleteData(column, autocomplete,
                    Collections.singleton(dcSubject));
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
        textAreaEdit.setBackground(editable
                                   ? EDITABLE_COLOR
                                   : getBackground());
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    @Override
    public void insertUpdate(DocumentEvent evt) {
        notifyTextChanged(column, "", textAreaEdit.getText());
        dirty = true;
    }

    @Override
    public void removeUpdate(DocumentEvent evt) {
        notifyTextChanged(column, "", textAreaEdit.getText());
        dirty = true;
    }

    @Override
    public void changedUpdate(DocumentEvent evt) {
        notifyTextChanged(column, "", textAreaEdit.getText());
        dirty = true;
    }

    public void addTextEntryListener(TextEntryListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        textEntryListenerSupport.add(listener);
    }

    public void removeTextEntryListener(TextEntryListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        textEntryListenerSupport.remove(listener);
    }

    private void notifyTextChanged(Column column, String oldText,
                                   String newText) {
        textEntryListenerSupport.notifyTextChanged(column, oldText, newText);
    }

    @Override
    public List<Component> getInputComponents() {
        return Arrays.asList((Component) textAreaEdit);
    }

    @Override
    public synchronized void addMouseListenerToInputComponents(
            MouseListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        List<Component> inputComponents = getInputComponents();

        for (Component component : inputComponents) {
            component.addMouseListener(l);
        }
    }

    @Override
    public synchronized void removeMouseListenerFromInputComponents(
            MouseListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        List<Component> inputComponents = getInputComponents();

        for (Component component : inputComponents) {
            component.removeMouseListener(l);
        }
    }

    /**
     * This method is called from within the constructor to
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
        textAreaEdit.setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerDropTextComponent());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(scrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelPrompt;
    private javax.swing.JScrollPane scrollPane;
    public javax.swing.JTextArea textAreaEdit;
    // End of variables declaration//GEN-END:variables
}
