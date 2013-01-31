package org.jphototagger.laf.defaults;

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
public final class CrossPlatformLookuAndFeelProvider implements LookAndFeelProvider {


    @Override
    public String getDisplayname() {
        return Bundle.getString(CrossPlatformLookuAndFeelProvider.class, "CrossPlatformLookuAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(CrossPlatformLookuAndFeelProvider.class, "CrossPlatformLookuAndFeelProvider.Description");
    }

    @Override
    public String getPreferencesKey() {
        return "CrossPlatformLookuAndFeelProvider";
    }

    @Override
    public void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(DefaultLookAndFeelProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getPosition() {
        return 0;
    }
}
