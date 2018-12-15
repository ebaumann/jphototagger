package org.jphototagger.program.module.wordsets;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.text.JTextComponent;
import org.jphototagger.domain.metadata.selections.AutoCompleteData;
import org.jphototagger.domain.metadata.selections.AutoCompleteDataOfMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.WordsetsRepository;
import org.jphototagger.domain.wordsets.Wordset;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.Autocomplete;
import org.jphototagger.lib.swing.util.DocumentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.resources.Icons;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class EditWordsetPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private final List<String> selectedWords = new ArrayList<>();
    private String currentWord = "";
    private final EnableSaveButtonListener enableSaveButtonListener = new EnableSaveButtonListener();
    private final Wordset wordset;
    private final WordsetsRepository wordsetsRepository = Lookup.getDefault().lookup(WordsetsRepository.class);
    private Autocomplete autocomplete;
    private boolean dirty;

    public EditWordsetPanel() {
        this(new Wordset(Bundle.getString(EditWordsetPanel.class, "EditWordset.DefaultWordsetName")));
    }

    public EditWordsetPanel(Wordset wordset) {
        org.jphototagger.resources.UiFactory.configure(this);
        if (wordset == null) {
            throw new NullPointerException("wordset == null");
        }
        this.wordset = wordset;
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
        textFieldName.getDocument().addDocumentListener(enableSaveButtonListener);
        listWords.getModel().addListDataListener(enableSaveButtonListener);
    }

    public void enableAutocomplete() {
        if (autocomplete == null) {
            autocomplete = new Autocomplete(true);
            XmpDcSubjectsSubjectMetaDataValue keywordsMeta = XmpDcSubjectsSubjectMetaDataValue.INSTANCE;
            AutoCompleteData keywordsAutocompleteData = AutoCompleteDataOfMetaDataValue.INSTANCE.get(keywordsMeta);
            List<String> keywords = keywordsAutocompleteData.get();
            boolean sorted = true;
            autocomplete.decorate(textAreaWord, keywords, sorted);
        }
    }

    public Wordset getWordset() {
        return wordset;
    }

    public List<String> getSelectedWords() {
        return Collections.unmodifiableList(selectedWords);
    }

    public void setSelectedWords(List<String> selectedWords) {
        this.selectedWords.clear();
        if (selectedWords != null) {
            this.selectedWords.addAll(selectedWords);
            buttonRemoveSelectedWords.setEnabled(!selectedWords.isEmpty());
        }
    }

    private void removeSelectedWords() {
        if (isRemoveSelectedWordsConfirm() && wordset.removeFromWords(selectedWords)) {
            dirty = true;
        }
    }

    private boolean isRemoveSelectedWordsConfirm() {
        if (selectedWords.isEmpty()) {
            return false;
        }
        return MessageDisplayer.confirmYesNo(this,
                Bundle.getString(EditWordsetPanel.class, "EditWordsetPanel.Confirm.RemoveSelectedWords"));
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
        buttonAddWord.setEnabled(StringUtil.hasContent(currentWord));
    }

    private void addCurrentWord() {
        if (StringUtil.hasContent(currentWord)) {
            if (wordset.addToWords(currentWord.trim())) {
                dirty = true;
            }
        }
    }

    public void saveWordset() {
        if (!dirty) {
            return;
        }
        boolean saved = false;
        if (isUpdate()) {
            if (checkValidUpdateName()) {
                saved = wordsetsRepository.update(wordset);
                showSaveSuccessMessage(saved);
            }
        } else if (isSave()) {
            if (checkNotExistingName()) {
                saved = wordsetsRepository.insert(wordset);
                if (saved) {
                    String wordsetName = wordset.getName();
                    long wordsetId = wordsetsRepository.findWordsetId(wordsetName);
                    wordset.setId(wordsetId);
                }
                showSaveSuccessMessage(saved);
            }
        }
        dirty = !saved;
    }

    private void showSaveSuccessMessage(boolean saved) {
        if (saved) {
            MessageDisplayer.information(this, Bundle.getString(EditWordsetPanel.class, "EditWordsetPanel.Save.Success"));
        } else {
            MessageDisplayer.warning(this, Bundle.getString(EditWordsetPanel.class, "EditWordsetPanel.Save.Error"));
        }
    }

    private boolean isUpdate() {
        return wordset.getId() > 0;
    }

    private boolean isSave() {
        return wordset.getId() <= 0;
    }

    private boolean checkValidUpdateName() {
        String name = wordset.getName().trim();
        String nameOfId = wordsetsRepository.findWordsetNameById(wordset.getId());
        if (!name.equals(nameOfId)) {
            return checkNotExistingName();
        }
        return true;
    }

    private boolean checkNotExistingName() {
        String name = wordset.getName();
        if (wordsetsRepository.existsWordset(name)) {
            MessageDisplayer.warning(this,
                    Bundle.getString(EditWordsetPanel.class, "EditWordsetPanel.Save.NameAlreadyExists", name));
            return false;
        }
        return true;
    }

    public boolean isDirty() {
        return dirty;
    }

    private class EnableSaveButtonListener implements DocumentListener, ListDataListener {

        @Override
        public void insertUpdate(DocumentEvent e) {
            setSaveButtonEnabled(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            setSaveButtonEnabled(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            setSaveButtonEnabled(e);
        }

        private void setSaveButtonEnabled(DocumentEvent e) {
            dirty = true;
            String wordsetName = DocumentUtil.getText(e);
            boolean nameHasContent = StringUtil.hasContent(wordsetName);
            boolean nameIsAllowed = !WordsetPreferences.isAutomaticWordsetName(wordsetName);
            buttonSave.setEnabled(dirty && nameHasContent && nameIsAllowed);
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            setSaveButtonEnabled();
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            setSaveButtonEnabled();
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            setSaveButtonEnabled();
        }

        private void setSaveButtonEnabled() {
            dirty = true;
            buttonSave.setEnabled(true);
        }
    }

    public void selectNameTextField() {
        selectTextComponent(textFieldName);
    }

    public void selectWordTextField() {
        selectTextComponent(textAreaWord);
    }

    private void selectTextComponent(JTextComponent tc) {
        tc.requestFocusInWindow();
        tc.selectAll();
    }

    private static class WordsetNamesComboBoxRenderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;
        private static final ImageIcon ICON = Icons.getIcon("icon_keyword.png");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(ICON);
            return label;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panelName = org.jphototagger.resources.UiFactory.panel();
        labelName = org.jphototagger.resources.UiFactory.label();
        textFieldName = org.jphototagger.resources.UiFactory.textField();
        buttonSave = org.jphototagger.resources.UiFactory.button();
        scrollPaneWords = org.jphototagger.resources.UiFactory.scrollPane();
        listWords = new javax.swing.JList<>();
        org.jphototagger.resources.UiFactory.configure(listWords);
        panelEdit = org.jphototagger.resources.UiFactory.panel();
        labelWord = org.jphototagger.resources.UiFactory.label();
        textAreaWord = org.jphototagger.resources.UiFactory.textArea();
        buttonAddWord = org.jphototagger.resources.UiFactory.button();
        buttonRemoveSelectedWords = org.jphototagger.resources.UiFactory.button();

        setLayout(new java.awt.GridBagLayout());

        panelName.setLayout(new java.awt.GridBagLayout());

        labelName.setLabelFor(textFieldName);
        labelName.setText(Bundle.getString(getClass(), "EditWordsetPanel.labelName.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelName.add(labelName, gridBagConstraints);

        textFieldName.setColumns(25);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${wordset.name}"), textFieldName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelName.add(textFieldName, gridBagConstraints);

        buttonSave.setText(Bundle.getString(getClass(), "EditWordsetPanel.buttonSave.text")); // NOI18N
        buttonSave.setEnabled(false);
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSaveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelName.add(buttonSave, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(panelName, gridBagConstraints);

        listWords.setCellRenderer(new WordsetNamesComboBoxRenderer());
        listWords.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        listWords.setVisibleRowCount(-1);

        org.jdesktop.beansbinding.ELProperty eLProperty = org.jdesktop.beansbinding.ELProperty.create("${wordset.words}");
        org.jdesktop.swingbinding.JListBinding jListBinding = org.jdesktop.swingbinding.SwingBindings.createJListBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, eLProperty, listWords);
        bindingGroup.addBinding(jListBinding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedWords}"), listWords, org.jdesktop.beansbinding.BeanProperty.create("selectedElements"));
        bindingGroup.addBinding(binding);

        listWords.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listWordsKeyPressed(evt);
            }
        });
        scrollPaneWords.setViewportView(listWords);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        add(scrollPaneWords, gridBagConstraints);

        panelEdit.setLayout(new java.awt.GridBagLayout());

        labelWord.setLabelFor(textAreaWord);
        labelWord.setText(Bundle.getString(getClass(), "EditWordsetPanel.labelWord.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelEdit.add(labelWord, gridBagConstraints);

        textAreaWord.setColumns(20);
        textAreaWord.setFont(UIManager.getFont("TextField.font"));
        textAreaWord.setRows(1);
        textAreaWord.setBorder(UIManager.getBorder("TextField.border"));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentWord}"), textAreaWord, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        textAreaWord.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textAreaWordKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelEdit.add(textAreaWord, gridBagConstraints);

        buttonAddWord.setMnemonic('+');
        buttonAddWord.setText("+"); // NOI18N
        buttonAddWord.setToolTipText(Bundle.getString(getClass(), "EditWordsetPanel.buttonAddWord.toolTipText")); // NOI18N
        buttonAddWord.setEnabled(false);
        buttonAddWord.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddWordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelEdit.add(buttonAddWord, gridBagConstraints);

        buttonRemoveSelectedWords.setMnemonic('-');
        buttonRemoveSelectedWords.setText("-"); // NOI18N
        buttonRemoveSelectedWords.setToolTipText(Bundle.getString(getClass(), "EditWordsetPanel.buttonRemoveSelectedWords.toolTipText")); // NOI18N
        buttonRemoveSelectedWords.setEnabled(false);
        buttonRemoveSelectedWords.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveSelectedWordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelEdit.add(buttonRemoveSelectedWords, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        add(panelEdit, gridBagConstraints);

        bindingGroup.bind();
    }//GEN-END:initComponents

    private void buttonRemoveSelectedWordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveSelectedWordsActionPerformed
        removeSelectedWords();
        selectTextComponent(textAreaWord);
    }//GEN-LAST:event_buttonRemoveSelectedWordsActionPerformed

    private void buttonAddWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddWordActionPerformed
        addCurrentWord();
        selectTextComponent(textAreaWord);
    }//GEN-LAST:event_buttonAddWordActionPerformed

    private void buttonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSaveActionPerformed
        saveWordset();
    }//GEN-LAST:event_buttonSaveActionPerformed

    private void textAreaWordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaWordKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addCurrentWord();
            textAreaWord.selectAll();
            textAreaWord.requestFocusInWindow();
            evt.consume();
        }
    }//GEN-LAST:event_textAreaWordKeyPressed

    private void listWordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listWordsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSelectedWords();
            selectTextComponent(textAreaWord);
        }
    }//GEN-LAST:event_listWordsKeyPressed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddWord;
    private javax.swing.JButton buttonRemoveSelectedWords;
    private javax.swing.JButton buttonSave;
    private javax.swing.JLabel labelName;
    private javax.swing.JLabel labelWord;
    private javax.swing.JList<Object> listWords;
    private javax.swing.JPanel panelEdit;
    private javax.swing.JPanel panelName;
    private javax.swing.JScrollPane scrollPaneWords;
    private javax.swing.JTextArea textAreaWord;
    private javax.swing.JTextField textFieldName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
