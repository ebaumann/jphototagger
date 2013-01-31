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
public final class DefaultLookAndFeelProvider implements LookAndFeelProvider {

    @Override
    public String getDisplayname() {
        return Bundle.getString(DefaultLookAndFeelProvider.class, "DefaultLookAndFeelProvider.Displayname");
    }

    @Override
    public String getDescription() {
        return Bundle.getString(DefaultLookAndFeelProvider.class, "DefaultLookAndFeelProvider.Description");
    }

    @Override
    public String getPreferencesKey() {
        return "DefaultLookAndFeelProvider"; // Do never change this!
    }

    @Override
    public void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Logger.getLogger(DefaultLookAndFeelProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int getPosition() {
        return Integer.MIN_VALUE;
    }
}
