package org.jphototagger.program.helper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.domain.database.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.xmp.XmpColumns;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.concurrent.Cancelable;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.api.event.ProgressEvent;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Renames or deletes values in XMP sidecar files.
 *
 * @author Elmar Baumann
 */
public final class RenameDeleteXmpValue {
    private RenameDeleteXmpValue() {}

    /**
     * Renames a XMP value in all XMP sidecar files containing that value in
     * associated with a specific column.
     * <p>
     * If renamed successfully, the database will be updated.
     *
     * @param column   XMP column, <em>not</em>
     *                 {@link ColumnXmpDcSubjectsSubject}
     * @param oldValue old value, will be trimmed
     * @throws         NullPointerException if one of the parameters is null
     * @throws         IllegalArgumentException if column is not a XMP column,
     *                 see {@link XmpColumns#get()}, or is
     *                 {@link ColumnXmpDcSubjectsSubject}
     */
    public static void rename(Column column, String oldValue) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (oldValue == null) {
            throw new NullPointerException("oldValue == null");
        }

        checkColumn(column);
        String info = Bundle.getString(RenameDeleteXmpValue.class, "RenameXmpValue.Input.NewValue");
        String input = oldValue;
        String newValue = MessageDisplayer.input(info, input);

        if (newValue != null) {
            newValue = newValue.trim();

            if (newValue.equals(oldValue.trim())) {
                String message = Bundle.getString(RenameDeleteXmpValue.class, "RenameXmpValue.Error.ValuesEquals");
                MessageDisplayer.error(null, message);
            } else {
                UserTasks.INSTANCE.add(new Rename(column, oldValue, newValue));
            }
        }
    }

    /**
     * Deletes a XMP value from all XMP sidecar files containing that value in
     * associated with a specific column.
     * <p>
     * If renamed successfully, the database will be updated.
     *
     * @param column XMP column, <em>not</em> {@link ColumnXmpDcSubjectsSubject}
     * @param value  value to be deleted
     * @throws       NullPointerException if one of the parameters is null
     * @throws       IllegalArgumentException if column is not a XMP column,
     *               see {@link XmpColumns#get()}, or is
     *               {@link ColumnXmpDcSubjectsSubject}
     */
    public static void delete(Column column, String value) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (value == null) {
            throw new NullPointerException("value == null");
        }

        checkColumn(column);

        if (value.trim().isEmpty()) {
            return;
        }

        String message = Bundle.getString(RenameDeleteXmpValue.class, "RenameXmpValue.Confirm.Delete", value);

        if (MessageDisplayer.confirmYesNo(null, message)) {
            UserTasks.INSTANCE.add(new Rename(column, value, ""));
        }
    }

    private static void checkColumn(Column column) {
        if (!XmpColumns.get().contains(column)) {
            throw new IllegalArgumentException("Not a XMP column: " + column);
        }

        if (column.equals(ColumnXmpDcSubjectsSubject.INSTANCE)) {
            throw new IllegalArgumentException("DC subjects are invalid!");
        }
    }

    private static class Rename extends Thread implements Cancelable {
        private ProgressBarUpdater pb = new ProgressBarUpdater(this, Bundle.getString(Rename.class, "RenameXmpValue.ProgressBar.String"));
        private final Column column;
        private final String newValue;
        private final String oldValue;
        private volatile boolean cancel;

        Rename(Column column, String oldValue, String newValue) {
            super("JPhotoTagger: Renaming XMP value");
            this.column = column;
            this.oldValue = oldValue.trim();
            this.newValue = newValue.trim();
        }

        @Override
        public void cancel() {
            cancel = true;
        }

        @Override
        public void run() {
            List<File> imageFiles = DatabaseImageFiles.INSTANCE.getImageFilesWhereColumnHasExactValue(column, oldValue);
            int size = imageFiles.size();
            int value = 0;

            notifyStarted(size);

            for (File imageFile : imageFiles) {
                if (cancel || isInterrupted()) {
                    break;
                }

                Xmp xmp = null;

                try {
                    xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);
                } catch (IOException ex) {
                    Logger.getLogger(RenameDeleteXmpValue.class.getName()).log(Level.SEVERE, null, ex);
                }

                if (xmp != null) {
                    rename(xmp);

                    if (XmpMetadata.writeXmpToSidecarFile(xmp, XmpMetadata.suggestSidecarFile(imageFile))) {
                        new InsertImageFilesIntoDatabase(Collections.singletonList(imageFile),
                                                         InsertIntoRepository.XMP).run();    // run in this thread!
                    }
                }

                notifyPerformed(++value, size);
            }

            DatabaseImageFiles.INSTANCE.deleteValueOfJoinedColumn(column, oldValue);
            MiscMetadataHelper.removeChildValueFrom(column, oldValue);
            notifyEnded(value, size);
        }

        private void rename(Xmp xmp) {
            xmp.setValue(column, newValue);
        }

        private void notifyStarted(int count) {
            pb.progressStarted(new ProgressEvent(this, 0, count, 0, null));
        }

        private void notifyPerformed(int value, int count) {
            pb.progressPerformed(new ProgressEvent(this, 0, count, value, null));
        }

        private void notifyEnded(int value, int count) {
            pb.progressEnded(new ProgressEvent(this, 0, count, value, null));
        }
    }
}
