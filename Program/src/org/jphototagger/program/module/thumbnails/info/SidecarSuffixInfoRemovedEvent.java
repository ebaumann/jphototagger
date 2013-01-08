package org.jphototagger.program.module.thumbnails.info;

/**
 * @author Elmar Baumann
 */
public final class SidecarSuffixInfoRemovedEvent {

    private final Object source;
    private final String suffix;

    public SidecarSuffixInfoRemovedEvent(Object source, String suffix) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        if (suffix == null) {
            throw new NullPointerException("suffix == null");
        }
        this.source = source;
        this.suffix = suffix;
    }

    public Object getSource() {
        return source;
    }

    public String getSuffix() {
        return suffix;
    }
}
