package org.jphototagger.program.model;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.DatabaseStatistics;
import org.jphototagger.program.database.metadata.xmp
    .ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;

import java.awt.EventQueue;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;

/**
 * Elements are keyword {@link String}s retrieved through
 * {@link DatabaseImageFiles#getAllDcSubjects()}.
 *
 * @author Elmar Baumann
 */
public final class ListModelKeywords extends DefaultListModel
        implements DatabaseImageFilesListener {
    private static final long serialVersionUID = -9181622876402951455L;

    public ListModelKeywords() {
        addElements();
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        Set<String> keywords = DatabaseImageFiles.INSTANCE.getAllDcSubjects();

        for (String keyword : keywords) {
            addElement(keyword);
        }
    }

    private void addNewKeywords(Collection<? extends String> keywords) {
        for (final String keyword : keywords) {
            if (!contains(keyword)) {
                addElement(keyword);
            }
        }
    }

    private void removeKeywordsNotInDb(Collection<? extends String> keywords) {
        for (final String keyword : keywords) {
            if (contains(keyword) &&!databaseHasKeyword(keyword)) {
                removeElement(keyword);
            }
        }
    }

    boolean databaseHasKeyword(String keyword) {
        return DatabaseStatistics.INSTANCE.existsValueIn(
            ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
    }

    @SuppressWarnings("unchecked")
    private List<String> getKeywords(Xmp xmp) {
        List<String> keywords = new ArrayList<String>();

        if (xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
            keywords.addAll(
                (List<String>) xmp.getValue(
                    ColumnXmpDcSubjectsSubject.INSTANCE));
        }

        return keywords;
    }

    @Override
    public void xmpInserted(File imageFile, final Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                addNewKeywords(getKeywords(xmp));
            }
        });
    }

    @Override
    public void xmpDeleted(File imageFile, final Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                removeKeywordsNotInDb(getKeywords(xmp));
            }
        });
    }

    @Override
    public void xmpUpdated(File imageFile, final Xmp oldXmp,
                           final Xmp updatedXmp) {
        if (oldXmp == null) {
            throw new NullPointerException("oldXmp == null");
        }

        if (updatedXmp == null) {
            throw new NullPointerException("updatedXmp == null");
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                addNewKeywords(getKeywords(updatedXmp));
                removeKeywordsNotInDb(getKeywords(oldXmp));
            }
        });
    }

    @Override
    public void dcSubjectDeleted(final String dcSubject) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                removeKeywordsNotInDb(Collections.singleton(dcSubject));
            }
        });
    }

    @Override
    public void dcSubjectInserted(final String dcSubject) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                addNewKeywords(Collections.singleton(dcSubject));
            }
        });
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
    public void imageFileRenamed(File oldImageFile, File newImageFile) {

        // ignore
    }
}
