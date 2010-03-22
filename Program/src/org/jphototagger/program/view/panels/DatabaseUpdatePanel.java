/*
 * @(#)DatabaseUpdatePanel.java    Created on 
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

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.app.MessageDisplayer;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.helper.HelperThread;
import org.jphototagger.program.helper.InsertKeywords;
import org.jphototagger.program.helper.RefreshExifInDbOfKnownFiles;
import org.jphototagger.program.helper.RefreshXmpInDbOfKnownFiles;
import org.jphototagger.program.helper.SetExifToXmp;
import org.jphototagger.program.helper.UpdateAllThumbnails;
import org.jphototagger.program.model.ListModelKeywords;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.dialogs.RenameFilenamesInDbDialog;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.lib.componentutil.MnemonicUtil;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 *
 * @author Elmar Baumann
 */
public class DatabaseUpdatePanel extends JPanel
        implements ActionListener, ProgressListener {
    private static final String BUTTON_TEXT_START =
        JptBundle.INSTANCE.getString("DatabaseUpdatePanel.DisplayName.Start");
    private static final String BUTTON_TEXT_STOP =
        JptBundle.INSTANCE.getString("DatabaseUpdatePanel.DisplayName.Stop");
    private static final long             serialVersionUID =
        3148751698141558616L;
    private transient UpdateAllThumbnails thumbnailUpdater;
    private final AbstractButton[]        buttons;
    private volatile boolean              stop;

    public DatabaseUpdatePanel() {
        initComponents();
        buttons = new AbstractButton[] {
            buttonCopyKeywordsToKeywordsTree, buttonRenameFiles,
            buttonUpdateThumbnails, toggleButtonRefreshExif,
            toggleButtonRefreshXmp, toggleButtonExifDateToXmpDateCreated,
        };
        MnemonicUtil.setMnemonics((Container) this);
    }

    private synchronized void updateThumbnails() {
        setEnabledAllButtons(false);

        synchronized (this) {
            thumbnailUpdater = new UpdateAllThumbnails();
            thumbnailUpdater.addActionListener(this);
            new Thread(thumbnailUpdater).start();
        }
    }

    private synchronized void updateExif() {
        startOrStopHelperThread(toggleButtonRefreshExif,
                                RefreshExifInDbOfKnownFiles.class);
    }

    private void updateXmp() {
        startOrStopHelperThread(toggleButtonRefreshXmp,
                                RefreshXmpInDbOfKnownFiles.class);
    }

    private void exifDateToXmpDateCreated() {
        startOrStopHelperThread(toggleButtonExifDateToXmpDateCreated,
                                SetExifToXmp.class);
    }

    private synchronized void startOrStopHelperThread(JToggleButton button,
            Class<?> helperThreadClass) {
        if (button.isSelected()) {
            try {
                HelperThread helperThread =
                    (HelperThread) helperThreadClass.newInstance();

                disableOtherButtons(button);
                helperThread.setProgressBar(progressBar);
                helperThread.addProgressListener(this);
                stop = false;
                helperThread.start();
                button.setText(BUTTON_TEXT_STOP);
            } catch (Exception ex) {
                AppLogger.logSevere(DatabaseUpdatePanel.class, ex);
            }
        } else {
            stop = true;
            setEnabledAllButtons(true);
            button.setText(BUTTON_TEXT_START);
        }
    }

    private synchronized void disableOtherButtons(JToggleButton buttonEnabled) {
        for (AbstractButton button : buttons) {
            if (button != buttonEnabled) {
                button.setEnabled(false);
            }
        }
    }

    private synchronized void setEnabledAllButtons(boolean enabled) {
        for (AbstractButton button : buttons) {
            button.setEnabled(enabled);
        }
    }

    private synchronized void deselectAllToggleButtons() {
        for (AbstractButton button : buttons) {
            if (button instanceof JToggleButton) {
                ((JToggleButton) button).setSelected(false);
                button.setText(BUTTON_TEXT_START);
            }
        }
    }

    private void renameFilesInDb() {
        RenameFilenamesInDbDialog dlg = new RenameFilenamesInDbDialog();

        setEnabledAllButtons(false);
        dlg.setVisible(true);
        setEnabledAllButtons(true);
    }

    private void copyKeywordsToKeywordsTree() {
        List<String> keywords = ListUtil.toStringList(
                                    ModelFactory.INSTANCE.getModel(
                                        ListModelKeywords.class));

        if (keywords.size() > 0) {
            setEnabledAllButtons(false);
            new InsertKeywords(keywords).run();    // Run not as thread
            MessageDisplayer.information(
                this, "DatabaseUpdatePanel.Info.CopyKeywordsToTree");
            setEnabledAllButtons(true);
        }
    }

    private void checkStop(ProgressEvent evt) {
        if (stop) {
            evt.setStop(stop);
        }
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        checkStop(evt);
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        checkStop(evt);
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        checkStop(evt);
        setEnabledAllButtons(true);
        deselectAllToggleButtons();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        synchronized (this) {
            if (e.getSource() == thumbnailUpdater) {
                thumbnailUpdater = null;
                buttonUpdateThumbnails.setEnabled(true);
                setEnabledAllButtons(true);
            }
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
        labelRefreshExif                     = new javax.swing.JLabel();
        toggleButtonRefreshExif              = new javax.swing.JToggleButton();
        labelRefreshXmp                      = new javax.swing.JLabel();
        toggleButtonRefreshXmp               = new javax.swing.JToggleButton();
        labelUpdateThumbnails                = new javax.swing.JLabel();
        buttonUpdateThumbnails               = new javax.swing.JButton();
        labelRenameFiles                     = new javax.swing.JLabel();
        buttonRenameFiles                    = new javax.swing.JButton();
        labelCopyKeywordsToKeywordsTree      = new javax.swing.JLabel();
        buttonCopyKeywordsToKeywordsTree     = new javax.swing.JButton();
        labelExifDateToXmpDateCreated        = new javax.swing.JLabel();
        progressBar                          = new javax.swing.JProgressBar();
        toggleButtonExifDateToXmpDateCreated = new javax.swing.JToggleButton();
        labelRefreshExif.setIcon(
            new javax.swing.ImageIcon(
                getClass().getResource(
                    "/org/jphototagger/program/resource/icons/icon_exif.png")));    // NOI18N

        java.util.ResourceBundle bundle =
            java.util.ResourceBundle.getBundle(
                "org/jphototagger/program/resource/properties/Bundle");    // NOI18N

        labelRefreshExif.setText(
            bundle.getString("DatabaseUpdatePanel.labelRefreshExif.text"));    // NOI18N
        toggleButtonRefreshExif.setText(
            bundle.getString(
                "DatabaseUpdatePanel.toggleButtonRefreshExif.text"));    // NOI18N
        toggleButtonRefreshExif.setHorizontalAlignment(
            javax.swing.SwingConstants.LEFT);
        toggleButtonRefreshExif.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonRefreshExifActionPerformed(evt);
            }
        });
        labelRefreshXmp.setIcon(
            new javax.swing.ImageIcon(
                getClass().getResource(
                    "/org/jphototagger/program/resource/icons/icon_xmp.png")));    // NOI18N
        labelRefreshXmp.setText(
            bundle.getString("DatabaseUpdatePanel.labelRefreshXmp.text"));    // NOI18N
        toggleButtonRefreshXmp.setText(
            bundle.getString(
                "DatabaseUpdatePanel.toggleButtonRefreshXmp.text"));    // NOI18N
        toggleButtonRefreshXmp.setHorizontalAlignment(
            javax.swing.SwingConstants.LEFT);
        toggleButtonRefreshXmp.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonRefreshXmpActionPerformed(evt);
            }
        });
        labelUpdateThumbnails.setIcon(
            new javax.swing.ImageIcon(
                getClass().getResource(
                    "/org/jphototagger/program/resource/icons/icon_image.png")));    // NOI18N
        labelUpdateThumbnails.setText(
            bundle.getString("DatabaseUpdatePanel.labelUpdateThumbnails.text"));    // NOI18N
        buttonUpdateThumbnails.setText(
            bundle.getString(
                "DatabaseUpdatePanel.buttonUpdateThumbnails.text"));    // NOI18N
        buttonUpdateThumbnails.setHorizontalAlignment(
            javax.swing.SwingConstants.LEFT);
        buttonUpdateThumbnails.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateThumbnailsActionPerformed(evt);
            }
        });
        labelRenameFiles.setText(
            bundle.getString("DatabaseUpdatePanel.labelRenameFiles.text"));    // NOI18N
        buttonRenameFiles.setText(
            bundle.getString("DatabaseUpdatePanel.buttonRenameFiles.text"));    // NOI18N
        buttonRenameFiles.setHorizontalAlignment(
            javax.swing.SwingConstants.LEFT);
        buttonRenameFiles.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRenameFilesActionPerformed(evt);
            }
        });
        labelCopyKeywordsToKeywordsTree.setIcon(
            new javax.swing.ImageIcon(
                getClass().getResource(
                    "/org/jphototagger/program/resource/icons/icon_tree.png")));    // NOI18N
        labelCopyKeywordsToKeywordsTree.setText(
            bundle.getString(
                "DatabaseUpdatePanel.labelCopyKeywordsToKeywordsTree.text"));    // NOI18N
        buttonCopyKeywordsToKeywordsTree.setText(
            bundle.getString(
                "DatabaseUpdatePanel.buttonCopyKeywordsToKeywordsTree.text"));    // NOI18N
        buttonCopyKeywordsToKeywordsTree.setHorizontalAlignment(
            javax.swing.SwingConstants.LEFT);
        buttonCopyKeywordsToKeywordsTree.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCopyKeywordsToKeywordsTreeActionPerformed(evt);
            }
        });
        labelExifDateToXmpDateCreated.setText(
            bundle.getString(
                "DatabaseUpdatePanel.labelExifDateToXmpDateCreated.text"));    // NOI18N
        toggleButtonExifDateToXmpDateCreated.setText(
            bundle.getString(
                "DatabaseUpdatePanel.toggleButtonExifDateToXmpDateCreated.text"));    // NOI18N
        toggleButtonExifDateToXmpDateCreated.setHorizontalAlignment(
            javax.swing.SwingConstants.LEFT);
        toggleButtonExifDateToXmpDateCreated.addActionListener(
            new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleButtonExifDateToXmpDateCreatedActionPerformed(evt);
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
                        progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE).addGroup(
                        layout.createSequentialGroup().addComponent(
                            labelRefreshExif).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED, 93, Short.MAX_VALUE).addComponent(
                            toggleButtonRefreshExif)).addGroup(
                                layout.createSequentialGroup().addComponent(
                                    labelRefreshXmp).addPreferredGap(
                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE).addComponent(
                                    toggleButtonRefreshXmp)).addGroup(
                                        layout.createSequentialGroup().addComponent(
                                            labelUpdateThumbnails).addPreferredGap(
                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED, 152, Short.MAX_VALUE).addComponent(
                                                    buttonUpdateThumbnails)).addGroup(
                                                        layout.createSequentialGroup().addComponent(
                                                            labelRenameFiles).addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE).addComponent(
                                                                    buttonRenameFiles)).addGroup(
                                                                        layout.createSequentialGroup().addComponent(
                                                                            labelCopyKeywordsToKeywordsTree).addPreferredGap(
                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED, 112, Short.MAX_VALUE).addComponent(
                                                                                    buttonCopyKeywordsToKeywordsTree)).addGroup(
                                                                                        layout.createSequentialGroup().addComponent(
                                                                                            labelExifDateToXmpDateCreated).addPreferredGap(
                                                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED, 176, Short.MAX_VALUE).addComponent(
                                                                                                    toggleButtonExifDateToXmpDateCreated))).addContainerGap()));
        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
                        new java.awt.Component[] {
            buttonCopyKeywordsToKeywordsTree, buttonRenameFiles,
            buttonUpdateThumbnails, toggleButtonExifDateToXmpDateCreated,
            toggleButtonRefreshExif, toggleButtonRefreshXmp
        });
        layout.setVerticalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup().addContainerGap().addGroup(
                    layout.createParallelGroup(
                        javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                        labelRefreshExif).addComponent(
                        toggleButtonRefreshExif)).addPreferredGap(
                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                            layout.createParallelGroup(
                                javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                labelRefreshXmp).addComponent(
                                toggleButtonRefreshXmp)).addPreferredGap(
                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                    layout.createParallelGroup(
                                        javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                        labelUpdateThumbnails).addComponent(
                                        buttonUpdateThumbnails)).addPreferredGap(
                                            javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                layout.createParallelGroup(
                                                    javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                                        labelRenameFiles).addComponent(
                                                            buttonRenameFiles)).addPreferredGap(
                                                                javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                                    layout.createParallelGroup(
                                                                        javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                                                            labelCopyKeywordsToKeywordsTree).addComponent(
                                                                                buttonCopyKeywordsToKeywordsTree)).addPreferredGap(
                                                                                    javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(
                                                                                        layout.createParallelGroup(
                                                                                            javax.swing.GroupLayout.Alignment.BASELINE).addComponent(
                                                                                                labelExifDateToXmpDateCreated).addComponent(
                                                                                                    toggleButtonExifDateToXmpDateCreated)).addPreferredGap(
                                                                                                        javax.swing.LayoutStyle.ComponentPlacement.RELATED,
                                                                                                            javax.swing.GroupLayout.DEFAULT_SIZE,
                                                                                                                Short.MAX_VALUE).addComponent(
                                                                                                                    progressBar,
                                                                                                                        javax.swing.GroupLayout.PREFERRED_SIZE,
                                                                                                                            23,
                                                                                                                            javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
    }    // </editor-fold>//GEN-END:initComponents

    private void toggleButtonRefreshExifActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonRefreshExifActionPerformed
        updateExif();
    }//GEN-LAST:event_toggleButtonRefreshExifActionPerformed

    private void buttonRenameFilesActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRenameFilesActionPerformed
        renameFilesInDb();
    }//GEN-LAST:event_buttonRenameFilesActionPerformed

    private void toggleButtonRefreshXmpActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonRefreshXmpActionPerformed
        updateXmp();
    }//GEN-LAST:event_toggleButtonRefreshXmpActionPerformed

    private void buttonUpdateThumbnailsActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateThumbnailsActionPerformed
        updateThumbnails();
    }//GEN-LAST:event_buttonUpdateThumbnailsActionPerformed

    private void buttonCopyKeywordsToKeywordsTreeActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCopyKeywordsToKeywordsTreeActionPerformed
        copyKeywordsToKeywordsTree();
    }//GEN-LAST:event_buttonCopyKeywordsToKeywordsTreeActionPerformed

    private void toggleButtonExifDateToXmpDateCreatedActionPerformed(
            java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleButtonExifDateToXmpDateCreatedActionPerformed
        exifDateToXmpDateCreated();
    }//GEN-LAST:event_toggleButtonExifDateToXmpDateCreatedActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton       buttonCopyKeywordsToKeywordsTree;
    private javax.swing.JButton       buttonRenameFiles;
    private javax.swing.JButton       buttonUpdateThumbnails;
    private javax.swing.JLabel        labelCopyKeywordsToKeywordsTree;
    private javax.swing.JLabel        labelExifDateToXmpDateCreated;
    private javax.swing.JLabel        labelRefreshExif;
    private javax.swing.JLabel        labelRefreshXmp;
    private javax.swing.JLabel        labelRenameFiles;
    private javax.swing.JLabel        labelUpdateThumbnails;
    private javax.swing.JProgressBar  progressBar;
    private javax.swing.JToggleButton toggleButtonExifDateToXmpDateCreated;
    private javax.swing.JToggleButton toggleButtonRefreshExif;
    private javax.swing.JToggleButton toggleButtonRefreshXmp;

    // End of variables declaration//GEN-END:variables
}
