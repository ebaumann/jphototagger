package org.jphototagger.program.misc;

import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.TabbedPaneUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.keywords.KeywordsPanel;
import org.jphototagger.program.module.metadatatemplates.MetaDataTemplatesPanel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * DialogExt for input assistance.
 *
 * @author Elmar Baumann
 */
public class InputHelperDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    public static final InputHelperDialog INSTANCE = new InputHelperDialog();
    private static final String KEY_SEL_INDEX_TABBED_PANE = "InputHelperDialog.SelIndexTabbedPane";
    private static final String KEY_TREE_MISC_XMP = "InputHelperDialog.TreeMiscXmpMetadata";

    private InputHelperDialog() {
        super(GUI.getAppFrame(), false);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        setKeyCards();
        TabbedPaneUtil.setMnemonics(tabbedPane);
        AnnotationProcessor.process(this);
    }

    private void setKeyCards() {
        panelKeywords.setKeyCard("InputHelperDialog.Keywords.Card");
        panelKeywords.setKeyTree("InputHelperDialog.Keywords.Tree");
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(InputHelperDialog.class, "InputHelperDialog.HelpPage"));
    }

    public void setModelKeywords(ListModel<?> model) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }
        panelKeywords.setListModel(model);
    }

    private void readProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        panelKeywords.readProperties();
        prefs.applyTreeSettings(KEY_TREE_MISC_XMP, panelMiscXmpMetadata.getTree());
        int selIndexTabbedPane = prefs.getInt(KEY_SEL_INDEX_TABBED_PANE);
        if ((selIndexTabbedPane >= 0) && (selIndexTabbedPane < tabbedPane.getTabCount())) {
            tabbedPane.setSelectedIndex(selIndexTabbedPane);
        }
    }

    private void writeProperties() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setInt(KEY_SEL_INDEX_TABBED_PANE, tabbedPane.getSelectedIndex());
        prefs.setTree(KEY_TREE_MISC_XMP, panelMiscXmpMetadata.getTree());
        panelKeywords.writeProperties();
    }

    @Override
    protected void escape() {
        setVisible(false);
    }

    public KeywordsPanel getPanelKeywords() {
        return panelKeywords;
    }

    public MetaDataTemplatesPanel getPanelMetaDataTemplates() {
        return panelMetaDataTemplates;
    }

    public MiscXmpMetadataPanel getPanelMiscXmpMetadata() {
        return panelMiscXmpMetadata;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            readProperties();
        } else {
            writeProperties();
        }
        super.setVisible(visible);
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = UiFactory.tabbedPane();
        panelKeywords = new org.jphototagger.program.module.keywords.KeywordsPanel();
        panelMiscXmpMetadata = new org.jphototagger.program.misc.MiscXmpMetadataPanel();
        panelMetaDataTemplates = new org.jphototagger.program.module.metadatatemplates.MetaDataTemplatesPanel();
        panelMetaDataTemplates.getList().setTransferHandler(new org.jphototagger.program.datatransfer.DragListItemsTransferHandler(org.jphototagger.program.datatransfer.Flavor.METADATA_TEMPLATES));
        labelInfo = UiFactory.label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "InputHelperDialog.title")); // NOI18N
        setAlwaysOnTop(true);
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setName("tabbedPane"); // NOI18N

        panelKeywords.setName("panelKeywords"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "InputHelperDialog.panelKeywords.TabConstraints.tabTitle"), panelKeywords); // NOI18N

        panelMiscXmpMetadata.setName("panelMiscXmpMetadata"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "InputHelperDialog.panelMiscXmpMetadata.TabConstraints.tabTitle"), panelMiscXmpMetadata); // NOI18N

        panelMetaDataTemplates.setName("panelMetaDataTemplates"); // NOI18N
        tabbedPane.addTab(Bundle.getString(getClass(), "InputHelperDialog.panelMetaDataTemplates.TabConstraints.tabTitle"), panelMetaDataTemplates); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 0, 7);
        getContentPane().add(tabbedPane, gridBagConstraints);

        labelInfo.setText(Bundle.getString(getClass(), "InputHelperDialog.labelInfo.text")); // NOI18N
        labelInfo.setName("labelInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 7, 7, 7);
        getContentPane().add(labelInfo, gridBagConstraints);

        pack();
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        setVisible(false);
    }

    private javax.swing.JLabel labelInfo;
    private org.jphototagger.program.module.keywords.KeywordsPanel panelKeywords;
    private org.jphototagger.program.module.metadatatemplates.MetaDataTemplatesPanel panelMetaDataTemplates;
    private org.jphototagger.program.misc.MiscXmpMetadataPanel panelMiscXmpMetadata;
    private javax.swing.JTabbedPane tabbedPane;
}
