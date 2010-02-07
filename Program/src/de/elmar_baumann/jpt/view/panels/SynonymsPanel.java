/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.selections.AutoCompleteDataOfColumn;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.model.ListModelSynonyms;
import de.elmar_baumann.lib.componentutil.Autocomplete;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.event.util.MouseEventUtil;
import java.awt.Container;
import java.awt.event.KeyEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-07
 */
public class SynonymsPanel extends javax.swing.JPanel implements ListSelectionListener, DocumentListener {

    private static final long              serialVersionUID  = 8012368048974373352L;
    private              ListModelSynonyms modelWords        = new ListModelSynonyms(ListModelSynonyms.Role.WORDS);
    private              ListModelSynonyms modelSynonyms     = new ListModelSynonyms(ListModelSynonyms.Role.SYNONYMS);
    private              Autocomplete      autocomplete      = new Autocomplete();;
    private              boolean           listenToDocuments = true;

    public SynonymsPanel() {
        initComponents();
        postInitComponents();
    }
    
    private void postInitComponents() {
        listWords.addListSelectionListener(this);
        listSynonyms.addListSelectionListener(this);
        textAreaWords.getDocument().addDocumentListener(this);
        textFieldSynonyms.getDocument().addDocumentListener(this);
        MnemonicUtil.setMnemonics((Container) this);
        autocomplete.decorate(
                textAreaWords,
                AutoCompleteDataOfColumn.INSTANCE.get(ColumnXmpDcSubjectsSubject.INSTANCE).get());
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            if (e.getSource() == listWords) {
                setSynonyms();
            }
            setEnabledButtons();
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

    private void setEnabledButtons() {
        setEnabledAddButtons();
        setEnabledRemoveButtons();
        setEnabledChangeButtons();
    }

    private void setEnabledAddButtons() {
        boolean wordExists    = !textAreaWords.getText().trim().isEmpty();
        boolean synonymExists = !textFieldSynonyms.getText().trim().isEmpty();
        boolean wordSelected  = listWords.getSelectedValue() != null;

        buttonAddWord.setEnabled(wordExists);
        buttonAddSynonym.setEnabled(synonymExists && wordSelected);
    }

    private void setEnabledRemoveButtons() {
        boolean wordSel    = listWords.getSelectedValue() != null;
        boolean synonymSel = listSynonyms.getSelectedValue() != null;

        buttonRemoveWord.setEnabled(wordSel);
        buttonRemoveSynonym.setEnabled(synonymSel);
    }

    private void setEnabledChangeButtons() {
        boolean wordSel    = listWords.getSelectedValue() != null;
        boolean synonymSel = listSynonyms.getSelectedValue() != null;

        buttonChangeWord.setEnabled(wordSel);
        buttonChangeSynonym.setEnabled(synonymSel);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        if (!listenToDocuments) return;
        setEnabledButtons();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        if (!listenToDocuments) return;
        setEnabledButtons();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if (!listenToDocuments) return;
        setEnabledButtons();
    }

    private void addWord() {
        String word = textAreaWords.getText().trim();
        if (!word.isEmpty()) {
            modelWords.addWord(word);
            listenToDocuments = false;
            textAreaWords.setText("");
            listenToDocuments = true;
            setEnabledButtons();
            textAreaWords.requestFocusInWindow();
        }
    }

    private void removeWord() {
        Object o = listWords.getSelectedValue();
        if (o instanceof String) {
            String word = (String) o;
            if (MessageDisplayer.confirmYesNo(this, "SynonymsPanel.Confirm.RemoveWord", word)) {
                modelWords.removeWord(word);
                modelSynonyms.removeAllElements();
            }
        }
    }

    private void changeWord() {
        Object o = listWords.getSelectedValue();
        if (o instanceof String) {
            String oldWord = (String)o;
            String newWord = MessageDisplayer.input("SynonymsPanel.Info.ChangeWord", oldWord, "SynonymsPanel.Pos.ChangeWord", oldWord);
            if (newWord != null && !newWord.equals(oldWord)) {
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
            setEnabledButtons();
            textFieldSynonyms.requestFocusInWindow();
        }
    }

    private void removeSynonym() {
        Object o = listSynonyms.getSelectedValue();
        if (o instanceof String) {
            String synonym = (String) o;
            if (MessageDisplayer.confirmYesNo(this, "SynonymsPanel.Confirm.RemoveSynonym", synonym)) {
                modelSynonyms.removeSynonym(synonym);
            }
        }
    }

    private void changeSynonym() {
        Object o = listSynonyms.getSelectedValue();
        if (o instanceof String) {
            String oldSynonym = (String)o;
            String newSynonym = MessageDisplayer.input("SynonymsPanel.Info.ChangeSynonym", oldSynonym, "SynonymsPanel.Pos.ChangeSynonym", oldSynonym);
            if (newSynonym != null && !newSynonym.equals(oldSynonym)) {
                modelSynonyms.changeSynonym(oldSynonym, newSynonym);
            }
        }
    }

    private void addAllKeywords() {
        for (String word : DatabaseImageFiles.INSTANCE.getAllDcSubjects()) {
            modelWords.addWord(word);
        }
        MessageDisplayer.information(this, "SynonymsPanel.Info.AddAllKeywords");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelTextAreaWord = new javax.swing.JLabel();
        scrollPaneTextAreaWords = new javax.swing.JScrollPane();
        textAreaWords = new javax.swing.JTextArea();
        labelListWords = new javax.swing.JLabel();
        scrollPaneListWords = new javax.swing.JScrollPane();
        listWords = new javax.swing.JList();
        buttonAddWord = new javax.swing.JButton();
        buttonRemoveWord = new javax.swing.JButton();
        buttonChangeWord = new javax.swing.JButton();
        labelTextFieldSynonym = new javax.swing.JLabel();
        textFieldSynonyms = new javax.swing.JTextField();
        labelListSynonyms = new javax.swing.JLabel();
        scrollPaneListSynonyms = new javax.swing.JScrollPane();
        listSynonyms = new javax.swing.JList();
        buttonAddSynonym = new javax.swing.JButton();
        buttonRemoveSynonym = new javax.swing.JButton();
        buttonChangeSynonym = new javax.swing.JButton();
        buttonAddAllKeywords = new javax.swing.JButton();

        labelTextAreaWord.setLabelFor(textAreaWords);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        labelTextAreaWord.setText(bundle.getString("SynonymsPanel.labelTextAreaWord.text")); // NOI18N

        scrollPaneTextAreaWords.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPaneTextAreaWords.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        textAreaWords.setColumns(20);
        textAreaWords.setRows(1);
        textAreaWords.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textAreaWordsKeyPressed(evt);
            }
        });
        scrollPaneTextAreaWords.setViewportView(textAreaWords);

