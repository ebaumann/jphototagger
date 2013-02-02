package org.jphototagger.laf.defaults;

import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.jphototagger.api.windows.LookAndFeelProvider;
import org.jphototagger.laf.LafSupport;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = LookAndFeelProvider.class)
public final class GtkLookAndFeelProvider implements LookAndFeelProvider {

    private static final String LAF_CLASSNAME = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
    private static final Logger LOGGER = Logger.getLogger(DefaultLookAndFeelProvider.class.getName());

    @Override
    public String getDisplayname() {
        return Bundle.getString(GtkLookAndFeelProvider.class, "GtkLookAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(GtkLookAndFeelProvider.class, "GtkLookAndFeelProvider.Description");
    }

    @Override
    public Component getPreferencesComponent() {
        return null;
    }

    @Override
    public String getPreferencesKey() {
        return "GtkLookAndFeelProvider"; // Do never change this!
    }

    @Override
    public boolean canInstall() {
        return LafSupport.canInstall(LAF_CLASSNAME);
    }

    @Override
    public void setLookAndFeel() {
        LOGGER.info("Setting Java GTK Look and Feel");
        try {
            UIManager.setLookAndFeel(LAF_CLASSNAME);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    @Override
    public int getPosition() {
        return 10000;
    }
}
