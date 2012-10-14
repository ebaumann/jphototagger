package org.jphototagger.api.windows;

import java.awt.Component;
import javax.swing.Icon;
import org.jphototagger.api.collections.PositionProvider;

/**
 * @author Elmar Baumann
 */
public interface OptionPageProvider extends PositionProvider {

    Component getComponent();

    String getTitle();

    /**
     * @return Icon or null
     */
    Icon getIcon();

    boolean isMiscOptionPage();
}
