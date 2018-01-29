package org.jphototagger.domain.repository.event.dcsubjects;

/**
 * @author Elmar Baumann
 */
public final class DcSubjectRenamedEvent {

    private final Object source;
    private final String fromName;
    private final String toName;

    public DcSubjectRenamedEvent(Object source, String fromName, String toName) {
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

    public String getToName() {
        return toName;
    }

    public Object getSource() {
        return source;
    }
}
