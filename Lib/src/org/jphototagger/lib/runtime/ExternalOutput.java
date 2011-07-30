package org.jphototagger.lib.runtime;

/**
 *
 *
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
        return outputStream;
    }

    public byte[] getErrorStream() {
        return errorStream;
    }
}
