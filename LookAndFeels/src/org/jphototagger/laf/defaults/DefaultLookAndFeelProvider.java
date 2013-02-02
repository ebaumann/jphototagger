package org.jphototagger.laf.defaults;

import org.jphototagger.laf.LafSupport;
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
public final class DefaultLookAndFeelProvider implements LookAndFeelProvider {

    private static final String LAF_CLASSNAME = UIManager.getSystemLookAndFeelClassName();
    private static final Logger LOGGER = Logger.getLogger(DefaultLookAndFeelProvider.class.getName());

    @Override
    public String getDisplayname() {
        return Bundle.getString(DefaultLookAndFeelProvider.class, "DefaultLookAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(DefaultLookAndFeelProvider.class, "DefaultLookAndFeelProvider.Description");
    }

    @Override
    public Component getPreferencesComponent() {
        return null;
    }

    @Override
    public String getPreferencesKey() {
        return "DefaultLookAndFeelProvider"; // Do never change this!
    }

    @Override
    public boolean canInstall() {
        return LafSupport.canInstall(LAF_CLASSNAME);
    }

    @Override
    public void setLookAndFeel() {
        LOGGER.info("Setting System Look and Feel");
        try {
            UIManager.setLookAndFeel(LAF_CLASSNAME);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    @Override
    public int getPosition() {
        return Integer.MIN_VALUE;
    }
}
