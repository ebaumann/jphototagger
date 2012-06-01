package org.jphototagger.lib.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.lib.io.IoUtil;

/**
 * Something what doesn't happen within the JVM.
 *
 * @author Elmar Baumann
 */
public final class External {

    /**
     * Executes an external command <em>without</em> waiting for it's termination.
     *
     * @param command command
     */
    public static void execute(String command) {
        if (command == null) {
            throw new NullPointerException("command == null");
        }
        try {
            Process process = Runtime.getRuntime().exec(parseQuotedCommandLine(command));
            InputStream inputStream = process.getInputStream();
            InputStream errorStream = process.getErrorStream();
            Thread outputGobblerThread = new Thread(new StreamGobbler(inputStream));
            Thread errorGobblerThread = new Thread(new StreamGobbler(errorStream));
            outputGobblerThread.setName("JPhotoTagger: Consuming stdout of " + command);
            errorGobblerThread.setName("JPhotoTagger: Consuming stderr of " + command);
            errorGobblerThread.start();
            outputGobblerThread.start();
        } catch (Throwable t) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    // Modified http://www.javaworld.com/javaworld/jw-12-2000/jw-1229-traps.html?page=4, see also http://kylecartmell.com/?p=9
    private static class StreamGobbler implements Runnable {

        private final InputStream inputStream;

        private StreamGobbler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            BufferedReader bufferedReader = null;
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                while (bufferedReader.readLine() != null) {
                }
            } catch (Throwable t) {
                Logger.getLogger(External.class.getName()).log(Level.WARNING, null, t);
            } finally {
                IoUtil.close(bufferedReader);
            }
        }
    }

    /**
     * Executes an external command, waits for it's termination and returns it's output.
     *
     * @param command command, e.g. {@code /bin/ls -l /home}
     * @param maxMillisecondsUntilDestroy Maximum time in milliseconds to wait before (automatically) destroying the
     * external process
     * @return Bytes output by the program or null if errors occured
     */
    public static FinishedProcessResult executeWaitForTermination(String command, long maxMillisecondsUntilDestroy) {
        if (command == null) {
            throw new NullPointerException("command == null");
        }
        if (maxMillisecondsUntilDestroy < 0) {
            throw new IllegalArgumentException("Negative maximum milliseconds until destroy: " + maxMillisecondsUntilDestroy);
        }
        Timer timer = new Timer(true);
        try {
            InterruptTimerTask interrupter = new InterruptTimerTask(Thread.currentThread()); // http://kylecartmell.com/?p=9
            timer.schedule(interrupter, maxMillisecondsUntilDestroy);
            Process process = Runtime.getRuntime().exec(parseQuotedCommandLine(command));
            return waitForTermination(process, command, maxMillisecondsUntilDestroy);
        } catch (Throwable t) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null, t);
            return null;
        } finally {
            timer.cancel();
            Thread.interrupted();
        }
    }

    private static class InterruptTimerTask extends TimerTask {

        private Thread thread;

        private InterruptTimerTask(Thread t) {
            this.thread = t;
        }

        @Override
        public void run() {
            thread.interrupt();
        }
    }

    private static FinishedProcessResult waitForTermination(Process process, String command, long maxMillisecondsUntilDestroy) {
        StreamReader stdOutStreamReader = new StreamReader(process.getInputStream());
        Thread stdOutReaderThread = new Thread(stdOutStreamReader);
        StreamReader stdErrStreamReader = new StreamReader(process.getErrorStream());
        Thread stdErrReaderThread = new Thread(stdErrStreamReader);
        stdOutReaderThread.setName("JPhotoTagger: Reading stdout of " + command);
        stdErrReaderThread.setName("JPhotoTagger: Reading stderr of " + command);
        stdOutReaderThread.start();
        stdErrReaderThread.start();
        try {
            int processExitValue = process.waitFor();
            byte[] stdOutBytes = stdOutStreamReader.readStreamBytes;
            byte[] stdErrBytes = stdErrStreamReader.readStreamBytes;
            return new FinishedProcessResult(stdOutBytes, stdErrBytes, processExitValue);
        } catch (InterruptedException ex) {
            process.destroy();
            Logger.getLogger(External.class.getName()).log(Level.SEVERE,
                    "The command {1} did run more than {0} milliseconds and was terminated.",
                    new Object[]{maxMillisecondsUntilDestroy, command});
            return null;
        }
    }

    private static class StreamReader implements Runnable {

        private static final int BUFFER_SIZE = 100 * 1024;
        private final byte[] streamBuffer = new byte[BUFFER_SIZE];
        private final InputStream stream;
        private byte[] readStreamBytes = null;

        private StreamReader(InputStream stream) {
            this.stream = stream;
        }

        @Override
        public void run() {
            try {
                int numberOfBytesRead;
                do {
                    numberOfBytesRead = stream.read(streamBuffer, 0, BUFFER_SIZE);
                    if (numberOfBytesRead > 0) {
                        if (readStreamBytes == null) {
                            readStreamBytes = new byte[numberOfBytesRead];
                            System.arraycopy(streamBuffer, 0, readStreamBytes, 0, numberOfBytesRead);
                        } else {
                            readStreamBytes = createMergedByteArray(readStreamBytes, streamBuffer, numberOfBytesRead);
                        }
                    }
                } while (numberOfBytesRead >= 0);
            } catch (Throwable t) {
                Logger.getLogger(External.class.getName()).log(Level.SEVERE, null, t);
            }
        }

        private static byte[] createMergedByteArray(byte[] array1, byte[] array2, int readNumberOfBytesInArray2) {
            byte[] newArray = new byte[array1.length + readNumberOfBytesInArray2];
            System.arraycopy(array1, 0, newArray, 0, array1.length);
            System.arraycopy(array2, 0, newArray, array1.length, readNumberOfBytesInArray2);
            return newArray;
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
