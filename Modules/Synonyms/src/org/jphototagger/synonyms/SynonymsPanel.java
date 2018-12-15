package org.jphototagger.synonyms;

import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.metadata.selections.AutoCompleteDataOfMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.Autocomplete;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class SynonymsPanel extends PanelExt implements ListSelectionListener, DocumentListener {

    private static final long serialVersionUID = 1L;
    private final SynonymsListModel modelWords = new SynonymsListModel(SynonymsListModel.Role.WORDS);
    private final SynonymsListModel modelSynonyms = new SynonymsListModel(SynonymsListModel.Role.SYNONYMS);
    private Autocomplete autocomplete;
    private boolean listenToDocuments = true;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public SynonymsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        listWords.addListSelectionListener(this);
        listSynonyms.addListSelectionListener(this);
        textAreaWords.getDocument().addDocumentListener(this);
        textFieldSynonyms.getDocument().addDocumentListener(this);

        menuItemChangeWord.setIcon(org.jphototagger.resources.Icons.getIcon("icon_xmp.png")); // NOI18N
        menuItemRemoveWord.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png")); // NOI18N
        menuItemChangeSynonym.setIcon(org.jphototagger.resources.Icons.getIcon("icon_rename.png")); // NOI18N
        menuItemRemoveSynonym.setIcon(org.jphototagger.resources.Icons.getIcon("icon_delete.png")); // NOI18N

        scrollPaneTextAreaWords.setMinimumSize(textFieldSynonyms.getPreferredSize());

        MnemonicUtil.setMnemonics((Container) this);
        setAutocomplete();
        setEnabled();
    }

    private void setAutocomplete() {
        if (isAutocomplete()) {
            autocomplete = new Autocomplete(isAutocompleteFastSearchIgnoreCase());
            autocomplete.decorate(textAreaWords, AutoCompleteDataOfMetaDataValue.INSTANCE.get(XmpDcSubjectsSubjectMetaDataValue.INSTANCE).get(), true);
        }
    }

    private boolean isAutocompleteFastSearchIgnoreCase() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_AUTOCOMPLETE_FAST_SEARCH_IGNORE_CASE)
                : true;
    }

    private boolean isAutocomplete() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs == null
                ? false
                : prefs.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                : true;
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            if (evt.getSource() == listWords) {
                setSynonyms();
            }

            setEnabled();
        }
    }

    private void setSynonyms() {
        Object o = listWords.getSelectedValue();

        if (o instanceof String) {
            String word = (String) o;

            if (word.trim().isEmpty()) {
                modelSynonyms.removeAllElements();
            } else {
                modelSynonyms.setWord(word.trim());
            }
        }
    }

    private void setEnabled() {
        setEnabledAdd();
        setEnabledRemove();
        setEnabledChange();
    }

    private void setEnabledAdd() {
        boolean wordExists = !textAreaWords.getText().trim().isEmpty();
        boolean synonymExists = !textFieldSynonyms.getText().trim().isEmpty();
        boolean wordSelected = listWords.getSelectedValue() != null;

        buttonAddWord.setEnabled(wordExists);
        buttonAddSynonym.setEnabled(synonymExists && wordSelected);
    }

    private void setEnabledRemove() {
        boolean wordSel = listWords.getSelectedValue() != null;
        boolean synonymSel = listSynonyms.getSelectedValue() != null;

        buttonRemoveWord.setEnabled(wordSel);
        menuItemRemoveWord.setEnabled(wordSel);
        buttonRemoveSynonym.setEnabled(synonymSel);
        menuItemRemoveSynonym.setEnabled(synonymSel);
    }

    private void setEnabledChange() {
        boolean wordSel = listWords.getSelectedValue() != null;
        boolean synonymSel = listSynonyms.getSelectedValue() != null;

        buttonChangeWord.setEnabled(wordSel);
        menuItemChangeWord.setEnabled(wordSel);
        buttonChangeSynonym.setEnabled(synonymSel);
        menuItemChangeSynonym.setEnabled(synonymSel);
    }

    @Override
    public void insertUpdate(DocumentEvent evt) {
        if (!listenToDocuments) {
            return;
        }

        setEnabled();
    }

    @Override
    public void removeUpdate(DocumentEvent evt) {
        if (!listenToDocuments) {
            return;
        }

        setEnabled();
    }

    @Override
    public void changedUpdate(DocumentEvent evt) {
        if (!listenToDocuments) {
            return;
        }

        setEnabled();
    }

    private void addWord() {
        String word = textAreaWords.getText().trim();

        if (!word.isEmpty()) {
            modelWords.addWord(word);
            listenToDocuments = false;
            textAreaWords.setText("");
            listenToDocuments = true;
            setEnabled();
            textAreaWords.requestFocusInWindow();
        }
    }

    private void removeWord() {
        Object o = listWords.getSelectedValue();

        if (o instanceof String) {
            String word = (String) o;
            String message = Bundle.getString(SynonymsPanel.class, "SynonymsPanel.Confirm.RemoveWord", word);

            if (MessageDisplayer.confirmYesNo(this, message)) {
                modelWords.removeWord(word);
                modelSynonyms.removeAllElements();
            }
        }
    }

    private void changeWord() {
        Object o = listWords.getSelectedValue();

        if (o instanceof String) {
            String oldWord = (String) o;
            String info = Bundle.getString(SynonymsPanel.class, "SynonymsPanel.Info.ChangeWord");
            String newWord = MessageDisplayer.input(info, oldWord);

            if (newWord != null &&!newWord.equals(oldWord)) {
                modelWords.changeWord(oldWord, newWord);
                modelSynonyms.setWord(newWord);
            }
        }
    }

    private void addSynonym() {
        String synonym = textFieldSynonyms.getText().trim();

        if (!synonym.isEmpty()) {
            modelSynonyms.addSynonym(synonym);
            listenToDocuments = false;
            textFieldSynonyms.setText("");
            listenToDocuments = true;
            setEnabled();
            textFieldSynonyms.requestFocusInWindow();
        }
    }

    private void removeSynonym() {
        Object o = listSynonyms.getSelectedValue();

        if (o instanceof String) {
            String synonym = (String) o;
            String message = Bundle.getString(SynonymsPanel.class, "SynonymsPanel.Confirm.RemoveSynonym", synonym);

            if (MessageDisplayer.confirmYesNo(this, message)) {
                modelSynonyms.removeSynonym(synonym);
            }
        }
    }

    private void changeSynonym() {
        Object o = listSynonyms.getSelectedValue();

        if (o instanceof String) {
            String oldSynonym = (String) o;
            String info = Bundle.getString(SynonymsPanel.class, "SynonymsPanel.Info.ChangeSynonym");
            String newSynonym = MessageDisplayer.input(info, oldSynonym);

            if ((newSynonym != null) &&!newSynonym.equals(oldSynonym)) {
                modelSynonyms.changeSynonym(oldSynonym, newSynonym);
            }
        }
    }

    private void addAllKeywords() {
        for (String word : repo.findAllDcSubjects()) {
            modelWords.addWord(word);
        }

        String message = Bundle.getString(SynonymsPanel.class, "SynonymsPanel.Info.AddAllKeywords");

        MessageDisplayer.information(this, message);
    }

    private void handleListSynonymsKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSynonym();
        } else if (evt.getKeyCode() == KeyEvent.VK_F2) {
            changeSynonym();
        }
    }

    private void handleListSynonymsMouseClicked(MouseEvent evt) {
        if (MouseEventUtil.isDoubleClick(evt)) {
            changeSynonym();
        }
    }

    private void handleListWordsKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeWord();
        } else if (evt.getKeyCode() == KeyEvent.VK_F2) {
            changeWord();
        }
    }

    private void handleListWordsMouseClicked(MouseEvent evt) {
        if (MouseEventUtil.isDoubleClick(evt)) {
            changeWord();
        }
    }

    private void handleTextAreaWordsKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addWord();
        }
    }

    private void handleTextFieldSynonymsKeyPressed(KeyEvent evt) {
        if ((evt.getKeyCode() == KeyEvent.VK_ENTER) && (listWords.getSelectedValue() != null)) {
            addSynonym();
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

        popupMenuWords = org.jphototagger.resources.UiFactory.popupMenu();
        menuItemChangeWord = org.jphototagger.resources.UiFactory.menuItem();
        menuItemRemoveWord = org.jphototagger.resources.UiFactory.menuItem();
        popupMenuSynonyms = org.jphototagger.resources.UiFactory.popupMenu();
        menuItemChangeSynonym = org.jphototagger.resources.UiFactory.menuItem();
        menuItemRemoveSynonym = org.jphototagger.resources.UiFactory.menuItem();
        panelWords = org.jphototagger.resources.UiFactory.panel();
        panelEditInputWord = org.jphototagger.resources.UiFactory.panel();
        labelTextAreaWord = org.jphototagger.resources.UiFactory.label();
        scrollPaneTextAreaWords = org.jphototagger.resources.UiFactory.scrollPane();
        textAreaWords = org.jphototagger.resources.UiFactory.textArea();
        buttonAddWord = org.jphototagger.resources.UiFactory.button();
        panelAddedWords = org.jphototagger.resources.UiFactory.panel();
        labelListWords = org.jphototagger.resources.UiFactory.label();
        scrollPaneListWords = org.jphototagger.resources.UiFactory.scrollPane();
        listWords = org.jphototagger.resources.UiFactory.jxList();
        buttonRemoveWord = org.jphototagger.resources.UiFactory.button();
        buttonChangeWord = org.jphototagger.resources.UiFactory.button();
        buttonAddAllKeywords = org.jphototagger.resources.UiFactory.button();
        panelSynonyms = org.jphototagger.resources.UiFactory.panel();
        panelEditInputSynonyms = org.jphototagger.resources.UiFactory.panel();
        labelTextFieldSynonym = org.jphototagger.resources.UiFactory.label();
        textFieldSynonyms = org.jphototagger.resources.UiFactory.textField();
        buttonAddSynonym = org.jphototagger.resources.UiFactory.button();
        panelAddedSynonyms = org.jphototagger.resources.UiFactory.panel();
        labelListSynonyms = org.jphototagger.resources.UiFactory.label();
        scrollPaneListSynonyms = org.jphototagger.resources.UiFactory.scrollPane();
        listSynonyms = org.jphototagger.resources.UiFactory.jxList();
        buttonRemoveSynonym = org.jphototagger.resources.UiFactory.button();
        buttonChangeSynonym = org.jphototagger.resources.UiFactory.button();
        labelInfoAddSynonym = org.jphototagger.resources.UiFactory.label();

        popupMenuWords.setName("popupMenuWords"); // NOI18N

        menuItemChangeWord.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        menuItemChangeWord.setText(Bundle.getString(getClass(), "SynonymsPanel.menuItemChangeWord.text")); // NOI18N
        menuItemChangeWord.setName("menuItemChangeWord"); // NOI18N
        menuItemChangeWord.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemChangeWordActionPerformed(evt);
            }
        });
        popupMenuWords.add(menuItemChangeWord);

        menuItemRemoveWord.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemRemoveWord.setText(Bundle.getString(getClass(), "SynonymsPanel.menuItemRemoveWord.text")); // NOI18N
        menuItemRemoveWord.setName("menuItemRemoveWord"); // NOI18N
        menuItemRemoveWord.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRemoveWordActionPerformed(evt);
            }
        });
        popupMenuWords.add(menuItemRemoveWord);

        popupMenuSynonyms.setName("popupMenuSynonyms"); // NOI18N

        menuItemChangeSynonym.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        menuItemChangeSynonym.setText(Bundle.getString(getClass(), "SynonymsPanel.menuItemChangeSynonym.text")); // NOI18N
        menuItemChangeSynonym.setName("menuItemChangeSynonym"); // NOI18N
        menuItemChangeSynonym.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemChangeSynonymActionPerformed(evt);
            }
        });
        popupMenuSynonyms.add(menuItemChangeSynonym);

        menuItemRemoveSynonym.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemRemoveSynonym.setText(Bundle.getString(getClass(), "SynonymsPanel.menuItemRemoveSynonym.text")); // NOI18N
        menuItemRemoveSynonym.setName("menuItemRemoveSynonym"); // NOI18N
        menuItemRemoveSynonym.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemRemoveSynonymActionPerformed(evt);
            }
        });
        popupMenuSynonyms.add(menuItemRemoveSynonym);

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridLayout(1, 2, UiFactory.scale(10), 0));

        panelWords.setName("panelWords"); // NOI18N
        panelWords.setLayout(new java.awt.GridBagLayout());

        panelEditInputWord.setName("panelEditInputWord"); // NOI18N
        panelEditInputWord.setLayout(new java.awt.GridBagLayout());

        labelTextAreaWord.setLabelFor(textAreaWords);
        labelTextAreaWord.setText(Bundle.getString(getClass(), "SynonymsPanel.labelTextAreaWord.text")); // NOI18N
        labelTextAreaWord.setName("labelTextAreaWord"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelEditInputWord.add(labelTextAreaWord, gridBagConstraints);

        scrollPaneTextAreaWords.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneTextAreaWords.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPaneTextAreaWords.setName("scrollPaneTextAreaWords"); // NOI18N

        textAreaWords.setColumns(20);
        textAreaWords.setRows(1);
        textAreaWords.setName("JPhotoTagger text area for a word with a synonym"); // NOI18N
        textAreaWords.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textAreaWordsKeyPressed(evt);
            }
        });
        scrollPaneTextAreaWords.setViewportView(textAreaWords);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelEditInputWord.add(scrollPaneTextAreaWords, gridBagConstraints);

        buttonAddWord.setText("+"); // NOI18N
        buttonAddWord.setToolTipText(Bundle.getString(getClass(), "SynonymsPanel.buttonAddWord.toolTipText")); // NOI18N
        buttonAddWord.setEnabled(false);
        buttonAddWord.setMargin(org.jphototagger.resources.UiFactory.insets(0, 2, 0, 2));
        buttonAddWord.setName("buttonAddWord"); // NOI18N
        buttonAddWord.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddWordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 3, 0, 0);
        panelEditInputWord.add(buttonAddWord, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelWords.add(panelEditInputWord, gridBagConstraints);

        panelAddedWords.setName("panelAddedWords"); // NOI18N
        panelAddedWords.setLayout(new java.awt.GridBagLayout());

        labelListWords.setLabelFor(listWords);
        labelListWords.setText(Bundle.getString(getClass(), "SynonymsPanel.labelListWords.text")); // NOI18N
        labelListWords.setName("labelListWords"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelAddedWords.add(labelListWords, gridBagConstraints);

        scrollPaneListWords.setName("scrollPaneListWords"); // NOI18N

        listWords.setModel(modelWords);
        listWords.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listWords.setComponentPopupMenu(popupMenuWords);
        listWords.setName("listWords"); // NOI18N
        listWords.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listWordsMouseClicked(evt);
            }
        });
        listWords.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listWordsKeyPressed(evt);
            }
        });
        scrollPaneListWords.setViewportView(listWords);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelAddedWords.add(scrollPaneListWords, gridBagConstraints);

        buttonRemoveWord.setText("-"); // NOI18N
        buttonRemoveWord.setToolTipText(Bundle.getString(getClass(), "SynonymsPanel.buttonRemoveWord.toolTipText")); // NOI18N
        buttonRemoveWord.setEnabled(false);
        buttonRemoveWord.setMargin(org.jphototagger.resources.UiFactory.insets(0, 2, 0, 2));
        buttonRemoveWord.setName("buttonRemoveWord"); // NOI18N
        buttonRemoveWord.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveWordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 3, 0, 0);
        panelAddedWords.add(buttonRemoveWord, gridBagConstraints);

        buttonChangeWord.setText("C"); // NOI18N
        buttonChangeWord.setToolTipText(Bundle.getString(getClass(), "SynonymsPanel.buttonChangeWord.toolTipText")); // NOI18N
        buttonChangeWord.setEnabled(false);
        buttonChangeWord.setMargin(org.jphototagger.resources.UiFactory.insets(0, 2, 0, 2));
        buttonChangeWord.setName("buttonChangeWord"); // NOI18N
        buttonChangeWord.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChangeWordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 3, 0, 0);
        panelAddedWords.add(buttonChangeWord, gridBagConstraints);

        buttonAddAllKeywords.setText(Bundle.getString(getClass(), "SynonymsPanel.buttonAddAllKeywords.text")); // NOI18N
        buttonAddAllKeywords.setToolTipText(Bundle.getString(getClass(), "SynonymsPanel.buttonAddAllKeywords.toolTipText")); // NOI18N
        buttonAddAllKeywords.setName("buttonAddAllKeywords"); // NOI18N
        buttonAddAllKeywords.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddAllKeywordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelAddedWords.add(buttonAddAllKeywords, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelWords.add(panelAddedWords, gridBagConstraints);

        add(panelWords);

        panelSynonyms.setName("panelSynonyms"); // NOI18N
        panelSynonyms.setLayout(new java.awt.GridBagLayout());

        panelEditInputSynonyms.setName("panelEditInputSynonyms"); // NOI18N
        panelEditInputSynonyms.setLayout(new java.awt.GridBagLayout());

        labelTextFieldSynonym.setLabelFor(textFieldSynonyms);
        labelTextFieldSynonym.setText(Bundle.getString(getClass(), "SynonymsPanel.labelTextFieldSynonym.text")); // NOI18N
        labelTextFieldSynonym.setName("labelTextFieldSynonym"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelEditInputSynonyms.add(labelTextFieldSynonym, gridBagConstraints);

        textFieldSynonyms.setName("JPhotoTagger text area for a synonym of a word"); // NOI18N
        textFieldSynonyms.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldSynonymsKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelEditInputSynonyms.add(textFieldSynonyms, gridBagConstraints);

        buttonAddSynonym.setText("+"); // NOI18N
        buttonAddSynonym.setToolTipText(Bundle.getString(getClass(), "SynonymsPanel.buttonAddSynonym.toolTipText")); // NOI18N
        buttonAddSynonym.setEnabled(false);
        buttonAddSynonym.setMargin(org.jphototagger.resources.UiFactory.insets(0, 2, 0, 2));
        buttonAddSynonym.setName("buttonAddSynonym"); // NOI18N
        buttonAddSynonym.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddSynonymActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 3, 0, 0);
        panelEditInputSynonyms.add(buttonAddSynonym, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        panelSynonyms.add(panelEditInputSynonyms, gridBagConstraints);

        panelAddedSynonyms.setName("panelAddedSynonyms"); // NOI18N
        panelAddedSynonyms.setLayout(new java.awt.GridBagLayout());

        labelListSynonyms.setLabelFor(listSynonyms);
        labelListSynonyms.setText(Bundle.getString(getClass(), "SynonymsPanel.labelListSynonyms.text")); // NOI18N
        labelListSynonyms.setName("labelListSynonyms"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelAddedSynonyms.add(labelListSynonyms, gridBagConstraints);

        scrollPaneListSynonyms.setName("scrollPaneListSynonyms"); // NOI18N

        listSynonyms.setModel(modelSynonyms);
        listSynonyms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSynonyms.setComponentPopupMenu(popupMenuSynonyms);
        listSynonyms.setName("listSynonyms"); // NOI18N
        listSynonyms.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listSynonymsMouseClicked(evt);
            }
        });
        listSynonyms.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listSynonymsKeyPressed(evt);
            }
        });
        scrollPaneListSynonyms.setViewportView(listSynonyms);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelAddedSynonyms.add(scrollPaneListSynonyms, gridBagConstraints);

        buttonRemoveSynonym.setText("-"); // NOI18N
        buttonRemoveSynonym.setToolTipText(Bundle.getString(getClass(), "SynonymsPanel.buttonRemoveSynonym.toolTipText")); // NOI18N
        buttonRemoveSynonym.setEnabled(false);
        buttonRemoveSynonym.setMargin(org.jphototagger.resources.UiFactory.insets(0, 2, 0, 2));
        buttonRemoveSynonym.setName("buttonRemoveSynonym"); // NOI18N
        buttonRemoveSynonym.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveSynonymActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 3, 0, 0);
        panelAddedSynonyms.add(buttonRemoveSynonym, gridBagConstraints);

        buttonChangeSynonym.setText("C"); // NOI18N
        buttonChangeSynonym.setToolTipText(Bundle.getString(getClass(), "SynonymsPanel.buttonChangeSynonym.toolTipText")); // NOI18N
        buttonChangeSynonym.setEnabled(false);
        buttonChangeSynonym.setMargin(org.jphototagger.resources.UiFactory.insets(0, 2, 0, 2));
        buttonChangeSynonym.setName("buttonChangeSynonym"); // NOI18N
        buttonChangeSynonym.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChangeSynonymActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 3, 0, 0);
        panelAddedSynonyms.add(buttonChangeSynonym, gridBagConstraints);

        labelInfoAddSynonym.setText(Bundle.getString(getClass(), "SynonymsPanel.labelInfoAddSynonym.text")); // NOI18N
        labelInfoAddSynonym.setName("labelInfoAddSynonym"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelAddedSynonyms.add(labelInfoAddSynonym, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelSynonyms.add(panelAddedSynonyms, gridBagConstraints);

        add(panelSynonyms);
    }//GEN-END:initComponents

    private void buttonAddWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddWordActionPerformed
        addWord();
    }//GEN-LAST:event_buttonAddWordActionPerformed

    private void buttonRemoveWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveWordActionPerformed
        removeWord();
    }//GEN-LAST:event_buttonRemoveWordActionPerformed

    private void buttonChangeWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChangeWordActionPerformed
        changeWord();
    }//GEN-LAST:event_buttonChangeWordActionPerformed

    private void buttonAddSynonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddSynonymActionPerformed
        addSynonym();
    }//GEN-LAST:event_buttonAddSynonymActionPerformed

    private void buttonRemoveSynonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveSynonymActionPerformed
        removeSynonym();
    }//GEN-LAST:event_buttonRemoveSynonymActionPerformed

    private void buttonChangeSynonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChangeSynonymActionPerformed
        changeSynonym();
    }//GEN-LAST:event_buttonChangeSynonymActionPerformed

    private void textAreaWordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaWordsKeyPressed
        handleTextAreaWordsKeyPressed(evt);
    }//GEN-LAST:event_textAreaWordsKeyPressed

    private void textFieldSynonymsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldSynonymsKeyPressed
        handleTextFieldSynonymsKeyPressed(evt);
    }//GEN-LAST:event_textFieldSynonymsKeyPressed

    private void listSynonymsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listSynonymsKeyPressed
        handleListSynonymsKeyPressed(evt);
    }//GEN-LAST:event_listSynonymsKeyPressed

    private void listWordsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listWordsMouseClicked
        handleListWordsMouseClicked(evt);
    }//GEN-LAST:event_listWordsMouseClicked

    private void listSynonymsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listSynonymsMouseClicked
        handleListSynonymsMouseClicked(evt);
    }//GEN-LAST:event_listSynonymsMouseClicked

    private void listWordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listWordsKeyPressed
        handleListWordsKeyPressed(evt);
    }//GEN-LAST:event_listWordsKeyPressed

    private void buttonAddAllKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddAllKeywordsActionPerformed
        addAllKeywords();
    }//GEN-LAST:event_buttonAddAllKeywordsActionPerformed

    private void menuItemChangeWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemChangeWordActionPerformed
        changeWord();
    }//GEN-LAST:event_menuItemChangeWordActionPerformed

    private void menuItemRemoveWordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRemoveWordActionPerformed
        removeWord();
    }//GEN-LAST:event_menuItemRemoveWordActionPerformed

    private void menuItemRemoveSynonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemRemoveSynonymActionPerformed
        removeSynonym();
    }//GEN-LAST:event_menuItemRemoveSynonymActionPerformed

    private void menuItemChangeSynonymActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemChangeSynonymActionPerformed
        changeSynonym();
    }//GEN-LAST:event_menuItemChangeSynonymActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddAllKeywords;
    private javax.swing.JButton buttonAddSynonym;
    private javax.swing.JButton buttonAddWord;
    private javax.swing.JButton buttonChangeSynonym;
    private javax.swing.JButton buttonChangeWord;
    private javax.swing.JButton buttonRemoveSynonym;
    private javax.swing.JButton buttonRemoveWord;
    private javax.swing.JLabel labelInfoAddSynonym;
    private javax.swing.JLabel labelListSynonyms;
    private javax.swing.JLabel labelListWords;
    private javax.swing.JLabel labelTextAreaWord;
    private javax.swing.JLabel labelTextFieldSynonym;
    private org.jdesktop.swingx.JXList listSynonyms;
    private org.jdesktop.swingx.JXList listWords;
    private javax.swing.JMenuItem menuItemChangeSynonym;
    private javax.swing.JMenuItem menuItemChangeWord;
    private javax.swing.JMenuItem menuItemRemoveSynonym;
    private javax.swing.JMenuItem menuItemRemoveWord;
    private javax.swing.JPanel panelAddedSynonyms;
    private javax.swing.JPanel panelAddedWords;
    private javax.swing.JPanel panelEditInputSynonyms;
    private javax.swing.JPanel panelEditInputWord;
    private javax.swing.JPanel panelSynonyms;
    private javax.swing.JPanel panelWords;
    private javax.swing.JPopupMenu popupMenuSynonyms;
    private javax.swing.JPopupMenu popupMenuWords;
    private javax.swing.JScrollPane scrollPaneListSynonyms;
    private javax.swing.JScrollPane scrollPaneListWords;
    private javax.swing.JScrollPane scrollPaneTextAreaWords;
    private javax.swing.JTextArea textAreaWords;
    private javax.swing.JTextField textFieldSynonyms;
    // End of variables declaration//GEN-END:variables
}
