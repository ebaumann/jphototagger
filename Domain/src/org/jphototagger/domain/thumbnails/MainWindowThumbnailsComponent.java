package org.jphototagger.domain.thumbnails;

import java.awt.Component;

/**
 * @author Elmar Baumann
 */
public interface MainWindowThumbnailsComponent {

    Component getThumbnailsComponent();

    Component getThumbnailsDisplayingComponent();

    void persistViewportPosition();

    void restoreViewportPosition();

    void validateViewportPosition();
}
