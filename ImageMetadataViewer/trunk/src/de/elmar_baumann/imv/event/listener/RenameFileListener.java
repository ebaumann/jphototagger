package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.event.RenameFileEvent;

/**
 * Listens to file rename actions.
 * 
 * @author Elmar Baumann <eb@elmar-baumann.de>
 */
public interface RenameFileListener {

    public void actionPerformed(RenameFileEvent action);
}
