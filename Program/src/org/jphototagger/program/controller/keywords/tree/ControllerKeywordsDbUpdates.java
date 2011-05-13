package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.DatabaseKeywords;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 * Listens to database updates and adds not existing keywords.
 *
 * @author Elmar Baumann
 */
public final class ControllerKeywordsDbUpdates implements DatabaseImageFilesListener {
    public ControllerKeywordsDbUpdates() {
        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    @SuppressWarnings("unchecked")
    private void addNotExistingKeywords(Xmp xmp) {
        Object o = xmp.getValue(ColumnXmpDcSubjectsSubject.INSTANCE);

        if (o instanceof List<?>) {
            addNotExistingKeywords((List<String>) o);
        }
    }

    private void addNotExistingKeywords(Collection<? extends String> keywords) {
        for (String keyword : keywords) {
            if (!DatabaseKeywords.INSTANCE.exists(keyword)) {
                addKeyword(keyword);
            }
        }
    }

    private void addKeyword(final String keyword) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                TreeModelKeywords model = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

                model.insert((DefaultMutableTreeNode) model.getRoot(), keyword, true, false);
            }
        });
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        addNotExistingKeywords(updatedXmp);
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {
        addNotExistingKeywords(Collections.singleton(dcSubject));
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        addNotExistingKeywords(xmp);
    }

    @Override
    public void imageFileDeleted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileInserted(File imageFile) {

        // ignore
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {

        // ignore
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {

        // ignore
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {

        // ignore
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {

        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {

        // ignore
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {

        // ignore
    }
}
