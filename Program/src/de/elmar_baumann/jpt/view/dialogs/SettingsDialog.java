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
package de.elmar_baumann.jpt.view.dialogs;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.Persistence;
import de.elmar_baumann.lib.componentutil.TabbedPaneUtil;
import de.elmar_baumann.lib.dialog.Dialog;
import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;

/**
 * Modaler Dialog für Anwendungseinstellungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class SettingsDialog extends Dialog {

    private static final String                 KEY_INDEX_TABBED_PANE = "UserSettingsDialog.TabbedPaneIndex";
    private static final long                   serialVersionUID      = -7576495084117427485L;
    private final        Map<Tab, Integer>      indexOfTab            = new HashMap<Tab, Integer>();
    private final        Map<Integer, Tab>      tabOfIndex            = new HashMap<Integer, Tab>();
    private final        Map<Component, String> helpUrlOfComponent    = new HashMap<Component, String>();
    private final        List<Persistence>      persistentPanels      = new ArrayList<Persistence>();
    public static final  SettingsDialog         INSTANCE              = new SettingsDialog();

    public enum Tab {
        ACTIONS,
        EDIT,
        FAST_SEARCH,
        FILE_EXCLUDE_PATTERNS,
        IPTC,
        MISC,
        PERFORMANCE,
        PLUGINS,
        PROGRAMS,
        TASKS,
        THUMBNAILS,
    };

    private SettingsDialog() {
        super(GUI.INSTANCE.getAppFrame(), false, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initMaps();
        initPersistentPanels();
        readProperties();
        TabbedPaneUtil.setMnemonics(tabbedPane);
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents"));
    }

    private void initMaps() {
        indexOfTab.put(Tab.PROGRAMS             ,  0);
        indexOfTab.put(Tab.FAST_SEARCH          ,  1);
        indexOfTab.put(Tab.THUMBNAILS           ,  2);
        indexOfTab.put(Tab.IPTC                 ,  3);
        indexOfTab.put(Tab.TASKS                ,  4);
        indexOfTab.put(Tab.PERFORMANCE          ,  5);
        indexOfTab.put(Tab.FILE_EXCLUDE_PATTERNS,  6);
        indexOfTab.put(Tab.EDIT                 ,  7);
        indexOfTab.put(Tab.MISC                 ,  8);
        indexOfTab.put(Tab.ACTIONS              ,  9);
        indexOfTab.put(Tab.PLUGINS              , 10);

        for (Tab tab : indexOfTab.keySet()) {
            tabOfIndex.put(indexOfTab.get(tab), tab);
        }

        helpUrlOfComponent.put(tabbedPane.getComponentAt( 0), Bundle.getString("Help.Url.UserSettingsDialog.Programs"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt( 1), Bundle.getString("Help.Url.UserSettingsDialog.FastSearch"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt( 2), Bundle.getString("Help.Url.UserSettingsDialog.Thumbnails"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt( 3), Bundle.getString("Help.Url.UserSettingsDialog.Iptc"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt( 4), Bundle.getString("Help.Url.UserSettingsDialog.Tasks"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt( 5), Bundle.getString("Help.Url.UserSettingsDialog.Performance"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt( 6), Bundle.getString("Help.Url.UserSettingsDialog.FileExcludePattern"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt( 7), Bundle.getString("Help.Url.UserSettingsDialog.Edit"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt( 8), Bundle.getString("Help.Url.UserSettingsDialog.Misc"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt( 9), Bundle.getString("Help.Url.UserSettingsDialog.Actions"));
        helpUrlOfComponent.put(tabbedPane.getComponentAt(10), Bundle.getString("Help.Url.UserSettingsDialog.Plugins"));
    }

    private void initPersistentPanels() {
        persistentPanels.add(panelActions);
        persistentPanels.add(panelEditColumns);
        persistentPanels.add(panelFastSearchColumns);
        persistentPanels.add(panelFileExcludePatterns);
        persistentPanels.add(panelIptc);
        persistentPanels.add(panelMisc);
        persistentPanels.add(panelPerformance);
        persistentPanels.add(panelPrograms);
        persistentPanels.add(panelTasks);
        persistentPanels.add(panelThumbnails);
    }

    public void selectTab(Tab tab) {
        tabbedPane.setSelectedIndex(indexOfTab.get(tab));
    }

    public void selectTab(de.elmar_baumann.jpt.view.panels.SettingsMiscPanel.Tab tab) {
        selectTab(Tab.MISC);
        panelMisc.selectTab(tab);
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().applySizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().applySettings(tabbedPane, KEY_INDEX_TABBED_PANE, null);
        for (Persistence panel : persistentPanels) {
            panel.readProperties();
        }
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().set(tabbedPane, KEY_INDEX_TABBED_PANE, null);
        for (Persistence panel : persistentPanels) {
            panel.writeProperties();
        }
        UserSettings.INSTANCE.writeToFile();
    }

    @Override
    public void setVisible(boolean visible) {
        if (!visible) {
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        panelPrograms = new de.elmar_baumann.jpt.view.panels.SettingsProgramsPanel();
        panelFastSearchColumns = new de.elmar_baumann.jpt.view.panels.SettingsFastSearchColumnsPanel();
        panelThumbnails = new de.elmar_baumann.jpt.view.panels.SettingsThumbnailsPanel();
        panelIptc = new de.elmar_baumann.jpt.view.panels.SettingsIptcPanel();
        panelTasks = new de.elmar_baumann.jpt.view.panels.SettingsScheduledTasksPanel();
        panelPerformance = new de.elmar_baumann.jpt.view.panels.SettingsPerformancePanel();
        panelFileExcludePatterns = new de.elmar_baumann.jpt.view.panels.SettingsFileExcludePatternsPanel();
        panelEditColumns = new de.elmar_baumann.jpt.view.panels.SettingsEditColumnsPanel();
        panelMisc = new de.elmar_baumann.jpt.view.panels.SettingsMiscPanel();
        panelActions = new de.elmar_baumann.jpt.view.panels.SettingsActionsPanel();
        panelPlugins = new de.elmar_baumann.jpt.view.panels.SettingsPluginsPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("SettingsDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabbedPane.addTab(Bundle.getString("SettingsDialog.panelPrograms.TabConstraints.tabTitle"), panelPrograms); // NOI18N
        tabbedPane.addTab(Bundle.getString("SettingsDialog.panelFastSearchColumns.TabConstraints.tabTitle"), panelFastSearchColumns); // NOI18N
        tabbedPane.addTab(Bundle.getString("SettingsDialog.panelThumbnails.TabConstraints.tabTitle"), panelThumbnails); // NOI18N
        tabbedPane.addTab(Bundle.getString("SettingsDialog.panelIptc.TabConstraints.tabTitle"), panelIptc); // NOI18N
        tabbedPane.addTab(Bundle.getString("SettingsDialog.panelTasks.TabConstraints.tabTitle"), panelTasks); // NOI18N
        tabbedPane.addTab(Bundle.getString("SettingsDialog.panelPerformance.TabConstraints.tabTitle"), panelPerformance); // NOI18N
        tabbedPane.addTab(Bundle.getString("SettingsDialog.panelFileExcludePatterns.TabConstraints.tabTitle"), panelFileExcludePatterns); // NOI18N
        tabbedPane.addTab(Bundle.getString("SettingsDialog.panelEditColumns.TabConstraints.tabTitle"), panelEditColumns); // NOI18N
        tabbedPane.addTab(Bundle.getString("SettingsDialog.panelMisc.TabConstraints.tabTitle"), panelMisc); // NOI18N
        tabbedPane.addTab(Bundle.getString("SettingsDialog.panelActions.TabConstraints.tabTitle"), panelActions); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/jpt/resource/properties/Bundle"); // NOI18N
        tabbedPane.addTab(bundle.getString("SettingsDialog.panelPlugins.TabConstraints.tabTitle"), panelPlugins); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 654, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
    private de.elmar_baumann.jpt.view.panels.SettingsActionsPanel panelActions;
    private de.elmar_baumann.jpt.view.panels.SettingsEditColumnsPanel panelEditColumns;
    private de.elmar_baumann.jpt.view.panels.SettingsFastSearchColumnsPanel panelFastSearchColumns;
    private de.elmar_baumann.jpt.view.panels.SettingsFileExcludePatternsPanel panelFileExcludePatterns;
    private de.elmar_baumann.jpt.view.panels.SettingsIptcPanel panelIptc;
    private de.elmar_baumann.jpt.view.panels.SettingsMiscPanel panelMisc;
    private de.elmar_baumann.jpt.view.panels.SettingsPerformancePanel panelPerformance;
    private de.elmar_baumann.jpt.view.panels.SettingsPluginsPanel panelPlugins;
    private de.elmar_baumann.jpt.view.panels.SettingsProgramsPanel panelPrograms;
    private de.elmar_baumann.jpt.view.panels.SettingsScheduledTasksPanel panelTasks;
    private de.elmar_baumann.jpt.view.panels.SettingsThumbnailsPanel panelThumbnails;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
