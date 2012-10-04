package org.jphototagger.program.settings;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.lib.help.HelpPageProvider;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.util.TabbedPaneUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * Modaler Dialog für Anwendungseinstellungen.
 *
 * @author Elmar Baumann
 */
public final class SettingsDialog extends Dialog {

    private static final String KEY_INDEX_TABBED_PANE = "UserSettingsDialog.TabbedPaneIndex";
    private static final long serialVersionUID = 1L;
    private final Map<Tab, Integer> indexOfTab = new EnumMap<Tab, Integer>(Tab.class);
    private final Map<Integer, Tab> tabOfIndex = new HashMap<Integer, Tab>();
    private final Map<Component, String> helpUrlOfComponent = new HashMap<Component, String>();
    private final List<Persistence> persistentPanels = new ArrayList<Persistence>();
    public static final SettingsDialog INSTANCE = new SettingsDialog();

    public enum Tab {
        ACTIONS,
        FILE_EXCLUDE_PATTERNS,
        MISC,
        PERFORMANCE,
        PLUGINS,
        PROGRAMS,
        TASKS,
        THUMBNAILS,
        DISPLAY_IN_FUTURE,
    }

    ;
    private SettingsDialog() {
        super(GUI.getAppFrame(), false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initMaps();
        initPersistentPanels();
        readProperties();
        TabbedPaneUtil.setMnemonics(tabbedPane);
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
        indexOfTab.put(Tab.DISPLAY_IN_FUTURE, 8);

        for (Tab tab : indexOfTab.keySet()) {
            tabOfIndex.put(indexOfTab.get(tab), tab);
        }

        for (int index = 0; index < tabbedPane.getTabCount(); index++) {
            Component component = tabbedPane.getComponentAt(index);
            if (component instanceof HelpPageProvider) {
                HelpPageProvider helpPageProvider = (HelpPageProvider) component;
                helpUrlOfComponent.put(component, helpPageProvider.getHelpPageUrl());
            } else {
                helpUrlOfComponent.put(component, "");
            }
        }
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
        panelSearch.addSearchWordsTo(StringUtil.getWordsOf(Bundle.getString(SettingsDialog.class, "SettingsDialog.AdditionalSearchWords.PanelThumbnails")), panelThumbnails);
        panelSearch.addSearchWordsTo(StringUtil.getWordsOf(Bundle.getString(SettingsDialog.class, "SettingsDialog.AdditionalSearchWords.PanelTasks")), panelTasks);
        panelSearch.addSearchWordsTo(StringUtil.getWordsOf(Bundle.getString(SettingsDialog.class, "SettingsDialog.AdditionalSearchWords.PanelPerformance")), panelPerformance);
        panelSearch.addSearchWordsTo(StringUtil.getWordsOf(Bundle.getString(SettingsDialog.class, "SettingsDialog.AdditionalSearchWords.PanelExclude")), panelFileExcludePatterns);
        panelSearch.addSearchWordsTo(StringUtil.getWordsOf(Bundle.getString(SettingsDialog.class, "SettingsDialog.AdditionalSearchWords.PanelPlugins")), panelPlugins);
        panelSearch.addSearchWordsTo(StringUtil.getWordsOf(Bundle.getString(SettingsDialog.class, "SettingsDialog.AdditionalSearchWords.PanelMiscMisc")), panelMisc);
    }

    public void selectTab(Tab tab) {
        if (tab == null) {
            throw new NullPointerException("tab == null");
        }

        int index = indexOfTab.get(tab);

        if ((index >= 0) && (index < tabbedPane.getComponentCount())) {
            tabbedPane.setSelectedIndex(index);
        }
    }

    private void readProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String key = getClass().getName();

        prefs.applySize(key, this);
        prefs.applyLocation(key, this);
        prefs.applyTabbedPaneSettings(KEY_INDEX_TABBED_PANE, tabbedPane, null);

        for (Persistence panel : persistentPanels) {
            panel.restore();
        }
    }

    private void writeProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setTabbedPane(KEY_INDEX_TABBED_PANE, tabbedPane, null);

