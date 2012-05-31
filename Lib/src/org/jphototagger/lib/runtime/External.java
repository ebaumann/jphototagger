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
 * Something what doesn't happen within the JVM.
 *
 * @author Elmar Baumann
 */
public final class External {

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

    private static class StreamBytes {

        private byte[] outBytes;
        private byte[] errBytes;

        private StreamBytes(byte[] outBytes, byte[] errBytes) {
            this.outBytes = outBytes;
            this.errBytes = errBytes;
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
        try {
            String[] commandLineToken = parseQuotedCommandLine(command);
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(commandLineToken);
            ProcessDestroyer processDestroyer = new ProcessDestroyer(process, maxMilliseconds, command);
            Thread threadProcessDestroyer = new Thread(processDestroyer, "JPhotoTagger: Destroying process");
            threadProcessDestroyer.start();
            StreamBytes streamBytes = getStreamBytes(process);
            ExternalOutput streamContent = new ExternalOutput(streamBytes.outBytes, streamBytes.errBytes);
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

    private static class ProcessStreamReaderThread extends Thread {

        private static final int BUFFER_SIZE = 100 * 1024;
        private final byte[] streamBuffer = new byte[BUFFER_SIZE];
        private final Process process;
        private final InputStream processStream;
        private byte[] readStreamBytes = null;

        private ProcessStreamReaderThread(Process process, InputStream processStream) {
            super("JPhotoTagger: Reading Process Stream");
            this.process = process;
            this.processStream = processStream;
        }

        @Override
        public void run() {
            try {
                boolean isFinish = false;
                while (!isFinish) {
                    int numberOfBytesRead = processStream.read(streamBuffer, 0, BUFFER_SIZE);
                    if (numberOfBytesRead > 0) {
                        if (readStreamBytes == null) {
                            readStreamBytes = new byte[numberOfBytesRead];
                            System.arraycopy(streamBuffer, 0, readStreamBytes, 0, numberOfBytesRead);
                        } else {
                            readStreamBytes = createMergedByteArray(readStreamBytes, streamBuffer, numberOfBytesRead);
                        }
                    }
                    isFinish = numberOfBytesRead < 0;
                }
                process.waitFor();
            } catch (Throwable t) {
                Logger.getLogger(External.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    private static StreamBytes getStreamBytes(Process process) {
        ProcessStreamReaderThread stdOutReader = new ProcessStreamReaderThread(process, process.getInputStream());
        ProcessStreamReaderThread stdErrReader = new ProcessStreamReaderThread(process, process.getErrorStream());
        stdOutReader.start();
        stdErrReader.start();
        try {
            stdOutReader.join();
            stdErrReader.join();
            return new StreamBytes(stdOutReader.readStreamBytes, stdErrReader.readStreamBytes);
        } catch (Throwable t) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null, t);
            return new StreamBytes(null, null);
        }
    }

    private static byte[] createMergedByteArray(byte[] array1, byte[] array2, int readNumberOfBytesInArray2) {
        byte[] newArray = new byte[array1.length + readNumberOfBytesInArray2];
        System.arraycopy(array1, 0, newArray, 0, array1.length);
        System.arraycopy(array2, 0, newArray, array1.length, readNumberOfBytesInArray2);
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
