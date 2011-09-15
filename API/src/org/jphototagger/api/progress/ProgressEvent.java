package org.jphototagger.api.progress;

/**
 *
 * @author Elmar Baumann
 */
public final class ProgressEvent {
    private final Object source;
    private int maximum;
    private int minimum;
    private boolean indeterminate;
    private int value;
    private Object info;
    private boolean cancel;

    public ProgressEvent(Object source, int minimum, int maximum, int value, Object info) {
        this.source = source;
        this.minimum = minimum;
        this.maximum = maximum;
        this.value = value;
        this.info = info;
    }

    /**
     * Creates an interminate Event.
     *
     * @param source
     * @param info
     */
    public ProgressEvent(Object source, Object info) {
        this.source = source;
        this.info = info;
        indeterminate = true;
    }

    public Object getSource() {
        return source;
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Object getInfo() {
        return info;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public boolean isCancel() {
        return cancel;
    }

    public boolean isIndeterminate() {
        return indeterminate;
    }

    public void setIndeterminate(boolean indeterminate) {
        this.indeterminate = indeterminate;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }
}