        for (Persistence panel : persistentPanels) {
            panel.persist();
        }
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
    protected void showHelp() {
        showHelp(helpUrlOfComponent.get(tabbedPane.getSelectedComponent()));
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

    private void initComponents() {//GEN-BEGIN:initComponents

        panelSearch = new org.jphototagger.lib.swing.TabbedPaneSearchPanel();
        tabbedPane = new javax.swing.JTabbedPane();
        panelPrograms = new org.jphototagger.program.module.programs.ProgramsSettingsPanel();
        panelThumbnails = new org.jphototagger.program.module.thumbnails.ThumbnailsSettingsPanel();
        panelTasks = new org.jphototagger.program.misc.ScheduledTasksSettingsPanel();
        panelPerformance = new org.jphototagger.program.misc.PerformanceSettingsPanel();
        panelFileExcludePatterns = new org.jphototagger.program.module.fileexcludepatterns.FileExcludePatternsSettingsPanel();
        panelMisc = new org.jphototagger.program.misc.MiscSettingsPanel();
        panelActions = new org.jphototagger.program.module.actions.ActionsSettingsPanel();
        panelPlugins = new org.jphototagger.program.plugins.PluginsSettingsPanel();
        panelDisplayMessagesInFuture = new org.jphototagger.program.settings.DisplayMessagesInFutureSettingsPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/settings/Bundle"); // NOI18N
        setTitle(bundle.getString("SettingsDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        panelSearch.setName("panelSearch"); // NOI18N

        tabbedPane.setName("tabbedPane"); // NOI18N

        panelPrograms.setName("panelPrograms"); // NOI18N
        tabbedPane.addTab(bundle.getString("SettingsDialog.panelPrograms.TabConstraints.tabTitle"), panelPrograms); // NOI18N

        panelThumbnails.setName("panelThumbnails"); // NOI18N
        tabbedPane.addTab(bundle.getString("SettingsDialog.panelThumbnails.TabConstraints.tabTitle"), panelThumbnails); // NOI18N

        panelTasks.setName("panelTasks"); // NOI18N
        tabbedPane.addTab(bundle.getString("SettingsDialog.panelTasks.TabConstraints.tabTitle"), panelTasks); // NOI18N

        panelPerformance.setName("panelPerformance"); // NOI18N
        tabbedPane.addTab(bundle.getString("SettingsDialog.panelPerformance.TabConstraints.tabTitle"), panelPerformance); // NOI18N

        panelFileExcludePatterns.setName("panelFileExcludePatterns"); // NOI18N
        tabbedPane.addTab(bundle.getString("SettingsDialog.panelFileExcludePatterns.TabConstraints.tabTitle"), panelFileExcludePatterns); // NOI18N

        panelMisc.setName("panelMisc"); // NOI18N
        tabbedPane.addTab(bundle.getString("SettingsDialog.panelMisc.TabConstraints.tabTitle"), panelMisc); // NOI18N

        panelActions.setName("panelActions"); // NOI18N
        tabbedPane.addTab(bundle.getString("SettingsDialog.panelActions.TabConstraints.tabTitle"), panelActions); // NOI18N

        panelPlugins.setName("panelPlugins"); // NOI18N
        tabbedPane.addTab(bundle.getString("SettingsDialog.panelPlugins.TabConstraints.tabTitle"), panelPlugins); // NOI18N

        panelDisplayMessagesInFuture.setName("panelDisplayMessagesInFuture"); // NOI18N
        tabbedPane.addTab(bundle.getString("SettingsDialog.panelDisplayMessagesInFuture.TabConstraints.tabTitle"), panelDisplayMessagesInFuture); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 583, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelSearch, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE)
                    .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 496, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

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
    private org.jphototagger.program.module.actions.ActionsSettingsPanel panelActions;
    private org.jphototagger.program.settings.DisplayMessagesInFutureSettingsPanel panelDisplayMessagesInFuture;
    private org.jphototagger.program.module.fileexcludepatterns.FileExcludePatternsSettingsPanel panelFileExcludePatterns;
    private org.jphototagger.program.misc.MiscSettingsPanel panelMisc;
    private org.jphototagger.program.misc.PerformanceSettingsPanel panelPerformance;
    private org.jphototagger.program.plugins.PluginsSettingsPanel panelPlugins;
    private org.jphototagger.program.module.programs.ProgramsSettingsPanel panelPrograms;
    private org.jphototagger.lib.swing.TabbedPaneSearchPanel panelSearch;
    private org.jphototagger.program.misc.ScheduledTasksSettingsPanel panelTasks;
    private org.jphototagger.program.module.thumbnails.ThumbnailsSettingsPanel panelThumbnails;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
