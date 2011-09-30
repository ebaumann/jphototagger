package org.jphototagger.program.view.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jdesktop.swingx.JXList;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.event.listener.TextEntryListener;
import org.jphototagger.domain.event.listener.TextEntryListenerSupport;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.AutoCompleteDataOfMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.text.TextEntry;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.lib.componentutil.Autocomplete;
import org.jphototagger.lib.componentutil.ComponentUtil;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.helper.AutocompleteHelper;
import org.jphototagger.program.types.Suggest;

/**
 * Panel with an input text field an a list. The list contains multiple words,
 * the input field one word.
 *
 * Text in the input field will be added to the list on hitting the ENTER key
 * or pushing the ADD button.
 *
 * @author Elmar Baumann
 */
public final class EditRepeatableTextEntryPanel extends JPanel implements TextEntry, ActionListener, DocumentListener, ListDataListener {
    private static final long serialVersionUID = -5581799743101447535L;
    private String bundleKeyPosRenameDialog;
    private final DefaultListModel model = new DefaultListModel();
    private transient MetaDataValue metaDataValue = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;
    private boolean editable = true;
    private boolean dirty = false;
    private Suggest suggest;
    private boolean ignoreIntervalAdded;
    private transient TextEntryListenerSupport textEntryListenerSupport = new TextEntryListenerSupport();
    private Autocomplete autocomplete;
    private Color editBackground;

    public EditRepeatableTextEntryPanel() {
        initComponents();
        postInitComponents();
    }

