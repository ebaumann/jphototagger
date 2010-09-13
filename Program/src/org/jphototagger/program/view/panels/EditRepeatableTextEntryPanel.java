/*
 * @(#)EditRepeatableTextEntryPanel.java    Created on 2008-09-18
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.view.panels;

import org.jphototagger.program.UserSettings;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.TextEntry;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections
    .AutoCompleteDataOfColumn;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.event.listener.impl.TextEntryListenerSupport;
import org.jphototagger.program.event.listener.TextEntryListener;
import org.jphototagger.program.helper.AutocompleteHelper;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Suggest;
import org.jphototagger.program.view.renderer.ListCellRendererKeywordsEdit;
import org.jphototagger.lib.componentutil.Autocomplete;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.event.util.KeyEventUtil;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

/**
 * Panel with an input text field an a list. The list contains multiple words,
 * the input field one word.
 *
 * Text in the input field will be added to the list on hitting the ENTER key
 * or pushing the ADD button.
 *
 * @author  Elmar Baumann
 */
public final class EditRepeatableTextEntryPanel extends JPanel
        implements TextEntry, ActionListener, DocumentListener,
                   ListDataListener, DatabaseImageFilesListener {
    private static final long                  serialVersionUID =
        -5581799743101447535L;
    private String                             bundleKeyPosRenameDialog;
    private final DefaultListModel             model    =
        new DefaultListModel();
    private transient Column                   column   =
        ColumnXmpDcSubjectsSubject.INSTANCE;
    private boolean                            editable = true;
    private boolean                            dirty    = false;
    private Suggest                            suggest;
    private boolean                            ignoreIntervalAdded;
    private transient TextEntryListenerSupport textEntryListenerSupport =
        new TextEntryListenerSupport();
    private Autocomplete autocomplete;
    private Color        editBackground;

    public EditRepeatableTextEntryPanel() {
        initComponents();
        postInitComponents();
    }

    public EditRepeatableTextEntryPanel(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        this.column = column;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        editBackground = textAreaInput.getBackground();
        textAreaInput.setInputVerifier(column.getInputVerifier());
        textAreaInput.getDocument().addDocumentListener(this);
        model.addListDataListener(this);
        DatabaseImageFiles.INSTANCE.addListener(this);
        setPropmt();
    }

    private void setPropmt() {
        labelPrompt.setText(column.getDescription());
        labelPrompt.setLabelFor(textAreaInput);
    }

    public void setPrompt(String text) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }

        labelPrompt.setText(text);
        labelPrompt.setLabelFor(textAreaInput);
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
            autocomplete.setTransferFocusForward(false);
            autocomplete.decorate(
                textAreaInput, AutoCompleteDataOfColumn.INSTANCE.get(column).get(),
                    true);
        }
    }

    /**
     * Returns the text from the input field.
     *
     * @return trimmed text
     */
    @Override
    public String getText() {
        return textAreaInput.getText().trim();
    }

    /**
     * Returns the list item texts.
     *
     * @return list item texts
     */
    public Collection<String> getRepeatableText() {
        List<String> texts = new ArrayList<String>(model.size());
        int          size  = model.getSize();

        for (int i = 0; i < size; i++) {
            texts.add(model.get(i).toString());
        }

        return texts;
    }

    /**
     * Sets a suggester for the keyword in the input text field.
     *
     * @param suggest suggest for keywords
     */
    public void setSuggest(Suggest suggest) {
        if (suggest == null) {
            throw new NullPointerException("suggest == null");
        }

        this.suggest = suggest;
        buttonSuggestion.setEnabled(editable);
        buttonSuggestion.setToolTipText(suggest.getDescription());
    }

    /**
     * Does nothing but removing the dirty flag. Don't call this!
     *
     * @param text ignroed
     * @see        #addText(java.lang.String)
     */
    @Override
    public void setText(String text) {
        assert false : "Don't call this (Called with text: '" + text + "')";
    }

    /**
     * Sets text to lists (replaces existing text) and sets the dirty to false.
     *
     * @param texts text to set, every text is a list item
     */
    public void setText(Collection<String> texts) {
        if (texts == null) {
            throw new NullPointerException("texts == null");
        }

        textAreaInput.setText("");
        model.removeAllElements();
        addToList(texts);
        setEnabledButtons();
        dirty = false;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenuList;
    }

    public JMenuItem getItemRename() {
        return menuItemRename;
    }

    public JMenuItem getItemRemove() {
        return menuItemRemove;
    }

    public JList getList() {
        return list;
    }

    public JTextArea getTextArea() {
        return textAreaInput;
    }

    public void setBundleKeyPosRenameDialog(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        bundleKeyPosRenameDialog = key;
    }

    @Override
    public void empty(boolean dirty) {
        textAreaInput.setText("");
        model.removeAllElements();
        this.dirty = dirty;
    }

    public void removeText(String text) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }

        if (!editable) {
            assert false;
            return;
        }

        model.removeElement(text);
        notifyTextRemoved(column, text);
        dirty = true;
    }

    /**
     * Adds text to the list <em>whithout</em> replacing existing words in the
     * list if editing is allowed and sets the dirty flag to true.
     *
     * @param text text
     */
    public void addText(String text) {
        if (text == null) {
            throw new NullPointerException("text == null");
        }

        if (!editable) {
            assert false;
            return;
        }

        addToList(Collections.singleton(text));
        dirty = true;
    }

    @Override
    public Column getColumn() {
        return column;
    }

    /**
     * Adds the text in the input text field to the list if the ENTER key
     * was pressed.
     *
     * @param evt key event
     */
    private void handleTextFieldKeyReleased(KeyEvent evt) {
        JComponent component = (JComponent) evt.getSource();

        if ((evt.getKeyCode() == KeyEvent.VK_ENTER)
                && component.getInputVerifier().verify(component)) {
            addInputToList();
        } else {
            setEnabledButtons();
        }
    }

    private void addInputToList() {
        if (addToList(Collections.singleton(textAreaInput.getText())) > 0) {
            textAreaInput.setText("");
        }
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean isEmpty() {
        return model.isEmpty();
    }

    /**
     * Focusses the input text field
     */
    @Override
    public void focus() {
        textAreaInput.requestFocus();
        textAreaInput.selectAll();
    }

    /**
     * Removes from the list all selected elements.
     */
    private void removeSelectedElements() {
        if ((list.getSelectedIndex() >= 0) && confirmRemoveSelectedItems()) {
            Object[] values = list.getSelectedValues();

            for (Object value : values) {
                model.removeElement(value);
                notifyTextRemoved(column, value.toString());
                dirty = true;
            }

            ComponentUtil.forceRepaint(getParent().getParent());
        }
    }

    private boolean confirmRemoveSelectedItems() {
        return MessageDisplayer.confirmYesNo(this,
                "EditRepeatableTextEntryPanel.Confirm.RemoveSelItems");
    }

    @Override
    public void setEditable(boolean editable) {
        this.editable = editable;
        textAreaInput.setEditable(editable);
        buttonAddInput.setEnabled(editable);
        buttonRemoveSelection.setEnabled(editable);
        buttonSuggestion.setEnabled(editable && (suggest != null));

        Color background = editable
                           ? editBackground
                           : getBackground();

        list.setBackground(background);
        textAreaInput.setBackground(background);
    }

    @Override
    public boolean isEditable() {
        return editable;
    }

    private void handleButtonAddInputActionPerformed() {
        addInputToList();
        textAreaInput.requestFocusInWindow();
    }

    /**
     * Removes the list's selected elements from the list.
     */
    private void handleButtonRemoveInputActionPerformed() {
        removeSelectedElements();
        textAreaInput.requestFocusInWindow();
    }

    private void handleListKeyPressed(KeyEvent evt) {
        if (editable) {
            if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
                removeSelectedElements();
            } else if (evt.getKeyCode() == KeyEvent.VK_F2) {
                renameSelectedListItems();
            }
        }
    }

    private void handleListValueChanged(ListSelectionEvent evt) {
        setEnabledButtons();
    }

    private void setEnabledButtons() {
        buttonAddInput.setEnabled(editable
                                  &&!textAreaInput.getText().isEmpty());
        buttonRemoveSelection.setEnabled(editable
                                         && (list.getSelectedIndex() >= 0));
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Removes the list's selected elements from the list.
     *
     * @param evt action event
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        removeSelectedElements();
    }

    /**
     * The input text field's document was changed: Sets the dirty flag.
     *
     * @param evt document event
     */
    @Override
    public void insertUpdate(DocumentEvent evt) {

        // Don't notify TextEntryListener listeners because the model doesn't
        // change
        dirty = true;
        buttonAddInput.setEnabled(true);
    }

    /**
     * The input text field's document was changed: Sets the dirty flag.
     *
     * @param evt document event
     */
    @Override
    public void removeUpdate(DocumentEvent evt) {

        // Don't notify TextEntryListener listeners because the model doesn't
        // change
        dirty = true;
        buttonAddInput.setEnabled(!textAreaInput.getText().isEmpty());
    }

    /**
     * The input text field's document was changed: Sets the dirty flag.
     *
     * @param evt document event
     */
    @Override
    public void changedUpdate(DocumentEvent evt) {

        // Don't notify TextEntryListener listeners because the model doesn't
        // change
        dirty = true;
    }

    /**
     * Invokes the text modifier if the <code>Ctrl+K</code> was pressed.
     *
     * @param evt key event
     */
    private void suggestText(java.awt.event.KeyEvent evt) {
        if (KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_K)) {
            suggestText();
        }
    }

    private void suggestText() {
        String trimmedInput = textAreaInput.getText().trim();

        if ((suggest != null) &&!trimmedInput.isEmpty()) {
            addToList(suggest.suggest(trimmedInput));
        }
    }

    private int addToList(Collection<String> texts) {
        if (!column.getInputVerifier().verify(textAreaInput)) {
            return 0;
        }

        ignoreIntervalAdded = true;

        int countAdded = 0;

        for (String text : texts) {
            String trimmedText = text.trim();

            if (!trimmedText.isEmpty() &&!model.contains(trimmedText)) {
                model.addElement(trimmedText);
                countAdded++;
                notifyTextAdded(column, trimmedText);
            }
        }

        if (countAdded > 0) {
            if (getParent() != null) {
                ComponentUtil.forceRepaint(getParent());

                if (getParent().getParent() != null) {
                    ComponentUtil.forceRepaint(getParent().getParent());
                }
            }

            if (autocomplete != null && UserSettings.INSTANCE.isAutocomplete()) {
                AutocompleteHelper.addAutocompleteData(column, autocomplete,
                        texts);
            }
        }

        ignoreIntervalAdded = false;

        return countAdded;
    }

    private void renameSelectedListItems() {
        int[] selIndices = list.getSelectedIndices();

        if (!checkSelected(selIndices.length)) {
            return;
        }

        for (int selIndex : selIndices) {
            renameListItem(selIndex);
        }
    }

    private void renameListItem(int index) {
        boolean ready    = false;
        String  fromName = model.getElementAt(index).toString();
        String  toName   = null;

        do {
            bundleKeyPosRenameDialog = getClass().getName();
            toName                   = MessageDisplayer.input(
                "EditRepeatableTextEntryPanel.Input.RenameListItem", fromName,
                bundleKeyPosRenameDialog);
            ready = toName == null;

            if ((toName != null) && toName.trim().equalsIgnoreCase(fromName)) {
                ready = !MessageDisplayer.confirmYesNo(list,
                        "EditRepeatableTextEntryPanel.Confirm.SameNames");
                toName = null;
            } else if ((toName != null)
                       && ListUtil.containsString(list.getModel(),
                           toName.trim())) {
                ready = !MessageDisplayer.confirmYesNo(list,
                        "EditRepeatableTextEntryPanel.Confirm.NameExists",
                        toName);
                toName = null;
            } else if ((toName != null) &&!toName.trim().isEmpty()) {
                ready   = true;
                toName = toName.trim();
            }
        } while (!ready);

        if (toName != null) {
            model.set(index, toName);
            dirty = true;
            notifyTextChanged(column, fromName, toName);
        }
    }

    private boolean checkSelected(int selCount) {
        if (selCount <= 0) {
            MessageDisplayer.error(this,
                                   "EditRepeatableTextEntryPanel.Error.Select");

            return false;
        }

        return true;
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

    private void notifyTextRemoved(Column column, String removedText) {
        textEntryListenerSupport.notifyTextRemoved(column, removedText);
    }

    private void notifyTextAdded(Column column, String addedText) {
        textEntryListenerSupport.notifyTextAdded(column, addedText);
    }

    private void notifyTextChanged(Column column, String oldText,
                                   String newText) {
        textEntryListenerSupport.notifyTextChanged(column, oldText, newText);
    }

    @Override
    public void intervalAdded(ListDataEvent evt) {
        if (ignoreIntervalAdded) {
            return;
        }

        // drop
        int index0 = evt.getIndex0();
        int index1 = evt.getIndex1();

        for (int i = index0; i <= index1; i++) {
            notifyTextAdded(column, model.get(i).toString());
            dirty = true;
        }
    }

    @Override
    public void intervalRemoved(ListDataEvent evt) {
        dirty = true;
    }

    @Override
    public void contentsChanged(ListDataEvent evt) {
        buttonRemoveSelection.setEnabled(list.getModel().getSize() > 0);
    }

    @Override
    public List<Component> getInputComponents() {
        return Arrays.asList((Component) list,
                             (Component) buttonRemoveSelection,
                             (Component) buttonAddInput,
                             (Component) buttonSuggestion,
                             (Component) textAreaInput);
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
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        addToAutocomplete(xmp);
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        if (updatedXmp == null) {
            throw new NullPointerException("updatedXmp == null");
        }

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
        if (dcSubject == null) {
            throw new NullPointerException("dcSubject == null");
        }

        if (isAutocomplete()) {
            AutocompleteHelper.addAutocompleteData(
                    ColumnXmpDcSubjectsSubject.INSTANCE, autocomplete,
                    Collections.singleton(dcSubject));
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

        popupMenuList = new javax.swing.JPopupMenu();
        menuItemRename = new javax.swing.JMenuItem();
        menuItemRemove = new javax.swing.JMenuItem();
        labelPrompt = new javax.swing.JLabel();
        scrollPaneList = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        panelButtons = new javax.swing.JPanel();
        buttonRemoveSelection = new javax.swing.JButton();
        buttonAddInput = new javax.swing.JButton();
        buttonSuggestion = new javax.swing.JButton();
        scrollPaneTextArea = new javax.swing.JScrollPane();
        textAreaInput = new javax.swing.JTextArea();

        menuItemRename.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        menuItemRename.setText(JptBundle.INSTANCE.getString("EditRepeatableTextEntryPanel.menuItemRename.text")); // NOI18N
        menuItemRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRenameActionPerformed(evt);
            }
        });
        popupMenuList.add(menuItemRename);

        menuItemRemove.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemRemove.setText(JptBundle.INSTANCE.getString("EditRepeatableTextEntryPanel.menuItemRemove.text")); // NOI18N
        menuItemRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRemoveActionPerformed(evt);
            }
        });
        popupMenuList.add(menuItemRemove);

        setLayout(new java.awt.GridBagLayout());

        labelPrompt.setText(JptBundle.INSTANCE.getString("EditRepeatableTextEntryPanel.labelPrompt.text")); // NOI18N
        labelPrompt.setToolTipText(column.getLongerDescription());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(labelPrompt, gridBagConstraints);

        scrollPaneList.setMinimumSize(new java.awt.Dimension(22, 44));

        list.setModel(model);
        list.setToolTipText(JptBundle.INSTANCE.getString("EditRepeatableTextEntryPanel.list.toolTipText")); // NOI18N
        list.setCellRenderer(new ListCellRendererKeywordsEdit());
        list.setComponentPopupMenu(popupMenuList);
        list.setDragEnabled(true);
        list.setDropMode(javax.swing.DropMode.INSERT);
        list.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        scrollPaneList.setViewportView(list);
        list.setTransferHandler(new org.jphototagger.program.datatransfer.TransferHandlerDropList());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPaneList, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridLayout(3, 1));

        buttonRemoveSelection.setMnemonic('-');
        buttonRemoveSelection.setText(JptBundle.INSTANCE.getString("EditRepeatableTextEntryPanel.buttonRemoveSelection.text")); // NOI18N
        buttonRemoveSelection.setToolTipText(JptBundle.INSTANCE.getString("EditRepeatableTextEntryPanel.buttonRemoveSelection.toolTipText")); // NOI18N
        buttonRemoveSelection.setContentAreaFilled(false);
        buttonRemoveSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveSelectionActionPerformed(evt);
            }
        });
        panelButtons.add(buttonRemoveSelection);

        buttonAddInput.setMnemonic('+');
        buttonAddInput.setText(JptBundle.INSTANCE.getString("EditRepeatableTextEntryPanel.buttonAddInput.text")); // NOI18N
        buttonAddInput.setToolTipText(JptBundle.INSTANCE.getString("EditRepeatableTextEntryPanel.buttonAddInput.toolTipText")); // NOI18N
        buttonAddInput.setContentAreaFilled(false);
        buttonAddInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddInputActionPerformed(evt);
            }
        });
        panelButtons.add(buttonAddInput);

        buttonSuggestion.setMnemonic('k');
        buttonSuggestion.setText(JptBundle.INSTANCE.getString("EditRepeatableTextEntryPanel.buttonSuggestion.text")); // NOI18N
        buttonSuggestion.setContentAreaFilled(false);
        buttonSuggestion.setEnabled(false);
        buttonSuggestion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSuggestionActionPerformed(evt);
            }
        });
        panelButtons.add(buttonSuggestion);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 1, 2);
        add(panelButtons, gridBagConstraints);

        scrollPaneTextArea.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneTextArea.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPaneTextArea.setMinimumSize(new java.awt.Dimension(7, 18));

        textAreaInput.setColumns(20);
        textAreaInput.setRows(1);
        textAreaInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textAreaInputKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textAreaInputKeyReleased(evt);
            }
        });
        scrollPaneTextArea.setViewportView(textAreaInput);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        add(scrollPaneTextArea, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void buttonAddInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddInputActionPerformed
        handleButtonAddInputActionPerformed();
    }//GEN-LAST:event_buttonAddInputActionPerformed

    private void buttonRemoveSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveSelectionActionPerformed
        handleButtonRemoveInputActionPerformed();
    }//GEN-LAST:event_buttonRemoveSelectionActionPerformed

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        handleListValueChanged(evt);
    }//GEN-LAST:event_listValueChanged

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        handleListKeyPressed(evt);
    }//GEN-LAST:event_listKeyPressed

    private void buttonSuggestionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSuggestionActionPerformed
        suggestText();
    }//GEN-LAST:event_buttonSuggestionActionPerformed

    private void menuItemRenameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRenameActionPerformed
        renameSelectedListItems();
    }//GEN-LAST:event_menuItemRenameActionPerformed

    private void menuItemRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRemoveActionPerformed
        removeSelectedElements();
    }//GEN-LAST:event_menuItemRemoveActionPerformed

    private void textAreaInputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaInputKeyReleased
        handleTextFieldKeyReleased(evt);
    }//GEN-LAST:event_textAreaInputKeyReleased

    private void textAreaInputKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaInputKeyPressed
        suggestText(evt);
    }//GEN-LAST:event_textAreaInputKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddInput;
    private javax.swing.JButton buttonRemoveSelection;
    private javax.swing.JButton buttonSuggestion;
    private javax.swing.JLabel labelPrompt;
    private javax.swing.JList list;
    private javax.swing.JMenuItem menuItemRemove;
    private javax.swing.JMenuItem menuItemRename;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPopupMenu popupMenuList;
    private javax.swing.JScrollPane scrollPaneList;
    private javax.swing.JScrollPane scrollPaneTextArea;
    public javax.swing.JTextArea textAreaInput;
    // End of variables declaration//GEN-END:variables
}
