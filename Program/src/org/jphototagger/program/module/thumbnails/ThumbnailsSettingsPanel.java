package org.jphototagger.program.module.thumbnails;

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
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.domain.thumbnails.ExternalThumbnailCreationCommand;
import org.jphototagger.image.ImagePreferencesKeys;
import org.jphototagger.image.thumbnail.ThumbnailCreationStrategy;
import org.jphototagger.image.thumbnail.ThumbnailCreationStrategyProvider;
import org.jphototagger.lib.help.HelpPageProvider;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailsSettingsPanel extends javax.swing.JPanel implements Persistence, HelpPageProvider {

    private static final long serialVersionUID = 1L;
    private JPopupMenu createExternalThumbnailCreatorPopupMenu;
    private final Map<JRadioButton, ThumbnailCreationStrategy> thumbnailCreatorOfRadioButton = new HashMap<>();
    private final EnumMap<ThumbnailCreationStrategy, JRadioButton> radioButtonOfThumbnailCreator = new EnumMap<>(ThumbnailCreationStrategy.class);

    public ThumbnailsSettingsPanel() {
        org.jphototagger.resources.UiFactory.configure(this);
        initComponents();
        initMaps();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void initMaps() {
        thumbnailCreatorOfRadioButton.put(radioButtonCreateThumbnailsWithExternalApp, ThumbnailCreationStrategy.EXTERNAL_APP);
        thumbnailCreatorOfRadioButton.put(radioButtonCreateThumbnailsWithJPhotoTagger, ThumbnailCreationStrategy.JPHOTOTAGGER);
        for (JRadioButton radioButton : thumbnailCreatorOfRadioButton.keySet()) {
            radioButtonOfThumbnailCreator.put(thumbnailCreatorOfRadioButton.get(radioButton), radioButton);
        }
    }

    @Override
    public void restore() {
        restoreThumbnailCreationStrategy();
        restoreExternalThumbnailAppEnabled();
        panelSettingsThumbnailDimensions.restore();
        textFieldExternalThumbnailCreationCommand.setText(getExternalThumbnailCreationCommand());
        checkBoxDisplayThumbnailTooltip.setSelected(isDisplayThumbnailTooltip());
        checkBoxDisplayThumbnailsBottomPanel.setSelected(isDisplayThumbnailsBottomPanel());
    }

    private boolean isDisplayThumbnailTooltip() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAIL_TOOLTIP)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAIL_TOOLTIP)
                : true;
    }

    private boolean isDisplayThumbnailsBottomPanel() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAILS_BOTTOM_PANEL)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAILS_BOTTOM_PANEL)
                : true;
    }

    private String getExternalThumbnailCreationCommand() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.getString(ImagePreferencesKeys.KEY_THUMBNAIL_CREATION_EXTERNAL_COMMAND);
    }

    private void restoreThumbnailCreationStrategy() {
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

    private void restoreExternalThumbnailAppEnabled() {
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

    @Override
    public String getHelpPageUrl() {
        return Bundle.getString(ThumbnailDimensionsSettingsPanel.class, "SettingsThumbnailDimensionsPanel.HelpPage");
    }

    private void showThumbnailCreatorsSettingsDialog() {
        ThumbnailCreatorSettingsDialog dialog = new ThumbnailCreatorSettingsDialog();
        dialog.setVisible(true);
        ComponentUtil.parentWindowToFront(this);
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setString(ImagePreferencesKeys.KEY_THUMBNAIL_CREATION_EXTERNAL_COMMAND, command);
    }

    private void setDisplayThumbnailTooltip() {
        boolean isDisplayThumbnailTooltip = checkBoxDisplayThumbnailTooltip.isSelected();
        setDisplayThumbnailTooltip(isDisplayThumbnailTooltip);
    }

    private void setDisplayThumbnailsBottomPanel() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean display = checkBoxDisplayThumbnailsBottomPanel.isSelected();
        prefs.setBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAILS_BOTTOM_PANEL, display);
    }

    private void setDisplayThumbnailTooltip(boolean display) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setBoolean(AppPreferencesKeys.KEY_UI_DISPLAY_THUMBNAIL_TOOLTIP, display);
    }

    @Override
    public void persist() {}

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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setString(ImagePreferencesKeys.KEY_THUMBNAIL_CREATION_CREATOR, creator.name());
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
        panelSettingsThumbnailDimensions = new org.jphototagger.program.module.thumbnails.ThumbnailDimensionsSettingsPanel();
        panelThumbnailCreator = org.jphototagger.resources.UiFactory.panel();
        radioButtonCreateThumbnailsWithJPhotoTagger = UiFactory.radioButton();
        radioButtonCreateThumbnailsWithExternalApp = UiFactory.radioButton();
        buttonChooseExternalThumbnailCreator = org.jphototagger.resources.UiFactory.button();
        panelExternalThumbnailApp = org.jphototagger.resources.UiFactory.panel();
        labelIsCreateThumbnailsWithExternalApp = org.jphototagger.resources.UiFactory.label();
        textFieldExternalThumbnailCreationCommand = org.jphototagger.resources.UiFactory.textField();
        checkBoxDisplayThumbnailTooltip = org.jphototagger.resources.UiFactory.checkBox();
        checkBoxDisplayThumbnailsBottomPanel = org.jphototagger.resources.UiFactory.checkBox();
        panelFurtherSettings = org.jphototagger.resources.UiFactory.panel();
        buttonThumbnailCreatorsSettings = org.jphototagger.resources.UiFactory.button();
        panelPadding = org.jphototagger.resources.UiFactory.panel();

        setLayout(new java.awt.GridBagLayout());

        panelSettingsThumbnailDimensions.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ThumbnailsSettingsPanel.panelSettingsThumbnailDimensions.border.title"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(panelSettingsThumbnailDimensions, gridBagConstraints);

        panelThumbnailCreator.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ThumbnailsSettingsPanel.panelThumbnailCreator.border.title"))); // NOI18N
        panelThumbnailCreator.setLayout(new java.awt.GridBagLayout());

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithJPhotoTagger);
        radioButtonCreateThumbnailsWithJPhotoTagger.setText(Bundle.getString(getClass(), "ThumbnailsSettingsPanel.radioButtonCreateThumbnailsWithJPhotoTagger.text")); // NOI18N
        radioButtonCreateThumbnailsWithJPhotoTagger.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCreateThumbnailsWithJPhotoTaggerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelThumbnailCreator.add(radioButtonCreateThumbnailsWithJPhotoTagger, gridBagConstraints);

        buttonGroupThumbnailCreator.add(radioButtonCreateThumbnailsWithExternalApp);
        radioButtonCreateThumbnailsWithExternalApp.setText(Bundle.getString(getClass(), "ThumbnailsSettingsPanel.radioButtonCreateThumbnailsWithExternalApp.text")); // NOI18N
        radioButtonCreateThumbnailsWithExternalApp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radioButtonCreateThumbnailsWithExternalAppActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelThumbnailCreator.add(radioButtonCreateThumbnailsWithExternalApp, gridBagConstraints);

        buttonChooseExternalThumbnailCreator.setText(Bundle.getString(getClass(), "ThumbnailsSettingsPanel.buttonChooseExternalThumbnailCreator.text")); // NOI18N
        buttonChooseExternalThumbnailCreator.setEnabled(false);
        buttonChooseExternalThumbnailCreator.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseExternalThumbnailCreatorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 5, 5);
        panelThumbnailCreator.add(buttonChooseExternalThumbnailCreator, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(panelThumbnailCreator, gridBagConstraints);

        panelExternalThumbnailApp.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ThumbnailsSettingsPanel.panelExternalThumbnailApp.border.title"))); // NOI18N
        panelExternalThumbnailApp.setLayout(new java.awt.GridBagLayout());

        labelIsCreateThumbnailsWithExternalApp.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelIsCreateThumbnailsWithExternalApp.setLabelFor(textFieldExternalThumbnailCreationCommand);
        labelIsCreateThumbnailsWithExternalApp.setText(Bundle.getString(getClass(), "ThumbnailsSettingsPanel.labelIsCreateThumbnailsWithExternalApp.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelExternalThumbnailApp.add(labelIsCreateThumbnailsWithExternalApp, gridBagConstraints);

        textFieldExternalThumbnailCreationCommand.setEnabled(false);
        textFieldExternalThumbnailCreationCommand.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldExternalThumbnailCreationCommandKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 5, 5, 5);
        panelExternalThumbnailApp.add(textFieldExternalThumbnailCreationCommand, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(panelExternalThumbnailApp, gridBagConstraints);

        checkBoxDisplayThumbnailTooltip.setText(Bundle.getString(getClass(), "ThumbnailsSettingsPanel.checkBoxDisplayThumbnailTooltip.text")); // NOI18N
        checkBoxDisplayThumbnailTooltip.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplayThumbnailTooltipActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(checkBoxDisplayThumbnailTooltip, gridBagConstraints);

        checkBoxDisplayThumbnailsBottomPanel.setText(Bundle.getString(getClass(), "ThumbnailsSettingsPanel.checkBoxDisplayThumbnailsBottomPanel.text")); // NOI18N
        checkBoxDisplayThumbnailsBottomPanel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxDisplayThumbnailsBottomPanelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 10, 0, 10);
        add(checkBoxDisplayThumbnailsBottomPanel, gridBagConstraints);

        panelFurtherSettings.setName("panelFurtherSettings"); // NOI18N
        panelFurtherSettings.setLayout(new java.awt.GridBagLayout());

        buttonThumbnailCreatorsSettings.setText(Bundle.getString(getClass(), "ThumbnailsSettingsPanel.buttonThumbnailCreatorsSettings.text")); // NOI18N
        buttonThumbnailCreatorsSettings.setName("buttonThumbnailCreatorsSettings"); // NOI18N
        buttonThumbnailCreatorsSettings.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonThumbnailCreatorsSettingsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelFurtherSettings.add(buttonThumbnailCreatorsSettings, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        add(panelFurtherSettings, gridBagConstraints);

        panelPadding.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 0, 10, 0);
        add(panelPadding, gridBagConstraints);
    }//GEN-END:initComponents

    private void textFieldExternalThumbnailCreationCommandKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldExternalThumbnailCreationCommandKeyReleased
        handleTextFieldExternalThumbnailCreationCommandKeyReleased();
    }//GEN-LAST:event_textFieldExternalThumbnailCreationCommandKeyReleased

    private void radioButtonCreateThumbnailsWithExternalAppActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCreateThumbnailsWithExternalAppActionPerformed
        setThumbnailCreator((JRadioButton) evt.getSource());
    }//GEN-LAST:event_radioButtonCreateThumbnailsWithExternalAppActionPerformed

    private void radioButtonCreateThumbnailsWithJPhotoTaggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radioButtonCreateThumbnailsWithJPhotoTaggerActionPerformed
        setThumbnailCreator((JRadioButton) evt.getSource());
    }//GEN-LAST:event_radioButtonCreateThumbnailsWithJPhotoTaggerActionPerformed

    private void buttonChooseExternalThumbnailCreatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseExternalThumbnailCreatorActionPerformed
        chooseExternalThumbnailCreator();
    }//GEN-LAST:event_buttonChooseExternalThumbnailCreatorActionPerformed

    private void checkBoxDisplayThumbnailTooltipActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplayThumbnailTooltipActionPerformed
        setDisplayThumbnailTooltip();
    }//GEN-LAST:event_checkBoxDisplayThumbnailTooltipActionPerformed

    private void checkBoxDisplayThumbnailsBottomPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplayThumbnailsBottomPanelActionPerformed
        setDisplayThumbnailsBottomPanel();
    }//GEN-LAST:event_checkBoxDisplayThumbnailsBottomPanelActionPerformed

    private void buttonThumbnailCreatorsSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonThumbnailCreatorsSettingsActionPerformed
        showThumbnailCreatorsSettingsDialog();
    }//GEN-LAST:event_buttonThumbnailCreatorsSettingsActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonChooseExternalThumbnailCreator;
    private javax.swing.ButtonGroup buttonGroupThumbnailCreator;
    private javax.swing.JButton buttonThumbnailCreatorsSettings;
    private javax.swing.JCheckBox checkBoxDisplayThumbnailTooltip;
    private javax.swing.JCheckBox checkBoxDisplayThumbnailsBottomPanel;
    private javax.swing.JLabel labelIsCreateThumbnailsWithExternalApp;
    private javax.swing.JPanel panelExternalThumbnailApp;
    private javax.swing.JPanel panelFurtherSettings;
    private javax.swing.JPanel panelPadding;
    private org.jphototagger.program.module.thumbnails.ThumbnailDimensionsSettingsPanel panelSettingsThumbnailDimensions;
    private javax.swing.JPanel panelThumbnailCreator;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithExternalApp;
    private javax.swing.JRadioButton radioButtonCreateThumbnailsWithJPhotoTagger;
    private javax.swing.JTextField textFieldExternalThumbnailCreationCommand;
    // End of variables declaration//GEN-END:variables
}
