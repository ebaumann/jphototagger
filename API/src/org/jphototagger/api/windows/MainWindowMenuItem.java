package org.jphototagger.api.windows;

import javax.swing.JMenuItem;

import org.jphototagger.api.collections.PositionProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MainWindowMenuItem extends PositionProvider {

    JMenuItem getMenuItem();

    boolean isSeparatorBefore();
}
