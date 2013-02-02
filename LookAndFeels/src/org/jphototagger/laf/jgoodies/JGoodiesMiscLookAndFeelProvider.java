package org.jphototagger.laf.jgoodies;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.LightGray;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.LookAndFeelProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = LookAndFeelProvider.class)
public final class JGoodiesMiscLookAndFeelProvider implements LookAndFeelProvider {

    static final String PREF_KEY = "JGoodiesMiscLookAndFeelProvider.LookAndFeel";
    static final String DEFAULT_LAF_CLASSNAME = "com.jgoodies.looks.plastic.PlasticLookAndFeel";
    private static final Logger LOGGER = Logger.getLogger(JGoodiesMiscLookAndFeelProvider.class.getName());

    @Override
    public String getDisplayname() {
        return Bundle.getString(JGoodiesMiscLookAndFeelProvider.class, "JGoodiesMiscLookAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(JGoodiesMiscLookAndFeelProvider.class, "JGoodiesMiscLookAndFeelProvider.Description");
    }

    @Override
    public Component getPreferencesComponent() {
        return new JGoodiesMiscLookAndFeelSettingsPanel();
    }

    @Override
    public String getPreferencesKey() {
        return "JGoodiesMiscLookAndFeelProvider"; // Do never change this!
    }

    @Override
    public boolean canInstall() {
        return true;
    }

    @Override
    public void setLookAndFeel() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String laf = prefs.containsKey(PREF_KEY)
                ? prefs.getString(PREF_KEY)
                : DEFAULT_LAF_CLASSNAME;
        LOGGER.log(Level.INFO, "Setting JGoodies Feel {0}", laf);
        try {
            // In future (?): Using different themes offered by JGoodies, e.g. Dark Star, Desert Blue, Silver, ...
            PlasticLookAndFeel.setCurrentTheme(new LightGray());
            UIManager.setLookAndFeel(laf);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    @Override
    public int getPosition() {
        return 1100;
    }
}
