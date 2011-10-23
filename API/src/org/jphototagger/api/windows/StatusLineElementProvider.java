package org.jphototagger.api.windows;

import java.awt.Component;

import org.jphototagger.api.collections.PositionProvider;

/**
 * @author Elmar Baumann
 */
public interface StatusLineElementProvider extends PositionProvider {

    Component getStatusLineElement();
}
