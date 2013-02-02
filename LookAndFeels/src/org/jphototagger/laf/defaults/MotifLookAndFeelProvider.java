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
public final class MotifLookAndFeelProvider implements LookAndFeelProvider {

    private static final String LAF_CLASSNAME = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    private static final Logger LOGGER = Logger.getLogger(MotifLookAndFeelProvider.class.getName());

    @Override
    public String getDisplayname() {
        return Bundle.getString(MotifLookAndFeelProvider.class, "MotifLookAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(MotifLookAndFeelProvider.class, "MotifLookAndFeelProvider.Description");
    }

    @Override
    public Component getPreferencesComponent() {
        return null;
    }

    @Override
    public String getPreferencesKey() {
        return "MotifLookAndFeelProvider"; // Do never change this!
    }

    @Override
    public boolean canInstall() {
        return LafSupport.canInstall(LAF_CLASSNAME);
    }

    @Override
    public void setLookAndFeel() {
        LOGGER.info("Setting Motif Look and Feel");
        try {
            UIManager.setLookAndFeel(LAF_CLASSNAME);
        } catch (Throwable t) {
            LOGGER.log(Level.SEVERE, null, t);
        }
    }

    @Override
    public int getPosition() {
        return 10000000;
    }
}
