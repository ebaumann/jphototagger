package org.jphototagger.api.progress;

import java.util.Objects;
import javax.swing.JProgressBar;

/**
 * @author Elmar Baumann
 */
public final class ProgressEvent {

    private Object source;
    private int maximum;
    private int minimum;
    private boolean indeterminate;
    private int value;
    private Object info;
    private String stringToPaint;
    private volatile boolean cancel;
    private boolean stringPainted;

    private ProgressEvent(Builder builder) {
        this.source = builder.source;
        this.minimum = builder.minimum;
        this.maximum = builder.maximum;
        this.value = builder.value;
        this.info = builder.info;
        this.stringPainted = builder.stringPainted;
        this.stringToPaint = builder.stringToPaint;
        this.indeterminate = builder.indeterminate;
    }

    public void setSource(Object source) {
        this.source = source;
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

    public boolean isStringPainted() {
        return stringPainted;
    }

    public void setStringPainted(boolean stringPainted) {
        this.stringPainted = stringPainted;
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

    public String getStringToPaint() {
        return stringToPaint;
    }

    public void setStringToPaint(String stringToPaint) {
        this.stringToPaint = stringToPaint;
    }

    public static class Builder {

        private Object source;
        private int maximum;
        private int minimum;
        private boolean indeterminate;
        private int value;
        private Object info;
        private String stringToPaint;
        private boolean stringPainted;

        public Builder indeterminate(boolean indeterminate) {
            this.indeterminate = indeterminate;
            return this;
        }

        public Builder info(Object info) {
            this.info = info;
            return this;
        }

        public Builder maximum(int maximum) {
            this.maximum = maximum;
            return this;
        }

        public Builder minimum(int minimum) {
            this.minimum = minimum;
            return this;
        }

        public Builder source(Object source) {
            this.source = source;
            return this;
        }

        public Builder value(int value) {
            this.value = value;
            return this;
        }

        public Builder stringPainted(boolean stringPainted) {
            this.stringPainted = stringPainted;
            return this;
        }

        public Builder stringToPaint(String stringToPaint) {
            this.stringToPaint = stringToPaint;
            return this;
        }

        public ProgressEvent build() {
            return new ProgressEvent(this);
        }
    }

    @Override
    public String toString() {
        return "ProgressEvent{"
                + "source=" + source
                + ", maximum=" + maximum
                + ", minimum=" + minimum
                + ", indeterminate=" + indeterminate
                + ", value=" + value
                + ", info=" + info
                + ", stringToPaint=" + stringToPaint
                + ", cancel=" + cancel
                + ", stringPainted=" + stringPainted
                + '}';
    }

    /**
     * Applies the current state to a {@code JProgressBar}. <em>Does not check,
     * whether the current thread ist the EDT!</em>
     *
     * @param progressBar progress bar
     */
    public void applyToProgressBar(JProgressBar progressBar) {
        Objects.requireNonNull(progressBar, "progressBar == null");

        if (isIndeterminate() && !progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(true);
        }

        if (!isIndeterminate() && progressBar.isIndeterminate()) {
            progressBar.setIndeterminate(false);
        }

        if (!isIndeterminate()) {
            progressBar.setMinimum(getMinimum());
            progressBar.setMaximum(getMaximum());
            progressBar.setValue(getValue());
        }

        progressBar.setStringPainted(isStringPainted());
        progressBar.setString(getStringToPaint());
    }
}
