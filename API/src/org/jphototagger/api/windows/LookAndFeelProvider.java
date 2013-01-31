package org.jphototagger.api.windows;

import java.awt.Component;
import org.jphototagger.api.collections.PositionProvider;

/**
 * Does set the Swing Look and Feel before the first GUI element will be displayed.
 *
 * @author Elmar Baumann
 */
public interface LookAndFeelProvider extends PositionProvider {

    String getDisplayname();

    String getDescription();

    /**
     * @return E.g. panel for additional user settings or null, if not provided
     */
    Component getPreferencesComponent();

    /**
     * @return Unique key between every implementation
     */
    String getPreferencesKey();

    /**
     * Does set the Look and Feel.
     */
    void setLookAndFeel();
}
