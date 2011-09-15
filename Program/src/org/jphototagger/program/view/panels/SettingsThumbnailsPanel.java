package org.jphototagger.program.view.panels;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;

import org.jphototagger.api.image.thumbnails.ExternalThumbnailCreationCommand;
import org.jphototagger.api.storage.Storage;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.program.image.thumbnail.ThumbnailCreationStrategy;
import org.jphototagger.program.image.thumbnail.ThumbnailCreationStrategyProvider;
import org.jphototagger.program.types.Persistence;
import org.openide.util.Lookup;

/**
 *
 * @author Elmar Baumann
 */
public final class SettingsThumbnailsPanel extends javax.swing.JPanel implements Persistence {
    private static final long serialVersionUID = -5283587664627790755L;
    private JPopupMenu createExternalThumbnailCreatorPopupMenu;
    private final Map<JRadioButton, ThumbnailCreationStrategy> thumbnailCreatorOfRadioButton = new HashMap<JRadioButton, ThumbnailCreationStrategy>();
    private final EnumMap<ThumbnailCreationStrategy, JRadioButton> radioButtonOfThumbnailCreator = new EnumMap<ThumbnailCreationStrategy, JRadioButton>(ThumbnailCreationStrategy.class);

    public SettingsThumbnailsPanel() {
        initComponents();
        initMaps();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void initMaps() {
        thumbnailCreatorOfRadioButton.put(radioButtonCreateThumbnailsWithExternalApp, ThumbnailCreationStrategy.EXTERNAL_APP);
        thumbnailCreatorOfRadioButton.put(radioButtonCreateThumbnailsWithImagero, ThumbnailCreationStrategy.IMAGERO);
        thumbnailCreatorOfRadioButton.put(radioButtonCreateThumbnailsWithJavaImageIo, ThumbnailCreationStrategy.JAVA_IMAGE_IO);
        thumbnailCreatorOfRadioButton.put(radioButtonUseEmbeddedThumbnails, ThumbnailCreationStrategy.EMBEDDED);

        for (JRadioButton radioButton : thumbnailCreatorOfRadioButton.keySet()) {
            radioButtonOfThumbnailCreator.put(thumbnailCreatorOfRadioButton.get(radioButton), radioButton);
        }
    }

    @Override
    public void readProperties() {
        setSelectedRadioButtons();
        setExternalThumbnailAppEnabled();
        panelSettingsThumbnailDimensions.readProperties();
        textFieldExternalThumbnailCreationCommand.setText(getExternalThumbnailCreationCommand());
        checkBoxDisplayThumbnailTooltip.setSelected(isDisplayThumbnailTooltip());
    }

    private boolean isDisplayThumbnailTooltip() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.containsKey(Storage.KEY_DISPLAY_THUMBNAIL_TOOLTIP)
                ? storage.getBoolean(Storage.KEY_DISPLAY_THUMBNAIL_TOOLTIP)
                : true;
    }

