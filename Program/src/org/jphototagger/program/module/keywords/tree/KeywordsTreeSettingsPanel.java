package org.jphototagger.program.module.keywords.tree;

import java.awt.Component;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.OptionPageProvider;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionPageProvider.class)
public class KeywordsTreeSettingsPanel extends PanelExt implements OptionPageProvider {

    private static final long serialVersionUID = 1L;

    public KeywordsTreeSettingsPanel() {
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        applyPersistedBoolean(KeywordsTreePreferencesKeys.KEY_AUTO_INSERT_UNKNOWN_KEYWORDS, true, checkBoxAutoInsertUnknownKeywords);
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return Bundle.getString(KeywordsTreeSettingsPanel.class, "KeywordsTreeSettingsPanel.Title");
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
        return 200;
    }

    private void persistBoolean(String key, boolean value) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setBoolean(key, value);
    }

    private void applyPersistedBoolean(String key, boolean def, AbstractButton button) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        button.setSelected(prefs.containsKey(key)
                ? prefs.getBoolean(key)
                : def);
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        checkBoxAutoInsertUnknownKeywords = UiFactory.checkBox();

        setLayout(new java.awt.GridBagLayout());

        checkBoxAutoInsertUnknownKeywords.setText(Bundle.getString(getClass(), "KeywordsTreeSettingsPanel.checkBoxAutoInsertUnknownKeywords.text")); // NOI18N
        checkBoxAutoInsertUnknownKeywords.setToolTipText(Bundle.getString(getClass(), "KeywordsTreeSettingsPanel.checkBoxAutoInsertUnknownKeywords.toolTipText")); // NOI18N
        checkBoxAutoInsertUnknownKeywords.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkBoxAutoInsertUnknownKeywordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(checkBoxAutoInsertUnknownKeywords, gridBagConstraints);
    }

    private void checkBoxAutoInsertUnknownKeywordsActionPerformed(java.awt.event.ActionEvent evt) {
        persistBoolean(KeywordsTreePreferencesKeys.KEY_AUTO_INSERT_UNKNOWN_KEYWORDS, checkBoxAutoInsertUnknownKeywords.isSelected());
    }

    private javax.swing.JCheckBox checkBoxAutoInsertUnknownKeywords;
}
