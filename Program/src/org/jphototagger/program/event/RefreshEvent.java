package org.jphototagger.program.event;

import org.jphototagger.program.view.panels.ThumbnailsPanel;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class RefreshEvent {
    private final Object source;
    private final Point currentViewPosition;
    private List<Integer> selThumbnails;

    public RefreshEvent(Object source, Point currentViewPosition) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (currentViewPosition == null) {
            throw new NullPointerException("currentViewPosition == null");
        }

        this.source = source;
        this.currentViewPosition = currentViewPosition;
    }

    public Point getCurrentViewPosition() {
        return currentViewPosition;
    }

    public Object getSource() {
        return source;
    }

    public List<Integer> getSelThumbnails() {
        return Collections.unmodifiableList(selThumbnails);
    }

    public void setSelThumbnails(List<Integer> selThumbnails) {
        if (selThumbnails == null) {
            throw new NullPointerException("selThumbnails == null");
        }

        this.selThumbnails = new ArrayList<Integer>(selThumbnails);
    }

    public boolean hasSelThumbnails() {
        return selThumbnails != null;
    }

    public ThumbnailsPanel.Settings getSettings() {
        return new ThumbnailsPanel.Settings(currentViewPosition, selThumbnails);
    }
}
