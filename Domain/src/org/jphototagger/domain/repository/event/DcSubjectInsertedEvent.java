package org.jphototagger.domain.repository.event;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class DcSubjectInsertedEvent {

    private final Object source;
    private final String dcSubject;

    public DcSubjectInsertedEvent(Object source, String dcSubject) {
        if (dcSubject == null) {
            throw new NullPointerException("dcSubject == null");
        }

        this.source = source;
        this.dcSubject = dcSubject;
    }

    public String getDcSubject() {
        return dcSubject;
    }

    public Object getSource() {
        return source;
    }
}
