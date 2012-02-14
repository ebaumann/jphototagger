package org.jphototagger.program.module.wordsets;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.applifecycle.AppWillExitEvent;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.event.listener.ListenerSupport;
import org.jphototagger.domain.repository.WordsetsRepository;
import org.jphototagger.domain.repository.event.wordsets.WordsetUpdatedEvent;
import org.jphototagger.domain.wordsets.Wordset;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;

/**
 * @author Elmar Baumann
 */
public class WordsetsPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;
    private static final Color LABEL_EDITABLE_BACKGROUND_COLOR = Color.WHITE;
    private static final Color LABEL_EDITABLE_FOREGROUND_COLOR = Color.BLACK;
    private static final Border LABEL_BORDER = BorderFactory.createLineBorder(Color.BLACK);
    private static final int AUTOMATIC_WORDSET_MAX_WORDCOUNT = 9;
    private final String[] automaticAddedWordsRingbuffer = new String[AUTOMATIC_WORDSET_MAX_WORDCOUNT];
    private final List<JLabel> wordLabels = new ArrayList<JLabel>();
    private final ListenerSupport<WordsetsPanelListener> listenerSupport = new ListenerSupport<WordsetsPanelListener>();
    private final Wordset automaticWordset = createAutomaticWordset();
    private Wordset selectedWordset;
    private String persistenceKeyPrefix;
    private boolean editable = true;

    public WordsetsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initAutomaticAddedWordsRingbuffer();
        setWordsOfSelectedWordsetName();
        listen();
    }

    private void initAutomaticAddedWordsRingbuffer() {
        for (int i = 0; i < AUTOMATIC_WORDSET_MAX_WORDCOUNT; i++) {
            automaticAddedWordsRingbuffer[i] = "";
        }
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    public void setPersistenceKeyPrefix(String prefix) {
        if (prefix == null) {
            throw new NullPointerException("prefix == null");
        }
        this.persistenceKeyPrefix = prefix;
        addPersistedWordsToWordset(automaticWordset);
        setAutomaticAddedWordsRingbuffer();
        initSelectComboBoxItem();
    }

    private void setAutomaticAddedWordsRingbuffer() {
        List<String> words = automaticWordset.getWords();
        int size = words.size();
        for (int i = 0; i < AUTOMATIC_WORDSET_MAX_WORDCOUNT; i++) {
            automaticAddedWordsRingbuffer[i] = i < size ? words.get(i) : "";
        }
    }

    private void initSelectComboBoxItem() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null && persistenceKeyPrefix != null) {
            String key = createComboBoxSelectedIndexPersistenceKey();
            if (prefs.containsKey(key)) {
                int index = prefs.getInt(key);
                if (index >= 0 && index < comboBoxWordsetNames.getItemCount()) {
                    comboBoxWordsetNames.setSelectedIndex(index);
                }
            }
        }

    }

    @EventSubscriber(eventClass = WordsetUpdatedEvent.class)
    public void wordsetUpdated(WordsetUpdatedEvent evt) {
        Wordset newWordset = evt.getNewWordset();
        long newWordsetId = newWordset.getId();
        if (selectedWordset != null && selectedWordset.getId() == newWordsetId) {
            setWordsOfSelectedWordsetName();
        }
    }

    private void setWordsOfSelectedWordsetName() {
        panelWords.removeAll();
        wordLabels.clear();
        WordsetsRepository repository = Lookup.getDefault().lookup(WordsetsRepository.class);
        if (repository != null) {
            String wordsetName = (String) comboBoxWordsetNames.getSelectedItem();
            if (wordsetName != null) {
                selectedWordset = isAutomaticWordsetSelected()
                        ? automaticWordset
                        : repository.find(wordsetName);
                if (selectedWordset != null) {
                    setWordsOfSelectedWordset();
                    repaintThisAndParent();
                }
            }
        }
    }

    private void setWordsOfSelectedWordset() {
        List<String> words = selectedWordset.getWords();
        for (String word : words) {
            addWordLabel(word);
        }
    }

    private void addWordLabel(String word) {
        JLabel labelWord = new JLabel(word);
        labelWord.setOpaque(true);
        labelWord.setForeground(LABEL_EDITABLE_FOREGROUND_COLOR);
        labelWord.setBackground(LABEL_EDITABLE_BACKGROUND_COLOR);
        labelWord.setBorder(LABEL_BORDER);
        labelWord.setToolTipText(word);
        labelWord.addMouseListener(wordClickedListener);
        wordLabels.add(labelWord);
        panelWords.add(labelWord);
    }

    private void repaintThisAndParent() {
        ComponentUtil.forceRepaint(this);
        Container parent = getParent();
        if (parent != null) {
            ComponentUtil.forceRepaint(parent);
        }
    }

    public void addToAutomaticWordset(String word) {
        if (word == null) {
            throw new NullPointerException("word == null");
        }
        if (editable && StringUtil.hasContent(word) && !automaticWordset.containsWord(word)) {
            automaticWordset.addToWords(word);
            String oldestWord = getOldestAutomaticWordsetWord();
            addToAutomaticWordsetRingBuffer(word);
            if (!oldestWord.isEmpty()) {
                automaticWordset.removeFromWords(oldestWord);
            }
            setWordsOfSelectedWordsetName();
        }
    }

    private void addToAutomaticWordsetRingBuffer(String word) {
        for (int i = AUTOMATIC_WORDSET_MAX_WORDCOUNT - 1; i > 0; i--) {
            automaticAddedWordsRingbuffer[i] = automaticAddedWordsRingbuffer[i - 1];
        }
        automaticAddedWordsRingbuffer[0] = word;
    }

    private String getOldestAutomaticWordsetWord() {
        return automaticAddedWordsRingbuffer[AUTOMATIC_WORDSET_MAX_WORDCOUNT - 1];
    }

    private boolean isAutomaticWordsetSelected() {
        Object selectedItem = comboBoxWordsetNames.getSelectedItem();
        return WordsetPreferences.AUTOMATIC_WORDSET_NAME.equals(selectedItem);
    }

    private Wordset createAutomaticWordset() {
        Wordset wordset = new Wordset(WordsetPreferences.AUTOMATIC_WORDSET_NAME);
        addPersistedWordsToWordset(wordset);
        return wordset;
    }

    private void addPersistedWordsToWordset(Wordset wordset) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null) {
            for (int index = 0; index < AUTOMATIC_WORDSET_MAX_WORDCOUNT; index++) {
                String key = createAutomaticWordsetPersistenceKey(index);
                if (prefs.containsKey(key)) {
                    String word = prefs.getString(key);
                    wordset.addToWords(word);
                }
            }
        }
    }

    @EventSubscriber(eventClass = AppWillExitEvent.class)
    public void appWillExit(AppWillExitEvent evt) {
        persistComboBoxSelectedIndex();
        persistAutomaticWordsetWords();
    }

    private void persistAutomaticWordsetWords() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null) {
            int persistedWordCount = 0;
            List<String> words = automaticWordset.getWords();
            for (String word : words) {
                String key = createAutomaticWordsetPersistenceKey(persistedWordCount);
                prefs.setString(key, word);
                persistedWordCount++;
            }
            for (int index = persistedWordCount; index < AUTOMATIC_WORDSET_MAX_WORDCOUNT; index++) {
                String key = createAutomaticWordsetPersistenceKey(index);
                prefs.removeKey(key);
            }
        }
    }

    private void persistComboBoxSelectedIndex() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null && persistenceKeyPrefix != null) {
            String key = createComboBoxSelectedIndexPersistenceKey();
            prefs.setInt(key, comboBoxWordsetNames.getSelectedIndex());
        }
    }

    private String createAutomaticWordsetPersistenceKey(int index) {
        return persistenceKeyPrefix == null ? "Default" : persistenceKeyPrefix
                + '.'
                + WordsetPreferences.KEY_PREFIX_AUTOMATIC_WORDSET_WORD
                + Integer.valueOf(index);
    }

    private String createComboBoxSelectedIndexPersistenceKey() {
        return persistenceKeyPrefix + ".SelectedWordsetIndex";
    }

    private void addWordset() {
        EditWordsetDialog dialog = new EditWordsetDialog();
        dialog.setVisible(true);
    }

    private void removeSelectedWordset() {
        String wordsetName = (String) comboBoxWordsetNames.getSelectedItem();
        if (wordsetName != null && !WordsetPreferences.isAutomaticWordsetName(wordsetName)
                && MessageDisplayer.confirmYesNo(this,
                Bundle.getString(WordsetsPanel.class, "WordsetPreferences.Confirm.Remove", wordsetName))) {
            WordsetsRepository wordsetsRepository = Lookup.getDefault().lookup(WordsetsRepository.class);
            if (!wordsetsRepository.remove(wordsetName)) {
                MessageDisplayer.error(this, Bundle.getString(WordsetsPanel.class, "WordsetsPanel.Error.Remove", wordsetName));
            }
        }
    }

    private void editSelectedWordset() {
        String wordsetName = (String) comboBoxWordsetNames.getSelectedItem();
        if (wordsetName != null && !WordsetPreferences.isAutomaticWordsetName(wordsetName)) {
            WordsetsRepository wordsetsRepository = Lookup.getDefault().lookup(WordsetsRepository.class);
            Wordset wordset = wordsetsRepository.find(wordsetName);
            if (wordset != null) {
                EditWordsetDialog dialog = new EditWordsetDialog(wordset);
                dialog.setVisible(true);
            } else {
                MessageDisplayer.error(this, Bundle.getString(WordsetsPanel.class, "WordsetsPanel.Error.Edit", wordsetName));
            }
        }
    }

    public void addWordsetsPanelListener(WordsetsPanelListener listener) {
        listenerSupport.add(listener);
    }

    public void removeWordsetsPanelListener(WordsetsPanelListener listener) {
        listenerSupport.remove(listener);
    }

    private void notifyWordClicked(String word) {
        Set<WordsetsPanelListener> listeners = listenerSupport.get();
        for (WordsetsPanelListener listener : listeners) {
            listener.wordClicked(word);
        }
    }
    private final MouseListener wordClickedListener = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (editable && MouseEventUtil.isLeftClick(e)) {
                JLabel label = (JLabel) e.getSource();
                String word = label.getText();
                notifyWordClicked(word);
            }
        }
    };
    private final ListCellRenderer wordsetNamesComboBoxRenderer = new DefaultListCellRenderer() {

        private static final long serialVersionUID = 1L;
        private final String AUTOMATIC_WORDSET_NAME = Bundle.getString(WordsetsPanel.class, "WordsetsPanel.ListCellRenderer.AutomaticWordsetName");

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (WordsetPreferences.AUTOMATIC_WORDSET_NAME.equals(value)) {
                label.setText(AUTOMATIC_WORDSET_NAME);
            }
            return label;
        }
    };

    public void setEditable(boolean editable) {
        this.editable = editable;
        comboBoxWordsetNames.setEnabled(editable);
        setButtonsEnabled();
        List<JLabel> labels = ComponentUtil.getAllOf(this, JLabel.class);
        for (JLabel label : labels) {
            label.setOpaque(editable);
        }
    }

    private void setButtonsEnabled() {
        boolean automaticWordsetSelected = isAutomaticWordsetSelected();
        buttonAddWordset.setEnabled(editable);
        buttonEditWordset.setEnabled(editable && !automaticWordsetSelected);
        buttonRemoveWordset.setEnabled(editable && !automaticWordsetSelected);
    }

    public boolean isEditable() {
        return editable;
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
     * content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelWordsets = new javax.swing.JPanel();
        comboBoxWordsetNames = new javax.swing.JComboBox();
        buttonRemoveWordset = new javax.swing.JButton();
        buttonAddWordset = new javax.swing.JButton();
        buttonEditWordset = new javax.swing.JButton();
        panelWords = new javax.swing.JPanel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/wordsets/Bundle"); // NOI18N
        setToolTipText(bundle.getString("WordsetsPanel.toolTipText")); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        panelWordsets.setLayout(new java.awt.GridBagLayout());

        comboBoxWordsetNames.setModel(new WordsetNamesComboBoxModel());
        comboBoxWordsetNames.setRenderer(wordsetNamesComboBoxRenderer);
        comboBoxWordsetNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxWordsetNamesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelWordsets.add(comboBoxWordsetNames, gridBagConstraints);

        buttonRemoveWordset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/module/wordsets/delete.png"))); // NOI18N
        buttonRemoveWordset.setToolTipText(bundle.getString("WordsetsPanel.buttonRemoveWordset.toolTipText")); // NOI18N
        buttonRemoveWordset.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveWordset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveWordsetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelWordsets.add(buttonRemoveWordset, gridBagConstraints);

        buttonAddWordset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/module/wordsets/add.png"))); // NOI18N
        buttonAddWordset.setToolTipText(bundle.getString("WordsetsPanel.buttonAddWordset.toolTipText")); // NOI18N
        buttonAddWordset.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonAddWordset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddWordsetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelWordsets.add(buttonAddWordset, gridBagConstraints);

        buttonEditWordset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/module/wordsets/edit.png"))); // NOI18N
        buttonEditWordset.setToolTipText(bundle.getString("WordsetsPanel.buttonEditWordset.toolTipText")); // NOI18N
        buttonEditWordset.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonEditWordset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonEditWordsetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelWordsets.add(buttonEditWordset, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(panelWordsets, gridBagConstraints);

        panelWords.setLayout(new java.awt.GridLayout(0, 3, 2, 2));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(panelWords, gridBagConstraints);
    }//GEN-END:initComponents

    private void comboBoxWordsetNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxWordsetNamesActionPerformed
        setWordsOfSelectedWordsetName();
        setButtonsEnabled();
    }//GEN-LAST:event_comboBoxWordsetNamesActionPerformed

    private void buttonAddWordsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddWordsetActionPerformed
        addWordset();
    }//GEN-LAST:event_buttonAddWordsetActionPerformed

    private void buttonRemoveWordsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveWordsetActionPerformed
        removeSelectedWordset();
    }//GEN-LAST:event_buttonRemoveWordsetActionPerformed

    private void buttonEditWordsetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonEditWordsetActionPerformed
        editSelectedWordset();
    }//GEN-LAST:event_buttonEditWordsetActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddWordset;
    private javax.swing.JButton buttonEditWordset;
    private javax.swing.JButton buttonRemoveWordset;
    private javax.swing.JComboBox comboBoxWordsetNames;
    private javax.swing.JPanel panelWords;
    private javax.swing.JPanel panelWordsets;
    // End of variables declaration//GEN-END:variables
}
