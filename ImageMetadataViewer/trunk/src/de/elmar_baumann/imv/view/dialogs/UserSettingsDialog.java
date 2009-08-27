package de.elmar_baumann.imv.view.dialogs;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.types.Persistence;
import de.elmar_baumann.lib.dialog.Dialog;
import de.elmar_baumann.lib.util.SettingsHints;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modaler Dialog für Anwendungseinstellungen.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class UserSettingsDialog extends Dialog {

    private static final String KEY_INDEX_TABBED_PANE =
            "UserSettingsDialog.TabbedPaneIndex"; // NOI18N
    private final Map<Tab, Integer> indexOfTab = new HashMap<Tab, Integer>();
    private final Map<Integer, Tab> tabOfIndex = new HashMap<Integer, Tab>();
    private final Map<Component, String> helpUrlOfComponent =
            new HashMap<Component, String>();
    private final List<Persistence> persistentPanels =
            new ArrayList<Persistence>();
    public static final UserSettingsDialog INSTANCE = new UserSettingsDialog();

    /**
     * Ein Tab mit bestimmten Einstellungen.
     */
    public enum Tab {

        /** Programme zum Öffnen von Bildern */
        PROGRAMS,
        /** Schnellsuche */
        FAST_SEARCH,
        /** THUMBNAILS */
        THUMBNAILS,
        /** IPTC */
        IPTC,
        /** Geplante TASKS */
        TASKS,
        /** Geschwindigkeit */
        PERFORMANCE,
        /**
         * File exclude patterns
         */
        FILE_EXCLUDE_PATTERNS,
        /**
         * Edit
         */
        EDIT,
        /** Verschiedenes */
        MISC,
        /** Actions */
        ACTIONS,
        /** Plugins */
        PLUGINS,
    };

    private UserSettingsDialog() {
        super((java.awt.Frame) null, false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initMaps();
        initPersistentPanels();
        setIconImages(AppIcons.getAppIcons());
        readProperties();
        setHelpContentsUrl(Bundle.getString("Help.Url.Contents")); // NOI18N
        registerKeyStrokes();
    }

    private void initMaps() {
        indexOfTab.put(Tab.PROGRAMS, 0);
        indexOfTab.put(Tab.FAST_SEARCH, 1);
        indexOfTab.put(Tab.THUMBNAILS, 2);
        indexOfTab.put(Tab.IPTC, 3);
        indexOfTab.put(Tab.TASKS, 4);
        indexOfTab.put(Tab.PERFORMANCE, 5);
        indexOfTab.put(Tab.FILE_EXCLUDE_PATTERNS, 6);
        indexOfTab.put(Tab.EDIT, 7);
        indexOfTab.put(Tab.MISC, 8);
        indexOfTab.put(Tab.ACTIONS, 9);
        indexOfTab.put(Tab.PLUGINS, 10);

        for (Tab tab : indexOfTab.keySet()) {
            tabOfIndex.put(indexOfTab.get(tab), tab);
        }

        helpUrlOfComponent.put(tabbedPane.getComponentAt(0),
                Bundle.getString("Help.Url.UserSettingsDialog.Programs")); // NOI18N
        helpUrlOfComponent.put(tabbedPane.getComponentAt(1),
                Bundle.getString("Help.Url.UserSettingsDialog.FastSearch")); // NOI18N
        helpUrlOfComponent.put(tabbedPane.getComponentAt(2),
                Bundle.getString("Help.Url.UserSettingsDialog.Thumbnails")); // NOI18N
        helpUrlOfComponent.put(tabbedPane.getComponentAt(3),
                Bundle.getString("Help.Url.UserSettingsDialog.Iptc")); // NOI18N
        helpUrlOfComponent.put(tabbedPane.getComponentAt(4),
                Bundle.getString("Help.Url.UserSettingsDialog.Tasks")); // NOI18N
        helpUrlOfComponent.put(tabbedPane.getComponentAt(5),
                Bundle.getString("Help.Url.UserSettingsDialog.Performance")); // NOI18N
        helpUrlOfComponent.put(tabbedPane.getComponentAt(6),
                Bundle.getString(
                "Help.Url.UserSettingsDialog.FileExcludePattern")); // NOI18N
        helpUrlOfComponent.put(tabbedPane.getComponentAt(7),
                Bundle.getString("Help.Url.UserSettingsDialog.Edit")); // NOI18N
        helpUrlOfComponent.put(tabbedPane.getComponentAt(8),
                Bundle.getString("Help.Url.UserSettingsDialog.Misc")); // NOI18N
        helpUrlOfComponent.put(tabbedPane.getComponentAt(9),
                Bundle.getString("Help.Url.UserSettingsDialog.Actions")); // NOI18N
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

    public void selectTab(
            de.elmar_baumann.imv.view.panels.SettingsMiscPanel.Tab tab) {
        selectTab(Tab.MISC);
        panelMisc.selectTab(tab);
    }

    private SettingsHints getPersistentSettingsHints() {
        return new SettingsHints(EnumSet.of(
                SettingsHints.Option.DONT_SET_TABBED_PANE_CONTENT));
    }

    private void readProperties() {
        UserSettings.INSTANCE.getSettings().getSizeAndLocation(this);
        UserSettings.INSTANCE.getSettings().getTabbedPane(tabbedPane,
                KEY_INDEX_TABBED_PANE, getPersistentSettingsHints());
        for (Persistence panel : persistentPanels) {
            panel.readProperties();
        }
    }

    private void writeProperties() {
        UserSettings.INSTANCE.getSettings().setTabbedPane(tabbedPane,
                KEY_INDEX_TABBED_PANE, getPersistentSettingsHints());
        UserSettings.INSTANCE.getSettings().setSizeAndLocation(this);
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();
        panelPrograms = new de.elmar_baumann.imv.view.panels.SettingsProgramsPanel();
        panelFastSearchColumns = new de.elmar_baumann.imv.view.panels.SettingsFastSearchColumnsPanel();
        panelThumbnails = new de.elmar_baumann.imv.view.panels.SettingsThumbnailsPanel();
        panelIptc = new de.elmar_baumann.imv.view.panels.SettingsIptcPanel();
        panelTasks = new de.elmar_baumann.imv.view.panels.SettingsTasksPanel();
        panelPerformance = new de.elmar_baumann.imv.view.panels.SettingsPerformancePanel();
        panelFileExcludePatterns = new de.elmar_baumann.imv.view.panels.SettingsFileExcludePatternsPanel();
        panelEditColumns = new de.elmar_baumann.imv.view.panels.SettingsEditColumnsPanel();
        panelMisc = new de.elmar_baumann.imv.view.panels.SettingsMiscPanel();
        panelActions = new de.elmar_baumann.imv.view.panels.SettingsActionsPanel();
        panelPlugins = new de.elmar_baumann.imv.view.panels.SettingsPluginsPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString("UserSettingsDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelPrograms.TabConstraints.tabTitle"), panelPrograms); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelFastSearchColumns.TabConstraints.tabTitle"), panelFastSearchColumns); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelThumbnails.TabConstraints.tabTitle"), panelThumbnails); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelIptc.TabConstraints.tabTitle"), panelIptc); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelTasks.TabConstraints.tabTitle"), panelTasks); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelPerformance.TabConstraints.tabTitle"), panelPerformance); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelFileExcludePatterns.TabConstraints.tabTitle"), panelFileExcludePatterns); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelEditColumns.TabConstraints.tabTitle"), panelEditColumns); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelMisc.TabConstraints.tabTitle"), panelMisc); // NOI18N
        tabbedPane.addTab(Bundle.getString("UserSettingsDialog.panelActions.TabConstraints.tabTitle"), panelActions); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("de/elmar_baumann/imv/resource/properties/Bundle"); // NOI18N
        tabbedPane.addTab(bundle.getString("UserSettingsDialog.panelPlugins.TabConstraints.tabTitle"), panelPlugins); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 654, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
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
                UserSettingsDialog dialog = UserSettingsDialog.INSTANCE;
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
    private de.elmar_baumann.imv.view.panels.SettingsActionsPanel panelActions;
    private de.elmar_baumann.imv.view.panels.SettingsEditColumnsPanel panelEditColumns;
    private de.elmar_baumann.imv.view.panels.SettingsFastSearchColumnsPanel panelFastSearchColumns;
    private de.elmar_baumann.imv.view.panels.SettingsFileExcludePatternsPanel panelFileExcludePatterns;
    private de.elmar_baumann.imv.view.panels.SettingsIptcPanel panelIptc;
    private de.elmar_baumann.imv.view.panels.SettingsMiscPanel panelMisc;
    private de.elmar_baumann.imv.view.panels.SettingsPerformancePanel panelPerformance;
    private de.elmar_baumann.imv.view.panels.SettingsPluginsPanel panelPlugins;
    private de.elmar_baumann.imv.view.panels.SettingsProgramsPanel panelPrograms;
    private de.elmar_baumann.imv.view.panels.SettingsTasksPanel panelTasks;
    private de.elmar_baumann.imv.view.panels.SettingsThumbnailsPanel panelThumbnails;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
