package org.jphototagger.program.view.panels;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.event.AppWillExitEvent;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.event.EditMetadataPanelsEditDisabledEvent;
import org.jphototagger.domain.metadata.event.EditMetadataPanelsEditEnabledEvent;
import org.jphototagger.domain.metadata.xmp.FileXmp;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.domain.text.TextEntry;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppPreferencesKeys;
import org.jphototagger.program.controller.keywords.tree.SuggestKeywords;
import org.jphototagger.program.helper.SaveXmp;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.ViewUtil;
import org.jphototagger.program.view.WaitDisplay;
import org.jphototagger.xmp.EditHints;
import org.jphototagger.xmp.EditHints.SizeEditField;
import org.jphototagger.xmp.EditMetaDataValues;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Panels mit Edit-Feldern zum Bearbeiten von Metadaten.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class EditMetadataPanels implements FocusListener {

    private final List<JPanel> panels = new ArrayList<JPanel>();
    private final List<FileXmp> imageFilesXmp = new ArrayList<FileXmp>();
    private volatile boolean editable = true;
    private WatchDifferentValues watchDifferentValues = new WatchDifferentValues();
    private JComponent container;
    private EditMetadataActionsPanel editActionsPanel;
    private Component lastFocussedEditControl;
    private final Object monitor = new Object();

    public EditMetadataPanels(JComponent container) {
        if (container == null) {
            throw new NullPointerException("container == null");
        }

        this.container = container;
        createEditPanels();
        addPanels();
        setFocusToFirstEditField();
        listenToActionSources();
        setEditable(false);
    }

    private boolean isDirty() {
        int size = panels.size();

        for (int i = 0; i < size; i++) {
            if (((TextEntry) panels.get(i)).isDirty()) {
                return true;
            }
        }

        return false;
    }

    private void saveIfDirty() {
        if (isDirty()) {
            save();
            setFocusToLastFocussedEditControl();
        }
    }

    private void save() {
        addInputToRepeatableTextEntries(); // XMP in imageFilesXmp implicit adding these values
        synchronized (monitor) {
            SaveXmp.save(imageFilesXmp);
        }
        setDirty(false);
    }

    private void addInputToRepeatableTextEntries() {
        for (JPanel panel : panels) {
            if (panel instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel editPanel = (EditRepeatableTextEntryPanel) panel;
                String text = editPanel.getText();

                if (!text.isEmpty()) {
                    editPanel.addText(text);
                }
            }
        }
    }

    public void setEditable(final boolean editable) {
        synchronized (monitor) {
            this.editable = editable;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                for (JPanel panel : panels) {
                    ((TextEntry) panel).setEditable(editable);
                }

                if (editable) {
                    EventBus.publish(new EditMetadataPanelsEditEnabledEvent(this));
                } else {
                    EventBus.publish(new EditMetadataPanelsEditDisabledEvent(this));
                }
            }
        });
    }

    public boolean isEditable() {
        synchronized (monitor) {
            return editable;
        }
    }

    private void showWaitSetImageFiles(int imgCount) {
        if (imgCount > 1) {
            WaitDisplay.show();
        }
    }

    private void hideWaitSetImageFiles(int imgCount) {
        if (imgCount > 1) {
            WaitDisplay.hide();
        }
    }

    public synchronized void setImageFiles(final Collection<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                showWaitSetImageFiles(imageFiles.size());
                emptyPanels(false);
                setXmpOfImageFiles(imageFiles);
                setXmpToEditPanels();
                setXmpOfFilesAsTextEntryListener(true);
                hideWaitSetImageFiles(imageFiles.size());
            }
        });
    }

    private void setXmpOfImageFiles(Collection<File> imageFiles) {
        synchronized (monitor) {
            imageFilesXmp.clear();
        }

        for (File imageFile : imageFiles) {
            Xmp xmp = null;

            if (XmpMetadata.hasImageASidecarFile(imageFile)) {
                try {
                    xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
                } catch (IOException ex) {
                    Logger.getLogger(EditMetadataPanels.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (xmp == null) {
                xmp = new Xmp();
            }

            synchronized (monitor) {
                imageFilesXmp.add(new FileXmp(imageFile, xmp));
            }
        }
    }

    private void setXmpOfFilesAsTextEntryListener(boolean add) {
        List<FileXmp> imageFilesXmpCopy = null;
        synchronized (monitor) {
            imageFilesXmpCopy = Collections.unmodifiableList(imageFilesXmp);
        }
        for (FileXmp imageFileXmp : imageFilesXmpCopy) {
            setXmpAsTextEntryListener(imageFileXmp.getXmp(), add);
        }
    }

    private void setXmpAsTextEntryListener(Xmp xmp, boolean add) {
        for (JPanel panel : panels) {
            if (panel instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel textPanel = (EditRepeatableTextEntryPanel) panel;

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
     * Returns an edit panel for a specific metadata value.
     *
     * @param  value
     * @return        panel or null if for that metadata value an edit panel doesn't
     *                exist
     */
    public JPanel getEditPanel(MetaDataValue value) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        for (JPanel panel : panels) {
            if (((TextEntry) panel).getMetaDataValue().equals(value)) {
                return panel;
            }
        }

        return null;
    }

    /**
     * Adds text to a panel if it's an instance of {@code EditRepeatableTextEntryPanel}.
     *
     * @param value
     * @param text   text to add
     */
    public void addText(final MetaDataValue value, final String text) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        if (text == null) {
            throw new NullPointerException("text == null");
        }

        synchronized (monitor) {
            if (!editable) {
                return;
            }
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                JPanel panelAdd = null;
                int size = panels.size();

                for (int i = 0; (panelAdd == null) && (i < size); i++) {
                    JPanel panel = panels.get(i);

                    if (((TextEntry) panel).getMetaDataValue().equals(value)) {
                        panelAdd = panel;
                    }
                }

                if (panelAdd instanceof EditRepeatableTextEntryPanel) {
                    ((EditRepeatableTextEntryPanel) panelAdd).addText(text);
                } else if (panelAdd instanceof TextEntry) {
                    TextEntry textEntry = (TextEntry) panelAdd;

                    textEntry.setText(text);
                    textEntry.setDirty(true);
                }

                saveIfDirtyAndInputIsSaveEarly();
            }
        });
    }

    public void removeText(final MetaDataValue value, final String text) {
        if (value == null) {
            throw new NullPointerException("value == null");
        }

        if (text == null) {
            throw new NullPointerException("text == null");
        }

        synchronized (monitor) {
            if (!editable) {
                return;
            }
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {

                JPanel panelRemove = null;
                int size = panels.size();

                for (int i = 0; (panelRemove == null) && (i < size); i++) {
                    JPanel panel = panels.get(i);

                    if (((TextEntry) panel).getMetaDataValue().equals(value)) {
                        panelRemove = panel;
                    }
                }

                if (panelRemove instanceof EditRepeatableTextEntryPanel) {
                    ((EditRepeatableTextEntryPanel) panelRemove).removeText(text);
                } else if (panelRemove instanceof TextEntry) {
                    TextEntry textEntry = (TextEntry) panelRemove;

                    textEntry.setText("");
                    textEntry.setDirty(true);
                }

                saveIfDirtyAndInputIsSaveEarly();
            }
        });
    }

    /**
     * Returns the current entries as a XMP object.
     *
     * @return XMP object
     */
    public Xmp getXmp() {
        Xmp xmp = new Xmp();

        for (JPanel panel : panels) {
            if (panel instanceof EditTextEntryPanel) {
                EditTextEntryPanel p = (EditTextEntryPanel) panel;

                xmp.setValue(p.getMetaDataValue(), p.getText());
            } else if (panel instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel p = (EditRepeatableTextEntryPanel) panel;
                MetaDataValue value = p.getMetaDataValue();

                xmp.setValue(value, p.getText());

                for (String text : p.getRepeatableText()) {
                    xmp.setValue(value, text);
                }
            } else if (panel instanceof RatingSelectionPanel) {
                RatingSelectionPanel p = (RatingSelectionPanel) panel;

                try {

                    // Only one call possible, so try catch within a loop is ok
                    String s = p.getText();

                    if ((s != null) && !s.isEmpty()) {
                        xmp.setValue(XmpRatingMetaDataValue.INSTANCE, Long.getLong(s));
                    }
                } catch (Exception ex) {
                    Logger.getLogger(EditMetadataPanels.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return xmp;
    }

    /**
     * Sets a XMP object to the edit panels.
     *
     * Adds repeating values and replaces not repeating values.
     *
     * @param xmp xmp object
     */
    public void setXmp(final Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        if (!editable) {
            return;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {

                for (JPanel panel : panels) {
                    if (panel instanceof EditTextEntryPanel) {
                        EditTextEntryPanel p = (EditTextEntryPanel) panel;
                        Object value = xmp.getValue(p.getMetaDataValue());

                        if (value != null) {
                            p.setText(value.toString());
                            p.setDirty(true);
                        }
                    } else if (panel instanceof EditRepeatableTextEntryPanel) {
                        EditRepeatableTextEntryPanel p = (EditRepeatableTextEntryPanel) panel;
                        MetaDataValue mdValue = p.getMetaDataValue();
                        Object value = xmp.getValue(mdValue);

                        if (value instanceof Collection<?>) {
                            Collection<?> collection = (Collection<?>) value;

                            for (Object o : collection) {
                                // addText() would set the dirty flag
                                p.addText(o.toString());
                            }
                        }
                    } else if (panel instanceof RatingSelectionPanel) {
                        RatingSelectionPanel p = (RatingSelectionPanel) panel;
                        Long rating = xmp.contains(XmpRatingMetaDataValue.INSTANCE)
                                ? (Long) xmp.getValue(XmpRatingMetaDataValue.INSTANCE)
                                : null;

                        if (rating != null) {
                            p.setText(Long.toString(rating));
                            p.setDirty(true);
                        }
                    }
                }

                saveIfDirtyAndInputIsSaveEarly();
            }
        });
    }

    /**
     * Sets the rating if the rating panel is present.
     *
     * @param rating
     */
    public void setRating(final Long rating) {
        if (rating == null) {
            throw new NullPointerException("rating == null");
        }

        if (!editable) {
            return;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                JPanel panelToSet = null;
                int size = panels.size();

                for (int i = 0; (panelToSet == null) && (i < size); i++) {
                    JPanel panel = panels.get(i);

                    if (((TextEntry) panel).getMetaDataValue().equals(XmpRatingMetaDataValue.INSTANCE)) {
                        panelToSet = panel;
                    }
                }

                if (panelToSet instanceof RatingSelectionPanel) {
                    RatingSelectionPanel ratingPanel = (RatingSelectionPanel) panelToSet;

                    ratingPanel.setTextAndNotify(Long.toString(rating));
                }

                saveIfDirtyAndInputIsSaveEarly();
            }
        });
    }

    public void setMetadataTemplate(final MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        synchronized (monitor) {
            if (!editable) {
                return;
            }
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                for (JPanel panel : panels) {
                    TextEntry textEntry = (TextEntry) panel;
                    Object value = template.getMetaDataValue(textEntry.getMetaDataValue());

                    if (value instanceof String) {
                        String string = (String) value;

                        if (!string.isEmpty()) {
                            textEntry.setText(string);
                            textEntry.setDirty(true);
                        }
                    } else if (value instanceof Collection<?>) {
                        @SuppressWarnings("unchecked") Collection<String> strings = (Collection<String>) value;
                        EditRepeatableTextEntryPanel repeatableTextEntry = (EditRepeatableTextEntryPanel) textEntry;

                        repeatableTextEntry.setText(strings);
                        repeatableTextEntry.setDirty(true);
                    }
                }
            }
        });
    }

    /**
     *
     * @return Template with current data
     */
    public MetadataTemplate getMetadataTemplate() {
        MetadataTemplate template = new MetadataTemplate();

        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;

            if (textEntry instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel repeatableEntry = (EditRepeatableTextEntryPanel) textEntry;

                template.setMetaDataValue(textEntry.getMetaDataValue(), repeatableEntry.getRepeatableText());
            } else {
                String value = textEntry.getText();

                if ((value != null) && !value.trim().isEmpty()) {
                    template.setMetaDataValue(textEntry.getMetaDataValue(), value.trim());
                }
            }
        }

        return template;
    }

    public void setDirty(final boolean dirty) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                for (JPanel panel : panels) {
                    ((TextEntry) panel).setDirty(dirty);
                }
            }
        });
    }

    private void setXmpToEditPanels() {
        watchDifferentValues.setListen(false);
        watchDifferentValues.setEntries(new ArrayList<TextEntry>());

        if (isEmpty()) {
            return;
        }

        List<TextEntry> watchEntries = new ArrayList<TextEntry>();

        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            MetaDataValue xmpValue = textEntry.getMetaDataValue();

            if (textEntry instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel editPanel = (EditRepeatableTextEntryPanel) textEntry;

                editPanel.setText(getCommonXmpCollection(xmpValue));
            } else {
                String commonText = getCommonXmpString(xmpValue);

                textEntry.setText(commonText);

                if (containsDataOfMultipleFiles() && commonText.isEmpty() && hasValue(xmpValue)) {
                    watchEntries.add(textEntry);
                }
            }

            textEntry.setDirty(false);
        }

        if (containsDataOfMultipleFiles() && (watchEntries.size() > 0)) {
            watchDifferentValues.setEntries(watchEntries);
            watchDifferentValues.setListen(true);
        }
    }

    private boolean isEmpty() {
        synchronized (monitor) {
            return imageFilesXmp.isEmpty();
        }
    }

    private boolean containsDataOfMultipleFiles() {
        synchronized (monitor) {
            return imageFilesXmp.size() > 1;
        }
    }

    private boolean containsDataOfExactlyOneFile() {
        synchronized (monitor) {
            return imageFilesXmp.size() == 1;
        }
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getCommonXmpCollection(MetaDataValue mdValue) {
        List<FileXmp> imageFilesXmpCopy = null;
        synchronized (monitor) {
            imageFilesXmpCopy = Collections.unmodifiableList(imageFilesXmp);
        }

        if (imageFilesXmpCopy.size() == 1) {
            FileXmp currentFileXmp = imageFilesXmpCopy.get(0);
            Xmp currentXmp = currentFileXmp.getXmp();
            Object currentValue = currentXmp.getValue(mdValue);

            if (currentValue instanceof List<?>) {
                return (List<String>) currentValue;
            } else {
                return new ArrayList<String>(1);
            }
        }

        // more then 1 file
        Stack<List<String>> lists = new Stack<List<String>>();

        for (FileXmp imageFileXmp : imageFilesXmpCopy) {
            Xmp xmp = imageFileXmp.getXmp();
            Object value = xmp.getValue(mdValue);

            if (value instanceof List<?>) {
                lists.push((List<String>) value);
            }
        }

        if (lists.size() != imageFilesXmpCopy.size()) {

            // 1 ore more files without metadata
            return new ArrayList<String>(1);
        }

        List<String> coll = lists.pop();

        while (!lists.isEmpty() && (coll.size() > 0)) {
            coll.retainAll(lists.pop());
        }

        return coll;
    }

    private String getCommonXmpString(MetaDataValue mdValue) {
        List<FileXmp> imageFilesXmpCopy = null;
        synchronized (monitor) {
            imageFilesXmpCopy = Collections.unmodifiableList(imageFilesXmp);
        }

        if (imageFilesXmpCopy.size() == 1) {
            FileXmp currentFilesXmp = imageFilesXmpCopy.get(0);
            Xmp currentXmp = currentFilesXmp.getXmp();
            Object currentValue = currentXmp.getValue(mdValue);
            String value = toString(currentValue);

            return (value == null)
                    ? ""
                    : value.trim();
        }

        // more then 1 file
        Stack<String> strings = new Stack<String>();

        for (FileXmp imageFileXmp : imageFilesXmpCopy) {
            Xmp xmp = imageFileXmp.getXmp();
            String value = toString(xmp.getValue(mdValue));

            if (value != null) {
                strings.push(value.trim());
            }
        }

        if (strings.size() != imageFilesXmpCopy.size()) {
            return "";
        }

        String string = strings.pop();

        while (!strings.empty()) {
            if (!strings.pop().equalsIgnoreCase(string)) {
                return "";
            }
        }

        return string;
    }

    private boolean hasValue(MetaDataValue mdValue) {
        List<FileXmp> imageFilesXmpCopy = null;
        synchronized (monitor) {
            imageFilesXmpCopy = Collections.unmodifiableList(imageFilesXmp);
        }

        for (FileXmp imageFileXmp : imageFilesXmpCopy) {
            Xmp xmp = imageFileXmp.getXmp();
            String value = toString(xmp.getValue(mdValue));

            if ((value != null) && !value.trim().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private String toString(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Long) {
            return Long.toOctalString((Long) value);
        } else {
            Logger.getLogger(EditMetadataPanels.class.getName()).log(Level.WARNING, "No string conversion implemented for ''{0}''", value);
            assert false : "No string conversion implemented for " + value;
        }

        return null;
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

            ExpandCollapseComponentPanel panel = new ExpandCollapseComponentPanel(panels.get(i));

            container.add(panel, constraints);
            panel.readExpandedState();
        }

        setMnemonics();
        addActionPanel();    // After setMnemonics()!
    }

    private void setMnemonics() {
        EditMetadataActionsPanel actionsPanel = GUI.getAppPanel().getPanelEditMetadataActions();
        List<Character> mnemonics = new ArrayList<Character>(10);

        /*
         * UPDATE IF other components of the application panel containing
         * buttons with mnemonics and can be visible and enabled when the edit
         * panel is displayed. Else Alt+Mnemonic triggers their button actions
         * even if the components with the buttons are not focussed.
         */
        mnemonics.add((char) actionsPanel.buttonEmptyMetadata.getMnemonic());
        mnemonics.add((char) actionsPanel.buttonMetadataTemplateCreate.getMnemonic());
        mnemonics.add((char) actionsPanel.buttonMetadataTemplateDelete.getMnemonic());
        mnemonics.add((char) actionsPanel.buttonMetadataTemplateEdit.getMnemonic());
        mnemonics.add((char) actionsPanel.buttonMetadataTemplateInsert.getMnemonic());
        mnemonics.add((char) actionsPanel.buttonMetadataTemplateRename.getMnemonic());
        mnemonics.add((char) actionsPanel.buttonMetadataTemplateUpdate.getMnemonic());
        mnemonics.add((char) actionsPanel.buttonMetadataTemplateAdd.getMnemonic());
        mnemonics.add((char) actionsPanel.labelPromptCurrentTemplate.getDisplayedMnemonic());
        mnemonics.addAll(MnemonicUtil.getMnemonicCharsOf(Arrays.asList(GUI.getAppPanel().getMnemonizedComponents())));
        ViewUtil.setDisplayedMnemonicsToLabels(container, mnemonics.toArray(new Character[]{}));
    }

    private GridBagConstraints newConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.weightx = 1;

        return gbc;
    }

    private void listenToActionSources() {
        AnnotationProcessor.process(this);
    }

    private void addActionPanel() {
        editActionsPanel = GUI.getAppPanel().getPanelEditMetadataActions();

        GridBagConstraints gbc = newConstraints();

        gbc.weighty = 1;
        container.add(editActionsPanel, gbc);
        editActionsPanel.tabbedPane.addFocusListener(this);
    }

    public void setFocusToFirstEditField() {
        if (panels.size() > 0) {
            TextEntry textEntry = (TextEntry) panels.get(0);

            textEntry.focus();
            lastFocussedEditControl = panels.get(0);
        }
    }

    public void setFocusToLastFocussedEditControl() {
        if (lastFocussedEditControl != null) {
            lastFocussedEditControl.requestFocus();
        } else {
            setFocusToFirstEditField();
        }
    }

    private void createEditPanels() {
        if (!panels.isEmpty()) {
            throw new IllegalStateException("Panels already created");
        }

        List<MetaDataValue> mdValues = EditMetaDataValues.get();

        for (MetaDataValue mdValue : mdValues) {
            EditHints editHints = EditMetaDataValues.getEditHints(mdValue);
            boolean large = editHints.getSizeEditField().equals(SizeEditField.LARGE);
            boolean isRepeatable = editHints.isRepeatable();

            if (isRepeatable) {
                EditRepeatableTextEntryPanel panel = new EditRepeatableTextEntryPanel(mdValue);

                panel.textAreaInput.addFocusListener(this);

                if (mdValue.equals(XmpDcSubjectsSubjectMetaDataValue.INSTANCE)) {
                    panel.setSuggest(new SuggestKeywords());
                    panel.setBundleKeyPosRenameDialog("EditMetadataPanels.Keywords.RenameDialog.Pos");
                }

                panels.add(panel);
            } else {
                if (mdValue.equals(XmpRatingMetaDataValue.INSTANCE)) {
                    RatingSelectionPanel panel = new RatingSelectionPanel(mdValue);

                    for (Component c : panel.getInputComponents()) {
                        c.addFocusListener(this);
                    }

                    panels.add(panel);
                } else {
                    EditTextEntryPanel panel = new EditTextEntryPanel(mdValue);

                    panel.textAreaEdit.addFocusListener(this);
                    panel.textAreaEdit.setRows(large
                            ? 4
                            : 1);
                    panels.add(panel);
                }
            }
        }
    }

    public void setAutocomplete() {
        if (isAutocomplete()) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    for (JPanel panel : panels) {
                        if (panel instanceof TextEntry) {
                            TextEntry textEntry = (TextEntry) panel;

                            textEntry.setAutocomplete();
                        }
                    }
                }
            });
        }
    }

    private boolean isAutocomplete() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? storage.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                : true;
    }

    public void emptyPanels(final boolean dirty) {
        saveIfDirty();

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {

                // The listeners shouldn't notified when emptying text because they
                // would delete their content
                setXmpOfFilesAsTextEntryListener(false);

                for (JPanel panel : panels) {
                    ((TextEntry) panel).empty(dirty);
                }
            }
        });
    }

    @Override
    public void focusGained(FocusEvent evt) {
        Component source = (Component) evt.getSource();

        if (isEditControl(source)) {
            lastFocussedEditControl = source;
        }

        scrollToVisible(evt.getSource());
    }

    @Override
    public void focusLost(FocusEvent evt) {
        if (isEditComponent(evt.getOppositeComponent())) {
            saveIfDirtyAndInputIsSaveEarly();
        }
    }

    private boolean isEditComponent(Component c) {
        if (c == null) {
            return false;
        }

        return (c instanceof JTextArea) || (c.getParent() instanceof RatingSelectionPanel);
    }

    private boolean isEditControl(Component c) {
        return (c instanceof JTextArea) || (c instanceof JTextField) || (c instanceof RatingSelectionPanel);
    }

    private void scrollToVisible(Object inputSource) {
        Component c = getParentNextToContainer(inputSource);

        if (c != null) {
            container.scrollRectToVisible(c.getBounds());
        }
    }

    private Component getParentNextToContainer(Object o) {
        if (o instanceof Component) {
            Component c = (Component) o;

            while (c != null) {
                if (c.getParent() == container) {
                    return c;
                }

                c = c.getParent();
            }
        }

        return null;
    }

    /**
     * When the XMP was changed and the data was not edited setting the new
     * XMP data.
     *
     * @param imageFile image file with new XMP data
     */
    private void setModifiedXmp(final File imageFile, final Xmp xmp) {
        if (!editable || isDirty() || !containsDataOfExactlyOneFile()) {
            return;
        }

        FileXmp imageFileXmp = null;
        synchronized (monitor) {
            imageFileXmp = imageFilesXmp.get(0);
        }

        final Xmp imageFileXmpsXmp = imageFileXmp.getXmp();

        if (imageFileXmp.getFile().equals(imageFile)) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    setXmpAsTextEntryListener(imageFileXmpsXmp, false);
                    setXmpAsTextEntryListener(xmp, true);
                    imageFilesXmp.set(0, new FileXmp(imageFile, xmp));
                    setXmpToEditPanels();
                }
            });
        }
    }

    @EventSubscriber(eventClass = AppWillExitEvent.class)
    public void appWillExit(AppWillExitEvent evt) {
        saveIfDirty();
    }

    public void saveIfDirtyAndInputIsSaveEarly() {
        if (!editable || !isSaveInputEarly() || !isDirty()) {
            return;
        }

        save();
    }

    private boolean isSaveInputEarly() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppPreferencesKeys.KEY_SAVE_INPUT_EARLY)
                ? storage.getBoolean(AppPreferencesKeys.KEY_SAVE_INPUT_EARLY)
                : true;
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        setModifiedXmp(evt.getImageFile(), evt.getXmp());
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        setModifiedXmp(evt.getImageFile(), evt.getUpdatedXmp());
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        setModifiedXmp(evt.getImageFile(), evt.getXmp());
    }

    private class WatchDifferentValues extends MouseAdapter {

        private final List<TextEntry> entries = new ArrayList<TextEntry>();
        private final Set<TextEntry> releasedEntries = new HashSet<TextEntry>();
        private volatile boolean listen;

        public synchronized void setListen(boolean listen) {
            if (listen) {
                listenToEntries();
            }

            this.listen = listen;
        }

        private void listenToEntries() {
            for (TextEntry entry : entries) {
                if (entry instanceof RatingSelectionPanel) {
                    // Text not parsable as number leads to an exception
                } else {
                    entry.setText(Bundle.getString(EditMetadataPanels.class, "EditMetadataPanels.DisableIfMultipleValues.Info.TextEntry"));
                }

                entry.addMouseListenerToInputComponents(this);
                entry.setDirty(false);
                entry.setEditable(false);
            }
        }

        private void releaseAllEntries() {
            for (TextEntry entry : entries) {
                if (!releasedEntries.contains(entry)) {
                    releaseEntry(entry);
                }
            }
        }

        private void releaseEntry(TextEntry entry) {
            entry.removeMouseListenerFromInputComponents(this);
            entry.setEditable(true);
            entry.setText("");
            entry.setDirty(false);
            releasedEntries.add(entry);
        }

        public synchronized void setEntries(Collection<TextEntry> entries) {
            if (entries == null) {
                throw new NullPointerException("entries == null");
            }

            releaseAllEntries();
            this.releasedEntries.clear();
            this.entries.clear();
            this.entries.addAll(entries);
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            synchronized (this) {
                if (!editable || !listen) {
                    return;
                }

                TextEntry entry = getTextEntry(evt.getSource());

                if (enableEdit(entry) && (entry instanceof RatingSelectionPanel)) {
                    ((RatingSelectionPanel) entry).repeatLastClick();
                }
            }
        }

        private TextEntry getTextEntry(Object o) {
            Object obj = o;

            if (obj instanceof TextEntry) {
                return (TextEntry) obj;
            }

            while (obj != null) {
                if (obj instanceof Component) {
                    obj = ((Component) obj).getParent();

                    if (obj instanceof TextEntry) {
                        return (TextEntry) obj;
                    }
                } else {
                    return null;
                }
            }

            return null;
        }

        public boolean enableEdit(TextEntry entry) {
            if (entry == null) {
                throw new NullPointerException("entry == null");
            }

            String message = Bundle.getString(WatchDifferentValues.class, "EditMetadataPanels.DisableIfMultipleValues.Confirm.Edit");

            if (MessageDisplayer.confirmYesNo(null, message)) {
                releaseEntry(entry);

                return true;
            }

            return false;
        }
    }
}
