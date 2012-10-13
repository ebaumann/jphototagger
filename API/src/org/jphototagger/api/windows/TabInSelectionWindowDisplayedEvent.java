package org.jphototagger.api.windows;

import java.awt.Component;

/**
 * @author Elmar Baumann
 */
public final class TabInSelectionWindowDisplayedEvent {

    private final Object source;
    private final Component selectedTabComponent;

    public TabInSelectionWindowDisplayedEvent(Object source, Component selectedTabComponent) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (selectedTabComponent == null) {
            throw new NullPointerException("selectedTabComponent == null");
        }

        this.source = source;
        this.selectedTabComponent = selectedTabComponent;
    }

    public Component getSelectedTabComponent() {
        return selectedTabComponent;
    }

    public Object getSource() {
        return source;
    }
}
