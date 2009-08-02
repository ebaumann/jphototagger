package de.elmar_baumann.imv.view.panels;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppLifeCycle;
import de.elmar_baumann.imv.controller.hierarchicalkeywords.SuggestHierarchicalKeywords;
import de.elmar_baumann.imv.helper.SaveEditedMetadata;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.MetadataEditTemplate;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.data.AutoCompleteUtil;
import de.elmar_baumann.imv.data.SelectedFile;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.EditHints;
import de.elmar_baumann.imv.database.metadata.selections.EditHints.SizeEditField;
import de.elmar_baumann.imv.database.metadata.selections.EditColumns;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.imv.event.TextSelectionEvent;
import de.elmar_baumann.imv.event.listener.AppExitListener;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import de.elmar_baumann.imv.event.listener.impl.ListenerProvider;
import de.elmar_baumann.imv.event.MetadataEditPanelEvent;
import de.elmar_baumann.imv.event.listener.MetadataEditPanelListener;
import de.elmar_baumann.imv.event.listener.TextSelectionListener;
import de.elmar_baumann.imv.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.TextSelectionDialog;
import de.elmar_baumann.lib.component.TabLeavingTextArea;
import de.elmar_baumann.lib.generics.Pair;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Panels mit Edit-Feldern zum Bearbeiten von Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class EditMetadataPanelsArray implements FocusListener,
                                                      DatabaseListener,
                                                      AppExitListener,
                                                      TextSelectionListener,
                                                      KeyListener {

    private final List<JPanel> panels = new ArrayList<JPanel>();
    private final List<Pair<String, Xmp>> filenamesXmp =
            new ArrayList<Pair<String, Xmp>>();
    private List<MetadataEditPanelListener> listeners =
            new LinkedList<MetadataEditPanelListener>();
    boolean editable = true;
    private JComponent container;
    private EditMetadataActionsPanel editActionsPanel;
    private Component lastFocussedComponent;
    private ListenerProvider listenerProvider;

    public EditMetadataPanelsArray(JComponent container) {
        this.container = container;
        listenerProvider = ListenerProvider.INSTANCE;
        listeners = listenerProvider.getMetadataEditPanelListeners();
        createEditPanels();
        addPanels();
        setFocusToFirstEditField();
        listenToActionSources();
        setEditable(false);
    }

    private boolean isDirty() {
        int size = panels.size();
        for (int i = 0; i < size; i++) {
            if (((TextEntry) panels.get(i)).isDirty()) return true;
        }
        return false;
    }

    private void checkDirty() {
        if (isDirty()) {
            save();
            setFocusToLastFocussedComponent();
            SelectedFile.INSTANCE.setFile(new File(""), null); // NOI18N
        }
    }

    private void save() {
        addInputToRepeatableTextEntries();
        SaveEditedMetadata.saveMetadata(filenamesXmp);
        setDirty(false);
    }

    private void saveIfDirty() {
        if (isDirty()) {
            save();
        }
    }

    private void addInputToRepeatableTextEntries() {
        for (JPanel panel : panels) {
            if (panel instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel editPanel =
                        (EditRepeatableTextEntryPanel) panel;
                String text = editPanel.getText();
                if (!text.isEmpty()) {
                    editPanel.addText(text);
                }
            }
        }
    }

    private synchronized void notifyActionListener(MetadataEditPanelEvent evt) {
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
                ? MetadataEditPanelEvent.Type.EDIT_ENABLED
                : MetadataEditPanelEvent.Type.EDIT_DISABLED));
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

    public synchronized void setFilenames(Collection<String> filenames) {
        emptyPanels(false);
        setXmpOfFiles(filenames);
        setXmpToEditPanels();
        setXmpOfFilesAsTextEntryListener(true);
    }

    private void setXmpOfFiles(Collection<String> filenames) {
        filenamesXmp.clear();
        for (String filename : filenames) {
            Xmp xmp = null;
            if (XmpMetadata.hasImageASidecarFile(filename)) {
                xmp = XmpMetadata.getXmpOfImageFile(filename);
            }
            if (xmp == null) {
                xmp = new Xmp();
            }
            filenamesXmp.add(new Pair<String, Xmp>(filename, xmp));
        }
    }

    private void setXmpOfFilesAsTextEntryListener(boolean add) {
        for (Pair<String, Xmp> pair : filenamesXmp) {
            setXmpAsTextEntryListener(pair.getSecond(), add);
        }
    }

    private void setXmpAsTextEntryListener(Xmp xmp, boolean add) {
        for (JPanel panel : panels) {
            if (panel instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel textPanel =
                        (EditRepeatableTextEntryPanel) panel;
                if (add) {
                    textPanel.addTextEntryListener(xmp);
                } else {
                    textPanel.removeTextEntryListener(xmp);
                }
            } else if (panel instanceof EditTextEntryPanel) {
                EditTextEntryPanel textPanel = (EditTextEntryPanel) panel;
                if (add) {
                    textPanel.addTextEntryListener(xmp);
                } else {
                    textPanel.removeTextEntryListener(xmp);
                }
            } else if (panel instanceof RatingSelectionPanel) {
                RatingSelectionPanel textPanel = (RatingSelectionPanel) panel;
                if (add) {
                    textPanel.addTextEntryListener(xmp);
                } else {
                    textPanel.removeTextEntryListener(xmp);
                }
            }
        }
    }

    /**
     * Returns an edit panel for a specific column.
     *
     * @param  column column
     * @return        panel or null if for that column an edit panel doesn't
     *                exist
     */
    public JPanel getEditPanel(Column column) {
        for (JPanel panel : panels) {
            if (((TextEntry) panel).getColumn().equals(column)) {
                return panel;
            }
        }
        return null;
    }

    public Collection<Pair<String, Xmp>> getFilenamesXmp() {
        return filenamesXmp;
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

    private void setXmpToEditPanels() {
        if (filenamesXmp.size() <= 0) return;
        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            Column xmpColumn = textEntry.getColumn();
            if (textEntry instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel editPanel =
                        (EditRepeatableTextEntryPanel) textEntry;
                editPanel.setText(getCommonXmpCollection(xmpColumn));
            } else if (textEntry instanceof TextEntry) {
                TextEntry editPanel = (TextEntry) textEntry;
                editPanel.setText(getCommonXmpString(xmpColumn));
            }
            textEntry.setDirty(false);
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getCommonXmpCollection(Column column) {
        assert filenamesXmp.size() >= 1 : "No files!"; // NOI18N
        if (filenamesXmp.size() == 1) {
            Object value = filenamesXmp.get(0).getSecond().getValue(column);
            assert value == null || value instanceof List : value;
            if (value instanceof List) {
                return (List) value;
            } else {
                return new ArrayList<String>(1);
            }
        }
        // more then 1 file
        Stack<List<String>> lists = new Stack<List<String>>();
        for (Pair<String, Xmp> pair : filenamesXmp) {
            Xmp xmp = pair.getSecond();
            Object value = xmp.getValue(column);
            assert value == null || value instanceof List : value;
            if (value instanceof List) {
                lists.push((List) value);
            }
        }
        if (lists.size() != filenamesXmp.size()) {
            // 1 ore more files without metadata
            return new ArrayList<String>(1);
        }
        List<String> coll = new ArrayList<String>();
        coll = lists.pop();
        while (!lists.isEmpty() && coll.size() > 0) {
            coll.retainAll(lists.pop());
        }
        return coll;
    }

    private String getCommonXmpString(Column column) {
        assert filenamesXmp.size() >= 1 : "No files!"; // NOI18N
        if (filenamesXmp.size() == 1) {
            Object value = filenamesXmp.get(0).getSecond().getValue(column);
            assert value == null || value instanceof String : value;
            if (value instanceof String) {
                return ((String) value).trim();
            } else {
                return ""; // NOI18N
            }
        }
        // more then 1 file
        Stack<String> strings = new Stack<String>();
        for (Pair<String, Xmp> pair : filenamesXmp) {
            Xmp xmp = pair.getSecond();
            Object value = xmp.getValue(column);
            assert value == null || value instanceof String : value;
            if (value instanceof String) {
                strings.push(((String) value).trim());
            }
        }
        if (strings.size() != filenamesXmp.size()) return ""; // NOI18N
        String string = strings.pop();
        while (!strings.empty()) {
            if (!strings.pop().equalsIgnoreCase(string)) {
                return ""; // NOI18N
            }
        }
        return string;
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
            ExpandCollapseComponentPanel panel =
                    new ExpandCollapseComponentPanel(panels.get(i));
            container.add(panel, constraints);
            panel.readExpandedState();
        }
        addActionPanel();
    }

    private GridBagConstraints newConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.anchor = GridBagConstraints.NORTHWEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(0, 10, 0, 10);
        constraints.weightx = 1;
        return constraints;
    }

    private void listenToActionSources() {
        DatabaseImageFiles.INSTANCE.addDatabaseListener(this);
        AppLifeCycle.INSTANCE.addAppExitListener(this);
        TextSelectionDialog.INSTANCE.getPanel().addTextSelectionListener(this);
    }

    private void addActionPanel() {
        editActionsPanel = GUI.INSTANCE.getAppPanel().
                getMetadataEditActionsPanel();
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
        List<Column> columns = UserSettings.INSTANCE.getEditColumns();

        for (Column column : columns) {
            EditHints editHints = EditColumns.getEditHints(column);
            boolean large = editHints.getSizeEditField().equals(
                    SizeEditField.LARGE);
            boolean isRepeatable = editHints.isRepeatable();

            if (isRepeatable) {
                EditRepeatableTextEntryPanel panel =
                        new EditRepeatableTextEntryPanel(column);
                panel.textFieldInput.addFocusListener(this);
                panel.textFieldInput.addKeyListener(this);
                if (column.equals(ColumnXmpDcSubjectsSubject.INSTANCE)) {
                    panel.setSuggest(new SuggestHierarchicalKeywords());
                }
                panels.add(panel);
            } else {
                if (column.equals(ColumnXmpRating.INSTANCE)) {
                    RatingSelectionPanel panel = new RatingSelectionPanel(column);
                    panels.add(panel);
                } else {
                    EditTextEntryPanel panel = new EditTextEntryPanel(column);
                    panel.textAreaEdit.addFocusListener(this);
                    panel.textAreaEdit.addKeyListener(this);
                    panel.textAreaEdit.setRows(large
                                           ? 2
                                           : 1);
                    panels.add(panel);
                }
            }
        }
    }

    public void setAutocomplete() {
        for (JPanel panel : panels) {
            if (panel instanceof TextEntry) {
                TextEntry textEntry = (TextEntry) panel;
                textEntry.setAutocomplete();
            }
        }
    }

    public void emptyPanels(boolean dirty) {
        checkDirty();
        // The listeners shouldn't notified when emptying text because they
        // would delete their content
        setXmpOfFilesAsTextEntryListener(false);
        for (JPanel panel : panels) {
            ((TextEntry) panel).empty(dirty);
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
        setTextToTextSelectionPanel(source);
    }

    private boolean isEditArea(Component c) {
        return c instanceof TabLeavingTextArea || c instanceof JTextField;
    }

    private void setTextToTextSelectionPanel(Component c) {
        String text = ""; // NOI18N
        if (c instanceof JTextField) {
            text = ((JTextField) c).getText().trim();
        } else if (c instanceof JTextArea) {
            text = ((JTextArea) c).getText().trim();
        }
        if (!text.isEmpty()) {
            TextSelectionDialog.INSTANCE.getPanel().setText(text);
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        // noting to do
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
    public void actionPerformed(DatabaseImageEvent event) {
        if (event.isTextMetadataAffected()) {
            ImageFile imageFile = event.getImageFile();
            setModifiedXmp(imageFile);
            if (imageFile != null && imageFile.getXmp() != null) {
                addAutoCompleteData(imageFile.getXmp());
            }
        }
    }

    /**
     * When the XMP was changed and the data was not edited settin the new
     * XMP data.
     *
     * @param imageFile image file with new XMP data
     */
    private void setModifiedXmp(ImageFile imageFile) {
        if (isDirty()) return;
        if (imageFile != null && imageFile.getXmp() != null) {
            String filename = imageFile.getFilename();
            if (filename == null) return;
            if (filenamesXmp.size() != 1) return;
            Pair<String, Xmp> pair = filenamesXmp.get(0);
            if (pair.getFirst().equals(filename)) {
                Xmp xmp = imageFile.getXmp();
                setXmpAsTextEntryListener(pair.getSecond(), false);
                setXmpAsTextEntryListener(xmp, true);
                filenamesXmp.set(0,
                        new Pair<String, Xmp>(filename, xmp));
                setXmpToEditPanels();
                return;
            }
        }
    }

    private void addAutoCompleteData(Xmp xmp) {
        for (JPanel panel : panels) {
            if (panel instanceof EditTextEntryPanel) {
                EditTextEntryPanel p = (EditTextEntryPanel) panel;
                AutoCompleteUtil.addData(xmp, p.getColumn(), p.
                        getAutoCompleteData());
            } else if (panel instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel p =
                        (EditRepeatableTextEntryPanel) panel;
                AutoCompleteUtil.addData(xmp, p.getColumn(), p.
                        getAutoCompleteData());
            }
        }
    }

    @Override
    public void appWillExit() {
        checkDirty();
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        // ignore
    }

    @Override
    public void textSelected(TextSelectionEvent evt) {
        if (lastFocussedComponent == null) return;
        if (!lastFocussedComponent.isVisible()) return;
        if (lastFocussedComponent instanceof JTextField) {
            ((JTextField) lastFocussedComponent).setText(evt.getText());
        } else if (lastFocussedComponent instanceof JTextArea) {
            ((JTextArea) lastFocussedComponent).setText(evt.getText());
        }
        lastFocussedComponent.requestFocusInWindow();
    }

    @Override
    public void textDeselected(TextSelectionEvent evt) {
        // ignore
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (lastFocussedComponent == null) return;
            if (lastFocussedComponent instanceof JTextField) {
                setTextToTextSelectionPanel(lastFocussedComponent);
            } else if (lastFocussedComponent instanceof JTextArea) {
                setTextToTextSelectionPanel(lastFocussedComponent);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}

