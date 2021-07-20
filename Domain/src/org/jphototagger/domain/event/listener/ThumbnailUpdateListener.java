package org.jphototagger.domain.event.listener;

import org.jphototagger.domain.thumbnails.event.TypedThumbnailUpdateEvent;

/**
 * Interface for receiving ThumbnailUpdateEvents
 *
 * @author  Martin Pohlack
 */
public interface ThumbnailUpdateListener {

    /**
     * A corresponding event occured.
     *
     * @param event  Event
     */
    void thumbnailUpdated(TypedThumbnailUpdateEvent event);
}
