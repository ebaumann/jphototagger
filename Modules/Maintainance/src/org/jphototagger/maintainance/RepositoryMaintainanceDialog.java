package org.jphototagger.maintainance;

import javax.swing.SwingUtilities;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesHints;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.TabbedPaneUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class RepositoryMaintainanceDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    public static final RepositoryMaintainanceDialog INSTANCE = new RepositoryMaintainanceDialog();

    private RepositoryMaintainanceDialog() {
        super(ComponentUtil.findFrameWithIcon(), false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        TabbedPaneUtil.setMnemonics(tabbedPane);
        AnnotationProcessor.process(this);
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(RepositoryMaintainanceDialog.class, "RepositoryMaintainanceDialog.HelpPage"));
    }

    @Override
    public void setVisible(boolean visible) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (visible) {
            prefs.applyComponentSettings(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));
        } else {
            prefs.setComponent(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));
        }
        panelMaintainance.getsVisible(visible);
        panelCount.listenToRepositoryChanges(visible);
        super.setVisible(visible);
    }

    private void close() {
        if (panelMaintainance.canClose()) {
            Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
            prefs.setComponent(this, new PreferencesHints(PreferencesHints.Option.SET_TABBED_PANE_CONTENT));
            setVisible(false);
        } else {
            String message = Bundle.getString(RepositoryMaintainanceDialog.class, "RepositoryMaintainanceDialog.Error.WaitBeforeClose");
            MessageDisplayer.error(this, message);
        }
    }

    @Override
    protected void escape() {
        close();
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = UiFactory.tabbedPane();
        panelCount = new org.jphototagger.maintainance.RepositoryInfoCountPanel();
        panelMaintainance = new org.jphototagger.maintainance.RepositoryMaintainancePanel();
        panelRepositoryUpdate = new org.jphototagger.maintainance.RepositoryUpdatePanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "RepositoryMaintainanceDialog.title")); // NOI18N
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        tabbedPane.setName("tabbedPane"); // NOI18N

        panelCount.setName("panelCount"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "RepositoryMaintainanceDialog.panelCount.TabConstraints.tabTitle"), panelCount); // NOI18N

        panelMaintainance.setName("panelMaintainance"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "RepositoryMaintainanceDialog.panelMaintainance.TabConstraints.tabTitle"), panelMaintainance); // NOI18N

        panelRepositoryUpdate.setName("panelRepositoryUpdate"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "RepositoryMaintainanceDialog.panelRepositoryUpdate.TabConstraints.tabTitle"), panelRepositoryUpdate); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(tabbedPane, gridBagConstraints);

        pack();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        close();
    }

    private org.jphototagger.maintainance.RepositoryInfoCountPanel panelCount;
    private org.jphototagger.maintainance.RepositoryMaintainancePanel panelMaintainance;
    private org.jphototagger.maintainance.RepositoryUpdatePanel panelRepositoryUpdate;
    private javax.swing.JTabbedPane tabbedPane;
}
