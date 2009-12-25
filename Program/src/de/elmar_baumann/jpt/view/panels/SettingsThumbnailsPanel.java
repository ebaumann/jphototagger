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
/*
 * SettingsThumbnailsPanel.java
 *
 * Created on 1. November 2008, 22:11
 */
package de.elmar_baumann.jpt.view.panels;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.event.listener.impl.ListenerProvider;
import de.elmar_baumann.jpt.event.UserSettingsChangeEvent;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.helper.UpdateAllThumbnails;
import de.elmar_baumann.jpt.types.Persistence;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-02
 */
public final class SettingsThumbnailsPanel extends javax.swing.JPanel
        implements ActionListener, Persistence {

    private UpdateAllThumbnails thumbnailsUpdater;
    private final ListenerProvider listenerProvider = ListenerProvider.INSTANCE;

    /** Creates new form SettingsThumbnailsPanel */
    public SettingsThumbnailsPanel() {
        initComponents();
    }

    private void handleActionCheckBoxUseEmbeddedThumbnails() {
        if (checkBoxIsUseEmbeddedThumbnails.isSelected()) {
            checkBoxIsCreateThumbnailsWithExternalApp.setSelected(false);
        }
        setExternalThumbnailAppEnabled();
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.IS_USE_EMBEDDED_THUMBNAILS, this);
        evt.setUseEmbeddedThumbnails(
                checkBoxIsUseEmbeddedThumbnails.isSelected());
        notifyChangeListener(evt);
    }

    private void handleStateChangedSpinnerMaxThumbnailWidth() {
        UserSettingsChangeEvent evt = new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.MAX_THUMBNAIL_WIDTH, this);
        evt.setMaxThumbnailWidth((Integer) spinnerMaxThumbnailWidth.getValue());
        notifyChangeListener(evt);
    }

    private void updateAllThumbnails() {
        synchronized (this) {
            buttonUpdateAllThumbnails.setEnabled(false);
            thumbnailsUpdater = new UpdateAllThumbnails();
            thumbnailsUpdater.addActionListener(this);
            Thread thread = new Thread(thumbnailsUpdater);
            thread.setName("Updating thumbnails" + " @ " + getClass().getName());
            thread.start();
        }
    }

    private synchronized void notifyChangeListener(UserSettingsChangeEvent evt) {
        listenerProvider.notifyUserSettingsChangeListener(evt);
    }

    @Override
    public void readProperties() {
        setExternalThumbnailAppEnabled();
        readContentFromProperties();
    }

    @Override
    public void writeProperties() {
    }

    private void readContentFromProperties() {
        UserSettings settings = UserSettings.INSTANCE;
        spinnerMaxThumbnailWidth.setValue(settings.getMaxThumbnailLength());
        checkBoxIsCreateThumbnailsWithExternalApp.setSelected(settings.
                isCreateThumbnailsWithExternalApp());
        checkBoxIsUseEmbeddedThumbnails.setSelected(settings.
                isUseEmbeddedThumbnails());
        textFieldExternalThumbnailCreationCommand.setText(settings.
                getExternalThumbnailCreationCommand());
    }

    private void handleActionCheckBoxExternalThumbnail() {
        if (checkBoxIsCreateThumbnailsWithExternalApp.isSelected()) {
            checkBoxIsUseEmbeddedThumbnails.setSelected(false);
        }
        UserSettingsChangeEvent evt =
                new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.IS_CREATE_THUMBNAILS_WITH_EXTERNAL_APP,
                this);
        evt.setCreateThumbnailsWithExternalApp(
                checkBoxIsCreateThumbnailsWithExternalApp.isSelected());
        notifyChangeListener(evt);
        setExternalThumbnailAppEnabled();
    }

    public void setEnabled() {
        textFieldExternalThumbnailCreationCommand.setEnabled(
                checkBoxIsCreateThumbnailsWithExternalApp.isSelected());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == thumbnailsUpdater) {
            buttonUpdateAllThumbnails.setEnabled(true);
        }
    }

    private void setExternalThumbnailAppEnabled() {
        textFieldExternalThumbnailCreationCommand.setEnabled(
                UserSettings.INSTANCE.isCreateThumbnailsWithExternalApp());
    }

    private void handleTextFieldExternalThumbnailCreationCommandKeyReleased() {
        UserSettingsChangeEvent evt =
                new UserSettingsChangeEvent(
                UserSettingsChangeEvent.Type.EXTERNAL_THUMBNAIL_CREATION_COMMAND,
                this);
        evt.setExternalThumbnailCreationCommand(
                textFieldExternalThumbnailCreationCommand.getText());
        notifyChangeListener(evt);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelThumbnailDimensions = new javax.swing.JPanel();
        labelMaxThumbnailWidth = new javax.swing.JLabel();
        spinnerMaxThumbnailWidth = new javax.swing.JSpinner();
        buttonUpdateAllThumbnails = new javax.swing.JButton();
        labelUpdateAllThumbnails = new javax.swing.JLabel();
        checkBoxIsUseEmbeddedThumbnails = new javax.swing.JCheckBox();
        panelExternalThumbnailApp = new javax.swing.JPanel();
        checkBoxIsCreateThumbnailsWithExternalApp = new javax.swing.JCheckBox();
        labelIsCreateThumbnailsWithExternalApp = new javax.swing.JLabel();
        textFieldExternalThumbnailCreationCommand = new javax.swing.JTextField();

        panelThumbnailDimensions.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("SettingsThumbnailsPanel.panelThumbnailDimensions.border.title")));

        labelMaxThumbnailWidth.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelMaxThumbnailWidth.setText(Bundle.getString("SettingsThumbnailsPanel.labelMaxThumbnailWidth.text"));

        spinnerMaxThumbnailWidth.setModel(new SpinnerNumberModel(150, 50, 256, 1));
        spinnerMaxThumbnailWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMaxThumbnailWidthStateChanged(evt);
            }
        });

        buttonUpdateAllThumbnails.setMnemonic('n');
        buttonUpdateAllThumbnails.setText(Bundle.getString("SettingsThumbnailsPanel.buttonUpdateAllThumbnails.text"));
        buttonUpdateAllThumbnails.setToolTipText(Bundle.getString("SettingsThumbnailsPanel.buttonUpdateAllThumbnails.toolTipText"));
        buttonUpdateAllThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateAllThumbnailsActionPerformed(evt);
            }
        });

        labelUpdateAllThumbnails.setForeground(new java.awt.Color(0, 0, 255));
        labelUpdateAllThumbnails.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelUpdateAllThumbnails.setText(Bundle.getString("SettingsThumbnailsPanel.labelUpdateAllThumbnails.text"));
        labelUpdateAllThumbnails.setPreferredSize(new java.awt.Dimension(1030, 32));

        javax.swing.GroupLayout panelThumbnailDimensionsLayout = new javax.swing.GroupLayout(panelThumbnailDimensions);
        panelThumbnailDimensions.setLayout(panelThumbnailDimensionsLayout);
        panelThumbnailDimensionsLayout.setHorizontalGroup(
            panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelUpdateAllThumbnails, javax.swing.GroupLayout.Alignment.LEADING, 0, 0, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panelThumbnailDimensionsLayout.createSequentialGroup()
                        .addComponent(labelMaxThumbnailWidth)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerMaxThumbnailWidth, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonUpdateAllThumbnails)))
                .addContainerGap())
        );
        panelThumbnailDimensionsLayout.setVerticalGroup(
            panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMaxThumbnailWidth)
                    .addComponent(spinnerMaxThumbnailWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonUpdateAllThumbnails))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUpdateAllThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        checkBoxIsUseEmbeddedThumbnails.setText(Bundle.getString("SettingsThumbnailsPanel.checkBoxIsUseEmbeddedThumbnails.text"));
        checkBoxIsUseEmbeddedThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsUseEmbeddedThumbnailsActionPerformed(evt);
            }
        });

        panelExternalThumbnailApp.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString("SettingsThumbnailsPanel.panelExternalThumbnailApp.border.title")));

        checkBoxIsCreateThumbnailsWithExternalApp.setText(Bundle.getString("SettingsThumbnailsPanel.checkBoxIsCreateThumbnailsWithExternalApp.text"));
        checkBoxIsCreateThumbnailsWithExternalApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxIsCreateThumbnailsWithExternalAppActionPerformed(evt);
            }
        });

        labelIsCreateThumbnailsWithExternalApp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelIsCreateThumbnailsWithExternalApp.setText(Bundle.getString("SettingsThumbnailsPanel.labelIsCreateThumbnailsWithExternalApp.text"));
        labelIsCreateThumbnailsWithExternalApp.setPreferredSize(new java.awt.Dimension(1694, 60));

        textFieldExternalThumbnailCreationCommand.setEnabled(false);
        textFieldExternalThumbnailCreationCommand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldExternalThumbnailCreationCommandKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelExternalThumbnailAppLayout = new javax.swing.GroupLayout(panelExternalThumbnailApp);
        panelExternalThumbnailApp.setLayout(panelExternalThumbnailAppLayout);
        panelExternalThumbnailAppLayout.setHorizontalGroup(
            panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxIsCreateThumbnailsWithExternalApp)
                    .addComponent(labelIsCreateThumbnailsWithExternalApp, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                    .addComponent(textFieldExternalThumbnailCreationCommand, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
                .addContainerGap())
        );
        panelExternalThumbnailAppLayout.setVerticalGroup(
            panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                .addComponent(checkBoxIsCreateThumbnailsWithExternalApp)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelIsCreateThumbnailsWithExternalApp, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldExternalThumbnailCreationCommand, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(checkBoxIsUseEmbeddedThumbnails, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelThumbnailDimensions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelThumbnailDimensions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxIsUseEmbeddedThumbnails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void spinnerMaxThumbnailWidthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMaxThumbnailWidthStateChanged
    handleStateChangedSpinnerMaxThumbnailWidth();
}//GEN-LAST:event_spinnerMaxThumbnailWidthStateChanged

private void buttonUpdateAllThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateAllThumbnailsActionPerformed
    updateAllThumbnails();
}//GEN-LAST:event_buttonUpdateAllThumbnailsActionPerformed

private void checkBoxIsUseEmbeddedThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsUseEmbeddedThumbnailsActionPerformed
    handleActionCheckBoxUseEmbeddedThumbnails();
}//GEN-LAST:event_checkBoxIsUseEmbeddedThumbnailsActionPerformed

private void checkBoxIsCreateThumbnailsWithExternalAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxIsCreateThumbnailsWithExternalAppActionPerformed
    handleActionCheckBoxExternalThumbnail();
}//GEN-LAST:event_checkBoxIsCreateThumbnailsWithExternalAppActionPerformed

private void textFieldExternalThumbnailCreationCommandKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldExternalThumbnailCreationCommandKeyReleased
    handleTextFieldExternalThumbnailCreationCommandKeyReleased();
}//GEN-LAST:event_textFieldExternalThumbnailCreationCommandKeyReleased
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonUpdateAllThumbnails;
    private javax.swing.JCheckBox checkBoxIsCreateThumbnailsWithExternalApp;
    private javax.swing.JCheckBox checkBoxIsUseEmbeddedThumbnails;
    private javax.swing.JLabel labelIsCreateThumbnailsWithExternalApp;
    private javax.swing.JLabel labelMaxThumbnailWidth;
    private javax.swing.JLabel labelUpdateAllThumbnails;
    private javax.swing.JPanel panelExternalThumbnailApp;
    private javax.swing.JPanel panelThumbnailDimensions;
    private javax.swing.JSpinner spinnerMaxThumbnailWidth;
    private javax.swing.JTextField textFieldExternalThumbnailCreationCommand;
    // End of variables declaration//GEN-END:variables
}