    public EditRepeatableTextEntryPanel(MetaDataValue metaDataValue) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        this.metaDataValue = metaDataValue;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        editBackground = textAreaInput.getBackground();
        textAreaInput.setInputVerifier(metaDataValue.getInputVerifier());
        textAreaInput.getDocument().addDocumentListener(this);
        model.addListDataListener(this);
        AnnotationProcessor.process(this);
        setPropmt();
        textAreaInput.setName("JPhotoTagger Text Area for " + metaDataValue.getDescription());
    }

    private void setPropmt() {
        labelPrompt.setText(metaDataValue.getDescription());
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
    public void enableAutocomplete() {
        if (getPersistedAutocomplete()) {
            synchronized (this) {
                if (autocomplete != null) {
                    return;
                }
            }
            autocomplete = new Autocomplete(false);
            autocomplete.setTransferFocusForward(false);
            autocomplete.decorate(textAreaInput, AutoCompleteDataOfMetaDataValue.INSTANCE.get(metaDataValue).get(), true);
        }
    }

    private boolean getPersistedAutocomplete() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage == null
                ? false
                : storage.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? storage.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                : true;
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
        int size  = model.getSize();

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
    public void setTexts(Collection<String> texts) {
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

    public JXList getList() {
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
    public void empty() {
        textAreaInput.setText("");
        model.removeAllElements();
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
        notifyTextRemoved(metaDataValue, text);
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
    public MetaDataValue getMetaDataValue() {
        return metaDataValue;
    }

    /**
     * Adds the text in the input text field to the list if the ENTER key
     * was pressed.
     *
     * @param evt key event
     */
    private void handleTextFieldKeyReleased(KeyEvent evt) {
        JComponent component = (JComponent) evt.getSource();

        if ((evt.getKeyCode() == KeyEvent.VK_ENTER) && component.getInputVerifier().verify(component)) {
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
    public void requestFocus() {
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
                notifyTextRemoved(metaDataValue, value.toString());
                dirty = true;
            }

            ComponentUtil.forceRepaint(getParent().getParent());
        }
    }

    private boolean confirmRemoveSelectedItems() {
        String message = Bundle.getString(EditRepeatableTextEntryPanel.class, "EditRepeatableTextEntryPanel.Confirm.RemoveSelItems");

        return MessageDisplayer.confirmYesNo(this, message);
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
        buttonAddInput.setEnabled(editable &&!textAreaInput.getText().isEmpty());
        buttonRemoveSelection.setEnabled(editable && (list.getSelectedIndex() >= 0));
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
        if (!metaDataValue.getInputVerifier().verify(textAreaInput)) {
            return 0;
        }

        ignoreIntervalAdded = true;

        int countAdded = 0;

        for (String text : texts) {
            String trimmedText = text.trim();

            if (!trimmedText.isEmpty() &&!model.contains(trimmedText)) {
                model.addElement(trimmedText);
                countAdded++;
                notifyTextAdded(metaDataValue, trimmedText);
            }
        }

        if (countAdded > 0) {
            if (getParent() != null) {
                ComponentUtil.forceRepaint(getParent());

                if (getParent().getParent() != null) {
                    ComponentUtil.forceRepaint(getParent().getParent());
                }
            }

            if (autocomplete != null && getPersistedAutocomplete()) {
                AutocompleteHelper.addAutocompleteData(metaDataValue, autocomplete, texts);
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
            int modelIndex = list.convertIndexToModel(selIndex);

            renameListItem(modelIndex);
        }
    }

    private void renameListItem(int modelIndex) {
        boolean ready = false;
        String fromName = model.getElementAt(modelIndex).toString();
        String toName = null;

        do {
            bundleKeyPosRenameDialog = getClass().getName();
            String info = Bundle.getString(EditRepeatableTextEntryPanel.class, "EditRepeatableTextEntryPanel.Input.RenameListItem");
            String input = fromName;
            toName = MessageDisplayer.input(info, input);
            ready = toName == null;

            if ((toName != null) && toName.trim().equalsIgnoreCase(fromName)) {
                String message = Bundle.getString(EditRepeatableTextEntryPanel.class, "EditRepeatableTextEntryPanel.Confirm.SameNames");
                ready = !MessageDisplayer.confirmYesNo(list, message);
                toName = null;
            } else if ((toName != null) && ListUtil.containsString(list.getModel(), toName.trim())) {
                String message = Bundle.getString(EditRepeatableTextEntryPanel.class, "EditRepeatableTextEntryPanel.Confirm.NameExists", toName);
                ready = !MessageDisplayer.confirmYesNo(list, message);
                toName = null;
            } else if ((toName != null) &&!toName.trim().isEmpty()) {
                ready = true;
                toName = toName.trim();
            }
        } while (!ready);

        if (toName != null) {
            model.set(modelIndex, toName);
            dirty = true;
            notifyTextChanged(metaDataValue, fromName, toName);
        }
    }

    private boolean checkSelected(int selCount) {
        if (selCount <= 0) {
            String message = Bundle.getString(EditRepeatableTextEntryPanel.class, "EditRepeatableTextEntryPanel.Error.Select");
            MessageDisplayer.error(this, message);

            return false;
        }

        return true;
    }

    @Override
    public void addTextEntryListener(TextEntryListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        textEntryListenerSupport.add(listener);
    }

    @Override
    public void removeTextEntryListener(TextEntryListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }

        textEntryListenerSupport.remove(listener);
    }

    private void notifyTextRemoved(MetaDataValue value, String removedText) {
        textEntryListenerSupport.notifyTextRemoved(value, removedText);
    }

    private void notifyTextAdded(MetaDataValue value, String addedText) {
        textEntryListenerSupport.notifyTextAdded(value, addedText);
    }

    private void notifyTextChanged(MetaDataValue value, String oldText, String newText) {
        textEntryListenerSupport.notifyTextChanged(value, oldText, newText);
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
            notifyTextAdded(metaDataValue, model.get(i).toString());
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

    private boolean isAutocomplete() {
        return autocomplete != null && getPersistedAutocomplete();
    }

    private void addToAutocomplete(Xmp xmp) {
        if (isAutocomplete()) {
            AutocompleteHelper.addAutocompleteData(metaDataValue, autocomplete, xmp);
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
            AutocompleteHelper.addAutocompleteData(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, autocomplete, Collections.singleton(evt.getDcSubject()));
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

        popupMenuList = new javax.swing.JPopupMenu();
        menuItemRename = new javax.swing.JMenuItem();
        menuItemRemove = new javax.swing.JMenuItem();
        labelPrompt = new javax.swing.JLabel();
        scrollPaneList = new javax.swing.JScrollPane();
        list = new org.jdesktop.swingx.JXList();
        panelButtons = new javax.swing.JPanel();
        buttonRemoveSelection = new javax.swing.JButton();
        buttonAddInput = new javax.swing.JButton();
        buttonSuggestion = new javax.swing.JButton();
        scrollPaneTextArea = new javax.swing.JScrollPane();
        textAreaInput = new javax.swing.JTextArea();

        popupMenuList.setName("popupMenuList"); // NOI18N

        menuItemRename.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        menuItemRename.setText(bundle.getString("EditRepeatableTextEntryPanel.menuItemRename.text")); // NOI18N
        menuItemRename.setName("menuItemRename"); // NOI18N
        menuItemRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRenameActionPerformed(evt);
            }
        });
        popupMenuList.add(menuItemRename);

        menuItemRemove.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemRemove.setText(bundle.getString("EditRepeatableTextEntryPanel.menuItemRemove.text")); // NOI18N
        menuItemRemove.setName("menuItemRemove"); // NOI18N
        menuItemRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRemoveActionPerformed(evt);
            }
        });
        popupMenuList.add(menuItemRemove);

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        labelPrompt.setText("Prompt:"); // NOI18N
        labelPrompt.setToolTipText(metaDataValue.getLongerDescription());
        labelPrompt.setName("labelPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(labelPrompt, gridBagConstraints);

        scrollPaneList.setMinimumSize(new java.awt.Dimension(22, 44));
        scrollPaneList.setName("scrollPaneList"); // NOI18N

        list.setModel(model);
        list.setToolTipText(bundle.getString("EditRepeatableTextEntryPanel.list.toolTipText")); // NOI18N
        list.setCellRenderer(new org.jphototagger.program.view.renderer.KeywordsEditPanelListCellRenderer());
        list.setComponentPopupMenu(popupMenuList);
        list.setDragEnabled(true);
        list.setDropMode(javax.swing.DropMode.INSERT);
        list.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        list.setName("list"); // NOI18N
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
        list.setTransferHandler(new org.jphototagger.program.datatransfer.DropListTransferHandler());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPaneList, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridLayout(3, 1));

        buttonRemoveSelection.setMnemonic('-');
        buttonRemoveSelection.setText("-"); // NOI18N
        buttonRemoveSelection.setToolTipText(bundle.getString("EditRepeatableTextEntryPanel.buttonRemoveSelection.toolTipText")); // NOI18N
        buttonRemoveSelection.setContentAreaFilled(false);
        buttonRemoveSelection.setName("buttonRemoveSelection"); // NOI18N
        buttonRemoveSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveSelectionActionPerformed(evt);
            }
        });
        panelButtons.add(buttonRemoveSelection);

        buttonAddInput.setMnemonic('+');
        buttonAddInput.setText("+"); // NOI18N
        buttonAddInput.setToolTipText(bundle.getString("EditRepeatableTextEntryPanel.buttonAddInput.toolTipText")); // NOI18N
        buttonAddInput.setContentAreaFilled(false);
        buttonAddInput.setName("buttonAddInput"); // NOI18N
        buttonAddInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddInputActionPerformed(evt);
            }
        });
        panelButtons.add(buttonAddInput);

        buttonSuggestion.setMnemonic('k');
        buttonSuggestion.setText("K"); // NOI18N
        buttonSuggestion.setContentAreaFilled(false);
        buttonSuggestion.setEnabled(false);
        buttonSuggestion.setName("buttonSuggestion"); // NOI18N
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
        scrollPaneTextArea.setName("scrollPaneTextArea"); // NOI18N

        textAreaInput.setColumns(20);
        textAreaInput.setRows(1);
        textAreaInput.setName("textAreaInput"); // NOI18N
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
    }//GEN-END:initComponents

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
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JMenuItem menuItemRemove;
    private javax.swing.JMenuItem menuItemRename;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPopupMenu popupMenuList;
    private javax.swing.JScrollPane scrollPaneList;
    private javax.swing.JScrollPane scrollPaneTextArea;
    public javax.swing.JTextArea textAreaInput;
    // End of variables declaration//GEN-END:variables
}
