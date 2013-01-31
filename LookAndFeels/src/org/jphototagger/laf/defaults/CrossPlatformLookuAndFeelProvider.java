package org.jphototagger.laf.defaults;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.jphototagger.api.windows.LookAndFeelProvider;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = LookAndFeelProvider.class)
public final class CrossPlatformLookuAndFeelProvider implements LookAndFeelProvider {

    private static final Logger LOGGER = Logger.getLogger(DefaultLookAndFeelProvider.class.getName());

    @Override
    public String getDisplayname() {
        return Bundle.getString(CrossPlatformLookuAndFeelProvider.class, "CrossPlatformLookuAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(CrossPlatformLookuAndFeelProvider.class, "CrossPlatformLookuAndFeelProvider.Description");
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
