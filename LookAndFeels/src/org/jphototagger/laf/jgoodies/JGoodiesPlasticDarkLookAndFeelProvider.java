package org.jphototagger.laf.jgoodies;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DarkStar;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.windows.LookAndFeelProvider;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = LookAndFeelProvider.class)
public final class JGoodiesPlasticDarkLookAndFeelProvider implements LookAndFeelProvider {

    private static final Logger LOGGER = Logger.getLogger(JGoodiesPlasticDarkLookAndFeelProvider.class.getName());

    public JGoodiesPlasticDarkLookAndFeelProvider() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

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
        return "JGoodiesPlasticDarkLookAndFeel";
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

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        if (evt.getProvider() instanceof JGoodiesPlasticDarkLookAndFeelProvider) {
            String message = Bundle.getString(JGoodiesPlasticDarkLookAndFeelProvider.class, "JGoodiesPlasticDarkLookAndFeelProvider.LafChanged.Message");
            MessageDisplayer.information(ComponentUtil.findFrameWithIcon(), message);
        }
    }
}
