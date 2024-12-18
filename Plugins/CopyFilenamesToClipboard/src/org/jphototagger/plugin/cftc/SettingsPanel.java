package org.jphototagger.plugin.cftc;

import java.awt.Component;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.lib.help.HelpUtil;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * Settings for {@code CopyFilenamesToClipboard}.
 *
 * @author Elmar Baumann
 */
public class SettingsPanel extends PanelExt {

    private static final long serialVersionUID = 1L;
    private final DelimiterModel model = new DelimiterModel();
    private final DelimiterRenderer renderer = new DelimiterRenderer();
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

    public SettingsPanel() {
        initComponents();
        setPersistentModelValue();
        MnemonicUtil.setMnemonics(this);
    }

    private void setPersistentModelValue() {
        if (prefs != null) {
            String delim = prefs.getString(CopyFilenamesToClipboard.KEY_FILENAME_DELIMITER);

            if (delim == null) {
                delim = CopyFilenamesToClipboard.DEFAULT_FILENAME_DELIMITER;
            }

            comboBoxDelimiter.setSelectedItem(delim);
        }
    }

    private void showHelp() {
        HelpUtil.showHelp("/org/jphototagger/plugin/cftc/help/index.html");
    }

    private static class DelimiterModel extends DefaultComboBoxModel<Object> {
        private static final long serialVersionUID = 1L;
        private final Map<String, String> descriptionOfDelimiter = new LinkedHashMap<>();

        DelimiterModel() {
            addElements();
        }

        private void addElements() {
            descriptionOfDelimiter.put("\n", Bundle.getString(SettingsPanel.class, "DelimiterModel.Description.Newline"));
            descriptionOfDelimiter.put(" ", Bundle.getString(SettingsPanel.class, "DelimiterModel.Description.Space"));
            descriptionOfDelimiter.put("\t", Bundle.getString(SettingsPanel.class, "DelimiterModel.Description.Tab"));
            descriptionOfDelimiter.put(";", Bundle.getString(SettingsPanel.class, "DelimiterModel.Description.Semicolon"));
            descriptionOfDelimiter.put(":", Bundle.getString(SettingsPanel.class, "DelimiterModel.Description.Colon"));

            for (String key : descriptionOfDelimiter.keySet()) {
                addElement(key);
            }
        }

        public String getDescription(String delimiter) {
            return descriptionOfDelimiter.get(delimiter);
        }
    }


    private void writeDelimiter() {
        if (prefs != null) {
            prefs.setString(CopyFilenamesToClipboard.KEY_FILENAME_DELIMITER, model.getSelectedItem().toString());
        }
    }

    private class DelimiterRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof String) {
                String delimiter = (String) value;
                String description = model.getDescription(delimiter);

                assert description != null : "No description of delimiter " + delimiter;

                if (description != null) {
                    label.setText(description);
                }
            }

            return label;
        }
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelDelimiter = UiFactory.panel();
        label = UiFactory.label();
        comboBoxDelimiter = UiFactory.comboBox();
        panelFill = UiFactory.panel();
        panelVersion = UiFactory.panel();
        labelVersion = UiFactory.label();
        buttonHelp = UiFactory.button();

        
        setLayout(new java.awt.GridBagLayout());

        panelDelimiter.setName("panelDelimiter"); // NOI18N
        panelDelimiter.setLayout(new java.awt.GridBagLayout());

        label.setDisplayedMnemonic('t');
        label.setLabelFor(comboBoxDelimiter);
        label.setText(Bundle.getString(getClass(), "SettingsPanel.label.text")); // NOI18N
        label.setName("label"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelDelimiter.add(label, gridBagConstraints);

        comboBoxDelimiter.setModel(model);
        comboBoxDelimiter.setName("comboBoxDelimiter"); // NOI18N
        comboBoxDelimiter.setRenderer(renderer);
        comboBoxDelimiter.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboBoxDelimiterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelDelimiter.add(comboBoxDelimiter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
        add(panelDelimiter, gridBagConstraints);

        panelFill.setName("panelFill"); // NOI18N
        panelFill.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(panelFill, gridBagConstraints);

        panelVersion.setName("panelVersion"); // NOI18N
        panelVersion.setLayout(new java.awt.GridBagLayout());

        labelVersion.setText(Bundle.getString(getClass(), "SettingsPanel.labelVersion.text")); // NOI18N
        labelVersion.setName("labelVersion"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        panelVersion.add(labelVersion, gridBagConstraints);

        buttonHelp.setText(Bundle.getString(getClass(), "SettingsPanel.buttonHelp.text")); // NOI18N
        buttonHelp.setName("buttonHelp"); // NOI18N
        buttonHelp.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHelpActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 0);
        panelVersion.add(buttonHelp, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 10, 10);
        add(panelVersion, gridBagConstraints);
    }

    private void comboBoxDelimiterActionPerformed(java.awt.event.ActionEvent evt) {
        writeDelimiter();
    }

    private void buttonHelpActionPerformed(java.awt.event.ActionEvent evt) {
        showHelp();
    }

    private javax.swing.JButton buttonHelp;
    private javax.swing.JComboBox<Object> comboBoxDelimiter;
    private javax.swing.JLabel label;
    private javax.swing.JLabel labelVersion;
    private javax.swing.JPanel panelDelimiter;
    private javax.swing.JPanel panelFill;
    private javax.swing.JPanel panelVersion;
}
