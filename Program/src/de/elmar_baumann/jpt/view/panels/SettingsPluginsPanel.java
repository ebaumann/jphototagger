/*
 * JPhotoTagger tags and finds images fast.
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

import de.elmar_baumann.jpt.factory.PluginManager;
import de.elmar_baumann.jpt.plugin.Plugin;
import de.elmar_baumann.jpt.types.Persistence;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.lib.componentutil.ComponentUtil;
import de.elmar_baumann.lib.componentutil.MnemonicUtil;
import de.elmar_baumann.lib.dialog.HelpBrowser;
import de.elmar_baumann.lib.generics.Pair;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.GridBagConstraints;

import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * Dynamically adds panels of plugins ({@link Plugin#getSettingsPanel()}).
 *
 * @author  Elmar Baumann
 * @version 2009-08-27
 */
public class SettingsPluginsPanel extends javax.swing.JPanel
        implements ChangeListener, Persistence {
    private static final long   serialVersionUID = 6790634142245254676L;
    private static final String KEY_TABBED_PANE  =
        "SettingsPluginsPanel.TabbedPane";
    private final Map<Component, Pair<String, String>> helpContentsPathOfTab =
        new HashMap<Component, Pair<String, String>>();

    public SettingsPluginsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        for (Plugin plugin : PluginManager.INSTANCE.getPlugins()) {
            addPluginSettingsPanel(plugin);
        }

        for (Plugin plugin : PluginManager.INSTANCE.getAllPlugins()) {
            addPluginCheckBox(plugin);
        }

        panelExcludeCheckboxes.add(new JPanel(), getGbcAfterLastCheckBox());    // ensures checkboxes vertically top and not centered
        MnemonicUtil.setMnemonics((Container) this);
        setEnabledHelpButton();
        tabbedPane.addChangeListener(this);
    }

    private void addPluginSettingsPanel(Plugin plugin) {
        JPanel panel = plugin.getSettingsPanel();

        if (panel != null) {
            helpContentsPathOfTab.put(
                panel,
                new Pair<String, String>(
                    plugin.getHelpContentsPath(),
                    plugin.getFirstHelpPageName()));
            tabbedPane.add(plugin.getName(), panel);
        }
    }

    private void addPluginCheckBox(Plugin plugin) {
        JCheckBox checkBox = new JCheckBox(new ActionExcludePlugin(plugin));

        checkBox.setSelected(!PluginManager.INSTANCE.isExcluded(plugin));
        panelExcludeCheckboxes.add(checkBox, getGbcCheckBox());
    }

    private GridBagConstraints getGbcCheckBox() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx  = GridBagConstraints.REMAINDER;
        gbc.gridy  = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill   = GridBagConstraints.NONE;

        return gbc;
    }

    private GridBagConstraints getGbcAfterLastCheckBox() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx   = GridBagConstraints.REMAINDER;
        gbc.gridy   = GridBagConstraints.RELATIVE;
        gbc.anchor  = GridBagConstraints.SOUTH;
        gbc.fill    = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        return gbc;
    }

    @Override
    public void readProperties() {
        UserSettings.INSTANCE.getSettings().applySettings(tabbedPane,
                KEY_TABBED_PANE, null);
    }

    @Override
    public void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(tabbedPane, KEY_TABBED_PANE,
                null);
        UserSettings.INSTANCE.writeToFile();
    }

    private static class ActionExcludePlugin extends AbstractAction {
        private static final long      serialVersionUID = -7156530079287891717L;
        private transient final Plugin plugin;

        public ActionExcludePlugin(Plugin plugin) {
            this.plugin = plugin;
            putValue(Action.NAME, plugin.getName());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JCheckBox cb = (JCheckBox) e.getSource();

            PluginManager.INSTANCE.exclude(plugin, !cb.isSelected());
        }
    }


    private void showHelp() {
        String helpContentsPath =
            helpContentsPathOfTab.get(
                tabbedPane.getSelectedComponent()).getFirst();
        String firstPageUrl =
            helpContentsPathOfTab.get(
                tabbedPane.getSelectedComponent()).getSecond();

        if (helpContentsPath != null) {
            HelpBrowser help = HelpBrowser.INSTANCE;

            help.setContentsUrl(helpContentsPath);

            if (firstPageUrl != null) {
                help.setDisplayUrl(firstPageUrl);
            }

            ComponentUtil.show(help);
        }
    }

    private void setEnabledHelpButton() {
        buttonHelpPlugin.setEnabled(
            helpContentsPathOfTab.get(tabbedPane.getSelectedComponent())
            != null);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        setEnabledHelpButton();
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
        buttonGroupActionsAfterDatabaseInsertion =
            new javax.swing.ButtonGroup();
        tabbedPane             = new javax.swing.JTabbedPane();
        panelExclude           = new javax.swing.JPanel();
        labelInfoExclude       = new javax.swing.JLabel();
        scrollPaneExclude      = new javax.swing.JScrollPane();
        panelExcludeCheckboxes = new javax.swing.JPanel();
        buttonHelpPlugin       = new javax.swing.JButton();
        tabbedPane.setTabLayoutPolicy(
            javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);

        java.util.ResourceBundle bundle =
            java.util.ResourceBundle.getBundle(
                "de/elmar_baumann/jpt/resource/properties/Bundle");    // NOI18N

        labelInfoExclude.setText(
            bundle.getString("SettingsPluginsPanel.labelInfoExclude.text"));    // NOI18N
        scrollPaneExclude.setAlignmentX(0.0F);
        scrollPaneExclude.setAlignmentY(0.0F);
        panelExcludeCheckboxes.setLayout(new java.awt.GridBagLayout());
        scrollPaneExclude.setViewportView(panelExcludeCheckboxes);

        javax.swing.GroupLayout panelExcludeLayout =
            new javax.swing.GroupLayout(panelExclude);

        panelExclude.setLayout(panelExcludeLayout);
        panelExcludeLayout
            .setHorizontalGroup(panelExcludeLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelExcludeLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(panelExcludeLayout
                        .createParallelGroup(javax.swing.GroupLayout.Alignment
                            .LEADING)
                                .addComponent(scrollPaneExclude,
                                    javax.swing.GroupLayout.DEFAULT_SIZE, 504,
                                    Short.MAX_VALUE)
                                        .addComponent(labelInfoExclude))
                                            .addContainerGap()));
        panelExcludeLayout
            .setVerticalGroup(panelExcludeLayout
                .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(panelExcludeLayout.createSequentialGroup()
                    .addContainerGap().addComponent(labelInfoExclude)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement
                        .RELATED)
                            .addComponent(scrollPaneExclude,
                                          javax.swing.GroupLayout.DEFAULT_SIZE,
                                          152, Short.MAX_VALUE)
                                              .addContainerGap()));
        tabbedPane.addTab(
            bundle.getString(
                "SettingsPluginsPanel.panelExclude.TabConstraints.tabTitle"), panelExclude);    // NOI18N
        buttonHelpPlugin.setText(
            bundle.getString("SettingsPluginsPanel.buttonHelpPlugin.text"));    // NOI18N
        buttonHelpPlugin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHelpPluginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);

        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(
                javax.swing.GroupLayout.Alignment.LEADING).addComponent(
                tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 533,
                Short.MAX_VALUE).addGroup(
                    javax.swing.GroupLayout.Alignment.TRAILING,
                    layout.createSequentialGroup().addContainerGap(
                        449, Short.MAX_VALUE).addComponent(
                        buttonHelpPlugin).addContainerGap()));
        layout.setVerticalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout
                .createSequentialGroup()
                .addComponent(tabbedPane, javax.swing.GroupLayout
                    .DEFAULT_SIZE, 224, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle
                            .ComponentPlacement.RELATED)
                                .addComponent(buttonHelpPlugin)
                                .addContainerGap()));
    }    // </editor-fold>//GEN-END:initComponents

    private void buttonHelpPluginActionPerformed(
            java.awt.event.ActionEvent evt) {    // GEN-FIRST:event_buttonHelpPluginActionPerformed
        showHelp();
    }    // GEN-LAST:event_buttonHelpPluginActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupActionsAfterDatabaseInsertion;
    private javax.swing.JButton     buttonHelpPlugin;
    private javax.swing.JLabel      labelInfoExclude;
    private javax.swing.JPanel      panelExclude;
    private javax.swing.JPanel      panelExcludeCheckboxes;
    private javax.swing.JScrollPane scrollPaneExclude;
    private javax.swing.JTabbedPane tabbedPane;

    // End of variables declaration//GEN-END:variables
}
