package org.jphototagger.program.view.panels;

import java.awt.event.ActionEvent;
import java.util.Collection;
import org.jphototagger.program.image.thumbnail.ThumbnailCreator;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.MnemonicUtil;

import java.awt.Container;
import java.util.EnumMap;

import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;

import javax.swing.JRadioButton;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.services.ExternalThumbnailCreator;

/**
 *
 * @author Elmar Baumann
 */
public final class SettingsThumbnailsPanel extends javax.swing.JPanel implements Persistence {
    private static final long serialVersionUID = -5283587664627790755L;
    private JPopupMenu createExternalThumbnailCreatorPopupMenu;
    private final Map<JRadioButton, ThumbnailCreator> thumbnailCreatorOfRadioButton = new HashMap<JRadioButton, ThumbnailCreator>();
    private final EnumMap<ThumbnailCreator, JRadioButton> radioButtonOfThumbnailCreator = new EnumMap<ThumbnailCreator, JRadioButton>(ThumbnailCreator.class);

    public SettingsThumbnailsPanel() {
        initComponents();
        initMaps();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void initMaps() {
        thumbnailCreatorOfRadioButton.put(radioButtonCreateThumbnailsWithExternalApp, ThumbnailCreator.EXTERNAL_APP);
        thumbnailCreatorOfRadioButton.put(radioButtonCreateThumbnailsWithImagero, ThumbnailCreator.IMAGERO);
        thumbnailCreatorOfRadioButton.put(radioButtonCreateThumbnailsWithJavaImageIo, ThumbnailCreator.JAVA_IMAGE_IO);
        thumbnailCreatorOfRadioButton.put(radioButtonUseEmbeddedThumbnails, ThumbnailCreator.EMBEDDED);

        for (JRadioButton radioButton : thumbnailCreatorOfRadioButton.keySet()) {
            radioButtonOfThumbnailCreator.put(thumbnailCreatorOfRadioButton.get(radioButton), radioButton);
        }
    }

    @Override
    public void readProperties() {
        setSelectedRadioButtons();
        setExternalThumbnailAppEnabled();
        panelSettingsThumbnailDimensions.readProperties();

        UserSettings settings = UserSettings.INSTANCE;

        textFieldExternalThumbnailCreationCommand.setText(settings.getExternalThumbnailCreationCommand());
        checkBoxDisplayThumbnailTooltip.setSelected(settings.isDisplayThumbnailTooltip());
    }

    private void setSelectedRadioButtons() {
        ThumbnailCreator creator = UserSettings.INSTANCE.getThumbnailCreator();

        for (JRadioButton radioButton : radioButtonOfThumbnailCreator.values()) {
            JRadioButton radioButtonOfCreator = radioButtonOfThumbnailCreator.get(creator);

            radioButton.setSelected(radioButtonOfCreator == radioButton);
        }
    }

    private void setExternalThumbnailAppEnabled() {
        boolean isCreatorExternalApp = UserSettings.INSTANCE.getThumbnailCreator().equals(ThumbnailCreator.EXTERNAL_APP);

        textFieldExternalThumbnailCreationCommand.setEnabled(isCreatorExternalApp);
        buttonChooseExternalThumbnailCreator.setEnabled(isCreatorExternalApp);
    }

    private void chooseExternalThumbnailCreator() {
        if (createExternalThumbnailCreatorPopupMenu == null) {
            createExternalThumbnailCreatorPopupMenu = createExternalThumbnailCreatorPopupMenu();
        }
        
        int buttonHeight = buttonChooseExternalThumbnailCreator.getHeight();

        createExternalThumbnailCreatorPopupMenu.show(buttonChooseExternalThumbnailCreator, 0, buttonHeight);
    }
    
    private JPopupMenu createExternalThumbnailCreatorPopupMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        Collection<? extends ExternalThumbnailCreator> externalThumbnailCreators = ServiceLookup.lookupAll(ExternalThumbnailCreator.class);
        
        for (ExternalThumbnailCreator externalThumbnailCreator : externalThumbnailCreators) {
            ExternalThumbnailCreatorAction action = new ExternalThumbnailCreatorAction(externalThumbnailCreator);
            
            popupMenu.add(action);
        }
        
        return popupMenu;
    }
    
    private class ExternalThumbnailCreatorAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private final ExternalThumbnailCreator externalThumbnailCreator;

