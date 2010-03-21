/*
 * @(#)RenameDeleteXmpValue.java    Created on 2010-03-15
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

package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.XmpColumns;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.tasks.UserTasks;
import de.elmar_baumann.jpt.view.panels.ProgressBarUpdater;

import java.io.File;

import java.util.Collections;
import java.util.List;

/**
 * Renames or deletes values in XMP sidecar files.
 *
 * @author  Elmar Baumann
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
        checkColumn(column);

        String newValue =
            MessageDisplayer.input("RenameXmpValue.Input.NewValue", oldValue,
                                   "RenameXmpValue.Settings", oldValue);

        if (newValue != null) {
            newValue = newValue.trim();

            if (newValue.equals(oldValue.trim())) {
                MessageDisplayer.error(null,
                                       "RenameXmpValue.Error.ValuesEquals");
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
        checkColumn(column);

        if (value.trim().isEmpty()) {
            return;
        }

        if (MessageDisplayer.confirmYesNo(null,
                                          "RenameXmpValue.Confirm.Delete",
                                          value)) {
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

    private static class Rename extends Thread {
        ProgressBarUpdater pb = new ProgressBarUpdater(
                                    JptBundle.INSTANCE.getString(
                                        "RenameXmpValue.ProgressBar.String"));
        private final Column column;
        private final String newValue;
        private final String oldValue;

        public Rename(Column column, String oldValue, String newValue) {
            this.column   = column;
            this.oldValue = oldValue.trim();
            this.newValue = newValue.trim();
            setName("Renaming XMP value @ " + getClass().getSimpleName());
        }

        @Override
        public void run() {
            List<File> imageFiles =
                DatabaseImageFiles.INSTANCE.getImageFilesWithColumnContent(
                    column, oldValue);
            int size  = imageFiles.size();
            int value = 0;

            notifyStarted(size);

            for (File imageFile : imageFiles) {
                Xmp xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFile);

                if (xmp != null) {
                    rename(xmp);

                    if (XmpMetadata.writeXmpToSidecarFile(xmp,
                            XmpMetadata.suggestSidecarFile(imageFile))) {
                        new InsertImageFilesIntoDatabase(
                            Collections.singletonList(imageFile),
                            Insert.XMP).run();    // No separate thread!
                    }
                }

                notifyPerformed(++value, size);
            }

            DatabaseImageFiles.INSTANCE.deleteValueOfJoinedColumn(column,
                    oldValue);
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
            pb.progressPerformed(new ProgressEvent(this, 0, count, value,
                    null));
        }

        private void notifyEnded(int value, int count) {
            pb.progressEnded(new ProgressEvent(this, 0, count, value, null));
        }
    }
}
