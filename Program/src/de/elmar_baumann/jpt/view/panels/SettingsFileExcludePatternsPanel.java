/*
 * JPhotoTagger tags and finds images fast
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

import de.elmar_baumann.jpt.database.DatabaseFileExcludePatterns;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.model.ListModelFileExcludePatterns;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.types.Persistence;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-02
 */
public final class SettingsFileExcludePatternsPanel extends javax.swing.JPanel
        implements ProgressListener, Persistence, ListSelectionListener {

    private static final    String                       ADD_INFO_TEXT    = JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.AddInfoText");
    private static final    long                         serialVersionUID = -3083582823254767001L;
    private final transient DatabaseFileExcludePatterns   db               = DatabaseFileExcludePatterns.INSTANCE;
    private final           ListModelFileExcludePatterns model            = new ListModelFileExcludePatterns();
    private                 boolean                      isUpdateDatabase = false;
    private                 boolean                      stop             = false;

    public SettingsFileExcludePatternsPanel() {
        initComponents();
        textFieldInputPatterns.requestFocusInWindow();
        MnemonicUtil.setMnemonics((Container) this);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            stop             = false;
            isUpdateDatabase = false;
            setEnabledButtons();
        } else {
            cancelUpdateDatabase();
        }
        super.setVisible(visible);
    }

    private void deletePattern() {
        if (listPatterns.getSelectedIndex() < 0) return;
        String pattern = (String) listPatterns.getSelectedValue();
        model.delete(pattern);
        setEnabledButtons();
        listPatterns.requestFocusInWindow();
    }

    private void insertPattern() {
        String input = textFieldInputPatterns.getText().trim();
        if (canInsertPattern(input)) {
            model.insert(input);
        }
        setEnabledButtons();
    }

    private boolean canInsertPattern(String input) {
        return !input.isEmpty() && !input.equals(ADD_INFO_TEXT);
    }

    private void setEnabledButtonInsertPattern() {
        buttonInsertPatterns.setEnabled(
                canInsertPattern(textFieldInputPatterns.getText().trim()));
    }

    private void setEnabledButtons() {
        int     size         = model.getSize();
        boolean itemSelected = listPatterns.getSelectedIndex() >= 0;

        buttonDeletePatterns.setEnabled(itemSelected);
        setEnabledButtonInsertPattern();
        buttonUpdateDatabase.setEnabled(size > 0 && !isUpdateDatabase);
        buttonCancelUpdateDatabase.setEnabled(isUpdateDatabase);
    }

    private void setSelectedPatternToInput() {
        String pattern = (String) listPatterns.getSelectedValue();

        if (pattern != null) {
            textFieldInputPatterns.setText(pattern);
        }
    }

    private void updateDatabase() {
        List<String> patterns = model.getPatterns();
        if (patterns.size() > 0) {
            isUpdateDatabase = true;
            stop = false;
            setEnabledButtons();
            db.deleteMatchingFiles(patterns, this);
        }
    }

    private void cancelUpdateDatabase() {
        stop = true;
    }

    private void checkStopEvent(ProgressEvent evt) {
        if (stop) {
            evt.stop();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        progressBarUpdateDatabase.setMinimum(evt.getMinimum());
        progressBarUpdateDatabase.setMaximum(evt.getMaximum());
        progressBarUpdateDatabase.setValue(evt.getValue());
        checkStopEvent(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        progressBarUpdateDatabase.setValue(evt.getValue());
        checkStopEvent(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        progressBarUpdateDatabase.setValue(evt.getValue());
        isUpdateDatabase = false;
        stop = false;
        setEnabledButtons();
    }

    @Override
    public void readProperties() {
    }

    @Override
    public void writeProperties() {
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            setEnabledButtons();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelInfoList = new javax.swing.JLabel();
        scrollPaneListPatterns = new javax.swing.JScrollPane();
        listPatterns = new javax.swing.JList();
        textFieldInputPatterns = new javax.swing.JTextField();
        buttonDeletePatterns = new javax.swing.JButton();
        buttonInsertPatterns = new javax.swing.JButton();
        labelInfoDatabase = new javax.swing.JLabel();
        progressBarUpdateDatabase = new javax.swing.JProgressBar();
        buttonCancelUpdateDatabase = new javax.swing.JButton();
        buttonUpdateDatabase = new javax.swing.JButton();

        labelInfoList.setLabelFor(listPatterns);
        labelInfoList.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.labelInfoList.text")); // NOI18N

        listPatterns.setModel(model);
        listPatterns.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listPatterns.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listPatternsValueChanged(evt);
            }
        });
        scrollPaneListPatterns.setViewportView(listPatterns);

        textFieldInputPatterns.setText(ADD_INFO_TEXT);
        textFieldInputPatterns.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldInputPatternsKeyReleased(evt);
            }
        });

        buttonDeletePatterns.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonDeletePatterns.text")); // NOI18N
        buttonDeletePatterns.setToolTipText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonDeletePatterns.toolTipText")); // NOI18N
        buttonDeletePatterns.setEnabled(false);
        buttonDeletePatterns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeletePatternsActionPerformed(evt);
            }
        });

        buttonInsertPatterns.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonInsertPatterns.text")); // NOI18N
        buttonInsertPatterns.setToolTipText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonInsertPatterns.toolTipText")); // NOI18N
        buttonInsertPatterns.setEnabled(false);
        buttonInsertPatterns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInsertPatternsActionPerformed(evt);
            }
        });

        labelInfoDatabase.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoDatabase.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.labelInfoDatabase.text")); // NOI18N

        buttonCancelUpdateDatabase.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonCancelUpdateDatabase.text")); // NOI18N
        buttonCancelUpdateDatabase.setEnabled(false);
        buttonCancelUpdateDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelUpdateDatabaseActionPerformed(evt);
            }
        });

        buttonUpdateDatabase.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonUpdateDatabase.text")); // NOI18N
        buttonUpdateDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateDatabaseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPaneListPatterns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                    .addComponent(labelInfoList, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textFieldInputPatterns, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonDeletePatterns)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonInsertPatterns))
                    .addComponent(labelInfoDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                    .addComponent(progressBarUpdateDatabase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 455, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonCancelUpdateDatabase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonUpdateDatabase)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfoList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneListPatterns, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldInputPatterns, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonInsertPatterns)
                    .addComponent(buttonDeletePatterns))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoDatabase)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBarUpdateDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonUpdateDatabase)
                    .addComponent(buttonCancelUpdateDatabase))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void listPatternsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listPatternsValueChanged
    setSelectedPatternToInput();
    setEnabledButtons();
}//GEN-LAST:event_listPatternsValueChanged

