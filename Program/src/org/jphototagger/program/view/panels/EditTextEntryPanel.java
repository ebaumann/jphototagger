package org.jphototagger.program.view.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.core.Storage;
import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.xmp.ColumnXmpDcTitle;
import org.jphototagger.domain.event.listener.TextEntryListener;
import org.jphototagger.domain.event.listener.impl.TextEntryListenerSupport;
import org.jphototagger.domain.repository.event.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.XmpUpdatedEvent;
import org.jphototagger.domain.text.TextEntry;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.componentutil.Autocomplete;
import org.jphototagger.program.database.metadata.selections.AutoCompleteDataOfColumn;
import org.jphototagger.program.helper.AutocompleteHelper;
import org.openide.util.Lookup;

/**
 * Panel zum Eingeben einzeiliger Texte.
 *
 * @author Elmar Baumann
 */
public final class EditTextEntryPanel extends JPanel implements TextEntry, DocumentListener {
    private static final Color EDITABLE_COLOR = Color.WHITE;
    private static final long serialVersionUID = -6455550547873630461L;
    private transient Column column;
    private boolean dirty = false;
    private boolean editable;
    private transient TextEntryListenerSupport textEntryListenerSupport = new TextEntryListenerSupport();
    private Autocomplete autocomplete;

    public EditTextEntryPanel() {
        column = ColumnXmpDcTitle.INSTANCE;
        initComponents();
        AnnotationProcessor.process(this);
    }

    public EditTextEntryPanel(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        this.column = column;
        initComponents();
        postSetColumn();
        AnnotationProcessor.process(this);
    }

    private void postSetColumn() {
        setPropmt();
        textAreaEdit.setInputVerifier(column.getInputVerifier());
        textAreaEdit.getDocument().addDocumentListener(this);
        textAreaEdit.setName("JPhotoTagger text area for " + column.getDescription());
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
        if (getPersistedAutocomplete()) {
            synchronized (this) {
                if (autocomplete != null) {
                    return;
                }
            }

            autocomplete = new Autocomplete(false);
            autocomplete.decorate(textAreaEdit, AutoCompleteDataOfColumn.INSTANCE.get(column).get(), true);
        }
    }

    private boolean getPersistedAutocomplete() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_ENABLE_AUTOCOMPLETE)
                ? storage.getBoolean(Storage.KEY_ENABLE_AUTOCOMPLETE)
                : true;
    }

    private boolean isAutocomplete() {
        return autocomplete != null && getPersistedAutocomplete();
    }

    private void addToAutocomplete(Xmp xmp) {
        if (isAutocomplete()) {
            AutocompleteHelper.addAutocompleteData(column, autocomplete, xmp);
        }
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        addToAutocomplete(evt.getXmp());
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        addToAutocomplete(evt.getUpdatedXmp());
    }

    @EventSubscriber(eventClass = DcSubjectInsertedEvent.class)
    public void dcSubjectInserted(DcSubjectInsertedEvent evt) {
        if (isAutocomplete()) {
            AutocompleteHelper.addAutocompleteData(column, autocomplete, Collections.singleton(evt.getDcSubject()));
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

    private void notifyTextChanged(Column column, String oldText, String newText) {
        textEntryListenerSupport.notifyTextChanged(column, oldText, newText);
    }

    @Override
    public List<Component> getInputComponents() {
        return Arrays.asList((Component) textAreaEdit);
    }

    @Override
    public synchronized void addMouseListenerToInputComponents(MouseListener l) {
        if (l == null) {
            throw new NullPointerException("l == null");
        }

        List<Component> inputComponents = getInputComponents();

        for (Component component : inputComponents) {
            component.addMouseListener(l);
        }
    }

    @Override
    public synchronized void removeMouseListenerFromInputComponents(MouseListener l) {
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

    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        labelPrompt = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        textAreaEdit = new javax.swing.JTextArea();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        labelPrompt.setText("Prompt:"); // NOI18N
        labelPrompt.setToolTipText(column.getLongerDescription());
        labelPrompt.setName("labelPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(labelPrompt, gridBagConstraints);

        scrollPane.setName("scrollPane"); // NOI18N

        textAreaEdit.setColumns(1);
        textAreaEdit.setLineWrap(true);
        textAreaEdit.setRows(1);
        textAreaEdit.setWrapStyleWord(true);
        textAreaEdit.setName("textAreaEdit"); // NOI18N
        scrollPane.setViewportView(textAreaEdit);
        textAreaEdit.setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerDropTextComponent());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(scrollPane, gridBagConstraints);
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelPrompt;
    private javax.swing.JScrollPane scrollPane;
    public javax.swing.JTextArea textAreaEdit;
    // End of variables declaration//GEN-END:variables
}
