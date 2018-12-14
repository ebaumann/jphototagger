package org.jphototagger.program.settings;

import java.awt.Component;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.Persistence;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
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
    private final Map<Tab, Integer> indexOfTab = new EnumMap<>(Tab.class);
    private final Map<Integer, Tab> tabOfIndex = new HashMap<>();
    private final Map<Component, String> helpUrlOfComponent = new HashMap<>();
    private final List<Persistence> persistentPanels = new ArrayList<>();
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
        AnnotationProcessor.process(this);
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

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = new javax.swing.JPanel();
        panelSearch = new org.jphototagger.lib.swing.TabbedPaneSearchPanel();
        tabbedPane = org.jphototagger.resources.UiFactory.tabbedPane();
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
        setTitle(Bundle.getString(getClass(), "SettingsDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        panelSearch.setName("panelSearch"); // NOI18N
        panelSearch.setMinimumSize(org.jphototagger.resources.UiFactory.dimension(175, 200));
        panelSearch.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(175, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 1.0;
        panelContent.add(panelSearch, gridBagConstraints);

        tabbedPane.setName("tabbedPane"); // NOI18N

        panelPrograms.setName("panelPrograms"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsDialog.panelPrograms.TabConstraints.tabTitle"), panelPrograms); // NOI18N

        panelThumbnails.setName("panelThumbnails"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsDialog.panelThumbnails.TabConstraints.tabTitle"), panelThumbnails); // NOI18N

        panelTasks.setName("panelTasks"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsDialog.panelTasks.TabConstraints.tabTitle"), panelTasks); // NOI18N

        panelPerformance.setName("panelPerformance"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsDialog.panelPerformance.TabConstraints.tabTitle"), panelPerformance); // NOI18N

        panelFileExcludePatterns.setName("panelFileExcludePatterns"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsDialog.panelFileExcludePatterns.TabConstraints.tabTitle"), panelFileExcludePatterns); // NOI18N

        panelMisc.setName("panelMisc"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsDialog.panelMisc.TabConstraints.tabTitle"), panelMisc); // NOI18N

        panelActions.setName("panelActions"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsDialog.panelActions.TabConstraints.tabTitle"), panelActions); // NOI18N

        panelPlugins.setName("panelPlugins"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsDialog.panelPlugins.TabConstraints.tabTitle"), panelPlugins); // NOI18N

        panelDisplayMessagesInFuture.setName("panelDisplayMessagesInFuture"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "SettingsDialog.panelDisplayMessagesInFuture.TabConstraints.tabTitle"), panelDisplayMessagesInFuture); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelContent.add(tabbedPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        writeProperties();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jphototagger.program.module.actions.ActionsSettingsPanel panelActions;
    private javax.swing.JPanel panelContent;
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
