package de.elmar_baumann.imv.view.panels;

import com.adobe.xmp.properties.XMPPropertyInfo;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.controller.metadata.ControllerSaveMetadata;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.MetadataEditTemplate;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.data.AutoCompleteUtil;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.EditHints;
import de.elmar_baumann.imv.database.metadata.selections.EditHints.SizeEditField;
import de.elmar_baumann.imv.database.metadata.selections.EditColumns;
import de.elmar_baumann.imv.database.metadata.mapping.IptcXmpMapping;
import de.elmar_baumann.imv.event.AppExitListener;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.imv.event.ListenerProvider;
import de.elmar_baumann.imv.event.MetadataEditPanelEvent;
import de.elmar_baumann.imv.event.MetadataEditPanelListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.lib.component.TabLeavingTextArea;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panels mit Edit-Feldern zum Bearbeiten von Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class EditMetadataPanelsArray implements FocusListener, DatabaseListener,
    AppExitListener {

    boolean editable = true;
    private JComponent container;
    private List<JPanel> panels = new ArrayList<JPanel>();
    private List<String> filenames = new ArrayList<String>();
    private List<MetadataEditPanelListener> listeners = new LinkedList<MetadataEditPanelListener>();
    private EditMetadataActionsPanel editActionsPanel;
    private boolean isUseAutocomplete = false;
    private Component lastFocussedComponent;
    private ListenerProvider listenerProvider;

    public EditMetadataPanelsArray(JComponent container) {
        this.container = container;
        listenerProvider = ListenerProvider.getInstance();
        listeners = listenerProvider.getMetadataEditPanelListeners();
        createEditPanels();
        addPanels();
        setFocusToFirstEditField();
        listenToActionSources();
    }

    private void checkDirty() {
        boolean dirty = false;
        int size = panels.size();
        for (int i = 0; !dirty && i < size; i++) {
            dirty = ((TextEntry) panels.get(i)).isDirty();
        }
        if (dirty) {
            save();
        }
    }

    private void save() {
        if (isEditable() && confirmSave()) {
            ControllerSaveMetadata.saveMetadata(this);
        }
    }

    private boolean confirmSave() {
        return JOptionPane.showConfirmDialog(
            null,
            Bundle.getString("EditMetadataPanelsArray.ConfirmMessage.Save"),
            Bundle.getString("EditMetadataPanelsArray.ConfirmMessage.Save.Title"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            AppSettings.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }

    private void notifyActionListener(MetadataEditPanelEvent evt) {
        for (MetadataEditPanelListener l : listeners) {
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
        notifyActionListener(new MetadataEditPanelEvent(this,
            editable
            ? MetadataEditPanelEvent.Type.EditEnabled
            : MetadataEditPanelEvent.Type.EditDisabled));
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
            textEntries.add(((TextEntry) panel).clone());
        }
        return textEntries;
    }

    /**
     * Setzt ein Metadaten-Edit-Template.
     * 
     * @param template  Template
     */
    public void setMetadataEditTemplate(MetadataEditTemplate template) {
        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            String value = template.getValueOfColumn(textEntry.getColumn());
            if (!value.isEmpty()) {
                textEntry.setText(value);
                textEntry.setDirty(true);
            }
        }
    }

    /**
     * Liefert ein Metadaten-Edit-Template mit den Daten der Panels.
     * 
     * @return Template <em>ohne</em> Name
     *         ({@link de.elmar_baumann.imv.data.MetadataEditTemplate#getName()})
     */
    public MetadataEditTemplate getMetadataEditTemplate() {
        MetadataEditTemplate template = new MetadataEditTemplate();
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
     * Sets the edit status.
     * 
     * @param dirty  true if changes were made
     */
    public void setDirty(boolean dirty) {
        for (JPanel panel : panels) {
            ((TextEntry) panel).setDirty(dirty);
        }
    }

    /**
     * Setzt IPTC-Einträge in die einzelnen Edit-Felder.
     * 
     * @param filenames Dateinamen, deren Metadaten angezeigt werden
     * @param infos     Zu setzende Einträge
     */
    public void setXmpPropertyInfos(List<String> filenames, List<XMPPropertyInfo> infos) {
        emptyPanels(false);
        this.filenames = filenames;
        IptcXmpMapping mapping = IptcXmpMapping.getInstance();
        XmpMetadata xmpMetadata = new XmpMetadata();
        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            Column xmpColumn = textEntry.getColumn();
            IPTCEntryMeta iptcEntryMeta = mapping.getIptcEntryMetaOfXmpColumn(xmpColumn);
            List<XMPPropertyInfo> matchingInfos =
                xmpMetadata.getFilteredPropertyInfosOfIptcEntryMeta(iptcEntryMeta, infos);

            int countMatchingInfos = matchingInfos.size();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < countMatchingInfos; i++) {
                buffer.append((i > 0 ? XmpMetadata.getArrayItemDelimiter() : "") + // NOI18N
                    matchingInfos.get(i).getValue().toString().trim());
            }
            if (buffer.length() > 0) {
                textEntry.setText(buffer.toString());
                textEntry.setDirty(false);
            }
        }
    }

    public void addDeleteListenerTo(JMenuItem itemDelete) {
        for (JPanel panel : panels) {
            if (panel instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel listener = (EditRepeatableTextEntryPanel) panel;
                itemDelete.addActionListener(listener);
            }
        }
    }

    private void addPanels() {
        container.removeAll();
        container.setLayout(new GridBagLayout());
        int size = panels.size();
        for (int i = 0; i < size; i++) {
            GridBagConstraints constraints = newConstraints();
            if (i == size - 1) {
                constraints.insets.bottom += 10;
            }
            container.add(panels.get(i), constraints);
        }
        addActionPanel();
    }

    private GridBagConstraints newConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(5, 10, 0, 10);
        constraints.weightx = 1;
        return constraints;
    }

    private void listenToActionSources() {
        DatabaseImageFiles.getInstance().addDatabaseListener(this);
        Panels.getInstance().getAppFrame().addAppExitListener(this);
    }

    private void addActionPanel() {
        editActionsPanel = Panels.getInstance().getAppPanel().getMetadataEditActionsPanel();
        GridBagConstraints gbc = newConstraints();
        gbc.weighty = 1;
        container.add(editActionsPanel, gbc);
        editActionsPanel.tabbedPane.addFocusListener(this);
    }

    public void setFocusToFirstEditField() {
        if (panels.size() > 0) {
            TextEntry textEntry = (TextEntry) panels.get(0);
            textEntry.focus();
            lastFocussedComponent = panels.get(0);
        }
    }

    public void setFocusToLastFocussedComponent() {
        if (lastFocussedComponent != null) {
            lastFocussedComponent.requestFocus();
        } else {
            setFocusToFirstEditField();
        }
    }

    private void createEditPanels() {
        List<Column> columns = UserSettings.getInstance().getEditColumns();

        for (Column column : columns) {
            EditHints editHints = EditColumns.getEditHints(column);
            boolean large = editHints.getSizeEditField().equals(SizeEditField.large);
            boolean isRepeatable = editHints.isRepeatable();

            if (isRepeatable) {
                EditRepeatableTextEntryPanel panel = new EditRepeatableTextEntryPanel(column);
                panel.textFieldInput.addFocusListener(this);
                panels.add(panel);
            } else {
                EditTextEntryPanel panel = new EditTextEntryPanel(column);
                panel.textAreaEdit.addFocusListener(this);
                panel.textAreaEdit.setRows(large ? 2 : 1);
                panels.add(panel);
            }
        }
    }

    public void setAutocomplete() {
        isUseAutocomplete = true;
        for (JPanel panel : panels) {
            if (panel instanceof TextEntry) {
                TextEntry textEntry = (TextEntry) panel;
                textEntry.setAutocomplete();
            }
        }
    }

    public void emptyPanels(boolean setDirty) {
        checkDirty();
        for (JPanel panel : panels) {
            TextEntry entry = (TextEntry) panel;
            entry.setText(""); // NOI18N
            entry.setDirty(setDirty);
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        Component source = (Component) e.getSource();
        if (isEditArea(source)) {
            lastFocussedComponent = source;
            scrollToVisible(e.getSource());
        } else {
            setFocusToFirstEditField();
        }
    }

    private boolean isEditArea(Component c) {
        return c instanceof TabLeavingTextArea || c instanceof JTextField;
    }

    @Override
    public void focusLost(FocusEvent e) {
    }

    private void scrollToVisible(Object inputSource) {
        Component parent = null;
        if (inputSource instanceof JTextField) {
            JTextField textField = (JTextField) inputSource;
            parent = textField.getParent();
            container.scrollRectToVisible(parent.getBounds());
        } else if (inputSource instanceof TabLeavingTextArea) {
            TabLeavingTextArea textArea = (TabLeavingTextArea) inputSource;
            parent = textArea.getParent().getParent().getParent();
            container.scrollRectToVisible(parent.getBounds());
        }
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
            if (panel instanceof EditTextEntryPanel) {
                EditTextEntryPanel p = (EditTextEntryPanel) panel;
                AutoCompleteUtil.addData(xmp, p.getColumn(), p.getAutoCompleteData());
            } else if (panel instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel p = (EditRepeatableTextEntryPanel) panel;
                AutoCompleteUtil.addData(xmp, p.getColumn(), p.getAutoCompleteData());
            }
        }
    }

    @Override
    public void appWillExit() {
        checkDirty();
    }
}

