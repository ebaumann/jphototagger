package org.jphototagger.api.messages;

/**
 * @author Elmar Baumann
 */
public enum MessageType {

    INFO,
    ERROR,
    ;

    public boolean isError() {
        return this.equals(ERROR);
    }

    public boolean isInfo() {
        return this.equals(INFO);
    }
}