        private ExternalThumbnailCreatorAction(ExternalThumbnailCreator externalThumbnailCreator) {
            putValue(Action.NAME, externalThumbnailCreator.getDisplayName());
            this.externalThumbnailCreator = externalThumbnailCreator;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String thumbnailCreationCommand = externalThumbnailCreator.getThumbnailCreationCommand();
            
            if (thumbnailCreationCommand != null) {
                textFieldExternalThumbnailCreationCommand.setText(thumbnailCreationCommand);
                UserSettings.INSTANCE.setExternalThumbnailCreationCommand(thumbnailCreationCommand);
            }
        }
        
    }

    private void setDisplayThumbnailTooltip() {
        boolean isDisplayThumbnailTooltip = checkBoxDisplayThumbnailTooltip.isSelected();

        UserSettings.INSTANCE.setDisplayThumbnailTooltip(isDisplayThumbnailTooltip);
    }

    @Override
    public void writeProperties() {}

    private void handleTextFieldExternalThumbnailCreationCommandKeyReleased() {
        UserSettings.INSTANCE.setExternalThumbnailCreationCommand(textFieldExternalThumbnailCreationCommand.getText());
    }

    private void setThumbnailCreator(JRadioButton radioButton) {
        UserSettings.INSTANCE.setThumbnailCreator(thumbnailCreatorOfRadioButton.get(radioButton));
        boolean selected = radioButtonCreateThumbnailsWithExternalApp.isSelected();

        textFieldExternalThumbnailCreationCommand.setEnabled(selected);
        buttonChooseExternalThumbnailCreator.setEnabled(selected);
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
        panelSettingsThumbnailDimensions = new org.jphototagger.program.view.panels.SettingsThumbnailDimensionsPanel();
        panelThumbnailCreator = new javax.swing.JPanel();
        radioButtonCreateThumbnailsWithJavaImageIo = new javax.swing.JRadioButton();
        radioButtonCreateThumbnailsWithImagero = new javax.swing.JRadioButton();
        radioButtonUseEmbeddedThumbnails = new javax.swing.JRadioButton();
        radioButtonCreateThumbnailsWithExternalApp = new javax.swing.JRadioButton();
        panelExternalThumbnailApp = new javax.swing.JPanel();
        labelIsCreateThumbnailsWithExternalApp = new javax.swing.JLabel();
        textFieldExternalThumbnailCreationCommand = new javax.swing.JTextField();
        panelPadding2 = new javax.swing.JPanel();
        buttonChooseExternalThumbnailCreator = new javax.swing.JButton();
        checkBoxDisplayThumbnailTooltip = new javax.swing.JCheckBox();

        setName("Form"); // NOI18N

        panelSettingsThumbnailDimensions.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.panelSettingsThumbnailDimensions.border.title"))); // NOI18N
        panelSettingsThumbnailDimensions.setName("panelSettingsThumbnailDimensions"); // NOI18N

        panelThumbnailCreator.setBorder(javax.swing.BorderFactory.createTitledBorder(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.panelThumbnailCreator.border.title"))); // NOI18N
        panelThumbnailCreator.setName("panelThumbnailCreator"); // NOI18N

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithJavaImageIo);
        radioButtonCreateThumbnailsWithJavaImageIo.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.radioButtonCreateThumbnailsWithJavaImageIo.text")); // NOI18N
        radioButtonCreateThumbnailsWithJavaImageIo.setName("radioButtonCreateThumbnailsWithJavaImageIo"); // NOI18N
        radioButtonCreateThumbnailsWithJavaImageIo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCreateThumbnailsWithJavaImageIoActionPerformed(evt);
            }
        });

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithImagero);
        radioButtonCreateThumbnailsWithImagero.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.radioButtonCreateThumbnailsWithImagero.text")); // NOI18N
        radioButtonCreateThumbnailsWithImagero.setName("radioButtonCreateThumbnailsWithImagero"); // NOI18N
        radioButtonCreateThumbnailsWithImagero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCreateThumbnailsWithImageroActionPerformed(evt);
            }
        });

        buttonGroupThumbnailCreator.add(radioButtonUseEmbeddedThumbnails);
        radioButtonUseEmbeddedThumbnails.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.radioButtonUseEmbeddedThumbnails.text")); // NOI18N
        radioButtonUseEmbeddedThumbnails.setName("radioButtonUseEmbeddedThumbnails"); // NOI18N
        radioButtonUseEmbeddedThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonUseEmbeddedThumbnailsActionPerformed(evt);
            }
        });

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithExternalApp);
        radioButtonCreateThumbnailsWithExternalApp.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.radioButtonCreateThumbnailsWithExternalApp.text")); // NOI18N
        radioButtonCreateThumbnailsWithExternalApp.setName("radioButtonCreateThumbnailsWithExternalApp"); // NOI18N
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
                        .addComponent(radioButtonCreateThumbnailsWithImagero, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                        .addGap(159, 159, 159))
                    .addComponent(radioButtonUseEmbeddedThumbnails)
                    .addGroup(panelThumbnailCreatorLayout.createSequentialGroup()
                        .addComponent(radioButtonCreateThumbnailsWithExternalApp, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
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
        panelExternalThumbnailApp.setName("panelExternalThumbnailApp"); // NOI18N

        labelIsCreateThumbnailsWithExternalApp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelIsCreateThumbnailsWithExternalApp.setLabelFor(textFieldExternalThumbnailCreationCommand);
        labelIsCreateThumbnailsWithExternalApp.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.labelIsCreateThumbnailsWithExternalApp.text")); // NOI18N
        labelIsCreateThumbnailsWithExternalApp.setName("labelIsCreateThumbnailsWithExternalApp"); // NOI18N
        labelIsCreateThumbnailsWithExternalApp.setPreferredSize(new java.awt.Dimension(1694, 60));

        textFieldExternalThumbnailCreationCommand.setEnabled(false);
        textFieldExternalThumbnailCreationCommand.setName("textFieldExternalThumbnailCreationCommand"); // NOI18N
        textFieldExternalThumbnailCreationCommand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldExternalThumbnailCreationCommandKeyReleased(evt);
            }
        });

        panelPadding2.setName("panelPadding2"); // NOI18N

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

        buttonChooseExternalThumbnailCreator.setText(JptBundle.INSTANCE.getString("SettingsThumbnailsPanel.buttonChooseExternalThumbnailCreator.text")); // NOI18N
        buttonChooseExternalThumbnailCreator.setEnabled(false);
        buttonChooseExternalThumbnailCreator.setName("buttonChooseExternalThumbnailCreator"); // NOI18N
        buttonChooseExternalThumbnailCreator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseExternalThumbnailCreatorActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelExternalThumbnailAppLayout = new javax.swing.GroupLayout(panelExternalThumbnailApp);
        panelExternalThumbnailApp.setLayout(panelExternalThumbnailAppLayout);
        panelExternalThumbnailAppLayout.setHorizontalGroup(
            panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                .addGroup(panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPadding2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(labelIsCreateThumbnailsWithExternalApp, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelExternalThumbnailAppLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(textFieldExternalThumbnailCreationCommand, javax.swing.GroupLayout.DEFAULT_SIZE, 550, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonChooseExternalThumbnailCreator)))
                .addContainerGap())
        );
        panelExternalThumbnailAppLayout.setVerticalGroup(
            panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelExternalThumbnailAppLayout.createSequentialGroup()
                .addGroup(panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelPadding2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelIsCreateThumbnailsWithExternalApp, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelExternalThumbnailAppLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldExternalThumbnailCreationCommand, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonChooseExternalThumbnailCreator))
                .addContainerGap())
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/resource/properties/Bundle"); // NOI18N
        checkBoxDisplayThumbnailTooltip.setText(bundle.getString("SettingsThumbnailsPanel.checkBoxDisplayThumbnailTooltip.text")); // NOI18N
        checkBoxDisplayThumbnailTooltip.setName("checkBoxDisplayThumbnailTooltip"); // NOI18N
        checkBoxDisplayThumbnailTooltip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplayThumbnailTooltipActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(checkBoxDisplayThumbnailTooltip)
                    .addComponent(panelSettingsThumbnailDimensions, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
                    .addComponent(panelThumbnailCreator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSettingsThumbnailDimensions, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelThumbnailCreator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxDisplayThumbnailTooltip)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

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

    private void buttonChooseExternalThumbnailCreatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseExternalThumbnailCreatorActionPerformed
        chooseExternalThumbnailCreator();
    }//GEN-LAST:event_buttonChooseExternalThumbnailCreatorActionPerformed

    private void checkBoxDisplayThumbnailTooltipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplayThumbnailTooltipActionPerformed
        setDisplayThumbnailTooltip();
    }//GEN-LAST:event_checkBoxDisplayThumbnailTooltipActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChooseExternalThumbnailCreator;
    private javax.swing.ButtonGroup buttonGroupThumbnailCreator;
    private javax.swing.JCheckBox checkBoxDisplayThumbnailTooltip;
    private javax.swing.JLabel labelIsCreateThumbnailsWithExternalApp;
    private javax.swing.JPanel panelExternalThumbnailApp;
    private javax.swing.JPanel panelPadding2;
    private org.jphototagger.program.view.panels.SettingsThumbnailDimensionsPanel panelSettingsThumbnailDimensions;
    private javax.swing.JPanel panelThumbnailCreator;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithExternalApp;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithImagero;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithJavaImageIo;
    private javax.swing.JRadioButton radioButtonUseEmbeddedThumbnails;
    private javax.swing.JTextField textFieldExternalThumbnailCreationCommand;
    // End of variables declaration//GEN-END:variables
}
