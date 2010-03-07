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

package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.types.Persistence;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.panels.SettingsMiscPanel;
import de.elmar_baumann.lib.componentutil.TabbedPaneUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.util.StringUtil;

import java.awt.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;

/**
 * Modaler Dialog für Anwendungseinstellungen.
 *
 * @author  Elmar Baumann
 * @version 2008-10-05
 */
public final class SettingsDialog extends Dialog {
    private static final String KEY_INDEX_TABBED_PANE =
        "UserSettingsDialog.TabbedPaneIndex";
    private static final long            serialVersionUID   =
        -7576495084117427485L;
    private final Map<Tab, Integer>      indexOfTab         = new HashMap<Tab,
                                                                  Integer>();
    private final Map<Integer, Tab>      tabOfIndex         =
        new HashMap<Integer, Tab>();
    private final Map<Component, String> helpUrlOfComponent =
        new HashMap<Component, String>();
    private final List<Persistence> persistentPanels =
        new ArrayList<Persistence>();
    public static final SettingsDialog INSTANCE = new SettingsDialog();

    public enum Tab {
        ACTIONS, FILE_EXCLUDE_PATTERNS, MISC, PERFORMANCE, PLUGINS, PROGRAMS,
        TASKS, THUMBNAILS,
    }

    ;
    private SettingsDialog() {
        super(GUI.INSTANCE.getAppFrame(), false,
              UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initMaps();
        initPersistentPanels();
        readProperties();
        TabbedPaneUtil.setMnemonics(tabbedPane);
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        initSearchPanel();
    }

    private void initMaps() {
        indexOfTab.put(Tab.PROGRAMS, 0);
        indexOfTab.put(Tab.THUMBNAILS, 1);
        indexOfTab.put(Tab.TASKS, 2);
        indexOfTab.put(Tab.PERFORMANCE, 3);
        indexOfTab.put(Tab.FILE_EXCLUDE_PATTERNS, 4);
        indexOfTab.put(Tab.MISC, 5);
        indexOfTab.put(Tab.ACTIONS, 6);
        indexOfTab.put(Tab.PLUGINS, 7);

        for (Tab tab : indexOfTab.keySet()) {
            tabOfIndex.put(indexOfTab.get(tab), tab);
        }

        helpUrlOfComponent.put(
            tabbedPane.getComponentAt(0),
            JptBundle.INSTANCE.getString(
                "Help.Url.UserSettingsDialog.Programs"));
        helpUrlOfComponent.put(
            tabbedPane.getComponentAt(1),
            JptBundle.INSTANCE.getString(
                "Help.Url.UserSettingsDialog.Thumbnails"));
        helpUrlOfComponent.put(
            tabbedPane.getComponentAt(2),
            JptBundle.INSTANCE.getString("Help.Url.UserSettingsDialog.Tasks"));
        helpUrlOfComponent.put(
            tabbedPane.getComponentAt(3),
            JptBundle.INSTANCE.getString(
                "Help.Url.UserSettingsDialog.Performance"));
        helpUrlOfComponent.put(
            tabbedPane.getComponentAt(4),
            JptBundle.INSTANCE.getString(
                "Help.Url.UserSettingsDialog.FileExcludePattern"));
        helpUrlOfComponent.put(
            tabbedPane.getComponentAt(5),
            JptBundle.INSTANCE.getString("Help.Url.UserSettingsDialog.Misc"));
        helpUrlOfComponent.put(
            tabbedPane.getComponentAt(6),
            JptBundle.INSTANCE.getString(
                "Help.Url.UserSettingsDialog.Actions"));
        helpUrlOfComponent.put(
            tabbedPane.getComponentAt(7),
            JptBundle.INSTANCE.getString(
                "Help.Url.UserSettingsDialog.Plugins"));
    }

    private void initPersistentPanels() {
        int tabCount = tabbedPane.getTabCount();

        for (int i = 0; i < tabCount; i++) {
            Component c = tabbedPane.getComponentAt(i);

            if (c instanceof Persistence) {
                persistentPanels.add((Persistence) c);
            }
        }
    }

    private void initSearchPanel() {
        panelSearch.setParentPane(tabbedPane);
        panelSearch.addSearchWordsTo(
            StringUtil.getWordsOf(
                JptBundle.INSTANCE.getString(
                    "SettingsDialog.AdditionalSearchWords.PanelThumbnails")), panelThumbnails);
        panelSearch.addSearchWordsTo(
            StringUtil.getWordsOf(
                JptBundle.INSTANCE.getString(
                    "SettingsDialog.AdditionalSearchWords.PanelTasks")), panelTasks);
        panelSearch.addSearchWordsTo(
            StringUtil.getWordsOf(
                JptBundle.INSTANCE.getString(
                    "SettingsDialog.AdditionalSearchWords.PanelPerformance")), panelPerformance);
        panelSearch.addSearchWordsTo(
            StringUtil.getWordsOf(
                JptBundle.INSTANCE.getString(
                    "SettingsDialog.AdditionalSearchWords.PanelExclude")), panelFileExcludePatterns);
        panelSearch.addSearchWordsTo(
            StringUtil.getWordsOf(
                JptBundle.INSTANCE.getString(
                    "SettingsDialog.AdditionalSearchWords.PanelPlugins")), panelPlugins);
        panelSearch.addSearchWordsTo(
            StringUtil.getWordsOf(
                JptBundle.INSTANCE.getString(
                    "SettingsDialog.AdditionalSearchWords.PanelMiscMisc")), panelMisc.getTab(
                        SettingsMiscPanel.Tab.MISCELLANEOUS));
    }

    public void selectTab(Tab tab) {
        int index = indexOfTab.get(tab);

        if ((index >= 0) && (index < tabbedPane.getComponentCount())) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    public void selectTab(
            de.elmar_baumann.jpt.view.panels.SettingsMiscPanel.Tab tab) {
        selectTab(Tab.MISC);
        panelMisc.selectTab(tab);
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().applySizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().applySettings(tabbedPane,
                KEY_INDEX_TABBED_PANE, null);

        for (Persistence panel : persistentPanels) {
            panel.readProperties();
        }
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(tabbedPane,
                KEY_INDEX_TABBED_PANE, null);

        for (Persistence panel : persistentPanels) {
            panel.writeProperties();
        }

        UserSettings.INSTANCE.writeToFile();
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            panelSearch.focusSearchTextfield();
        } else {
            writeProperties();
        }

        super.setVisible(visible);
    }

    @Override
    protected void help() {
        help(helpUrlOfComponent.get(tabbedPane.getSelectedComponent()));
    }

    @Override
    protected void escape() {
        setVisible(false);
    }

    public JButton getButtonScheduledTasks() {
        return panelTasks.getButtonScheduledTasks();
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
        panelSearch =
            new de.elmar_baumann.lib.component.TabbedPaneSearchPanel();
        tabbedPane    = new javax.swing.JTabbedPane();
        panelPrograms =
            new de.elmar_baumann.jpt.view.panels.SettingsProgramsPanel();
        panelThumbnails =
            new de.elmar_baumann.jpt.view.panels.SettingsThumbnailsPanel();
        panelTasks =
            new de.elmar_baumann.jpt.view.panels.SettingsScheduledTasksPanel();
        panelPerformance =
            new de.elmar_baumann.jpt.view.panels.SettingsPerformancePanel();
        panelFileExcludePatterns =
            new de.elmar_baumann.jpt.view.panels
                .SettingsFileExcludePatternsPanel();
        panelMisc    = new de.elmar_baumann.jpt.view.panels.SettingsMiscPanel();
        panelActions =
            new de.elmar_baumann.jpt.view.panels.SettingsActionsPanel();
        panelPlugins =
            new de.elmar_baumann.jpt.view.panels.SettingsPluginsPanel();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("SettingsDialog.title"));    // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        tabbedPane.setTabLayoutPolicy(
            javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab(
            JptBundle.INSTANCE.getString(
                "SettingsDialog.panelPrograms.TabConstraints.tabTitle"), panelPrograms);    // NOI18N
        tabbedPane.addTab(
            JptBundle.INSTANCE.getString(
                "SettingsDialog.panelThumbnails.TabConstraints.tabTitle"), panelThumbnails);    // NOI18N
        tabbedPane.addTab(
            JptBundle.INSTANCE.getString(
                "SettingsDialog.panelTasks.TabConstraints.tabTitle"), panelTasks);    // NOI18N
        tabbedPane.addTab(
            JptBundle.INSTANCE.getString(
                "SettingsDialog.panelPerformance.TabConstraints.tabTitle"), panelPerformance);    // NOI18N
        tabbedPane.addTab(
            JptBundle.INSTANCE.getString(
                "SettingsDialog.panelFileExcludePatterns.TabConstraints.tabTitle"), panelFileExcludePatterns);    // NOI18N
        tabbedPane.addTab(
            JptBundle.INSTANCE.getString(
                "SettingsDialog.panelMisc.TabConstraints.tabTitle"), panelMisc);    // NOI18N
        tabbedPane.addTab(
            JptBundle.INSTANCE.getString(
                "SettingsDialog.panelActions.TabConstraints.tabTitle"), panelActions);    // NOI18N

        java.util.ResourceBundle bundle =
            java.util.ResourceBundle.getBundle(
                "de/elmar_baumann/jpt/resource/properties/Bundle");    // NOI18N

        tabbedPane.addTab(
            bundle.getString(
                "SettingsDialog.panelPlugins.TabConstraints.tabTitle"), panelPlugins);    // NOI18N

        javax.swing.GroupLayout layout =
            new javax.swing.GroupLayout(getContentPane());

        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addContainerGap()
                .addComponent(panelSearch,
                              javax.swing.GroupLayout.DEFAULT_SIZE,
                              javax.swing.GroupLayout.DEFAULT_SIZE,
                              Short.MAX_VALUE)
                                  .addPreferredGap(javax.swing.LayoutStyle
                                      .ComponentPlacement.RELATED)
                                          .addComponent(tabbedPane,
                                              javax.swing.GroupLayout
                                                  .PREFERRED_SIZE, 583,
                                                      Short.MAX_VALUE)
                                                          .addContainerGap()));
        layout.setVerticalGroup(layout
            .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout
                .createSequentialGroup().addGap(12, 12, 12)
                .addGroup(layout
                    .createParallelGroup(javax.swing.GroupLayout.Alignment
                        .TRAILING)
                            .addComponent(panelSearch, javax.swing.GroupLayout
                                .Alignment.LEADING, javax.swing.GroupLayout
                                .DEFAULT_SIZE, 455, Short.MAX_VALUE)
                                    .addComponent(tabbedPane, javax.swing
                                        .GroupLayout.Alignment.LEADING, javax
                                        .swing.GroupLayout
                                        .DEFAULT_SIZE, 455, Short.MAX_VALUE))
                                            .addContainerGap()));
        pack();
    }    // </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        writeProperties();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                SettingsDialog dialog = SettingsDialog.INSTANCE;

                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.elmar_baumann.jpt.view.panels.SettingsActionsPanel             panelActions;
    private de.elmar_baumann.jpt.view.panels.SettingsFileExcludePatternsPanel panelFileExcludePatterns;
    private de.elmar_baumann.jpt.view.panels.SettingsMiscPanel        panelMisc;
    private de.elmar_baumann.jpt.view.panels.SettingsPerformancePanel panelPerformance;
    private de.elmar_baumann.jpt.view.panels.SettingsPluginsPanel  panelPlugins;
    private de.elmar_baumann.jpt.view.panels.SettingsProgramsPanel panelPrograms;
    private de.elmar_baumann.lib.component.TabbedPaneSearchPanel         panelSearch;
    private de.elmar_baumann.jpt.view.panels.SettingsScheduledTasksPanel panelTasks;
    private de.elmar_baumann.jpt.view.panels.SettingsThumbnailsPanel panelThumbnails;
    private javax.swing.JTabbedPane tabbedPane;

    // End of variables declaration//GEN-END:variables
}
