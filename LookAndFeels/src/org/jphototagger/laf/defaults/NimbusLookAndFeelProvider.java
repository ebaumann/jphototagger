package org.jphototagger.laf.defaults;

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
public final class NimbusLookAndFeelProvider implements LookAndFeelProvider {

    private static final Logger LOGGER = Logger.getLogger(DefaultLookAndFeelProvider.class.getName());

    @Override
    public String getDisplayname() {
        return Bundle.getString(NimbusLookAndFeelProvider.class, "NimbusLookAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(NimbusLookAndFeelProvider.class, "NimbusLookAndFeelProvider.Description");
    }

    @Override
    public Component getPreferencesComponent() {
        return null;
    }

    @Override
    public String getPreferencesKey() {
        return "NimbusLookAndFeelProvider";
    }

    @Override
    public void setLookAndFeel() {
        LOGGER.info("Setting Java Nimbus Look and Feel");
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    @Override
    public int getPosition() {
        return 10;
    }
}
