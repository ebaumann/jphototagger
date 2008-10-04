package de.elmar_baumann.imagemetadataviewer.view.panels;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imagemetadataviewer.data.MetaDataEditTemplate;
import de.elmar_baumann.imagemetadataviewer.data.TextEntry;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.EditHints;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.EditHints.SizeEditField;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.EditColumns;
import de.elmar_baumann.imagemetadataviewer.database.metadata.mapping.IptcXmpMapping;
import de.elmar_baumann.imagemetadataviewer.event.MetaDataEditPanelEvent;
import de.elmar_baumann.imagemetadataviewer.event.MetaDataEditPanelListener;
import de.elmar_baumann.imagemetadataviewer.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.lib.component.TabLeavingTextArea;
import de.elmar_baumann.lib.component.text.MaxLengthDocument;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Set;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Panels mit Edit-Feldern zum Bearbeiten von Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public class MetaDataEditPanelsArray implements FocusListener {

    boolean editable = true;
    private JComponent container;
    private Vector<JPanel> panels = new Vector<JPanel>();
    private Vector<String> filenames = new Vector<String>();
    private Vector<MetaDataEditPanelListener> listener = new Vector<MetaDataEditPanelListener>();

    public MetaDataEditPanelsArray(JComponent container) {
        this.container = container;
        createEditPanels();
        addEditPanels();
        focus();
    }

    /**
     * Fügt einen Beobachter hinzu.
     * 
     * @param listener  Beobachter
     */
    public void addActionListener(MetaDataEditPanelListener listener) {
        this.listener.add(listener);
    }

    /**
     * Entfernt einen Beobachter.
     * 
     * @param listener  Beobachter
     */
    public void removeActionListener(MetaDataEditPanelListener listener) {
        this.listener.remove(listener);
    }

    private void notifyActionListener(MetaDataEditPanelEvent evt) {
        for (MetaDataEditPanelListener l : listener) {
            l.actionPerformed(evt);
        }
    }

    /**
     * Setzt, ob die Daten bearbeitet werden können.
     * 
     * @param editable  true, wenn bearbeitbar
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
        for (JPanel panel : panels) {
            ((TextEntry) panel).setEditable(editable);
        }
        notifyActionListener(new MetaDataEditPanelEvent(this,
            editable
            ? MetaDataEditPanelEvent.Type.EditEnabled
            : MetaDataEditPanelEvent.Type.EditDisabled));
    }

    /**
     * Liefert, ob die Daten bearbeitet werden können.
     * 
     * @return true, wenn Bearbeiten möglich ist.
     *         Default: true.
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * Liefert die Dateinamen, deren Daten angezeigt werden.
     * 
     * @return Dateinamen
     */
    public Vector<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(Vector<String> filenames) {
        this.filenames = filenames;
    }

    /**
     * Liefert alle Texte.
     * 
     * @return Texte
     */
    public Vector<TextEntry> getTextEntries() {
        Vector<TextEntry> textEntries = new Vector<TextEntry>();
        for (JPanel panel : panels) {
            textEntries.add((TextEntry) panel);
        }
        return textEntries;
    }

    /**
     * Setzt ein Metadaten-Edit-Template.
     * 
     * @param template  Template
     */
    public void setMetaDataEditTemplate(MetaDataEditTemplate template) {
        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            String value = template.getValueOfColumn(textEntry.getColumn());
            if (!value.isEmpty()) {
                textEntry.setText(value);
            }
        }
    }

    /**
     * Liefert ein Metadaten-Edit-Template mit den Daten der Panels.
     * 
     * @return Template <em>ohne</em> Name
     *         ({@link de.elmar_baumann.imagemetadataviewer.data.MetaDataEditTemplate#getName()})
     */
    public MetaDataEditTemplate getMetaDataEditTemplate() {
        MetaDataEditTemplate template = new MetaDataEditTemplate();
        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            String value = textEntry.getText().trim();
            if (!value.isEmpty()) {
                template.setValueOfColumn(textEntry.getColumn(), value);
            }
        }
        return template;
    }

    /**
     * Setzt IPTC-Einträge in die einzelnen Edit-Felder.
     * 
     * @param filenames Dateinamen, deren Metadaten angezeigt werden
     * @param infos     Zu setzende Einträge
     */
    public void setXmpPropertyInfos(Vector<String> filenames, Vector<XMPPropertyInfo> infos) {
        this.filenames = filenames;
        emptyPanels();
        IptcXmpMapping mapping = IptcXmpMapping.getInstance();
        XmpMetadata xmpMetaData = new XmpMetadata();
        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            Column xmpColumn = textEntry.getColumn();
            IPTCEntryMeta iptcEntryMeta = mapping.getIptcEntryMetaOfXmpColumn(xmpColumn);
            Vector<XMPPropertyInfo> matchingInfos =
                xmpMetaData.getFilteredPropertyInfosOfIptcEntryMeta(iptcEntryMeta, infos);
            int countMatchingInfos = matchingInfos.size();

            for (int i = 0; i < countMatchingInfos; i++) {
                textEntry.setText(textEntry.getText() +
                    (i > 0 ? ", " : "") + // NOI18N
                    matchingInfos.get(i).getValue().toString().trim());
            }
        }
    }

    private void addEditPanels() {
        container.removeAll();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = getConstraints();
        container.setLayout(layout);
        int size = panels.size();
        for (int i = 0; i < size; i++) {
            if (i == size - 1) {
                constraints.insets.bottom += 10;
            }
            layout.setConstraints(panels.get(i), constraints);
            container.add(panels.get(i));
        }
    }

    private GridBagConstraints getConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(5, 15, 0, 10);
        constraints.ipadx = 0;
        constraints.ipady = 0;
        constraints.weightx = 1;
        return constraints;
    }

    /**
     * Setzt den Fokus auf das erste Eingabefeld.
     */
    public void focus() {
        if (panels.size() > 0) {
            TextEntry textEntry = (TextEntry) panels.get(0);
            textEntry.focus();
        }
    }

    private void createEditPanels() {
        EditColumns editColumns = EditColumns.getInstance();
        Set<Column> columns = editColumns.getColumns();

        for (Column column : columns) {
            EditHints editHints = editColumns.getEditHintsForColumn(column);
            SizeEditField size = editHints.getSizeEditField();

            if (size.equals(SizeEditField.large)) {
                TextEntryEditAreaPanel panel = new TextEntryEditAreaPanel(column);
                panel.textAreaEdit.setDocument(getDocument(column, editHints));
                panel.textAreaEdit.addFocusListener(this);
                panels.add(panel);
            } else {
                TextEntryEditFieldPanel panel = new TextEntryEditFieldPanel(column);
                panel.textFieldEdit.setDocument(getDocument(column, editHints));
                panel.textFieldEdit.addFocusListener(this);
                panels.add(panel);
            }
        }
    }

    private Document getDocument(Column column, EditHints editHints) {
        if (editHints.isRepeatable()) {
            return new PlainDocument();
        } else {
            return new MaxLengthDocument(column.getLength());
        }
    }

    /**
     * Leert die Panels.
     */
    public void emptyPanels() {
        for (JPanel panel : panels) {
            TextEntry entry = (TextEntry) panel;
            entry.setText(""); // NOI18N
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        scrollToVisible(e.getSource());
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

    private void scrollToVisible(Object inputSource) {
        Component parent = null;
        if (inputSource instanceof JTextField) {
            JTextField textField = (JTextField) inputSource;
            parent = textField.getParent();
        } else if (inputSource instanceof TabLeavingTextArea) {
            TabLeavingTextArea textArea = (TabLeavingTextArea) inputSource;
            parent = textArea.getParent().getParent().getParent();
        }
        container.scrollRectToVisible(parent.getBounds());
    }
}

