package org.jphototagger.api.windows;

import javax.swing.Action;

import org.jphototagger.api.collections.PositionProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MainWindowMenuAction extends PositionProvider {

    Action getAction();

    boolean isUsedInMenusSeparatorBefore();
}
