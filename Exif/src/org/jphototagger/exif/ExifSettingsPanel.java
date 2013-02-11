package org.jphototagger.exif;

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.OptionPageProvider;
import org.jphototagger.domain.metadata.exif.ExifCacheProvider;
import org.jphototagger.lib.swing.ObjectsSelectionDialog;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionPageProvider.class)
public class ExifSettingsPanel extends javax.swing.JPanel implements OptionPageProvider {

    private static final long serialVersionUID = 1L;
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private final DefaultListModel<String> excludeSuffixesListModel = new DefaultListModel<>();

    public ExifSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initExcludeFromReadSuffixesListModel();
        listExcludeSuffixes.addListSelectionListener(suffixListSelectionListener);
        listExcludeSuffixes.addKeyListener(deleteSuffixesKeyListener);
    }

    private void initExcludeFromReadSuffixesListModel() {
        if (prefs == null) {
            return;
        }
        List<String> suffixes = new ArrayList<>(prefs.getStringCollection(ExifSupport.PREF_KEY_EXCLUDE_FROM_READ_SUFFIXES));
        Collections.sort(suffixes);
        for (String suffix : suffixes) {
            excludeSuffixesListModel.addElement(suffix);
        }
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return Bundle.getString(ExifSettingsPanel.class, "ExifSettingsPanel.Title");
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public boolean isMiscOptionPage() {
        return true;
    }

    @Override
    public int getPosition() {
        return 2;
    }

    private void addExcludeSuffixes() {
        ObjectsSelectionDialog<String> dlg = new ObjectsSelectionDialog<>();
        dlg.setTitle(Bundle.getString(ExifSettingsPanel.class, "ExifSettingsPanel.AddSuffixes.DialogTitle"));
        dlg.setObjects(getNotExcludeSuffixes());
        dlg.setVisible(true);
        if (dlg.isAccepted()) {
            for (String suffix : dlg.getSelectedObjects()) {
                excludeSuffixesListModel.addElement(suffix);
            }
            prefs.setStringCollection(ExifSupport.PREF_KEY_EXCLUDE_FROM_READ_SUFFIXES, getExcludeSuffixes());
            clearExifCache();
        }
    }

    private List<String> getNotExcludeSuffixes() {
        List<String> suffixes = new ArrayList<>(ExifSupport.INSTANCE.getSupportedSuffixes());
        suffixes.removeAll(getExcludeSuffixes());
        Collections.sort(suffixes);
        return suffixes;
    }

    private List<String> getExcludeSuffixes() {
        List<String> suffixes = new ArrayList<>(excludeSuffixesListModel.size());
        for (Enumeration<String> e = excludeSuffixesListModel.elements(); e.hasMoreElements(); ) {
            suffixes.add(e.nextElement());
        }
        return suffixes;
    }

    private void removeExcludeSuffixes() {
        List<String> suffixes = listExcludeSuffixes.getSelectedValuesList();
        if (suffixes.isEmpty()) {
            return;
        }
        for (String suffix : suffixes) {
            excludeSuffixesListModel.removeElement(suffix);
        }
        prefs.setStringCollection(ExifSupport.PREF_KEY_EXCLUDE_FROM_READ_SUFFIXES, getExcludeSuffixes());
        clearExifCache();
    }

    private void clearExifCache() {
        ExifCacheProvider cache = Lookup.getDefault().lookup(ExifCacheProvider.class);
        if (cache != null) {
            cache.clear();
        }
    }

    private final ListSelectionListener suffixListSelectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                setRemoveSuffixesButtonEnabeld();
            }
        }
    };

    private void setRemoveSuffixesButtonEnabeld() {
        boolean suffixSelected = listExcludeSuffixes.getSelectedIndex() >= 0;
        buttonRemoveExcludeSuffixes.setEnabled(suffixSelected);
    }

    private final KeyListener deleteSuffixesKeyListener = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                removeExcludeSuffixes();
            }
        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = new javax.swing.JTabbedPane();
        panelExcludeSuffixes = new javax.swing.JPanel();
        panelExcludeSuffixesContent = new javax.swing.JPanel();
        labelExcludeSuffixes = new javax.swing.JLabel();
        scrollPaneExcludeSuffixes = new javax.swing.JScrollPane();
        listExcludeSuffixes = new javax.swing.JList<>();
        buttonAddExcludeSuffixes = new javax.swing.JButton();
        buttonRemoveExcludeSuffixes = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        panelExcludeSuffixes.setLayout(new java.awt.GridBagLayout());

        panelExcludeSuffixesContent.setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/exif/Bundle"); // NOI18N
        labelExcludeSuffixes.setText(bundle.getString("ExifSettingsPanel.labelExcludeSuffixes.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelExcludeSuffixesContent.add(labelExcludeSuffixes, gridBagConstraints);

        listExcludeSuffixes.setModel(excludeSuffixesListModel);
        scrollPaneExcludeSuffixes.setViewportView(listExcludeSuffixes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        panelExcludeSuffixesContent.add(scrollPaneExcludeSuffixes, gridBagConstraints);

        buttonAddExcludeSuffixes.setText("+"); // NOI18N
        buttonAddExcludeSuffixes.setToolTipText(bundle.getString("ExifSettingsPanel.buttonAddExcludeSuffixes.toolTipText")); // NOI18N
        buttonAddExcludeSuffixes.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonAddExcludeSuffixes.setPreferredSize(new java.awt.Dimension(22, 22));
        buttonAddExcludeSuffixes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonAddExcludeSuffixesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        panelExcludeSuffixesContent.add(buttonAddExcludeSuffixes, gridBagConstraints);

        buttonRemoveExcludeSuffixes.setText("-"); // NOI18N
        buttonRemoveExcludeSuffixes.setToolTipText(bundle.getString("ExifSettingsPanel.buttonRemoveExcludeSuffixes.toolTipText")); // NOI18N
        buttonRemoveExcludeSuffixes.setEnabled(false);
        buttonRemoveExcludeSuffixes.setMargin(new java.awt.Insets(2, 2, 2, 2));
        buttonRemoveExcludeSuffixes.setPreferredSize(new java.awt.Dimension(22, 22));
        buttonRemoveExcludeSuffixes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonRemoveExcludeSuffixesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelExcludeSuffixesContent.add(buttonRemoveExcludeSuffixes, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panelExcludeSuffixes.add(panelExcludeSuffixesContent, gridBagConstraints);

        tabbedPane.addTab(bundle.getString("ExifSettingsPanel.panelExcludeSuffixes.TabConstraints.tabTitle"), panelExcludeSuffixes); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(tabbedPane, gridBagConstraints);
    }//GEN-END:initComponents

    private void buttonAddExcludeSuffixesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonAddExcludeSuffixesActionPerformed
        addExcludeSuffixes();
    }//GEN-LAST:event_buttonAddExcludeSuffixesActionPerformed

    private void buttonRemoveExcludeSuffixesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonRemoveExcludeSuffixesActionPerformed
        removeExcludeSuffixes();
    }//GEN-LAST:event_buttonRemoveExcludeSuffixesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddExcludeSuffixes;
    private javax.swing.JButton buttonRemoveExcludeSuffixes;
    private javax.swing.JLabel labelExcludeSuffixes;
    private javax.swing.JList<String> listExcludeSuffixes;
    private javax.swing.JPanel panelExcludeSuffixes;
    private javax.swing.JPanel panelExcludeSuffixesContent;
    private javax.swing.JScrollPane scrollPaneExcludeSuffixes;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
