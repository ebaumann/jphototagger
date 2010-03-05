/*
 * JPhotoTagger tags and finds images fast.
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

package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.event.listener.impl.ProgressListenerSupport;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.lib.generics.Pair;
import de.elmar_baumann.lib.io.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * Kopieren von Dateien.
 *
 * @author  Elmar Baumann
 * @version 2008-09-24
 */
public final class CopyFiles implements Runnable {
    private final ProgressListenerSupport listenerSupport =
        new ProgressListenerSupport();
    private final List<File>             errorFiles = new ArrayList<File>();
    private final List<Pair<File, File>> sourceTargetFiles;
    private final Options                options;
    private boolean                      stop = false;

    /**
     * Copy options.
     */
    public enum Options {

        /** Overwrite existing files only if confirmed */
        CONFIRM_OVERWRITE(0),

        /** Overwrite existing files without confirmYesNo */
        FORCE_OVERWRITE(1),

        /** Rename the source file if the target file exists */
        RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS(2),
        ;

        private final int index;

        private Options(int index) {
            this.index = index;
        }

        public int getInt() {
            return index;
        }

        public static Options fromInt(int index) {
            for (Options o : values()) {
                if (o.getInt() == index) {
                    return o;
                }
            }

            assert false : "Invalid index: " + index;

            return CONFIRM_OVERWRITE;
        }
    }

    /**
     * Konstruktor
     *
     * @param sourceTargetFiles    Zu kopierende Dateien. Die erste im Paar
     *                 ist die Quelldatei, die zweite die Zieldatei.
     * @param options  Optionen
     */
    public CopyFiles(List<Pair<File, File>> sourceTargetFiles,
                     Options options) {
        this.sourceTargetFiles = new ArrayList<Pair<File,
                File>>(sourceTargetFiles);
        this.options = options;
    }

    /**
     * Beendet das Kopieren.
     */
    public void stop() {
        stop = true;
    }

    /**
     * Fügt einen Aktionsbeobachter hinzu.
     * {@link de.elmar_baumann.jpt.event.listener.ProgressListener#progressPerformed(de.elmar_baumann.jpt.event.ProgressEvent)}
     * liefert ein
     * {@link  de.elmar_baumann.jpt.event.ProgressEvent}-Objekt,
     * das mit {@link  de.elmar_baumann.jpt.event.ProgressEvent#getInfo()}
     * ein {@link de.elmar_baumann.lib.generics.Pair}-Objekt liefert mit der
     * aktuellen Quelldatei als erstes Element und der Zieldatei als zweites.
     *
     * {@link de.elmar_baumann.jpt.event.listener.ProgressListener#progressEnded(de.elmar_baumann.jpt.event.ProgressEvent)}
     * liefert ein
     * {@link  de.elmar_baumann.jpt.event.ProgressEvent}-Objekt,
     * das mit {@link  de.elmar_baumann.jpt.event.ProgressEvent#getInfo()}
     * ein {@link java.util.List}-Objekt mit den Dateinamen der Dateien, die nicht
     * kopiert werden konnten.
     *
     * @param listener  Beobachter
     */
    public synchronized void addProgressListener(ProgressListener listener) {
        listenerSupport.add(listener);
    }

    @Override
    public void run() {
        notifyStart();

        int size = sourceTargetFiles.size();

        for (int i = 0; !stop && (i < size); i++) {
            Pair<File, File> filePair = sourceTargetFiles.get(i);

            if (checkDifferent(filePair) && checkOverwrite(filePair)) {
                try {
                    File sourceFile = filePair.getFirst();
                    File targetFile = getTargetFile(filePair);

                    logCopyFile(sourceFile.getAbsolutePath(),
                                targetFile.getAbsolutePath());
                    FileUtil.copyFile(sourceFile, targetFile);
                } catch (Exception ex) {
                    AppLogger.logSevere(CopyFiles.class, ex);
                    errorFiles.add(filePair.getFirst());
                }
            }

            notifyPerformed(i, filePair);
        }

        notifyEnded();
    }

    private File getTargetFile(Pair<File, File> files) {
        File targetFile = files.getSecond();

        if (options.equals(Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS)
                && targetFile.exists()) {
            targetFile = FileUtil.getNotExistingFile(targetFile);
        }

        return targetFile;
    }

    private void logCopyFile(String sourceFilename, String targetFilename) {
        AppLogger.logInfo(CopyFiles.class, "CopyFiles.Info.StartCopy",
                          sourceFilename, targetFilename);
    }

    private synchronized void notifyStart() {
        ProgressEvent evt = new ProgressEvent(this, 0,
                                sourceTargetFiles.size(), 0, null);

        listenerSupport.notifyStarted(evt);
    }

    private synchronized void notifyPerformed(int value,
            Pair<File, File> filePair) {
        ProgressEvent evt = new ProgressEvent(this, 0,
                                sourceTargetFiles.size(), value, filePair);

        listenerSupport.notifyPerformed(evt);
    }

    private synchronized void notifyEnded() {
        ProgressEvent evt = new ProgressEvent(this, 0,
                                sourceTargetFiles.size(),
                                sourceTargetFiles.size(), errorFiles);

        listenerSupport.notifyEnded(evt);
    }

    private boolean checkOverwrite(Pair<File, File> filePair) {
        if (options.equals(Options.FORCE_OVERWRITE)
                || options.equals(
                    Options.RENAME_SRC_FILE_IF_TARGET_FILE_EXISTS)) {
            return true;
        }

        File target = filePair.getSecond();

        if (target.exists()) {
            MessageDisplayer.ConfirmAction action =
                MessageDisplayer.confirmYesNoCancel(null,
                    "CopyFiles.Confirm.OverwriteExisting",
                    filePair.getSecond(), filePair.getFirst());

            if (action.equals(MessageDisplayer.ConfirmAction.CANCEL)) {
                stop();
            } else {
                return action.equals(MessageDisplayer.ConfirmAction.YES);
            }
        }

        return true;
    }

    private boolean checkDifferent(Pair<File, File> filePair) {
        if (filePair.getFirst().equals(filePair.getSecond())) {
            MessageDisplayer.error(null, "CopyFiles.Error.FilesAreEquals",
                                   filePair.getFirst());

            return false;
        }

        return true;
    }
}
