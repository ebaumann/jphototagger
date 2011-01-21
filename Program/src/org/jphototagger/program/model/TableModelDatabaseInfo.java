package org.jphototagger.program.model;

import org.jphototagger.program.data.Exif;
import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.DatabaseStatistics;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections
    .DatabaseInfoRecordCountColumns;
import org.jphototagger.program.event.listener.DatabaseImageFilesListener;
import org.jphototagger.program.resource.JptBundle;

import java.awt.EventQueue;

import java.io.File;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

/**
 * Elements are {@link Column}s retrieved through
 * {@link DatabaseInfoRecordCountColumns#get()}.
 *
 * This model contains information about the database content, currently the
 * count of table rows. If the database content changes, this model updates
 * itself if set through {@link #setListenToDatabase(boolean)}.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class TableModelDatabaseInfo extends DefaultTableModel
        implements DatabaseImageFilesListener {
    private static final long                         serialVersionUID =
        1974343527501774916L;
    private final LinkedHashMap<Column, StringBuffer> bufferOfColumn =
        new LinkedHashMap<Column, StringBuffer>();
    private boolean listenToDatabase;

    public TableModelDatabaseInfo() {
        initBufferOfColumn();
        addColumnHeaders();
        addRows();
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    private void initBufferOfColumn() {
        List<Column> columns = DatabaseInfoRecordCountColumns.get();

        for (Column column : columns) {
            bufferOfColumn.put(column, new StringBuffer());
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void update() {
        if (listenToDatabase) {
            setCount();
        }
    }

    public void setListenToDatabase(boolean listen) {
        listenToDatabase = listen;
    }

    private void addColumnHeaders() {
        addColumn(
            JptBundle.INSTANCE.getString(
                "TableModelDatabaseInfo.HeaderColumn.1"));
        addColumn(
            JptBundle.INSTANCE.getString(
                "TableModelDatabaseInfo.HeaderColumn.2"));
    }

    private void addRows() {
        Set<Column> columns = bufferOfColumn.keySet();

        for (Column column : columns) {
            addRow(getRow(column, bufferOfColumn.get(column)));
        }
    }

    private Object[] getRow(Column rowHeader, StringBuffer bufferDifferent) {
        return new Object[] { rowHeader, bufferDifferent };
    }

    private void setCount() {
        new SetCountThread().start();
    }

    @Override
    public void imageFileDeleted(File imageFile) {
        update();
    }

    @Override
    public void imageFileInserted(File imageFile) {
        update();
    }

    @Override
    public void xmpUpdated(File imageFile, Xmp oldXmp, Xmp updatedXmp) {
        update();
    }

    @Override
    public void dcSubjectDeleted(String dcSubject) {
        update();
    }

    @Override
    public void dcSubjectInserted(String dcSubject) {
        update();
    }

    @Override
    public void xmpInserted(File imageFile, Xmp xmp) {
        update();
    }

    @Override
    public void xmpDeleted(File imageFile, Xmp xmp) {
        update();
    }

    @Override
    public void exifInserted(File imageFile, Exif exif) {
        update();
    }

    @Override
    public void exifUpdated(File imageFile, Exif oldExif, Exif updatedExif) {
        update();
    }

    @Override
    public void exifDeleted(File imageFile, Exif exif) {
        update();
    }

    @Override
    public void imageFileRenamed(File oldImageFile, File newImageFile) {

        // ignore
    }

    @Override
    public void thumbnailUpdated(File imageFile) {

        // ignore
    }

    private void setCountToBuffer(StringBuffer buffer, Integer count) {
        buffer.replace(0, buffer.length(), count.toString());
    }

    private class SetCountThread extends Thread {
        SetCountThread() {
            super("JPhotoTagger: Setting count in database info");
            setPriority(MIN_PRIORITY);
        }

        @Override
        public void run() {
            for (Column column : bufferOfColumn.keySet()) {
                setCountToBuffer(
                    bufferOfColumn.get(column),
                    DatabaseStatistics.INSTANCE.getTotalRecordCountOf(column));
            }

            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    fireTableDataChanged();
                }
            });
        }
    }
}
