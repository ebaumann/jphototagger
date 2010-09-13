/*
 * @(#)RenameFilenamesInDbPanel.java    Created on 2009-06-16
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

import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.util.Settings;

import java.awt.Container;

import javax.swing.JPanel;

/**
 * Uses
 * {@link DatabaseImageFiles#updateRenameFilenamesStartingWith(java.lang.String, java.lang.String, org.jphototagger.program.event.listener.ProgressListener)}
 * to replace substrings in filenames.
 *
 * @author  Elmar Baumann
 */
public class RenameFilenamesInDbPanel extends JPanel
        implements ProgressListener, Persistence {
    private static final long   serialVersionUID = -4207218985613254920L;
    private static final String KEY_SEARCH       =
        "RenameFilenamesInDbPanel.Search";
    private static final String KEY_REPLACEMENT  =
        "RenameFilenamesInDbPanel.Replacement";
    private volatile boolean runs;

    public RenameFilenamesInDbPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        UserSettings.INSTANCE.getSettings().applySettings(this, null);
        setButtonReplaceEnabled();
        MnemonicUtil.setMnemonics((Container) this);
    }

    public boolean runs() {
        return runs;
    }

    private void replace(final ProgressListener progressListener) {
        if (confirmReplace()) {
            runs = true;
            setInputEnabled(false);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int count =
                        DatabaseImageFiles.INSTANCE
                            .updateRenameFilenamesStartingWith(textFieldSearch
                                .getText(), textFieldReplacement.getText(),
                                            progressListener);

                    MessageDisplayer.information(
                        null, "RenameFilenamesInDbPanel.Info.Count", count);
                    runs = false;
                }
            }, "JPhotoTagger: Renaming files in database");

            thread.start();
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        progressBar.setMinimum(evt.getMinimum());
        progressBar.setMaximum(evt.getMaximum());
        progressBar.setValue(evt.getValue());
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        progressBar.setValue(evt.getValue());
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        progressBar.setValue(0);
        setInputEnabled(true);
    }

    private void setInputEnabled(boolean enabled) {
        buttonReplace.setEnabled(enabled);
        textFieldSearch.setEnabled(enabled);
        textFieldReplacement.setEnabled(enabled);
    }

    private void setButtonReplaceEnabled() {
        String  searchText      = textFieldSearch.getText().trim();
        String  replacementText = textFieldReplacement.getText().trim();
        boolean textsEquals     = searchText.equals(replacementText);

        buttonReplace.setEnabled(!searchText.isEmpty()
                                 &&!replacementText.isEmpty() &&!textsEquals);
    }

    private boolean confirmReplace() {
        return MessageDisplayer.confirmYesNo(this,
                "RenameFilenamesInDbPanel.Confirm.Replace",
                textFieldSearch.getText(), textFieldReplacement.getText());
    }

    @Override
    public void readProperties() {
        if (this == null) {
            throw new NullPointerException("this == null");
        }

        Settings settings = UserSettings.INSTANCE.getSettings();

        textFieldSearch.setText(settings.getString(KEY_SEARCH));
        textFieldReplacement.setText(settings.getString(KEY_REPLACEMENT));
    }

    @Override
    public void writeProperties() {
        if (this == null) {
            throw new NullPointerException("this == null");
        }

        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.set(textFieldSearch.getText(), KEY_SEARCH);
        settings.set(textFieldReplacement.getText(), KEY_REPLACEMENT);
        UserSettings.INSTANCE.writeToFile();
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
        buttonGroupPosition  = new javax.swing.ButtonGroup();
        labelTitle           = new javax.swing.JLabel();
        labelSearch          = new javax.swing.JLabel();
        textFieldSearch      = new javax.swing.JTextField();
        labelReplacement     = new javax.swing.JLabel();
        textFieldReplacement = new javax.swing.JTextField();
        progressBar          = new javax.swing.JProgressBar();
        buttonReplace        = new javax.swing.JButton();
        labelTitle.setForeground(new java.awt.Color(0, 0, 255));
        labelTitle.setText(
            JptBundle.INSTANCE.getString(
                "RenameFilenamesInDbPanel.labelTitle.text"));    // NOI18N
        labelSearch.setForeground(new java.awt.Color(0, 196, 0));
        labelSearch.setLabelFor(textFieldSearch);
        labelSearch.setText(
            JptBundle.INSTANCE.getString(
                "RenameFilenamesInDbPanel.labelSearch.text"));    // NOI18N
        textFieldSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldSearchKeyReleased(evt);
            }
        });
        labelReplacement.setForeground(new java.awt.Color(0, 196, 0));
        labelReplacement.setLabelFor(textFieldReplacement);
        labelReplacement.setText(
            JptBundle.INSTANCE.getString(
                "RenameFilenamesInDbPanel.labelReplacement.text"));    // NOI18N
        textFieldReplacement.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldReplacementKeyReleased(evt);
            }
        });
        buttonReplace.setText(
            JptBundle.INSTANCE.getString(
                "RenameFilenamesInDbPanel.buttonReplace.text"));    // NOI18N
        buttonReplace.setEnabled(false);
        buttonReplace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonReplaceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);

        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                        labelTitle, javax.swing.GroupLayout.DEFAULT_SIZE, 324,
                        Short.MAX_VALUE).addGroup(
                            javax.swing.GroupLayout.Alignment.TRAILING,
                            layout.createSequentialGroup().addGroup(
                                layout.createParallelGroup(
                                    javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                    labelSearch,
                                    javax.swing.GroupLayout.Alignment.TRAILING).addComponent(
                                        labelReplacement,
                                        javax.swing.GroupLayout.Alignment.TRAILING)).addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                layout.createParallelGroup(
                                                    javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                                                        textFieldSearch,
                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                211,
                                                                Short.MAX_VALUE).addComponent(
                                                                    textFieldReplacement,
                                                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                            211,
                                                                            Short.MAX_VALUE))).addComponent(
                                                                                buttonReplace,
                                                                                    javax.swing.GroupLayout.Alignment.TRAILING).addComponent(
                                                                                        progressBar,
                                                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                324,
                                                                                                Short.MAX_VALUE)).addContainerGap()));
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addComponent(
                    labelTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 17,
                    javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                        javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                        layout.createParallelGroup(
                            javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                            textFieldSearch,
                            javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE,
                            javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(
                                labelSearch)).addPreferredGap(
                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                    layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                        labelReplacement).addComponent(
                                        textFieldReplacement,
                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                        javax.swing.GroupLayout.DEFAULT_SIZE,
                                        javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(
                                                progressBar,
                                                    javax.swing.GroupLayout.PREFERRED_SIZE,
                                                        24,
                                                        javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(
                                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(
                                                                buttonReplace).addContainerGap(
                                                                    javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                        Short.MAX_VALUE)));
        layout.linkSize(javax.swing.SwingConstants.VERTICAL,
                        new java.awt.Component[] { textFieldReplacement,
                textFieldSearch });
    }    // </editor-fold>//GEN-END:initComponents

    private void buttonReplaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonReplaceActionPerformed
        replace(this);
    }//GEN-LAST:event_buttonReplaceActionPerformed

    private void textFieldSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldSearchKeyReleased
        setButtonReplaceEnabled();
    }//GEN-LAST:event_textFieldSearchKeyReleased

    private void textFieldReplacementKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldReplacementKeyReleased
        setButtonReplaceEnabled();
    }//GEN-LAST:event_textFieldReplacementKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup  buttonGroupPosition;
    private javax.swing.JButton      buttonReplace;
    private javax.swing.JLabel       labelReplacement;
    private javax.swing.JLabel       labelSearch;
    private javax.swing.JLabel       labelTitle;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JTextField   textFieldReplacement;
    private javax.swing.JTextField   textFieldSearch;

    // End of variables declaration//GEN-END:variables
}
