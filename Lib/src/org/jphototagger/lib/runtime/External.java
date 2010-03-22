/*
 * @(#)External.java    Created on 2008-08-02
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.lib.runtime;

import org.jphototagger.lib.generics.Pair;
import org.jphototagger.lib.resource.JslBundle;

import java.io.InputStream;
import java.io.IOException;
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
 * @author  Elmar Baumann
 */
public final class External {
    private enum Stream { STANDARD_ERROR, STANDARD_IN, STANDARD_OUT, }

    public static class ProcessResult {
        private static final byte[] emptyStream = {};
        private final int           exitValue;
        private final String        outputStream;
        private final String        errorStream;

        public ProcessResult(int exitValue, String outputStream,
                             String errorStream) {
            this.exitValue    = exitValue;
            this.outputStream = outputStream;
            this.errorStream  = errorStream;
        }

        public ProcessResult(Process process) {
            byte[] os = getStream(process, Stream.STANDARD_OUT);
            byte[] es = getStream(process, Stream.STANDARD_ERROR);

            this.exitValue    = process.exitValue();
            this.outputStream = new String((os == null)
                                           ? emptyStream
                                           : os);
            this.errorStream  = new String((es == null)
                                           ? emptyStream
                                           : es);
        }

        public String getErrorStream() {
            return errorStream;
        }

        public int getExitValue() {
            return exitValue;
        }

        public String getOutputStream() {
            return outputStream;
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
        try {
            Runtime  runtime = Runtime.getRuntime();
            String[] cmd     = parseQuotedCommandLine(command);
            Process  p       = runtime.exec(cmd);

            if (wait) {
                p.waitFor();

                return new ProcessResult(p);
            }
        } catch (Exception ex) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null,
                             ex);
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
    public static Pair<byte[], byte[]> executeGetOutput(String command,
            long maxMilliseconds) {
        if (command == null) {
            throw new NullPointerException("command == null");
        }

        Runtime runtime = Runtime.getRuntime();
        Process process = null;

        try {
            process = runtime.exec(parseQuotedCommandLine(command));

            ProcessDestroyer processDestroyer = new ProcessDestroyer(process,
                                                    maxMilliseconds, command);
            Thread threadProcessDestroyer = new Thread(processDestroyer);

            threadProcessDestroyer.start();

            Pair<byte[], byte[]> streamContent =
                new Pair<byte[],
                         byte[]>(getStream(process, Stream.STANDARD_OUT),
                                 getStream(process, Stream.STANDARD_ERROR));

            processDestroyer.processFinished();

            if (processDestroyer.destroyed()) {
                return null;
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

        final int buffersize  = 100 * 1024;
        byte[]    returnBytes = null;

        try {
            InputStream stream    = s.equals(Stream.STANDARD_OUT)
                                    ? process.getInputStream()
                                    : process.getErrorStream();
            byte[]      buffer    = new byte[buffersize];
            int         bytesRead = -1;
            boolean     finished  = false;

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

            process.waitFor();
        } catch (Exception ex) {
            Logger.getLogger(External.class.getName()).log(Level.SEVERE, null,
                             ex);
        }

        return returnBytes;
    }

    private static byte[] appendToByteArray(byte[] appendTo, byte[] appendThis,
            int count) {
        byte[] newArray = new byte[appendTo.length + count];

        System.arraycopy(appendTo, 0, newArray, 0, appendTo.length);
        System.arraycopy(appendThis, 0, newArray, appendTo.length, count);

        return newArray;
    }

    private static class ProcessDestroyer implements Runnable {
        private final Process    process;
        private final long       millisecondsWait;
        private final String     command;
        private volatile boolean processFinished;
        private boolean          destroyed = false;

        public ProcessDestroyer(Process process, long millisecondsWait,
                                String command) {
            this.process          = process;
            this.millisecondsWait = millisecondsWait;
            this.command          = command;
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
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null,
                                 ex);
            }

            if (!processFinished) {
                process.destroy();
                destroyed = true;

                String errorMessage =
                    JslBundle.INSTANCE.getString(
                        "External.ExecuteGetOutput.ErrorMessage",
                        millisecondsWait, command);

                Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                                 errorMessage);
            }
        }
    }


    public static String[] parseQuotedCommandLine(String command)
            throws IOException {

        // http://gcc.gnu.org/ml/java-patches/2000-q3/msg00026.html:
        // "\nnn (octal esacpe) are converted to the appropriate char values"
        // On Windows systems, where the backslash is a path delimiter, the
        // tokenizer converts e.g. \200 in F:\2009 to '\u0080'. This results in
        // an invalid path. Let's escaping all backslashes, because it's not
        // plausible, that a command string has an octal escape, that shall be
        // replaced by one unicode character.
        command = command.replace("\\", "\\\\");

        String[]        cmd_array = new String[0];
        String[]        new_cmd_array;
        StreamTokenizer st = new StreamTokenizer(new StringReader(command));

        st.resetSyntax();
        st.wordChars('\u0000', '\uFFFF');
        st.whitespaceChars(' ', ' ');
        st.quoteChar('"');

        for (int i = 0; st.nextToken() != StreamTokenizer.TT_EOF; i++) {
            new_cmd_array    = new String[i + 1];
            new_cmd_array[i] = st.sval;
            System.arraycopy(cmd_array, 0, new_cmd_array, 0, i);
            cmd_array = new_cmd_array;
        }

        return cmd_array;
    }

    private External() {}
}
