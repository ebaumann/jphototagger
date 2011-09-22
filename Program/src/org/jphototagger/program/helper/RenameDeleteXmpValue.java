package org.jphototagger.program.helper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpMetaDataValues;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.tasks.UserTasks;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.jphototagger.xmp.XmpMetadata;

/**
 * Renames or deletes values in XMP sidecar files.
 *
 * @author Elmar Baumann
 */
public final class RenameDeleteXmpValue {

    private RenameDeleteXmpValue() {
    }

    /**
     * Renames a XMP value in all XMP sidecar files containing that value in
     * associated with a specific metadata value.
     * <p>
     * If renamed successfully, the repository will be updated.
     *
     * @param metaDataValue   XMP metadata value, <em>not</em> {@code XmpDcSubjectsSubjectMetaDataValue}
     * @param oldValue old value, will be trimmed
     * @throws         NullPointerException if one of the parameters is null
     * @throws         IllegalArgumentException if the metadata value is not a XMP metacata value,
     *                 see {@code XmpMetaDataValues#get()}, or is
     *                 {@code XmpDcSubjectsSubjectMetaDataValue}
     */
    public static void rename(MetaDataValue metaDataValue, String oldValue) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        if (oldValue == null) {
            throw new NullPointerException("oldValue == null");
        }

        checkMetaDataValue(metaDataValue);
        String info = Bundle.getString(RenameDeleteXmpValue.class, "RenameXmpValue.Input.NewValue");
        String input = oldValue;
        String newValue = MessageDisplayer.input(info, input);

        if (newValue != null) {
            newValue = newValue.trim();

            if (newValue.equals(oldValue.trim())) {
                String message = Bundle.getString(RenameDeleteXmpValue.class, "RenameXmpValue.Error.ValuesEquals");
                MessageDisplayer.error(null, message);
            } else {
                UserTasks.INSTANCE.add(new Rename(metaDataValue, oldValue, newValue));
            }
        }
    }

    /**
     * Deletes a XMP value from all XMP sidecar files containing that value in
     * associated with a specific metadata value.
     * <p>
     * If renamed successfully, the repository will be updated.
     *
     * @param mdValue XMP metadata value, <em>not</em> {@code XmpDcSubjectsSubjectMetaDataValue}
     * @param value  value to be deleted
     * @throws       NullPointerException if one of the parameters is null
     * @throws       IllegalArgumentException if mdValue is not a XMP metadata value,
     *               see {@code XmpMetaDataValues#get()}, or is
     *               {@code XmpDcSubjectsSubjectMetaDataValue}
     */
    public static void delete(MetaDataValue mdValue, String value) {
        if (mdValue == null) {
            throw new NullPointerException("mdValue == null");
        }

        if (value == null) {
            throw new NullPointerException("value == null");
        }

        checkMetaDataValue(mdValue);

        if (value.trim().isEmpty()) {
            return;
        }

        String message = Bundle.getString(RenameDeleteXmpValue.class, "RenameXmpValue.Confirm.Delete", value);

        if (MessageDisplayer.confirmYesNo(null, message)) {
            UserTasks.INSTANCE.add(new Rename(mdValue, value, ""));
        }
    }

    private static void checkMetaDataValue(MetaDataValue mdValue) {
        if (!XmpMetaDataValues.get().contains(mdValue)) {
            throw new IllegalArgumentException("Not a XMP metadata value: " + mdValue);
        }

        if (mdValue.equals(XmpDcSubjectsSubjectMetaDataValue.INSTANCE)) {
            throw new IllegalArgumentException("DC subjects are invalid!");
        }
    }

    private static class Rename extends Thread implements Cancelable {

        private ProgressBarUpdater pb = new ProgressBarUpdater(this, Bundle.getString(Rename.class, "RenameXmpValue.ProgressBar.String"));
        private final MetaDataValue metaDataValue;
        private final String newValue;
        private final String oldValue;
        private volatile boolean cancel;
        private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

        Rename(MetaDataValue metaDataValue, String oldValue, String newValue) {
            super("JPhotoTagger: Renaming XMP value");
            this.metaDataValue = metaDataValue;
            this.oldValue = oldValue.trim();
            this.newValue = newValue.trim();
        }

        @Override
        public void cancel() {
            cancel = true;
        }

        @Override
        public void run() {
            List<File> imageFiles = repo.findImageFilesWhereMetaDataValueHasExactValue(metaDataValue, oldValue);
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
                        new InsertImageFilesIntoRepository(Collections.singletonList(imageFile),
                                InsertIntoRepository.XMP).run();    // run in this thread!
                    }
                }

                notifyPerformed(++value, size);
            }

            repo.deleteValueOfJoinedMetaDataValue(metaDataValue, oldValue);
            MiscMetadataHelper.removeChildValueFrom(metaDataValue, oldValue);
            notifyEnded(value, size);
        }

        private void rename(Xmp xmp) {
            xmp.setValue(metaDataValue, newValue);
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