        labelListWords.setLabelFor(listWords);
        labelListWords.setText(bundle.getString("SynonymsPanel.labelListWords.text")); // NOI18N

        listWords.setModel(modelWords);
        listWords.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listWords.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listWordsMouseClicked(evt);
            }
        });
        listWords.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listWordsKeyPressed(evt);
            }
        });
        scrollPaneListWords.setViewportView(listWords);

        buttonAddWord.setText(bundle.getString("SynonymsPanel.buttonAddWord.text")); // NOI18N
        buttonAddWord.setToolTipText(bundle.getString("SynonymsPanel.buttonAddWord.toolTipText")); // NOI18N
        buttonAddWord.setEnabled(false);
        buttonAddWord.setMargin(new java.awt.Insets(0, 2, 0, 2));
        buttonAddWord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddWordActionPerformed(evt);
            }
        });

        buttonRemoveWord.setText(bundle.getString("SynonymsPanel.buttonRemoveWord.text")); // NOI18N
        buttonRemoveWord.setToolTipText(bundle.getString("SynonymsPanel.buttonRemoveWord.toolTipText")); // NOI18N
        buttonRemoveWord.setEnabled(false);
        buttonRemoveWord.setMargin(new java.awt.Insets(0, 2, 0, 2));
        buttonRemoveWord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveWordActionPerformed(evt);
            }
        });

        buttonChangeWord.setText(bundle.getString("SynonymsPanel.buttonChangeWord.text")); // NOI18N
        buttonChangeWord.setToolTipText(bundle.getString("SynonymsPanel.buttonChangeWord.toolTipText")); // NOI18N
        buttonChangeWord.setEnabled(false);
        buttonChangeWord.setMargin(new java.awt.Insets(0, 2, 0, 2));
        buttonChangeWord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChangeWordActionPerformed(evt);
            }
        });

        labelTextFieldSynonym.setLabelFor(textFieldSynonyms);
        labelTextFieldSynonym.setText(bundle.getString("SynonymsPanel.labelTextFieldSynonym.text")); // NOI18N

        textFieldSynonyms.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldSynonymsKeyPressed(evt);
            }
        });

        labelListSynonyms.setLabelFor(listSynonyms);
        labelListSynonyms.setText(bundle.getString("SynonymsPanel.labelListSynonyms.text")); // NOI18N

        listSynonyms.setModel(modelSynonyms);
        listSynonyms.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listSynonyms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listSynonymsMouseClicked(evt);
            }
        });
        listSynonyms.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listSynonymsKeyPressed(evt);
            }
        });
        scrollPaneListSynonyms.setViewportView(listSynonyms);

        buttonAddSynonym.setText(bundle.getString("SynonymsPanel.buttonAddSynonym.text")); // NOI18N
        buttonAddSynonym.setToolTipText(bundle.getString("SynonymsPanel.buttonAddSynonym.toolTipText")); // NOI18N
        buttonAddSynonym.setEnabled(false);
        buttonAddSynonym.setMargin(new java.awt.Insets(0, 2, 0, 2));
        buttonAddSynonym.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddSynonymActionPerformed(evt);
            }
        });

        buttonRemoveSynonym.setText(bundle.getString("SynonymsPanel.buttonRemoveSynonym.text")); // NOI18N
        buttonRemoveSynonym.setToolTipText(bundle.getString("SynonymsPanel.buttonRemoveSynonym.toolTipText")); // NOI18N
        buttonRemoveSynonym.setEnabled(false);
        buttonRemoveSynonym.setMargin(new java.awt.Insets(0, 2, 0, 2));
        buttonRemoveSynonym.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveSynonymActionPerformed(evt);
            }
        });

        buttonChangeSynonym.setText(bundle.getString("SynonymsPanel.buttonChangeSynonym.text")); // NOI18N
        buttonChangeSynonym.setToolTipText(bundle.getString("SynonymsPanel.buttonChangeSynonym.toolTipText")); // NOI18N
        buttonChangeSynonym.setEnabled(false);
        buttonChangeSynonym.setMargin(new java.awt.Insets(0, 2, 0, 2));
        buttonChangeSynonym.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChangeSynonymActionPerformed(evt);
            }
        });

        buttonAddAllKeywords.setText(bundle.getString("SynonymsPanel.buttonAddAllKeywords.text")); // NOI18N
        buttonAddAllKeywords.setToolTipText(bundle.getString("SynonymsPanel.buttonAddAllKeywords.toolTipText")); // NOI18N
        buttonAddAllKeywords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddAllKeywordsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPaneListWords, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(scrollPaneTextAreaWords, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(labelListWords)
                            .addComponent(buttonAddAllKeywords, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(buttonAddWord)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(buttonRemoveWord)
                                .addComponent(buttonChangeWord)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelTextAreaWord)
                        .addGap(124, 124, 124)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPaneListSynonyms, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(textFieldSynonyms, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 208, Short.MAX_VALUE)
                            .addComponent(labelListSynonyms))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(buttonAddSynonym)
                            .addComponent(buttonChangeSynonym)
                            .addComponent(buttonRemoveSynonym))
                        .addGap(14, 14, 14))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labelTextFieldSynonym)
                        .addGap(137, 137, 137))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonAddSynonym, buttonAddWord, buttonChangeSynonym, buttonChangeWord, buttonRemoveSynonym, buttonRemoveWord});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelTextAreaWord)
                    .addComponent(labelTextFieldSynonym))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scrollPaneTextAreaWords, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelListWords))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(buttonAddWord)
                            .addComponent(textFieldSynonyms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelListSynonyms))
                    .addComponent(buttonAddSynonym))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonRemoveSynonym)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChangeSynonym))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonRemoveWord)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChangeWord))
                    .addComponent(scrollPaneListWords, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                    .addComponent(scrollPaneListSynonyms, javax.swing.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonAddAllKeywords)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonAddSynonym, buttonAddWord, buttonChangeSynonym, buttonChangeWord, buttonRemoveSynonym, buttonRemoveWord});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {scrollPaneTextAreaWords, textFieldSynonyms});

    }// </editor-fold>//GEN-END:initComponents

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
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            addWord();
        }
    }//GEN-LAST:event_textAreaWordsKeyPressed

    private void textFieldSynonymsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldSynonymsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER && listWords.getSelectedValue() != null) {
            addSynonym();
        }
    }//GEN-LAST:event_textFieldSynonymsKeyPressed

    private void listSynonymsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listSynonymsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeSynonym();
        } else if (evt.getKeyCode() == KeyEvent.VK_F2) {
            changeSynonym();
        }
    }//GEN-LAST:event_listSynonymsKeyPressed

    private void listWordsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listWordsMouseClicked
        if (MouseEventUtil.isDoubleClick(evt)) {
            changeWord();
        }
    }//GEN-LAST:event_listWordsMouseClicked

    private void listSynonymsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listSynonymsMouseClicked
        if (MouseEventUtil.isDoubleClick(evt)) {
            changeSynonym();
        }
    }//GEN-LAST:event_listSynonymsMouseClicked

    private void listWordsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listWordsKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            removeWord();
        } else if (evt.getKeyCode() == KeyEvent.VK_F2) {
            changeWord();
        }
    }//GEN-LAST:event_listWordsKeyPressed

    private void buttonAddAllKeywordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddAllKeywordsActionPerformed
        addAllKeywords();
    }//GEN-LAST:event_buttonAddAllKeywordsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddAllKeywords;
    private javax.swing.JButton buttonAddSynonym;
    private javax.swing.JButton buttonAddWord;
    private javax.swing.JButton buttonChangeSynonym;
    private javax.swing.JButton buttonChangeWord;
    private javax.swing.JButton buttonRemoveSynonym;
    private javax.swing.JButton buttonRemoveWord;
    private javax.swing.JLabel labelListSynonyms;
    private javax.swing.JLabel labelListWords;
    private javax.swing.JLabel labelTextAreaWord;
    private javax.swing.JLabel labelTextFieldSynonym;
    private javax.swing.JList listSynonyms;
    private javax.swing.JList listWords;
    private javax.swing.JScrollPane scrollPaneListSynonyms;
    private javax.swing.JScrollPane scrollPaneListWords;
    private javax.swing.JScrollPane scrollPaneTextAreaWords;
    private javax.swing.JTextArea textAreaWords;
    private javax.swing.JTextField textFieldSynonyms;
    // End of variables declaration//GEN-END:variables
}
