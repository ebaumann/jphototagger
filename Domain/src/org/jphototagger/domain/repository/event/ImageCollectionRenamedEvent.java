package org.jphototagger.domain.repository.event;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ImageCollectionRenamedEvent {

    private final Object source;
    private final String fromName;
    private final String toName;

    public ImageCollectionRenamedEvent(Object source, String fromName, String toName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }

        if (toName == null) {
            throw new NullPointerException("toName == null");
        }

        this.source = source;
        this.fromName = fromName;
        this.toName = toName;
    }

    public String getFromName() {
        return fromName;
    }

    public Object getSource() {
        return source;
    }

    public String getToName() {
        return toName;
    }
}
