/*
 * JPhotoTagger tags and finds images fast.
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

import de.elmar_baumann.jpt.app.AppLifeCycle;
import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.controller.keywords.tree.SuggestKeywords;
import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.data.SelectedFile;
import de.elmar_baumann.jpt.data.TextEntry;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.selections.EditColumns;
import de.elmar_baumann.jpt.database.metadata.selections.EditHints;
import de.elmar_baumann.jpt.database.metadata.selections.EditHints
    .SizeEditField;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpRating;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.EditMetadataPanelsEvent;
import de.elmar_baumann.jpt.event.listener.AppExitListener;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.event.listener.EditMetadataPanelsListener;
import de.elmar_baumann.jpt.event.listener.impl
    .EditMetadataPanelsListenerSupport;
import de.elmar_baumann.jpt.helper.SaveXmp;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.ViewUtil;
import de.elmar_baumann.lib.generics.Pair;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Panels mit Edit-Feldern zum Bearbeiten von Metadaten.
 *
 * @author  Elmar Baumann, Tobias Stening
 * @version 2008-10-05
 */
public final class EditMetadataPanels
        implements FocusListener, DatabaseImageFilesListener, AppExitListener {
    private final List<JPanel>            panels       =
        new ArrayList<JPanel>();
    private final List<Pair<String, Xmp>> filenamesXmp =
        new ArrayList<Pair<String, Xmp>>();
    private boolean              editable             = true;
    private WatchDifferentValues watchDifferentValues =
        new WatchDifferentValues();
    private final EditMetadataPanelsListenerSupport listenerSupport =
        new EditMetadataPanelsListenerSupport();
    private JComponent               container;
    private EditMetadataActionsPanel editActionsPanel;
    private Component                lastFocussedEditControl;

    public EditMetadataPanels(JComponent container) {
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

    private void checkDirty() {
        if (isDirty()) {
            save();
            setFocusToLastFocussedEditControl();
            SelectedFile.INSTANCE.setFile(new File(""), null);
        }
    }

    private void save() {
        addInputToRepeatableTextEntries();
        SaveXmp.save(filenamesXmp);
        setDirty(false);
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

        listenerSupport.notifyListeners(new EditMetadataPanelsEvent(this,
                editable
                ? EditMetadataPanelsEvent.Type.EDIT_ENABLED
                : EditMetadataPanelsEvent.Type.EDIT_DISABLED));
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
                xmp = XmpMetadata.getXmpFromSidecarFileOf(filename);
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

    /**
     * Adds text to a panel if it's an instance of
     * {@link EditRepeatableTextEntryPanel} and if {@link #isEditable()} is
     * true.
     *
     * @param column column
     * @param text   text to add
     */
    public void addText(Column column, String text) {
        assert isEditable();

        if (!isEditable()) {
            return;
        }

        JPanel panelAdd = null;
        int    size     = panels.size();

        for (int i = 0; (panelAdd == null) && (i < size); i++) {
            JPanel panel = panels.get(i);

            if (((TextEntry) panel).getColumn().equals(column)) {
                panelAdd = panel;
            }
        }

        if (panelAdd instanceof EditRepeatableTextEntryPanel) {
            ((EditRepeatableTextEntryPanel) panelAdd).addText(text);
        }

        checkSaveOnChanges();
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

                xmp.setValue(p.getColumn(), p.getText());
            } else if (panel instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel p =
                    (EditRepeatableTextEntryPanel) panel;
                Column column = p.getColumn();

                xmp.setValue(column, p.getText());

                for (String text : p.getRepeatableText()) {
                    xmp.setValue(column, text);
                }
            } else if (panel instanceof RatingSelectionPanel) {
                RatingSelectionPanel p = (RatingSelectionPanel) panel;

                try {

                    // Only one call possible, so try catch within a loop is ok
                    String s = p.getText();

                    if ((s != null) &&!s.isEmpty()) {
                        xmp.setValue(ColumnXmpRating.INSTANCE, Long.getLong(s));
                    }
                } catch (Exception ex) {
                    AppLogger.logSevere(getClass(), ex);
                }
            } else {
                assert false : "Unknown panel type: " + panel;
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
    public void setXmp(Xmp xmp) {
        assert isEditable();

        if (!isEditable()) {
            return;
        }

        for (JPanel panel : panels) {
            if (panel instanceof EditTextEntryPanel) {
                EditTextEntryPanel p     = (EditTextEntryPanel) panel;
                Object             value = xmp.getValue(p.getColumn());

                if (value != null) {
                    p.setText(value.toString());
                    p.setDirty(true);
                }
            } else if (panel instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel p =
                    (EditRepeatableTextEntryPanel) panel;
                Column column = p.getColumn();
                Object value  = xmp.getValue(column);

                if (value instanceof Collection<?>) {
                    Collection<?> collection = (Collection<?>) value;

                    for (Object o : collection) {
                        assert o != null;
                        p.addText(o.toString());    // addText() sets the dirty flag
                    }
                }
            } else if (panel instanceof RatingSelectionPanel) {
                RatingSelectionPanel p      = (RatingSelectionPanel) panel;
                Long                 rating =
                    xmp.contains(ColumnXmpRating.INSTANCE)
                    ? (Long) xmp.getValue(ColumnXmpRating.INSTANCE)
                    : null;

                if (rating != null) {
                    p.setText(Long.toString(rating));
                    p.setDirty(true);
                }
            } else {
                assert false : "Unknown panel type: " + panel;
            }
        }

        checkSaveOnChanges();
    }

    /**
     * Sets the rating if the rating panel is present.
     *
     * @param rating rating
     */
    public void setRating(Long rating) {
        assert isEditable();

        if (!isEditable()) {
            return;
        }

        JPanel panelToSet = null;
        int    size       = panels.size();

        for (int i = 0; (panelToSet == null) && (i < size); i++) {
            JPanel panel = panels.get(i);

            if (((TextEntry) panel).getColumn().equals(
                    ColumnXmpRating.INSTANCE)) {
                panelToSet = panel;
            }
        }

        if (panelToSet instanceof RatingSelectionPanel) {
            RatingSelectionPanel ratingPanel =
                (RatingSelectionPanel) panelToSet;

            ratingPanel.setTextAndNotify(Long.toString(rating));
        }

        checkSaveOnChanges();
    }

    public Collection<Pair<String, Xmp>> getFilenamesXmp() {
        return filenamesXmp;
    }

    /**
     * Setzt ein Metadaten-Edit-Template.
     *
     * @param template  Template
     */
    public void setMetadataTemplate(MetadataTemplate template) {
        if (!isEditable()) {
            return;
        }

        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            Object    value     =
                template.getValueOfColumn(textEntry.getColumn());

            if (value instanceof String) {
                String string = (String) value;

                if (!string.isEmpty()) {
                    textEntry.setText(string);
                    textEntry.setDirty(true);
                }
            } else if (value instanceof Collection<?>) {
                @SuppressWarnings("unchecked") Collection<String> strings =
                    (Collection<String>) value;

                ((EditRepeatableTextEntryPanel) textEntry).setText(strings);
            }
        }

        checkSaveOnChanges();
    }

    /**
     * Liefert ein Metadaten-Edit-Template mit den Daten der Panels.
     *
     * @return Template <em>ohne</em> Name
     *         ({@link de.elmar_baumann.jpt.data.MetadataTemplate#getName()})
     */
    public MetadataTemplate getMetadataTemplate() {
        MetadataTemplate template = new MetadataTemplate();

        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;

            if (textEntry instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel repeatableEntry =
                    (EditRepeatableTextEntryPanel) textEntry;

                template.setValueOfColumn(textEntry.getColumn(),
                                          repeatableEntry.getRepeatableText());
            } else {
                String value = textEntry.getText();

                if ((value != null) &&!value.trim().isEmpty()) {
                    template.setValueOfColumn(textEntry.getColumn(),
                                              value.trim());
                }
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
        watchDifferentValues.setListen(false);
        watchDifferentValues.setEntries(new ArrayList<TextEntry>());

        if (filenamesXmp.size() <= 0) {
            return;
        }

        List<TextEntry> watchEntries = new ArrayList<TextEntry>();

        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            Column    xmpColumn = textEntry.getColumn();

            if (textEntry instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel editPanel =
                    (EditRepeatableTextEntryPanel) textEntry;

                editPanel.setText(getCommonXmpCollection(xmpColumn));
            } else {
                String commonText = getCommonXmpString(xmpColumn);

                textEntry.setText(commonText);

                if (multipleFiles() && commonText.isEmpty()
                        && hasValue(xmpColumn)) {
                    watchEntries.add(textEntry);
                }
            }

            textEntry.setDirty(false);
        }

        if (multipleFiles() && (watchEntries.size() > 0)) {
            watchDifferentValues.setEntries(watchEntries);
            watchDifferentValues.setListen(true);
        }
    }

    private boolean multipleFiles() {
        return filenamesXmp.size() > 1;
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getCommonXmpCollection(Column column) {
        assert filenamesXmp.size() >= 1 : "No files!";

        if (filenamesXmp.size() == 1) {
            Object value = filenamesXmp.get(0).getSecond().getValue(column);

            if (value instanceof List<?>) {
                return (List<String>) value;
            } else {
                return new ArrayList<String>(1);
            }
        }

        // more then 1 file
        Stack<List<String>> lists = new Stack<List<String>>();

        for (Pair<String, Xmp> pair : filenamesXmp) {
            Xmp    xmp   = pair.getSecond();
            Object value = xmp.getValue(column);

            if (value instanceof List<?>) {
                lists.push((List<String>) value);
            }
        }

        if (lists.size() != filenamesXmp.size()) {

            // 1 ore more files without metadata
            return new ArrayList<String>(1);
        }

        List<String> coll = lists.pop();

        while (!lists.isEmpty() && (coll.size() > 0)) {
            coll.retainAll(lists.pop());
        }

        return coll;
    }

    private String getCommonXmpString(Column column) {
        assert filenamesXmp.size() >= 1 : "No files!";

        if (filenamesXmp.size() == 1) {
            String value =
                toString(filenamesXmp.get(0).getSecond().getValue(column));

            return (value == null)
                   ? ""
                   : value.trim();
        }

        // more then 1 file
        Stack<String> strings = new Stack<String>();

        for (Pair<String, Xmp> pair : filenamesXmp) {
            Xmp    xmp   = pair.getSecond();
            String value = toString(xmp.getValue(column));

            if (value != null) {
                strings.push(value.trim());
            }
        }

        if (strings.size() != filenamesXmp.size()) {
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

    private boolean hasValue(Column column) {
        for (Pair<String, Xmp> pair : filenamesXmp) {
            Xmp    xmp   = pair.getSecond();
            String value = toString(xmp.getValue(column));

            if ((value != null) &&!value.trim().isEmpty()) {
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

            ExpandCollapseComponentPanel panel =
                new ExpandCollapseComponentPanel(panels.get(i));

            container.add(panel, constraints);
            panel.readExpandedState();
        }

        setMnemonics();
        addActionPanel();    // After setMnemonics()!
    }

    private void setMnemonics() {
        EditMetadataActionsPanel actionsPanel =
            GUI.INSTANCE.getAppPanel().getMetadataEditActionsPanel();

        // TODO permanent: Exlude to edit panel possible visible app panel buttons.
        // Else Alt+Mnemonic triggers that action even if the component with the
        // button is not focussed.
        ViewUtil
            .setDisplayedMnemonicsToLabels(container, (char) actionsPanel
                .buttonEmptyMetadata.getMnemonic(), (char) actionsPanel
                .buttonMetadataTemplateCreate.getMnemonic(), (char) actionsPanel
                .buttonMetadataTemplateDelete.getMnemonic(), (char) actionsPanel
                .buttonMetadataTemplateEdit.getMnemonic(), (char) actionsPanel
                .buttonMetadataTemplateInsert.getMnemonic(), (char) actionsPanel
                .buttonMetadataTemplateRename.getMnemonic(), (char) actionsPanel
                .buttonMetadataTemplateUpdate.getMnemonic(), (char) actionsPanel
                .buttonMetadataTemplateAdd.getMnemonic(), (char) actionsPanel
                .labelPromptCurrentTemplate.getDisplayedMnemonic());
    }

    private GridBagConstraints newConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor    = GridBagConstraints.NORTHWEST;
        gbc.fill      = GridBagConstraints.BOTH;
        gbc.insets    = new Insets(0, 10, 0, 10);
        gbc.weightx   = 1;

        return gbc;
    }

    private void listenToActionSources() {
        DatabaseImageFiles.INSTANCE.addListener(this);
        AppLifeCycle.INSTANCE.addAppExitListener(this);
    }

    private void addActionPanel() {
        editActionsPanel =
            GUI.INSTANCE.getAppPanel().getMetadataEditActionsPanel();

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
        List<Column> columns = EditColumns.get();

        for (Column column : columns) {
            EditHints editHints = EditColumns.getEditHints(column);
            boolean   large     =
                editHints.getSizeEditField().equals(SizeEditField.LARGE);
            boolean isRepeatable = editHints.isRepeatable();

            if (isRepeatable) {
                EditRepeatableTextEntryPanel panel =
                    new EditRepeatableTextEntryPanel(column);

                panel.textAreaInput.addFocusListener(this);

                if (column.equals(ColumnXmpDcSubjectsSubject.INSTANCE)) {
                    panel.setSuggest(new SuggestKeywords());
                    panel.setBundleKeyPosRenameDialog(
                        "EditMetadataPanels.Keywords.RenameDialog.Pos");
                }

                panels.add(panel);
            } else {
                if (column.equals(ColumnXmpRating.INSTANCE)) {
                    RatingSelectionPanel panel =
                        new RatingSelectionPanel(column);

                    for (Component c : panel.getInputComponents()) {
                        c.addFocusListener(this);
                    }

                    panels.add(panel);
                } else {
                    EditTextEntryPanel panel = new EditTextEntryPanel(column);

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
        if (UserSettings.INSTANCE.isAutocomplete()) {
            for (JPanel panel : panels) {
                if (panel instanceof TextEntry) {
                    TextEntry textEntry = (TextEntry) panel;

                    textEntry.setAutocomplete();
                }
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

        if (isEditControl(source)) {
            lastFocussedEditControl = source;
        }

        scrollToVisible(e.getSource());
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (isEditComponent(e.getOppositeComponent())) {
            checkSaveOnChanges();
        }
    }

    private boolean isEditComponent(Component c) {
        if (c == null) {
            return false;
        }

        return (c instanceof JTextArea)
               || (c.getParent() instanceof RatingSelectionPanel);
    }

    private boolean isEditControl(Component c) {
        return (c instanceof JTextArea) || (c instanceof JTextField)
               || (c instanceof RatingSelectionPanel)
        ;
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

    @Override
    public void actionPerformed(DatabaseImageFilesEvent event) {
        if (event.isTextMetadataAffected()) {
            ImageFile imageFile = event.getImageFile();

            setModifiedXmp(imageFile);
        }
    }

    /**
     * When the XMP was changed and the data was not edited setting the new
     * XMP data.
     *
     * @param imageFile image file with new XMP data
     */
    private void setModifiedXmp(ImageFile imageFile) {
        if (isDirty()) {
            return;
        }

        if ((imageFile != null) && (imageFile.getXmp() != null)) {
            String filename = imageFile.getFilename();

            if (filename == null) {
                return;
            }

            if (filenamesXmp.size() != 1) {
                return;
            }

            Pair<String, Xmp> pair = filenamesXmp.get(0);

            if (pair.getFirst().equals(filename)) {
                Xmp xmp = imageFile.getXmp();

                setXmpAsTextEntryListener(pair.getSecond(), false);
                setXmpAsTextEntryListener(xmp, true);
                filenamesXmp.set(0, new Pair<String, Xmp>(filename, xmp));
                setXmpToEditPanels();

                return;
            }
        }
    }

    @Override
    public void appWillExit() {
        checkDirty();
    }

    /**
     * Checks whether content was changed and saves in that case the content.
     */
    public void checkSaveOnChanges() {
        if (!isEditable()) {
            return;
        }

        if (UserSettings.INSTANCE.isSaveInputEarly() && isDirty()) {
            save();
        }
    }

    public void addEditMetadataPanelsListener(
            EditMetadataPanelsListener listener) {
        listenerSupport.add(listener);
    }

    public void removeEditMetadataPanelsListener(
            EditMetadataPanelsListener listener) {
        listenerSupport.remove(listener);
    }

    private class WatchDifferentValues extends MouseAdapter {
        private final List<TextEntry> entries         =
            new ArrayList<TextEntry>();
        private final Set<TextEntry>  releasedEntries =
            new HashSet<TextEntry>();
        private volatile boolean      listen;

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
                    entry.setText(
                        JptBundle.INSTANCE.getString(
                            "EditMetadataPanels.DisableIfMultipleValues.Info.TextEntry"));
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
            releaseAllEntries();
            this.releasedEntries.clear();
            this.entries.clear();
            this.entries.addAll(entries);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            synchronized (this) {
                if (!editable ||!listen) {
                    return;
                }

                TextEntry entry = getTextEntry(e.getSource());

                if (enableEdit(entry)
                        && (entry instanceof RatingSelectionPanel)) {
                    ((RatingSelectionPanel) entry).repeatLastClick();
                }
            }
        }

        private TextEntry getTextEntry(Object o) {
            if (o instanceof TextEntry) {
                return (TextEntry) o;
            }

            while (o != null) {
                if (o instanceof Component) {
                    o = ((Component) o).getParent();

                    if (o instanceof TextEntry) {
                        return (TextEntry) o;
                    }
                } else {
                    return null;
                }
            }

            return null;
        }

        public boolean enableEdit(TextEntry entry) {
            if (MessageDisplayer.confirmYesNo(
                    null,
                    "EditMetadataPanels.DisableIfMultipleValues.Confirm.Edit")) {
                releaseEntry(entry);

                return true;
            }

            return false;
        }
    }
}
