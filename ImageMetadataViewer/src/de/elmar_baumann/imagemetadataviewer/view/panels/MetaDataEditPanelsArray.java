package de.elmar_baumann.imagemetadataviewer.view.panels;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imagemetadataviewer.data.MetaDataEditTemplate;
import de.elmar_baumann.imagemetadataviewer.data.TextEntry;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.EditColumnHints;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.EditColumnHints.SizeEditField;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.EditColumns;
import de.elmar_baumann.imagemetadataviewer.database.metadata.mapping.IptcEntryMetaIptcColumnMapping;
import de.elmar_baumann.imagemetadataviewer.database.metadata.mapping.IptcXmpMapping;
import de.elmar_baumann.imagemetadataviewer.event.MetaDataEditPanelEvent;
import de.elmar_baumann.imagemetadataviewer.event.MetaDataEditPanelListener;
import de.elmar_baumann.imagemetadataviewer.image.metadata.iptc.IptcEntry;
import de.elmar_baumann.imagemetadataviewer.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.imagemetadataviewer.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.lib.component.TabLeavingTextArea;
import de.elmar_baumann.lib.component.text.MaxLengthDocument;
import de.elmar_baumann.lib.template.Pair;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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

    private Vector<JPanel> panels = new Vector<JPanel>();
    private JComponent container;
    private Vector<String> filenames = new Vector<String>();
    boolean editable = true;
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
            String value = template.getValueOfColumn(textEntry.getColumns().getSecond());
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
                template.setValueOfColumn(textEntry.getColumns().getSecond(), value);
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
        IptcEntryMetaIptcColumnMapping mapping = IptcEntryMetaIptcColumnMapping.getInstance();
        XmpMetadata xmpMetaData = new XmpMetadata();
        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            Column panelIptcColumn = textEntry.getColumns().getFirst();
            IPTCEntryMeta panelEntryMeta = mapping.getEntryMetaOfColumn(panelIptcColumn);
            Vector<XMPPropertyInfo> matchingInfos =
                xmpMetaData.getFilteredPropertyInfosOfIptcMeta(panelEntryMeta, infos);
            int countMatchingInfos = matchingInfos.size();

            for (int i = 0; i < countMatchingInfos; i++) {
                textEntry.setText(textEntry.getText() +
                    (i > 0 ? ", " : "") + // NOI18N
                    matchingInfos.get(i).getValue().toString().trim());
            }
        }
    }

    /**
     * Setzt IPTC-Einträge in die einzelnen Edit-Felder.
     * 
     * @param filenames    Dateinamen, deren Metadaten angezeigt werden
     * @param fileEntries  Zu setzende Einträge
     */
    public void setIptcEntries(Vector<String> filenames, Vector<IptcEntry> fileEntries) {
        this.filenames = filenames;
        emptyPanels();
        IptcEntryMetaIptcColumnMapping mapping = IptcEntryMetaIptcColumnMapping.getInstance();
        IptcMetadata iptcMetadata = new IptcMetadata();
        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            Column panelIptcColumn = textEntry.getColumns().getFirst();
            IPTCEntryMeta panelEntryMeta = mapping.getEntryMetaOfColumn(panelIptcColumn);
            Vector<IptcEntry> matchingEntries = iptcMetadata.getFilteredEntries(
                fileEntries, panelEntryMeta);
            int countMatchingEntries = matchingEntries.size();

            for (int i = 0; i < countMatchingEntries; i++) {
                textEntry.setText(textEntry.getText() +
                    (i > 0 ? ", " : "") + // NOI18N
                    matchingEntries.get(i).getData().trim());
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
        Vector<Pair<Column, EditColumnHints>> pairs = EditColumns.getInstance().getIptcColumns();
        IptcXmpMapping mapping = IptcXmpMapping.getInstance();

        for (Pair<Column, EditColumnHints> pair : pairs) {
            Column iptcColumn = pair.getFirst();
            Column xmpColumn = mapping.getXmpColumnOfIptcColumn(iptcColumn);
            SizeEditField size = pair.getSecond().getSizeEditField();
            EditColumnHints hints = pair.getSecond();
            Pair<Column, Column> columnPair = new Pair<Column, Column>(iptcColumn, xmpColumn);

            if (size.equals(SizeEditField.large)) {
                TextEntryEditAreaPanel panel = new TextEntryEditAreaPanel(columnPair);
                panel.textAreaEdit.setDocument(getDocument(iptcColumn, hints));
                panel.textAreaEdit.addFocusListener(this);
                panels.add(panel);
            } else {
                TextEntryEditFieldPanel panel = new TextEntryEditFieldPanel(columnPair);
                panel.textFieldEdit.setDocument(getDocument(iptcColumn, hints));
                panel.textFieldEdit.addFocusListener(this);
                panels.add(panel);
            }
        }
    }

    private Document getDocument(Column column, EditColumnHints hints) {
        if (hints.isRepeatable()) {
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

