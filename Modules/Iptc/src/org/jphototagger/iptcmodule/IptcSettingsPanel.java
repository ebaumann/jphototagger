package org.jphototagger.iptcmodule;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.windows.OptionPageProvider;
import org.jphototagger.iptc.IptcPreferencesKeys;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionPageProvider.class)
public class IptcSettingsPanel extends PanelExt implements OptionPageProvider {

    private static final long serialVersionUID = 1L;

    public IptcSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setIptcCharsetFromPreferences();
        setDisplayIptcFromPreferences();
        setBorder(BorderFactory.createEmptyBorder(UiFactory.scale(10), UiFactory.scale(10), UiFactory.scale(10), UiFactory.scale(10)));
        AnnotationProcessor.process(this);
    }

    private void setSelectedIptcCharsetToPreferences() {
        String charset = comboBoxIptcCharset.getSelectedItem().toString();
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null) {
            prefs.setString(IptcPreferencesKeys.KEY_IPTC_CHARSET, charset);
        }
    }

    private void setIptcCharsetFromPreferences() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String charset = prefs == null
                ? ""
                : prefs.getString(IptcPreferencesKeys.KEY_IPTC_CHARSET);
        String iptcCharSet = charset.isEmpty()
                ? "UTF-8"
                : charset;
        comboBoxIptcCharset.getModel().setSelectedItem(iptcCharSet);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (IptcPreferencesKeys.KEY_IPTC_CHARSET.equals(evt.getKey())) {
            setIptcCharsetFromPreferences();
        } else if (IptcPreferencesKeys.KEY_DISPLAY_IPTC.equals(evt.getKey())) {
            setDisplayIptcFromPreferences();
        }
    }

    private void setDisplayIptcToPreferences() {
        boolean display = checkBoxDisplayIptc.isSelected();
        Preferences preferences = Lookup.getDefault().lookup(Preferences.class);
        preferences.setBoolean(IptcPreferencesKeys.KEY_DISPLAY_IPTC, display);
    }

    private void setDisplayIptcFromPreferences() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean isPreferred = prefs != null && prefs.containsKey(IptcPreferencesKeys.KEY_DISPLAY_IPTC)
                ? prefs.getBoolean(IptcPreferencesKeys.KEY_DISPLAY_IPTC)
                : false;
        checkBoxDisplayIptc.setSelected(isPreferred);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return Bundle.getString(IptcSettingsPanel.class, "IptcSettingsPanel.Title");
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
        return 100;
    }

    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        checkBoxDisplayIptc = UiFactory.checkBox();
        labelIptcCharset = UiFactory.label();
        comboBoxIptcCharset = UiFactory.comboBox();
        panelVfill = UiFactory.panel();

        
        setLayout(new GridBagLayout());

        checkBoxDisplayIptc.setText(Bundle.getString(getClass(), "IptcSettingsPanel.checkBoxDisplayIptc.text")); // NOI18N
        checkBoxDisplayIptc.setName("checkBoxDisplayIptc"); // NOI18N
        checkBoxDisplayIptc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                checkBoxDisplayIptcActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        add(checkBoxDisplayIptc, gridBagConstraints);

        labelIptcCharset.setText(Bundle.getString(getClass(), "IptcSettingsPanel.labelIptcCharset.text")); // NOI18N
        labelIptcCharset.setName("labelIptcCharset"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 0);
        add(labelIptcCharset, gridBagConstraints);

        comboBoxIptcCharset.setModel(new IptcCharsetComboBoxModel());
        comboBoxIptcCharset.setName("comboBoxIptcCharset"); // NOI18N
        comboBoxIptcCharset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                comboBoxIptcCharsetActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        add(comboBoxIptcCharset, gridBagConstraints);

        panelVfill.setName("panelVfill"); // NOI18N
        panelVfill.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        add(panelVfill, gridBagConstraints);
    }

    private void comboBoxIptcCharsetActionPerformed(ActionEvent evt) {
        setSelectedIptcCharsetToPreferences();
    }

    private void checkBoxDisplayIptcActionPerformed(ActionEvent evt) {
        setDisplayIptcToPreferences();
    }

    private JCheckBox checkBoxDisplayIptc;
    private JComboBox<Object> comboBoxIptcCharset;
    private JLabel labelIptcCharset;
    private JPanel panelVfill;
}
