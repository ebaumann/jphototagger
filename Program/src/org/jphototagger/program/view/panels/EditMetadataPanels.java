/*
 * @(#)EditMetadataPanels.java    Created on 2008-10-05
 *
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

package org.jphototagger.program.view.panels;

import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.generics.Pair;
import org.jphototagger.program.app.AppLifeCycle;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.controller.keywords.tree.SuggestKeywords;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.MetadataTemplate;
import org.jphototagger.program.data.TextEntry;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections.EditColumns;
import org.jphototagger.program.database.metadata.selections.EditHints;
import org.jphototagger.program.database.metadata.selections.EditHints
    .SizeEditField;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;
import org.jphototagger.program.event.listener.AppExitListener;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.event.listener.EditMetadataPanelsListener;
import org.jphototagger.program.event.listener.impl
    .EditMetadataPanelsListenerSupport;
import org.jphototagger.program.helper.SaveXmp;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.ViewUtil;

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
import java.util.Arrays;
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
 */
public final class EditMetadataPanels
        implements FocusListener, DatabaseImageFilesListener, AppExitListener {
    private final List<JPanel>          panels = new ArrayList<JPanel>();
    private final List<Pair<File, Xmp>> imageFilesXmp =
        new ArrayList<Pair<File, Xmp>>();
    private boolean              editable = true;
    private WatchDifferentValues watchDifferentValues =
        new WatchDifferentValues();
    private final EditMetadataPanelsListenerSupport ls =
        new EditMetadataPanelsListenerSupport();
    private JComponent               container;
    private EditMetadataActionsPanel editActionsPanel;
    private Component                lastFocussedEditControl;

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

    private void checkDirty() {
        if (isDirty()) {
            save();
            setFocusToLastFocussedEditControl();
        }
    }

    private void save() {
        addInputToRepeatableTextEntries();
        SaveXmp.save(imageFilesXmp);
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

        if (editable) {
            ls.notifyEditEnabled();
        } else {
            ls.notifyEditDisabled();
        }
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

    public synchronized void setImageFiles(Collection<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        emptyPanels(false);
        setXmpOfImageFiles(imageFiles);
        setXmpToEditPanels();
        setXmpOfFilesAsTextEntryListener(true);
    }

    private void setXmpOfImageFiles(Collection<File> imageFiles) {
        imageFilesXmp.clear();

        for (File imageFile : imageFiles) {
            Xmp xmp = null;

            if (XmpMetadata.hasImageASidecarFile(imageFile)) {
                xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
            }

            if (xmp == null) {
                xmp = new Xmp();
            }

            imageFilesXmp.add(new Pair<File, Xmp>(imageFile, xmp));
        }
    }

    private void setXmpOfFilesAsTextEntryListener(boolean add) {
        for (Pair<File, Xmp> pair : imageFilesXmp) {
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
        if (column == null) {
            throw new NullPointerException("column == null");
        }

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
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (text == null) {
            throw new NullPointerException("text == null");
        }

        if (!isEditable()) {
            assert false;

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
        } else if (panelAdd instanceof TextEntry) {
            TextEntry textEntry = (TextEntry) panelAdd;

            textEntry.setText(text);
            textEntry.setDirty(true);
        } else {
            assert false : panelAdd;
        }

        checkSaveOnChanges();
    }

    public void removeText(Column column, String text) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (text == null) {
            throw new NullPointerException("text == null");
        }

        if (!isEditable()) {
            assert false;

            return;
        }

        JPanel panelRemove = null;
        int    size        = panels.size();

        for (int i = 0; (panelRemove == null) && (i < size); i++) {
            JPanel panel = panels.get(i);

            if (((TextEntry) panel).getColumn().equals(column)) {
                panelRemove = panel;
            }
        }

        if (panelRemove instanceof EditRepeatableTextEntryPanel) {
            ((EditRepeatableTextEntryPanel) panelRemove).removeText(text);
        } else if (panelRemove instanceof TextEntry) {
            TextEntry textEntry = (TextEntry) panelRemove;

            textEntry.setText("");
            textEntry.setDirty(true);
        } else {
            assert false;
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
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        if (!isEditable()) {
            assert false;

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

                        // addText() would set the dirty flag
                        p.addText(o.toString());
                    }
                }
            } else if (panel instanceof RatingSelectionPanel) {
                RatingSelectionPanel p = (RatingSelectionPanel) panel;
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
        if (rating == null) {
            throw new NullPointerException("rating == null");
        }

        if (!isEditable()) {
            assert false;

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

    public Collection<Pair<File, Xmp>> getImageFilesXmp() {
        return new ArrayList<Pair<File, Xmp>>(imageFilesXmp);
    }

    /**
     * Setzt ein Metadaten-Edit-Template.
     *
     * @param template  Template
     */
    public void setMetadataTemplate(MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }

        if (!isEditable()) {
            assert false;

            return;
        }

        for (JPanel panel : panels) {
            TextEntry textEntry = (TextEntry) panel;
            Object    value = template.getValueOfColumn(textEntry.getColumn());

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
     *        ({@link org.jphototagger.program.data.MetadataTemplate#getName()})
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

        if (imageFilesXmp.size() <= 0) {
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
        return imageFilesXmp.size() > 1;
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getCommonXmpCollection(Column column) {
        assert imageFilesXmp.size() >= 1 : "No files!";

        if (imageFilesXmp.size() == 1) {
            Object value = imageFilesXmp.get(0).getSecond().getValue(column);

            if (value instanceof List<?>) {
                return (List<String>) value;
            } else {
                return new ArrayList<String>(1);
            }
        }

        // more then 1 file
        Stack<List<String>> lists = new Stack<List<String>>();

        for (Pair<File, Xmp> pair : imageFilesXmp) {
            Xmp    xmp   = pair.getSecond();
            Object value = xmp.getValue(column);

            if (value instanceof List<?>) {
                lists.push((List<String>) value);
            }
        }

        if (lists.size() != imageFilesXmp.size()) {

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
        assert imageFilesXmp.size() >= 1 : "No files!";

        if (imageFilesXmp.size() == 1) {
            String value =
                toString(imageFilesXmp.get(0).getSecond().getValue(column));

            return (value == null)
                   ? ""
                   : value.trim();
        }

        // more then 1 file
        Stack<String> strings = new Stack<String>();

        for (Pair<File, Xmp> pair : imageFilesXmp) {
            Xmp    xmp   = pair.getSecond();
            String value = toString(xmp.getValue(column));

            if (value != null) {
                strings.push(value.trim());
            }
        }

        if (strings.size() != imageFilesXmp.size()) {
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
        for (Pair<File, Xmp> pair : imageFilesXmp) {
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
            GUI.INSTANCE.getAppPanel().getPanelEditMetadataActions();
        List<Character> mnemonics = new ArrayList<Character>(10);

        /*
         * UPDATE IF other components of the application panel containing
         * buttons with mnemonics and can be visible and enabled when the edit
         * panel is displayed. Else Alt+Mnemonic triggers their button actions
         * even if the components with the buttons are not focussed.
         */
        mnemonics.add((char) actionsPanel.buttonEmptyMetadata.getMnemonic());
        mnemonics.add(
            (char) actionsPanel.buttonMetadataTemplateCreate.getMnemonic());
        mnemonics.add(
            (char) actionsPanel.buttonMetadataTemplateDelete.getMnemonic());
        mnemonics.add(
            (char) actionsPanel.buttonMetadataTemplateEdit.getMnemonic());
        mnemonics.add(
            (char) actionsPanel.buttonMetadataTemplateInsert.getMnemonic());
        mnemonics.add(
            (char) actionsPanel.buttonMetadataTemplateRename.getMnemonic());
        mnemonics.add(
            (char) actionsPanel.buttonMetadataTemplateUpdate.getMnemonic());
        mnemonics.add(
            (char) actionsPanel.buttonMetadataTemplateAdd.getMnemonic());
        mnemonics
            .add((char) actionsPanel.labelPromptCurrentTemplate
                .getDisplayedMnemonic());
        mnemonics.addAll(
            MnemonicUtil.getMnemonicCharsOf(
                Arrays.asList(
                    GUI.INSTANCE.getAppPanel().getMnemonizedComponents())));
        ViewUtil.setDisplayedMnemonicsToLabels(container,
                mnemonics.toArray(new Character[] {}));
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
            GUI.INSTANCE.getAppPanel().getPanelEditMetadataActions();

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
            boolean   large =
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

    /**
     * When the XMP was changed and the data was not edited setting the new
     * XMP data.
     *
     * @param imageFile image file with new XMP data
     */
    private void setModifiedXmp(File imageFile, Xmp xmp) {
        if (!editable || isDirty() || (imageFilesXmp.size() != 1)) {
            return;
        }

        Pair<File, Xmp> pair = imageFilesXmp.get(0);

        if (pair.getFirst().equals(imageFile)) {
            setXmpAsTextEntryListener(pair.getSecond(), false);
            setXmpAsTextEntryListener(xmp, true);
            imageFilesXmp.set(0, new Pair<File, Xmp>(imageFile, xmp));
            setXmpToEditPanels();

            return;
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
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.add(listener);
    }

    public void removeEditMetadataPanelsListener(
            EditMetadataPanelsListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        ls.remove(listener);
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        setModifiedXmp(imageFile, xmp);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (updatedXmp == null) {
            throw new NullPointerException("updatedXmp == null");
        }

        setModifiedXmp(imageFile, updatedXmp);
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        setModifiedXmp(imageFile, xmp);
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
    public void thumbnailUpdated(File imageFile) {

        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {

        // ignore
    }

    private class WatchDifferentValues extends MouseAdapter {
        private final List<TextEntry> entries = new ArrayList<TextEntry>();
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
            if (entries == null) {
                throw new NullPointerException("entries == null");
            }

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
            if (entry == null) {
                throw new NullPointerException("entry == null");
            }

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
