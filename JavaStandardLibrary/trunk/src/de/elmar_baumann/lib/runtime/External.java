package de.elmar_baumann.lib.runtime;

import de.elmar_baumann.lib.resource.Bundle;
import de.elmar_baumann.lib.generics.Pair;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Something what doesn't happen in the JVM.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-02
 */
public final class External {

    private enum Stream {

        STANDARD_ERROR,
        STANDARD_IN,
        STANDARD_OUT,
    }

    /**
     * Executes an external program.
     *
     * @param command command
     */
    public static void execute(String command) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec(command);
        } catch (Exception ex) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    /**
     * Executes an external program and returns it's output.
     * 
     * @param  command          command, e.g. <code>/bin/ls -l /home</code>
     * @param  maxMilliseconds  Maximum time in milliseconds to wait for closing
     *                          the process' streams. If this time is exceeded,
     *                          the process streams will be closed. In this case
     *                          {@link Pair#getSecond()} contains an error
     *                          message that the stream was closed.
     * @return         Pair of bytes written by the program or null if errors
     *                 occured. The first element of the pair is null if the
     *                 program didn't write anything to the system's standard
     *                 output or the bytes the program has written to the
     *                 system's standard output. The second element of the pair
     *                 is null if the program didn't write anything to the
     *                 system's standard error output or the bytes the program
     *                 has written to the system's standard error output.
     */
    public static Pair<byte[], byte[]> executeGetOutput(
            String command, long maxMilliseconds) {
        if (command == null) {
            throw new NullPointerException("command == null"); // NOI18N
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(parseQuotedCommandLine(command));
            String errorMessage =
                    Bundle.getString("External.ExecuteGetOutput.ErrorMessage", // NOI18N
                    maxMilliseconds, command);
            InputStreamCloser closerStdOut = new InputStreamCloser(
                    process.getInputStream(), maxMilliseconds, errorMessage);
            InputStreamCloser closerStdErr = new InputStreamCloser(
                    process.getErrorStream(), maxMilliseconds, errorMessage);
            Thread threadCloseStdOut = new Thread(closerStdOut);
            Thread threadCloseStdErr = new Thread(closerStdErr);
            threadCloseStdOut.start();
            threadCloseStdErr.start();
            Pair<byte[], byte[]> streamContent = new Pair<byte[], byte[]>(
                    getStream(process, Stream.STANDARD_OUT),
                    getStream(process, Stream.STANDARD_ERROR));
            boolean closed = closerStdOut.ready();
            closed = closed || closerStdErr.ready();
            if (closed) {
                byte[] errorMessageBytes = errorMessage.getBytes();
                byte[] second = appendToByteArray(streamContent.getSecond(),
                        errorMessageBytes, errorMessageBytes.length);
                streamContent = new Pair<byte[], byte[]>(
                        streamContent.getFirst(), second);
            }
            return streamContent;
        } catch (Exception ex) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null,
                    ex);
            return null;
        }
    }

    private static byte[] getStream(Process process, Stream s) {
        assert process != null;
        assert s.equals(Stream.STANDARD_ERROR) || s.equals(Stream.STANDARD_OUT);

        final int buffersize = 100 * 1024;
        byte[] returnBytes = null;
        try {
            InputStream stream = s.equals(Stream.STANDARD_OUT)
                                 ? process.getInputStream()
                                 : process.getErrorStream();
            byte[] buffer = new byte[buffersize];
            int bytesRead = -1;
            boolean finished = false;

            while (!finished) {
                bytesRead = stream.read(buffer, 0, buffersize);
                if (bytesRead > 0) {
                    if (returnBytes == null) {
                        returnBytes = new byte[bytesRead];
                        System.arraycopy(buffer, 0, returnBytes, 0, bytesRead);
                    } else {
                        returnBytes = appendToByteArray(returnBytes, buffer,
                                bytesRead);
                    }
                }
                finished = bytesRead < 0;
            }
            try {
                process.waitFor();
            } catch (InterruptedException ex) {
                Logger.getLogger(External.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
        return returnBytes;
    }

    private static byte[] appendToByteArray(
            byte[] appendTo, byte[] appendThis, int count) {
        byte[] newArray = new byte[appendTo.length + count];
        System.arraycopy(appendTo, 0, newArray, 0, appendTo.length);
        System.arraycopy(appendThis, 0, newArray, appendTo.length, count);
        return newArray;
    }

    private static class InputStreamCloser implements Runnable {

        private final InputStream is;
        private final long millisecondsToClose;
        private final String errorMessage;
        private volatile boolean ready;
        private boolean closed = false;

        public InputStreamCloser(InputStream is, long millisecondsToClose,
                String errorMessage) {
            this.is = is;
            this.millisecondsToClose = millisecondsToClose;
            this.errorMessage = errorMessage;
        }

        public boolean ready() {
            ready = true;
            return closed;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(millisecondsToClose);
            } catch (InterruptedException ex) {
                Logger.getLogger(External.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
            if (!ready) {
                try {
                    is.close();
                    closed = true;
                    Logger.getLogger(External.class.getName()).log(Level.SEVERE,
                            errorMessage);
                } catch (IOException ex) {
                    Logger.getLogger(External.class.getName()).log(Level.SEVERE,
                            null, ex);
                }
            }
        }
    }

    public static String[] parseQuotedCommandLine(String command) throws IOException {
        String[] cmd_array = new String[0];
        String[] new_cmd_array;
        StreamTokenizer st = new StreamTokenizer(new StringReader(command));
        st.resetSyntax();
        st.wordChars('\u0000','\uFFFF');
        st.whitespaceChars(' ', ' ');
        st.quoteChar('\"');
        for (int i = 0; st.nextToken() != StreamTokenizer.TT_EOF; i++) {
            new_cmd_array = new String[i + 1];
            new_cmd_array[i] = st.sval;
            System.arraycopy(cmd_array, 0, new_cmd_array, 0, i);
            cmd_array = new_cmd_array;
        }
        return cmd_array;
    }

    private External() {
    }
}
