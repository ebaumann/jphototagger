package org.jphototagger.laf.defaults;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import org.jphototagger.api.windows.LookAndFeelProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = LookAndFeelProvider.class)
public final class CrossPlatformLookAndFeelProvider implements LookAndFeelProvider {

    private static final Logger LOGGER = Logger.getLogger(DefaultLookAndFeelProvider.class.getName());

    @Override
    public String getDisplayname() {
        return Bundle.getString(CrossPlatformLookAndFeelProvider.class, "CrossPlatformLookAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(CrossPlatformLookAndFeelProvider.class, "CrossPlatformLookAndFeelProvider.Description");
    }

    @Override
    public Component getPreferencesComponent() {
        return null;
    }

    @Override
    public String getPreferencesKey() {
        return "CrossPlatformLookuAndFeelProvider";
    }

    @Override
    public void setLookAndFeel() {
        LOGGER.info("Setting Cross Platform Look and Feel");
        try {
            MetalLookAndFeel.setCurrentTheme(new OceanTheme());
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    @Override
    public int getPosition() {
        return 0;
    }
}
