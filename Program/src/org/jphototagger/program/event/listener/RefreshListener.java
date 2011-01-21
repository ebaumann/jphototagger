package org.jphototagger.program.event.listener;

import org.jphototagger.program.event.RefreshEvent;

/**
 * Listens to the action: Refresh the contents.
 *
 * @author Elmar Baumann
 */
public interface RefreshListener {

    /**
     * Refreshes the content.
     *
     * @param evt event
     */
    void refresh(RefreshEvent evt);
}
