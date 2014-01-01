package org.jphototagger.api.component;

import javax.swing.Icon;

/**
 * @author Elmar Baumann
 */
public interface IconProvider {

    Icon getSmallIcon();

    /**
     *
     * @return maybe null
     */
    Icon getLargeIcon();
}
