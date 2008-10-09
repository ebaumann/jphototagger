package de.elmar_baumann.imv.view.panels;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.MetaDataEditTemplate;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.data.XmpUtil;
import de.elmar_baumann.imv.database.Database;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.EditHints;
import de.elmar_baumann.imv.database.metadata.selections.EditHints.SizeEditField;
import de.elmar_baumann.imv.database.metadata.selections.EditColumns;
import de.elmar_baumann.imv.database.metadata.mapping.IptcXmpMapping;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.MetaDataEditPanelEvent;
import de.elmar_baumann.imv.event.MetaDataEditPanelListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.lib.component.TabLeavingTextArea;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panels mit Edit-Feldern zum Bearbeiten von Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class MetadataEditPanelsArray implements FocusListener, DatabaseListener {

    boolean editable = true;
    private JComponent container;
    private List<JPanel> panels = new ArrayList<JPanel>();
    private List<String> filenames = new ArrayList<String>();
    private List<MetaDataEditPanelListener> listener = new ArrayList<MetaDataEditPanelListener>();
    private MetaDataEditActionsPanel metadataEditActionsPanel;
    private boolean isUseAutocomplete = UserSettings.getInstance().isUseAutocomplete();
    private Component lastFocussedComponent;

    public MetadataEditPanelsArray(JComponent container) {
        this.container = container;
        createEditPanels();
        addPanels();
        setFocusToFirstEditField();
        Database.getInstance().addDatabaseListener(this);
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
    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    /**
     * Liefert alle Texte.
     * 
     * @return Texte
     */
    public List<TextEntry> getTextEntries() {
        List<TextEntry> textEntries = new ArrayList<TextEntry>();
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
     *         ({@link de.elmar_baumann.imv.data.MetaDataEditTemplate#getName()})
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
    public void setXmpPropertyInfos(List<String> filenames, List<XMPPropertyInfo> infos) {
        this.filenames = filenames;
        emptyPanels();
        IptcXmpMapping mapping = IptcXmpMapping.getInstance();
        XmpMetadata xmpMetaData = new XmpMetadata();
        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            Column xmpColumn = textEntry.getColumn();
            IPTCEntryMeta iptcEntryMeta = mapping.getIptcEntryMetaOfXmpColumn(xmpColumn);
            List<XMPPropertyInfo> matchingInfos =
                xmpMetaData.getFilteredPropertyInfosOfIptcEntryMeta(iptcEntryMeta, infos);
            int countMatchingInfos = matchingInfos.size();

            for (int i = 0; i < countMatchingInfos; i++) {
                textEntry.setText(textEntry.getText() +
                    (i > 0 ? ", " : "") + // NOI18N
                    matchingInfos.get(i).getValue().toString().trim());
            }
        }
    }

    private void addPanels() {
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
        addActionPanel(layout);
    }

    private void addActionPanel(GridBagLayout layout) {
        metadataEditActionsPanel = Panels.getInstance().getAppPanel().getMetaDataEditActionsPanel();
        layout.setConstraints(metadataEditActionsPanel, getConstraints());
        container.add(metadataEditActionsPanel);
        metadataEditActionsPanel.tabbedPane.addFocusListener(this);
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

    private void setFocusToFirstEditField() {
        if (panels.size() > 0) {
            TextEntry textEntry = (TextEntry) panels.get(0);
            textEntry.focus();
        }
    }

    public void setFocusToLastFocussedComponent() {
        if (lastFocussedComponent != null) {
            lastFocussedComponent.requestFocus();
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
                panel.textAreaEdit.addFocusListener(this);
                panels.add(panel);
            } else {
                TextEntryEditFieldPanel panel = new TextEntryEditFieldPanel(column);
                panel.textFieldEdit.addFocusListener(this);
                panels.add(panel);
            }
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
        if (e.getComponent() == metadataEditActionsPanel.tabbedPane) {
            setFocusToFirstEditField();
        } else {
            lastFocussedComponent = e.getComponent();
            scrollToVisible(e.getSource());
        }
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

    @Override
    public void actionPerformed(DatabaseAction action) {
        if (isUseAutocomplete && action.isImageModified()) {
            ImageFile data = action.getImageFileData();
            if (data != null && data.getXmp() != null) {
                addAutoCompleteData(data.getXmp());
            }
        }
    }

    private void addAutoCompleteData(Xmp xmp) {
        for (JPanel panel : panels) {
            if (panel instanceof TextEntryEditFieldPanel) {
                TextEntryEditFieldPanel p = (TextEntryEditFieldPanel) panel;
                XmpUtil.addData(xmp, p.getColumn(), p.getAutoCompleteData());
            }
        }
    }
}

