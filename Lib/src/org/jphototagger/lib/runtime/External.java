package org.jphototagger.lib.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.lib.io.IoUtil;

/**
 * Something what doesn't happen in the JVM.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public final class External {

    private enum Stream {

        STANDARD_ERROR,
        STANDARD_IN,
        STANDARD_OUT,}

    public static class ProcessResult {

        private final int exitValue;
        private final String outputStream;
        private final String errorStream;

        public ProcessResult(int exitValue, String outputStream, String errorStream) {
            this.exitValue = exitValue;
            this.outputStream = outputStream;
            this.errorStream = errorStream;
        }

        public int getExitValue() {
            return exitValue;
        }

        public String getOutputStreamAsString() {
            return outputStream;
        }

        public String getErrorStreamAsString() {
            return errorStream;
        }
    }

    // Modified http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4
    private static class StreamGobbler extends Thread {

        private final InputStream is;
        private final StringBuilder sb = new StringBuilder();
        private final boolean storeStreamInString;

        private StreamGobbler(InputStream is, boolean storeStreamInString) {
            this.is = is;
            this.storeStreamInString = storeStreamInString;
        }

        private String getStreamAsString() {
            return sb.toString();
        }

        @Override
        public void run() {
            BufferedReader br = null;

            try {
                InputStreamReader isr = new InputStreamReader(is);
                br = new BufferedReader(isr);

                if (storeStreamInString) {
                    String line = null;

                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                } else {
                    while (br.readLine() != null) {
                    }
                }
            } catch (Throwable t) {
                Logger.getLogger(External.class.getName()).log(Level.WARNING, null, t);
            } finally {
                IoUtil.close(br);
            }
        }
    }

    /**
     * Executes an external program and can wait until it's completed.
     *
     * @param  command command
     * @param wait     true if wait for the process' completion
     * @return         process result after execution or null if not to wait or
     *                 on errors
     */
    public static ProcessResult execute(String command, boolean wait) {
        if (command == null) {
            throw new NullPointerException("command == null");
        }

        try {
            Runtime runtime = Runtime.getRuntime();
            String[] cmd = parseQuotedCommandLine(command);
            Process p = runtime.exec(cmd);
            boolean storeStreamInString = wait;
            InputStream inputStream = p.getInputStream();
            InputStream errorStream = p.getErrorStream();
            StreamGobbler outputGobbler = new StreamGobbler(inputStream, storeStreamInString);
            StreamGobbler errorGobbler = new StreamGobbler(errorStream, storeStreamInString);

            errorGobbler.start();
            outputGobbler.start();

            if (wait) {
                int exitValue = p.waitFor();
                String outputStreamString = outputGobbler.getStreamAsString();
                String errorStreamString = errorGobbler.getStreamAsString();

                return new ProcessResult(exitValue, outputStreamString, errorStreamString);
            }
        } catch (Throwable t) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null, t);
        }

        return null;
    }

    /**
     * Executes an external program and returns it's output.
     *
     * @param  command          command, e.g. <code>/bin/ls -l /home</code>
     * @param  maxMilliseconds  Maximum time in milliseconds to wait for closing
     *                          the process' streams. If this time is exceeded,
     *                          the process streams will be closed. In this case
     *                          {@code ExternalOutput#getErrorStream()} contains an error
     *                          message that the stream was closed.
     * @return         Bytes written by the program or null if errors
     *                 occured. {@code ExternalOutput#getOutputStream()} is null if the
     *                 program didn't write anything to the system's standard
     *                 output or the bytes the program has written to the
     *                 system's standard output. The {@code ExternalOutput#getErrorStream()}
     *                 is null if the program didn't write anything to the
     *                 system's standard error output or the bytes the program
     *                 has written to the system's standard error output.
     */
    public static ExternalOutput executeGetOutput(String command, long maxMilliseconds) {
        if (command == null) {
            throw new NullPointerException("command == null");
        }

        if (maxMilliseconds < 0) {
            throw new IllegalArgumentException("Negative maxMilliseconds: " + maxMilliseconds);
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        try {
            process = runtime.exec(parseQuotedCommandLine(command));

            ProcessDestroyer processDestroyer = new ProcessDestroyer(process, maxMilliseconds, command);
            Thread threadProcessDestroyer = new Thread(processDestroyer, "JPhotoTagger: Destroying process");

            threadProcessDestroyer.start();
            byte[] outputStream = getStream(process, Stream.STANDARD_OUT);
            byte[] errorStream = getStream(process, Stream.STANDARD_ERROR);

            ExternalOutput streamContent = new ExternalOutput(outputStream, errorStream);

            processDestroyer.processFinished();
            threadProcessDestroyer.interrupt();

            if (processDestroyer.destroyed()) {
                return null;
            }

            return streamContent;
        } catch (Exception ex) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null, ex);

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
                        returnBytes = appendToByteArray(returnBytes, buffer, bytesRead);
                    }
                }

                finished = bytesRead < 0;
            }

            process.waitFor();
        } catch (Exception ex) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnBytes;
    }

    private static byte[] appendToByteArray(byte[] appendTo, byte[] appendThis, int count) {
        byte[] newArray = new byte[appendTo.length + count];

        System.arraycopy(appendTo, 0, newArray, 0, appendTo.length);
        System.arraycopy(appendThis, 0, newArray, appendTo.length, count);

        return newArray;
    }

    private static class ProcessDestroyer implements Runnable {

        private final Process process;
        private final long millisecondsWait;
        private final String command;
        private volatile boolean processFinished;
        private boolean destroyed = false;

        ProcessDestroyer(Process process, long millisecondsWait, String command) {
            this.process = process;
            this.millisecondsWait = millisecondsWait;
            this.command = command;
        }

        public boolean destroyed() {
            return destroyed;
        }

        public void processFinished() {
            processFinished = true;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(millisecondsWait);
            } catch (InterruptedException ex) {
                // ignore
            }

            if (!processFinished) {
                process.destroy();
                destroyed = true;

                Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                        "The command {1} did run more than {0} milliseconds and was terminated.",
                        new Object[]{millisecondsWait, command});
            }
        }
    }

    public static String[] parseQuotedCommandLine(String command) throws IOException {
        if (command == null) {
            throw new NullPointerException("command == null");
        }

        // http://gcc.gnu.org/ml/java-patches/2000-q3/msg00026.html:
        // "\nnn (octal esacpe) are converted to the appropriate char values"
        // On Windows systems, where the backslash is a path delimiter, the
        // tokenizer converts e.g. \200 in F:\2009 to '\u0080'. This results in
        // an invalid path. Let's escaping all backslashes, because it's not
        // plausible, that a command string has an octal escape, that shall be
        // replaced by one unicode character.
        String cmd = command.replace("\\", "\\\\");
        String[] cmd_array = new String[0];
        String[] new_cmd_array;
        StreamTokenizer st = new StreamTokenizer(new StringReader(cmd));

        st.resetSyntax();
        st.wordChars('\u0000', '\uFFFF');
        st.whitespaceChars(' ', ' ');
        st.quoteChar('"');

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
