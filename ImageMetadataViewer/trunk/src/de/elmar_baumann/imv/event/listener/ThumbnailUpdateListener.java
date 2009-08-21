/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.elmar_baumann.imv.event.listener;

import de.elmar_baumann.imv.event.ThumbnailUpdateEvent;

/**
 * Interface for receiving ThumbnailUpdateEvents
 *
 * @author  Martin Pohlack  <martinp@gmx.de>
 * @version 2009-08-18
 */
public interface ThumbnailUpdateListener {
    /**
     * A corresponding event occured.
     *
     * @param event  Event
     */
    public void actionPerformed(ThumbnailUpdateEvent event);
}