    private String getExternalThumbnailCreationCommand() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        return storage.getString(Storage.KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND);
    }

    private void setSelectedRadioButtons() {
        ThumbnailCreationStrategy strategy = getThumbnailCreationStrategy();

        for (JRadioButton radioButton : radioButtonOfThumbnailCreator.values()) {
            JRadioButton radioButtonOfCreator = radioButtonOfThumbnailCreator.get(strategy);

            radioButton.setSelected(radioButtonOfCreator == radioButton);
        }
    }

    private ThumbnailCreationStrategy getThumbnailCreationStrategy() {
        ThumbnailCreationStrategyProvider provider = Lookup.getDefault().lookup(ThumbnailCreationStrategyProvider.class);

        return provider.getThumbnailCreationStrategy();
    }

    private void setExternalThumbnailAppEnabled() {
        ThumbnailCreationStrategy strategy = getThumbnailCreationStrategy();
        boolean isCreatorExternalApp = strategy.equals(ThumbnailCreationStrategy.EXTERNAL_APP);

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
        Collection<? extends ExternalThumbnailCreationCommand> externalThumbnailCreators = Lookup.getDefault().lookupAll(ExternalThumbnailCreationCommand.class);

        for (ExternalThumbnailCreationCommand externalThumbnailCreator : externalThumbnailCreators) {
            if (externalThumbnailCreator.isEnabled()) {
                ExternalThumbnailCreatorAction action = new ExternalThumbnailCreatorAction(externalThumbnailCreator);

                popupMenu.add(action);
            }
        }

        return popupMenu;
    }

    private class ExternalThumbnailCreatorAction extends AbstractAction {
        private static final long serialVersionUID = 1L;
        private final ExternalThumbnailCreationCommand externalThumbnailCreator;

        private ExternalThumbnailCreatorAction(ExternalThumbnailCreationCommand externalThumbnailCreator) {
            putValue(Action.NAME, externalThumbnailCreator.getDisplayName());
            this.externalThumbnailCreator = externalThumbnailCreator;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String thumbnailCreationCommand = externalThumbnailCreator.getThumbnailCreationCommand();

            if (thumbnailCreationCommand != null) {
                textFieldExternalThumbnailCreationCommand.setText(thumbnailCreationCommand);
                setExternalThumbnailCreationCommand(thumbnailCreationCommand);
            }
        }

    }

    private void setExternalThumbnailCreationCommand(String command) {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        storage.setString(Storage.KEY_EXTERNAL_THUMBNAIL_CREATION_COMMAND, command);
    }

    private void setDisplayThumbnailTooltip() {
        boolean isDisplayThumbnailTooltip = checkBoxDisplayThumbnailTooltip.isSelected();

        setDisplayThumbnailTooltip(isDisplayThumbnailTooltip);
    }

    private void setDisplayThumbnailTooltip(boolean display) {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        storage.setBoolean(Storage.KEY_DISPLAY_THUMBNAIL_TOOLTIP, display);
    }

    @Override
    public void writeProperties() {}

    private void handleTextFieldExternalThumbnailCreationCommandKeyReleased() {
        setExternalThumbnailCreationCommand(textFieldExternalThumbnailCreationCommand.getText());
    }

    private void setThumbnailCreator(JRadioButton radioButton) {
        setThumbnailCreationStrategy(thumbnailCreatorOfRadioButton.get(radioButton));
        boolean selected = radioButtonCreateThumbnailsWithExternalApp.isSelected();

        textFieldExternalThumbnailCreationCommand.setEnabled(selected);
        buttonChooseExternalThumbnailCreator.setEnabled(selected);
    }

    private void setThumbnailCreationStrategy(ThumbnailCreationStrategy creator) {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        storage.setString(Storage.KEY_THUMBNAIL_CREATOR, creator.name());
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
        buttonChooseExternalThumbnailCreator = new javax.swing.JButton();
        checkBoxDisplayThumbnailTooltip = new javax.swing.JCheckBox();

        setName("Form"); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        panelSettingsThumbnailDimensions.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsThumbnailsPanel.panelSettingsThumbnailDimensions.border.title"))); // NOI18N
        panelSettingsThumbnailDimensions.setName("panelSettingsThumbnailDimensions"); // NOI18N

        panelThumbnailCreator.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsThumbnailsPanel.panelThumbnailCreator.border.title"))); // NOI18N
        panelThumbnailCreator.setName("panelThumbnailCreator"); // NOI18N

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithJavaImageIo);
        radioButtonCreateThumbnailsWithJavaImageIo.setText(bundle.getString("SettingsThumbnailsPanel.radioButtonCreateThumbnailsWithJavaImageIo.text")); // NOI18N
        radioButtonCreateThumbnailsWithJavaImageIo.setName("radioButtonCreateThumbnailsWithJavaImageIo"); // NOI18N
        radioButtonCreateThumbnailsWithJavaImageIo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCreateThumbnailsWithJavaImageIoActionPerformed(evt);
            }
        });

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithImagero);
        radioButtonCreateThumbnailsWithImagero.setText(bundle.getString("SettingsThumbnailsPanel.radioButtonCreateThumbnailsWithImagero.text")); // NOI18N
        radioButtonCreateThumbnailsWithImagero.setName("radioButtonCreateThumbnailsWithImagero"); // NOI18N
        radioButtonCreateThumbnailsWithImagero.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCreateThumbnailsWithImageroActionPerformed(evt);
            }
        });

        buttonGroupThumbnailCreator.add(radioButtonUseEmbeddedThumbnails);
        radioButtonUseEmbeddedThumbnails.setText(bundle.getString("SettingsThumbnailsPanel.radioButtonUseEmbeddedThumbnails.text")); // NOI18N
        radioButtonUseEmbeddedThumbnails.setName("radioButtonUseEmbeddedThumbnails"); // NOI18N
        radioButtonUseEmbeddedThumbnails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonUseEmbeddedThumbnailsActionPerformed(evt);
            }
        });

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithExternalApp);
        radioButtonCreateThumbnailsWithExternalApp.setText(bundle.getString("SettingsThumbnailsPanel.radioButtonCreateThumbnailsWithExternalApp.text")); // NOI18N
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
                        .addComponent(radioButtonCreateThumbnailsWithImagero, javax.swing.GroupLayout.DEFAULT_SIZE, 818, Short.MAX_VALUE)
                        .addGap(159, 159, 159))
                    .addComponent(radioButtonUseEmbeddedThumbnails)
                    .addGroup(panelThumbnailCreatorLayout.createSequentialGroup()
                        .addComponent(radioButtonCreateThumbnailsWithExternalApp, javax.swing.GroupLayout.DEFAULT_SIZE, 866, Short.MAX_VALUE)
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

        panelExternalThumbnailApp.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("SettingsThumbnailsPanel.panelExternalThumbnailApp.border.title"))); // NOI18N
        panelExternalThumbnailApp.setName("panelExternalThumbnailApp"); // NOI18N
        panelExternalThumbnailApp.setLayout(new java.awt.GridBagLayout());

        labelIsCreateThumbnailsWithExternalApp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelIsCreateThumbnailsWithExternalApp.setLabelFor(textFieldExternalThumbnailCreationCommand);
        labelIsCreateThumbnailsWithExternalApp.setText(bundle.getString("SettingsThumbnailsPanel.labelIsCreateThumbnailsWithExternalApp.text")); // NOI18N
        labelIsCreateThumbnailsWithExternalApp.setName("labelIsCreateThumbnailsWithExternalApp"); // NOI18N
        labelIsCreateThumbnailsWithExternalApp.setPreferredSize(new java.awt.Dimension(1694, 60));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelExternalThumbnailApp.add(labelIsCreateThumbnailsWithExternalApp, gridBagConstraints);

        textFieldExternalThumbnailCreationCommand.setEnabled(false);
        textFieldExternalThumbnailCreationCommand.setName("textFieldExternalThumbnailCreationCommand"); // NOI18N
        textFieldExternalThumbnailCreationCommand.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldExternalThumbnailCreationCommandKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 0);
        panelExternalThumbnailApp.add(textFieldExternalThumbnailCreationCommand, gridBagConstraints);

        buttonChooseExternalThumbnailCreator.setText(bundle.getString("SettingsThumbnailsPanel.buttonChooseExternalThumbnailCreator.text")); // NOI18N
        buttonChooseExternalThumbnailCreator.setEnabled(false);
        buttonChooseExternalThumbnailCreator.setName("buttonChooseExternalThumbnailCreator"); // NOI18N
        buttonChooseExternalThumbnailCreator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseExternalThumbnailCreatorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        panelExternalThumbnailApp.add(buttonChooseExternalThumbnailCreator, gridBagConstraints);

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
                    .addComponent(panelSettingsThumbnailDimensions, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelThumbnailCreator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 995, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSettingsThumbnailDimensions, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelThumbnailCreator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelExternalThumbnailApp, javax.swing.GroupLayout.PREFERRED_SIZE, 130, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkBoxDisplayThumbnailTooltip)
                .addContainerGap())
        );
    }//GEN-END:initComponents

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
    private org.jphototagger.program.view.panels.SettingsThumbnailDimensionsPanel panelSettingsThumbnailDimensions;
    private javax.swing.JPanel panelThumbnailCreator;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithExternalApp;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithImagero;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithJavaImageIo;
    private javax.swing.JRadioButton radioButtonUseEmbeddedThumbnails;
    private javax.swing.JTextField textFieldExternalThumbnailCreationCommand;
    // End of variables declaration//GEN-END:variables
}
