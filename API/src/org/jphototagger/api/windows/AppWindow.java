package org.jphototagger.api.windows;

import java.awt.Component;
import javax.swing.Icon;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface AppWindow {

    Component getComponent();

    /**
     *
     * @return null if not present
     */
    String getTitle();

    /**
     *
     * @return null if not present
     */
    String getTip();

    /**
     *
     * @return null if not present
     */
    Icon getIcon();

    /**
     *
     * @return negative value if not present
     */
    int getPosition();
}
