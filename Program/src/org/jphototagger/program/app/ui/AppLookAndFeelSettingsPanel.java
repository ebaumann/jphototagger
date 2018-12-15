package org.jphototagger.program.app.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.preferences.CommonPreferences;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.LookAndFeelProvider;
import org.jphototagger.api.windows.OptionPageProvider;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.api.PositionProviderAscendingComparator;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionPageProvider.class)
public class AppLookAndFeelSettingsPanel extends javax.swing.JPanel implements OptionPageProvider {

    private static final long serialVersionUID = 1L;
    private boolean listen = true;

    public AppLookAndFeelSettingsPanel() {
        org.jphototagger.resources.UiFactory.configure(this);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        comboBoxFontScale.setSelectedItem(CommonPreferences.getFontScale());
        comboBoxFontScale.addActionListener(fontScaleListener);
        restoreLaf();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return Bundle.getString(AppLookAndFeelSettingsPanel.class, "AppLookAndFeelSettingsPanel.Title");
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
        return 50000;
    }

    private void setProvider(LookAndFeelProvider provider) {
        editorPaneDescription.setText(provider.getDescription());
        Component c = provider.getPreferencesComponent();
        scrollPanePreferences.setViewportView(c == null ? panelNoPreferences : c);
    }

    private void changeLookAndFeel() {
        LookAndFeelProvider provider = (LookAndFeelProvider) lafComboBoxModel.getSelectedItem();
        setProvider(provider);
        provider.setLookAndFeel();
        EventBus.publish(new LookAndFeelChangedEvent(this, provider));
        persistLaf(provider);
    }

    private void persistLaf(LookAndFeelProvider provider) {
        String key = provider.getPreferencesKey();
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setString(AppLookAndFeel.PREF_KEY_LOOK_AND_FEEL, key);
    }

