/*
 * @(#)SettingsThumbnailsPanel.java    Created on 2008-11-02
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
/*
 * SettingsThumbnailsPanel.java
 *
 * Created on 1. November 2008, 22:11
 */

package org.jphototagger.program.view.panels;

import org.jphototagger.program.helper.UpdateAllThumbnails;
import org.jphototagger.program.image.thumbnail.ThumbnailCreator;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.MnemonicUtil;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JRadioButton;
import javax.swing.SpinnerNumberModel;

/**
 *
 * @author  Elmar Baumann
 */
public final class SettingsThumbnailsPanel extends javax.swing.JPanel
        implements ActionListener, Persistence {
    private static final long                         serialVersionUID =
        -5283587664627790755L;
    private transient UpdateAllThumbnails             thumbnailsUpdater;
    private final Map<JRadioButton, ThumbnailCreator> thumbnailCreatorOfRadioButton =
        new HashMap<JRadioButton, ThumbnailCreator>();
    private final Map<ThumbnailCreator, JRadioButton> radioButtonOfThumbnailCreator =
        new HashMap<ThumbnailCreator, JRadioButton>();

    public SettingsThumbnailsPanel() {
        initComponents();
        initMaps();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void initMaps() {
        thumbnailCreatorOfRadioButton.put(
            radioButtonCreateThumbnailsWithExternalApp,
            ThumbnailCreator.EXTERNAL_APP);
        thumbnailCreatorOfRadioButton.put(
            radioButtonCreateThumbnailsWithImagero, ThumbnailCreator.IMAGERO);
        thumbnailCreatorOfRadioButton.put(
            radioButtonCreateThumbnailsWithJavaImageIo,
            ThumbnailCreator.JAVA_IMAGE_IO);
        thumbnailCreatorOfRadioButton.put(radioButtonUseEmbeddedThumbnails,
                                          ThumbnailCreator.EMBEDDED);

        for (JRadioButton radioButton : thumbnailCreatorOfRadioButton
                .keySet()) {
            radioButtonOfThumbnailCreator.put(
                thumbnailCreatorOfRadioButton.get(radioButton), radioButton);
        }
    }

    private void handleStateChangedSpinnerMaxThumbnailWidth() {
        UserSettings.INSTANCE.setMaxThumbnailWidth(
            (Integer) spinnerMaxThumbnailWidth.getValue());
    }

    private void updateAllThumbnails() {
        synchronized (this) {
            buttonUpdateAllThumbnails.setEnabled(false);
            thumbnailsUpdater = new UpdateAllThumbnails();
            thumbnailsUpdater.addActionListener(this);

            Thread thread = new Thread(thumbnailsUpdater);

            thread.setName("Updating thumbnails @ "
                           + getClass().getSimpleName());
            thread.start();
        }
    }

    @Override
    public void readProperties() {
        setSelectedRadioButtons();
        setExternalThumbnailAppEnabled();

        UserSettings settings = UserSettings.INSTANCE;

        spinnerMaxThumbnailWidth.setValue(settings.getMaxThumbnailWidth());
        textFieldExternalThumbnailCreationCommand.setText(
            settings.getExternalThumbnailCreationCommand());
    }

    private void setSelectedRadioButtons() {
        ThumbnailCreator creator = UserSettings.INSTANCE.getThumbnailCreator();

        for (JRadioButton radioButton : radioButtonOfThumbnailCreator
                .values()) {
            radioButton.setSelected(radioButtonOfThumbnailCreator.get(creator)
                                    == radioButton);
        }
    }

    private void setExternalThumbnailAppEnabled() {
        textFieldExternalThumbnailCreationCommand.setEnabled(
            UserSettings.INSTANCE.getThumbnailCreator().equals(
                ThumbnailCreator.EXTERNAL_APP));
    }

    @Override
    public void writeProperties() {}

    @Override
    public void actionPerformed(ActionEvent evt) {
        synchronized (this) {
            if (evt.getSource() == thumbnailsUpdater) {
                buttonUpdateAllThumbnails.setEnabled(true);
            }
        }
    }

    private void handleTextFieldExternalThumbnailCreationCommandKeyReleased() {
        UserSettings.INSTANCE.setExternalThumbnailCreationCommand(
            textFieldExternalThumbnailCreationCommand.getText());
    }

    private void setThumbnailCreator(JRadioButton radioButton) {
        UserSettings.INSTANCE.setThumbnailCreator(
            thumbnailCreatorOfRadioButton.get(radioButton));
        textFieldExternalThumbnailCreationCommand.setEnabled(
            radioButtonCreateThumbnailsWithExternalApp.isSelected());
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

        buttonGroupThumbnailCreator = new javax.swing.ButtonGroup();
        panelThumbnailDimensions = new javax.swing.JPanel();
        labelMaxThumbnailWidth = new javax.swing.JLabel();
        spinnerMaxThumbnailWidth = new javax.swing.JSpinner();
        buttonUpdateAllThumbnails = new javax.swing.JButton();
        labelUpdateAllThumbnails = new javax.swing.JLabel();
        panelThumbnailCreator = new javax.swing.JPanel();
        radioButtonCreateThumbnailsWithJavaImageIo = new javax.swing.JRadioButton();
        radioButtonCreateThumbnailsWithImagero = new javax.swing.JRadioButton();
        radioButtonUseEmbeddedThumbnails = new javax.swing.JRadioButton();
        radioButtonCreateThumbnailsWithExternalApp = new javax.swing.JRadioButton();
        panelExternalThumbnailApp = new javax.swing.JPanel();
        labelIsCreateThumbnailsWithExternalApp = new javax.swing.JLabel();
        textFieldExternalThumbnailCreationCommand = new javax.swing.JTextField();
        panelPadding2 = new javax.swing.JPanel();

        panelThumbnailDimensions.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.panelThumbnailDimensions.border.title"))); // NOI18N

        labelMaxThumbnailWidth.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        labelMaxThumbnailWidth.setLabelFor(spinnerMaxThumbnailWidth);
        labelMaxThumbnailWidth.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.labelMaxThumbnailWidth.text")); // NOI18N

        spinnerMaxThumbnailWidth.setModel(new SpinnerNumberModel(150, 50, 256, 1));
        spinnerMaxThumbnailWidth.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spinnerMaxThumbnailWidthStateChanged(evt);
            }
        });

        buttonUpdateAllThumbnails.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.buttonUpdateAllThumbnails.text")); // NOI18N
        buttonUpdateAllThumbnails.setToolTipText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.buttonUpdateAllThumbnails.toolTipText")); // NOI18N
        buttonUpdateAllThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateAllThumbnailsActionPerformed(evt);
            }
        });

        labelUpdateAllThumbnails.setForeground(new java.awt.Color(0, 0, 255));
        labelUpdateAllThumbnails.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelUpdateAllThumbnails.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.labelUpdateAllThumbnails.text")); // NOI18N

        javax.swing.GroupLayout panelThumbnailDimensionsLayout = new javax.swing.GroupLayout(panelThumbnailDimensions);
        panelThumbnailDimensions.setLayout(panelThumbnailDimensionsLayout);
        panelThumbnailDimensionsLayout.setHorizontalGroup(
            panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelThumbnailDimensionsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(labelUpdateAllThumbnails, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                    .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                        .addComponent(labelMaxThumbnailWidth, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spinnerMaxThumbnailWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(buttonUpdateAllThumbnails)))
                .addContainerGap())
        );
        panelThumbnailDimensionsLayout.setVerticalGroup(
            panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailDimensionsLayout.createSequentialGroup()
                .addGroup(panelThumbnailDimensionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMaxThumbnailWidth)
                    .addComponent(buttonUpdateAllThumbnails)
                    .addComponent(spinnerMaxThumbnailWidth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelUpdateAllThumbnails, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panelThumbnailCreator.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.panelThumbnailCreator.border.title"))); // NOI18N

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithJavaImageIo);
        radioButtonCreateThumbnailsWithJavaImageIo.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.radioButtonCreateThumbnailsWithJavaImageIo.text")); // NOI18N
        radioButtonCreateThumbnailsWithJavaImageIo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCreateThumbnailsWithJavaImageIoActionPerformed(evt);
            }
        });

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithImagero);
        radioButtonCreateThumbnailsWithImagero.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.radioButtonCreateThumbnailsWithImagero.text")); // NOI18N
        radioButtonCreateThumbnailsWithImagero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCreateThumbnailsWithImageroActionPerformed(evt);
            }
        });

        buttonGroupThumbnailCreator.add(radioButtonUseEmbeddedThumbnails);
        radioButtonUseEmbeddedThumbnails.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.radioButtonUseEmbeddedThumbnails.text")); // NOI18N
        radioButtonUseEmbeddedThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonUseEmbeddedThumbnailsActionPerformed(evt);
            }
        });

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithExternalApp);
        radioButtonCreateThumbnailsWithExternalApp.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.radioButtonCreateThumbnailsWithExternalApp.text")); // NOI18N
        radioButtonCreateThumbnailsWithExternalApp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCreateThumbnailsWithExternalAppActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelThumbnailCreatorLayout = new javax.swing.GroupLayout(panelThumbnailCreator);
        panelThumbnailCreator.setLayout(panelThumbnailCreatorLayout);
        panelThumbnailCreatorLayout.setHorizontalGroup(
            panelThumbnailCreatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailCreatorLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelThumbnailCreatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelThumbnailCreatorLayout.createSequentialGroup()
                        .addComponent(radioButtonCreateThumbnailsWithJavaImageIo)
                        .addGap(146, 146, 146))
                    .addGroup(panelThumbnailCreatorLayout.createSequentialGroup()
                        .addComponent(radioButtonCreateThumbnailsWithImagero, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                        .addGap(159, 159, 159))
                    .addComponent(radioButtonUseEmbeddedThumbnails)
                    .addGroup(panelThumbnailCreatorLayout.createSequentialGroup()
                        .addComponent(radioButtonCreateThumbnailsWithExternalApp, javax.swing.GroupLayout.DEFAULT_SIZE, 469, Short.MAX_VALUE)
                        .addGap(111, 111, 111)))
                .addContainerGap())
        );
        panelThumbnailCreatorLayout.setVerticalGroup(
            panelThumbnailCreatorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelThumbnailCreatorLayout.createSequentialGroup()
                .addComponent(radioButtonCreateThumbnailsWithJavaImageIo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonCreateThumbnailsWithImagero)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonUseEmbeddedThumbnails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(radioButtonCreateThumbnailsWithExternalApp)
                .addContainerGap())
        );

        panelExternalThumbnailApp.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.panelExternalThumbnailApp.border.title"))); // NOI18N

        labelIsCreateThumbnailsWithExternalApp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelIsCreateThumbnailsWithExternalApp.setLabelFor(textFieldExternalThumbnailCreationCommand);
        labelIsCreateThumbnailsWithExternalApp.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.labelIsCreateThumbnailsWithExternalApp.text")); // NOI18N
        labelIsCreateThumbnailsWithExternalApp.setPreferredSize(new java.awt.Dimension(1694, 60));

        textFieldExternalThumbnailCreationCommand.setEnabled(false);
        textFieldExternalThumbnailCreationCommand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldExternalThumbnailCreationCommandKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panelPadding2Layout = new javax.swing.GroupLayout(panelPadding2);
        panelPadding2.setLayout(panelPadding2Layout);
        panelPadding2Layout.setHorizontalGroup(
            panelPadding2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelPadding2Layout.setVerticalGroup(
            panelPadding2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout panelExternalThumbnailAppLayout = new javax.swing.GroupLayout(panelExternalThumbnailApp);
        panelExternalThumbnailApp.setLayout(panelExternalThumbnailAppLayout);
        panelExternalThumbnailAppLayout.setHorizontalGroup(
            panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                .addGroup(panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPadding2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelIsCreateThumbnailsWithExternalApp, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE))
                    .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(textFieldExternalThumbnailCreationCommand, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelExternalThumbnailAppLayout.setVerticalGroup(
            panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                .addGroup(panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPadding2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelIsCreateThumbnailsWithExternalApp, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(textFieldExternalThumbnailCreationCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelThumbnailDimensions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelThumbnailCreator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelThumbnailDimensions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelThumbnailCreator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void spinnerMaxThumbnailWidthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spinnerMaxThumbnailWidthStateChanged
        handleStateChangedSpinnerMaxThumbnailWidth();
    }//GEN-LAST:event_spinnerMaxThumbnailWidthStateChanged

    private void buttonUpdateAllThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateAllThumbnailsActionPerformed
        updateAllThumbnails();
    }//GEN-LAST:event_buttonUpdateAllThumbnailsActionPerformed

    private void textFieldExternalThumbnailCreationCommandKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldExternalThumbnailCreationCommandKeyReleased
        handleTextFieldExternalThumbnailCreationCommandKeyReleased();
    }//GEN-LAST:event_textFieldExternalThumbnailCreationCommandKeyReleased

    private void radioButtonCreateThumbnailsWithExternalAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCreateThumbnailsWithExternalAppActionPerformed
        setThumbnailCreator((JRadioButton) evt.getSource());
    }//GEN-LAST:event_radioButtonCreateThumbnailsWithExternalAppActionPerformed

    private void radioButtonUseEmbeddedThumbnailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonUseEmbeddedThumbnailsActionPerformed
        setThumbnailCreator((JRadioButton) evt.getSource());
    }//GEN-LAST:event_radioButtonUseEmbeddedThumbnailsActionPerformed

    private void radioButtonCreateThumbnailsWithImageroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCreateThumbnailsWithImageroActionPerformed
        setThumbnailCreator((JRadioButton) evt.getSource());
    }//GEN-LAST:event_radioButtonCreateThumbnailsWithImageroActionPerformed

    private void radioButtonCreateThumbnailsWithJavaImageIoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCreateThumbnailsWithJavaImageIoActionPerformed
        setThumbnailCreator((JRadioButton) evt.getSource());
    }//GEN-LAST:event_radioButtonCreateThumbnailsWithJavaImageIoActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupThumbnailCreator;
    private javax.swing.JButton buttonUpdateAllThumbnails;
    private javax.swing.JLabel labelIsCreateThumbnailsWithExternalApp;
    private javax.swing.JLabel labelMaxThumbnailWidth;
    private javax.swing.JLabel labelUpdateAllThumbnails;
    private javax.swing.JPanel panelExternalThumbnailApp;
    private javax.swing.JPanel panelPadding2;
    private javax.swing.JPanel panelThumbnailCreator;
    private javax.swing.JPanel panelThumbnailDimensions;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithExternalApp;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithImagero;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithJavaImageIo;
    private javax.swing.JRadioButton radioButtonUseEmbeddedThumbnails;
    private javax.swing.JSpinner spinnerMaxThumbnailWidth;
    private javax.swing.JTextField textFieldExternalThumbnailCreationCommand;
    // End of variables declaration//GEN-END:variables
}
