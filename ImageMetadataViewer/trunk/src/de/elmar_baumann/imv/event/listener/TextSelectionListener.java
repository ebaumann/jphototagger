package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.event.TextSelectionEvent;

/**
 * Listens to text selections.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/26
 */
public interface TextSelectionListener {

    /**
     * Text was selected.
     *
     * @param evt event
     */
    public void textSelected(TextSelectionEvent evt);

    /**
     * Text was deselected.
     *
     * @param evt event
     */
    public void textDeselected(TextSelectionEvent evt);

}
