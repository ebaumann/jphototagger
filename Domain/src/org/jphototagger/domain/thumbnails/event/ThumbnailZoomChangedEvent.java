package org.jphototagger.domain.thumbnails.event;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailZoomChangedEvent {

    private final Object source;
    private final int zoomValue;

    public ThumbnailZoomChangedEvent(Object source, int zoomValue) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        this.source = source;
        this.zoomValue = zoomValue;
    }

    public Object getSource() {
        return source;
    }

    public int getZoomValue() {
        return zoomValue;
    }
}
