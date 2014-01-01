package org.jphototagger.domain.metadata.thumbnails;

import java.awt.Component;
import org.jphototagger.api.collections.PositionProvider;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;

/**
 * Listening for {@link ThumbnailsSelectionChangedEvent}s and provides graphical information about the selected image.
 *
 * @author Elmar Baumann
 */
public interface ThumbnailInfoProvider extends PositionProvider {

    /**
     * @return Component with info
     */
    Component getComponent();
}
