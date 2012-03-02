package org.jphototagger.lib.runtime;

/**
 * @author Elmar Baumann
 */
public final class ExternalOutput {

    private final byte[] outputStream;
    private final byte[] errorStream;

    public ExternalOutput(byte[] outputStream, byte[] errorStream) {
        this.outputStream = outputStream;
        this.errorStream = errorStream;
    }

    public byte[] getOutputStream() {
        return outputStream; // performance, resources: no copy
    }

    public byte[] getErrorStream() {
        return errorStream; // performance, resources: no copy
    }

    public boolean hasErrorStream() {
        return errorStream != null && errorStream.length > 0;
    }

    public boolean hasOutputStream() {
        return outputStream != null && outputStream.length > 0;
    }
}
