package org.jphototagger.laf.jgoodies;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DarkStar;
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

    @Override
    public String getDisplayname() {
        return Bundle.getString(JGoodiesPlasticDarkLookAndFeelProvider.class, "JGoodiesPlasticDarkLookAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(JGoodiesPlasticDarkLookAndFeelProvider.class, "JGoodiesPlasticDarkLookAndFeelProvider.Description");
    }

    @Override
    public void setLookAndFeel() {
        PlasticLookAndFeel.setPlasticTheme(new DarkStar());
        try {
            UIManager.setLookAndFeel(new PlasticLookAndFeel());
        } catch (Throwable t) {
            Logger.getLogger(JGoodiesPlasticDarkLookAndFeelProvider.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    @Override
    public String getPreferencesKey() {
        return "JGoodiesPlasticDarkLookAndFeel";
    }

    @Override
    public int getPosition() {
        return 1000;
    }
}
