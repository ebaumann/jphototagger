package org.jphototagger.program.view.dialogs;

import javax.swing.ListModel;

import org.jphototagger.lib.componentutil.TabbedPaneUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.datatransfer.TransferHandlerDragListItems;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.KeywordsPanel;
import org.jphototagger.program.view.panels.MetaDataTemplatesPanel;
import org.jphototagger.program.view.panels.MiscXmpMetadataPanel;

/**
 * Dialog for input assistance.
 *
 * @author Elmar Baumann
 */
public class InputHelperDialog extends Dialog {
    private static final long serialVersionUID = 38960516048549937L;
    public static final InputHelperDialog INSTANCE = new InputHelperDialog();
    private static final String KEY_SEL_INDEX_TABBED_PANE = "InputHelperDialog.SelIndexTabbedPane";
    private static final String KEY_TREE_MISC_XMP = "InputHelperDialog.TreeMiscXmpMetadata";

    public InputHelperDialog() {
        super(GUI.getAppFrame(), false, UserSettings.INSTANCE.getSettings(), null);
        initComponents();
        postInitComponents();
        TabbedPaneUtil.setMnemonics(tabbedPane);
    }

    private void postInitComponents() {
        setHelpPages();
        setKeyCards();
    }

    private void setKeyCards() {
        panelKeywords.setKeyCard("InputHelperDialog.Keywords.Card");
        panelKeywords.setKeyTree("InputHelperDialog.Keywords.Tree");
    }

    private void setHelpPages() {
        setHelpContentsUrl(JptBundle.INSTANCE.getString("Help.Url.Contents"));
        setHelpPageUrl(JptBundle.INSTANCE.getString("Help.Url.InputHelpers"));
    }

    public void setModelKeywords(ListModel model) {
        if (model == null) {
            throw new NullPointerException("model == null");
        }

        panelKeywords.setListModel(model);
    }

    private void readProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        panelKeywords.readProperties();
        settings.applySettings(KEY_TREE_MISC_XMP, panelMiscXmpMetadata.getTree());

        int selIndexTabbedPane = settings.getInt(KEY_SEL_INDEX_TABBED_PANE);

        if ((selIndexTabbedPane >= 0) && (selIndexTabbedPane < tabbedPane.getTabCount())) {
            tabbedPane.setSelectedIndex(selIndexTabbedPane);
        }
    }

    private void writeProperties() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.set(KEY_SEL_INDEX_TABBED_PANE, tabbedPane.getSelectedIndex());
        settings.set(KEY_TREE_MISC_XMP, panelMiscXmpMetadata.getTree());
        panelKeywords.writeProperties();
        UserSettings.INSTANCE.writeToFile();
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

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents

        tabbedPane = new javax.swing.JTabbedPane();
        panelKeywords = new org.jphototagger.program.view.panels.KeywordsPanel();
        panelMiscXmpMetadata = new org.jphototagger.program.view.panels.MiscXmpMetadataPanel();
        panelMetaDataTemplates = new org.jphototagger.program.view.panels.MetaDataTemplatesPanel();
        panelMetaDataTemplates.getList().setTransferHandler(new TransferHandlerDragListItems(org.jphototagger.program.datatransfer.Flavor.METADATA_TEMPLATES));
        labelInfo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/dialogs/Bundle"); // NOI18N
        setTitle(bundle.getString("InputHelperDialog.title")); // NOI18N
        setAlwaysOnTop(true);
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        tabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.setName("tabbedPane"); // NOI18N

        panelKeywords.setName("panelKeywords"); // NOI18N
        tabbedPane.addTab(bundle.getString("InputHelperDialog.panelKeywords.TabConstraints.tabTitle"), panelKeywords); // NOI18N

        panelMiscXmpMetadata.setName("panelMiscXmpMetadata"); // NOI18N
        tabbedPane.addTab(bundle.getString("InputHelperDialog.panelMiscXmpMetadata.TabConstraints.tabTitle"), panelMiscXmpMetadata); // NOI18N

        panelMetaDataTemplates.setName("panelMetaDataTemplates"); // NOI18N
        tabbedPane.addTab(bundle.getString("InputHelperDialog.panelMetaDataTemplates.TabConstraints.tabTitle"), panelMetaDataTemplates); // NOI18N

        labelInfo.setText(bundle.getString("InputHelperDialog.labelInfo.text")); // NOI18N
        labelInfo.setName("labelInfo"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 283, Short.MAX_VALUE)
                    .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                InputHelperDialog dialog = new InputHelperDialog();

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
    private javax.swing.JLabel labelInfo;
    private org.jphototagger.program.view.panels.KeywordsPanel panelKeywords;
    private org.jphototagger.program.view.panels.MetaDataTemplatesPanel panelMetaDataTemplates;
    private org.jphototagger.program.view.panels.MiscXmpMetadataPanel panelMiscXmpMetadata;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
