package org.jphototagger.program.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectDeletedEvent;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifDeletedEvent;
import org.jphototagger.domain.repository.event.exif.ExifInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifUpdatedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileDeletedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.model.TableModelExt;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.database.DatabaseStatistics;
import org.jphototagger.program.database.metadata.selections.DatabaseInfoRecordCountColumns;

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
public final class TableModelDatabaseInfo extends TableModelExt {

    private static final long serialVersionUID = 1974343527501774916L;
    private final LinkedHashMap<Column, StringBuffer> bufferOfColumn = new LinkedHashMap<Column, StringBuffer>();
    private boolean listenToDatabase;

    public TableModelDatabaseInfo() {
        initBufferOfColumn();
        addColumnHeaders();
        addRows();
        AnnotationProcessor.process(this);
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
        addColumn(Bundle.getString(TableModelDatabaseInfo.class, "TableModelDatabaseInfo.HeaderColumn.1"));
        addColumn(Bundle.getString(TableModelDatabaseInfo.class, "TableModelDatabaseInfo.HeaderColumn.2"));
    }

    private void addRows() {
        Set<Column> columns = bufferOfColumn.keySet();

        for (Column column : columns) {
            addRow(getRow(column, bufferOfColumn.get(column)));
        }
    }

    private Object[] getRow(Column rowHeader, StringBuffer bufferDifferent) {
        return new Object[]{rowHeader, bufferDifferent};
    }

    private void setCount() {
        new SetCountThread().start();
    }

    @EventSubscriber(eventClass = ImageFileDeletedEvent.class)
    public void imageFileDeleted(ImageFileDeletedEvent evt) {
        update();
    }

    @EventSubscriber(eventClass = ImageFileInsertedEvent.class)
    public void imageFileInserted(ImageFileInsertedEvent evt) {
        update();
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        update();
    }

    @EventSubscriber(eventClass = DcSubjectDeletedEvent.class)
    public void dcSubjectDeleted(DcSubjectDeletedEvent evt) {
        update();
    }

    @EventSubscriber(eventClass = DcSubjectInsertedEvent.class)
    public void dcSubjectInserted(DcSubjectInsertedEvent evt) {
        update();
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        update();
    }

    @EventSubscriber(eventClass = ExifInsertedEvent.class)
    public void exifInserted(ExifInsertedEvent evt) {
        update();
    }

    @EventSubscriber(eventClass = ExifUpdatedEvent.class)
    public void exifUpdated(ExifUpdatedEvent evt) {
        update();
    }

    @EventSubscriber(eventClass = ExifDeletedEvent.class)
    public void exifDeleted(ExifDeletedEvent evt) {
        update();
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
                setCountToBuffer(bufferOfColumn.get(column), DatabaseStatistics.INSTANCE.getTotalRecordCountOf(column));
            }

            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    fireTableDataChanged();
                }
            });
        }
    }
}