    private void restoreLaf() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String laf = prefs.getString(AppLookAndFeel.PREF_KEY_LOOK_AND_FEEL);
        if (!StringUtil.hasContent(laf)) {
            laf = "DefaultLookAndFeelProvider";
        }
        for (LookAndFeelProvider provider : Lookup.getDefault().lookupAll(LookAndFeelProvider.class)) {
            if (laf.equals(provider.getPreferencesKey())) {
                listen = false;
                comboBoxLaf.setSelectedItem(provider);
                setProvider(provider);
                listen = true;
                break;
            }
        }
    }

    private final ComboBoxModel<LookAndFeelProvider> lafComboBoxModel = new DefaultComboBoxModel<LookAndFeelProvider>() {

        private static final long serialVersionUID = 1L;

        {
            List<LookAndFeelProvider> providers = new ArrayList<LookAndFeelProvider>(Lookup.getDefault().lookupAll(LookAndFeelProvider.class));
            Collections.sort(providers, PositionProviderAscendingComparator.INSTANCE);
            for (LookAndFeelProvider provider : providers) {
                if (provider.canInstall()) {
                    addElement(provider);
                }
            }
        }
    };

    private final ListCellRenderer<LookAndFeelProvider> lafListCellRenderer = new ListCellRenderer<LookAndFeelProvider>() {

        private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList<? extends LookAndFeelProvider> list, LookAndFeelProvider value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setText(value.getDisplayname());
            return label;
        }
    };

    private final ActionListener fontScaleListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            Float scale = (Float) comboBoxFontScale.getSelectedItem();
            CommonPreferences.persistFontScale(scale);
        }
    };

    private final ListCellRenderer<Float> fontScaleRenderer = new ListCellRenderer<Float>() {

        private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();
        private final NumberFormat fmt = NumberFormat.getInstance();

        @Override
        public Component getListCellRendererComponent(JList<? extends Float> list, Float value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setText(fmt.format(value));
            return label;
        }
    };

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelNoPreferences = org.jphototagger.resources.UiFactory.panel();
        labelNoPreferences = org.jphototagger.resources.UiFactory.label();
        panelContent = org.jphototagger.resources.UiFactory.panel();
        labelInfo = org.jphototagger.resources.UiFactory.label();
        comboBoxLaf = org.jphototagger.resources.UiFactory.comboBox();
        scrollPaneDescription = org.jphototagger.resources.UiFactory.scrollPane();
        editorPaneDescription = org.jphototagger.resources.UiFactory.editorPane();
        panelPreferences = org.jphototagger.resources.UiFactory.panel();
        scrollPanePreferences = org.jphototagger.resources.UiFactory.scrollPane();
        panelFontScale = org.jphototagger.resources.UiFactory.panel();
        labelFontScalePrompt = org.jphototagger.resources.UiFactory.label();
        comboBoxFontScale = org.jphototagger.resources.UiFactory.comboBox();
        labelFontScaleInfo = org.jphototagger.resources.UiFactory.label();

        panelNoPreferences.setLayout(new java.awt.GridBagLayout());

        labelNoPreferences.setText(Bundle.getString(getClass(), "AppLookAndFeelSettingsPanel.labelNoPreferences.text")); // NOI18N
        panelNoPreferences.add(labelNoPreferences, new java.awt.GridBagConstraints());

        setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        labelInfo.setText(Bundle.getString(getClass(), "AppLookAndFeelSettingsPanel.labelInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(labelInfo, gridBagConstraints);

        comboBoxLaf.setModel(lafComboBoxModel);
        comboBoxLaf.setRenderer(lafListCellRenderer);
        comboBoxLaf.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxLafActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(comboBoxLaf, gridBagConstraints);

        scrollPaneDescription.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "AppLookAndFeelSettingsPanel.scrollPaneDescription.border.title"))); // NOI18N
        scrollPaneDescription.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(250, 100));

        editorPaneDescription.setEditable(false);
        editorPaneDescription.setContentType("text/html"); // NOI18N
        scrollPaneDescription.setViewportView(editorPaneDescription);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(scrollPaneDescription, gridBagConstraints);

        panelPreferences.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "AppLookAndFeelSettingsPanel.panelPreferences.border.title"))); // NOI18N
        panelPreferences.setPreferredSize(org.jphototagger.resources.UiFactory.dimension(250, 200));
        panelPreferences.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelPreferences.add(scrollPanePreferences, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        panelContent.add(panelPreferences, gridBagConstraints);

        panelFontScale.setLayout(new java.awt.GridBagLayout());

        labelFontScalePrompt.setText(Bundle.getString(getClass(), "AppLookAndFeelSettingsPanel.labelFontScalePrompt.text")); // NOI18N
        panelFontScale.add(labelFontScalePrompt, new java.awt.GridBagConstraints());

        comboBoxFontScale.setModel(new javax.swing.DefaultComboBoxModel<>(CommonPreferences.getValidFontScales()));
        comboBoxFontScale.setRenderer(fontScaleRenderer);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelFontScale.add(comboBoxFontScale, gridBagConstraints);

        labelFontScaleInfo.setText(Bundle.getString(getClass(), "AppLookAndFeelSettingsPanel.labelFontScaleInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelFontScale.add(labelFontScaleInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(panelFontScale, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        add(panelContent, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void comboBoxLafActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboBoxLafActionPerformed
        if (listen) {
            changeLookAndFeel();
        }
    }//GEN-LAST:event_comboBoxLafActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Float> comboBoxFontScale;
    private javax.swing.JComboBox<LookAndFeelProvider> comboBoxLaf;
    private javax.swing.JEditorPane editorPaneDescription;
    private javax.swing.JLabel labelFontScaleInfo;
    private javax.swing.JLabel labelFontScalePrompt;
    private javax.swing.JLabel labelInfo;
    private javax.swing.JLabel labelNoPreferences;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelFontScale;
    private javax.swing.JPanel panelNoPreferences;
    private javax.swing.JPanel panelPreferences;
    private javax.swing.JScrollPane scrollPaneDescription;
    private javax.swing.JScrollPane scrollPanePreferences;
    // End of variables declaration//GEN-END:variables
}
