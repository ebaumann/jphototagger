package org.jphototagger.api.windows;

import java.awt.Component;

import javax.swing.KeyStroke;

import org.jphototagger.api.collections.PositionProvider;
import org.jphototagger.api.component.IconProvider;
import org.jphototagger.api.component.TitleProvider;
import org.jphototagger.api.component.TooltipTextProvider;

/**
 * @author Elmar Baumann
 */
public interface MainWindowComponent extends IconProvider, PositionProvider, TitleProvider, TooltipTextProvider {

    Component getComponent();

    /**
     * @return null or key stroke which selects this component
     */
    KeyStroke getOptionalSelectionAccelaratorKey();
}
