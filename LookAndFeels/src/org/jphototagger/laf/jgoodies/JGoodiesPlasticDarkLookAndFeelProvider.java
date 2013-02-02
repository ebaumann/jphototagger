package org.jphototagger.laf.jgoodies;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DarkStar;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.jphototagger.api.windows.LookAndFeelProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = LookAndFeelProvider.class)
public final class JGoodiesPlasticDarkLookAndFeelProvider implements LookAndFeelProvider {

    private static final Logger LOGGER = Logger.getLogger(JGoodiesPlasticDarkLookAndFeelProvider.class.getName());

    @Override
    public String getDisplayname() {
        return Bundle.getString(JGoodiesPlasticDarkLookAndFeelProvider.class, "JGoodiesPlasticDarkLookAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(JGoodiesPlasticDarkLookAndFeelProvider.class, "JGoodiesPlasticDarkLookAndFeelProvider.Description");
    }

    @Override
    public Component getPreferencesComponent() {
        return null;
    }

    @Override
    public String getPreferencesKey() {
        return "JGoodiesPlasticDarkLookAndFeel"; // Do never change this!
    }

    @Override
    public boolean canInstall() {
        return true;
    }

    @Override
    public void setLookAndFeel() {
        LOGGER.info("Setting JGoodies PlasticLookAndFeel, theme DarkStar");
        PlasticLookAndFeel.setPlasticTheme(new DarkStar());
        try {
            UIManager.setLookAndFeel(new PlasticLookAndFeel());
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    @Override
    public int getPosition() {
        return 1000;
    }
}
