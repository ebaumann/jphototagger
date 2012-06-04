package org.jphototagger.lib.runtime;

/**
 * @author Elmar Baumann
 */
public final class ProcessResult {

    private final byte[] stdOutBytes;
    private final byte[] stdErrBytes;
    private final int exitValue;

    public ProcessResult(byte[] stdOutBytes, byte[] stdErrBytes, int exitValue) {
        this.stdOutBytes = stdOutBytes;
        this.stdErrBytes = stdErrBytes;
        this.exitValue = exitValue;
    }

    public byte[] getStdOutBytes() {
        return stdOutBytes; // performance, resources: no copy
    }

    public byte[] getStdErrBytes() {
        return stdErrBytes; // performance, resources: no copy
    }

    public boolean hasStdErrBytes() {
        return stdErrBytes != null && stdErrBytes.length > 0;
    }

    public boolean hasStdOutBytes() {
        return stdOutBytes != null && stdOutBytes.length > 0;
    }

    public int getExitValue() {
        return exitValue;
    }
}