private void textFieldInputPatternsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldInputPatternsKeyReleased
    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
        insertPattern();
    } else {
        setEnabledButtonInsertPattern();
    }
}//GEN-LAST:event_textFieldInputPatternsKeyReleased

private void buttonCancelUpdateDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelUpdateDatabaseActionPerformed
    cancelUpdateDatabase();
}//GEN-LAST:event_buttonCancelUpdateDatabaseActionPerformed

private void buttonDeletePatternsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeletePatternsActionPerformed
    deletePattern();
}//GEN-LAST:event_buttonDeletePatternsActionPerformed

private void buttonUpdateDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateDatabaseActionPerformed
    updateDatabase();
}//GEN-LAST:event_buttonUpdateDatabaseActionPerformed

private void buttonInsertPatternsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonInsertPatternsActionPerformed
    insertPattern();
}//GEN-LAST:event_buttonInsertPatternsActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancelUpdateDatabase;
    private javax.swing.JButton buttonDeletePatterns;
    private javax.swing.JButton buttonInsertPatterns;
    private javax.swing.JButton buttonUpdateDatabase;
    private javax.swing.JLabel labelInfoDatabase;
    private javax.swing.JLabel labelInfoList;
    private javax.swing.JList listPatterns;
    private javax.swing.JProgressBar progressBarUpdateDatabase;
    private javax.swing.JScrollPane scrollPaneListPatterns;
    private javax.swing.JTextField textFieldInputPatterns;
    // End of variables declaration//GEN-END:variables
}
