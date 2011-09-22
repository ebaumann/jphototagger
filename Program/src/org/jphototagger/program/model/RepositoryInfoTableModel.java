package org.jphototagger.program.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.RepositoryInfoCountOfMetaDataValues;
import org.jphototagger.domain.repository.RepositoryStatistics;
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

/**
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class RepositoryInfoTableModel extends TableModelExt {

    private static final long serialVersionUID = 1974343527501774916L;
    private final LinkedHashMap<MetaDataValue, StringBuffer> bufferOfMetaDataValue = new LinkedHashMap<MetaDataValue, StringBuffer>();
    private boolean listenToRepository;
    private final RepositoryStatistics repo = Lookup.getDefault().lookup(RepositoryStatistics.class);

    public RepositoryInfoTableModel() {
        initBufferOfMetaDataValue();
        addColumnHeaders();
        addRows();
        AnnotationProcessor.process(this);
    }

    private void initBufferOfMetaDataValue() {
        List<MetaDataValue> metaDataValues = RepositoryInfoCountOfMetaDataValues.get();

        for (MetaDataValue mdValue : metaDataValues) {
            bufferOfMetaDataValue.put(mdValue, new StringBuffer());
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void update() {
        if (listenToRepository) {
            setCount();
        }
    }

    public void setListenToRepository(boolean listen) {
        listenToRepository = listen;
    }

    private void addColumnHeaders() {
        addColumn(Bundle.getString(RepositoryInfoTableModel.class, "RepositoryInfoTableModel.HeaderColumn.1"));
        addColumn(Bundle.getString(RepositoryInfoTableModel.class, "RepositoryInfoTableModel.HeaderColumn.2"));
    }

    private void addRows() {
        Set<MetaDataValue> columns = bufferOfMetaDataValue.keySet();

        for (MetaDataValue column : columns) {
            addRow(getRow(column, bufferOfMetaDataValue.get(column)));
        }
    }

    private Object[] getRow(MetaDataValue rowHeader, StringBuffer bufferDifferent) {
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
            super("JPhotoTagger: Setting count in repository info");
            setPriority(MIN_PRIORITY);
        }

        @Override
        public void run() {
            for (MetaDataValue mdValue : bufferOfMetaDataValue.keySet()) {
                setCountToBuffer(bufferOfMetaDataValue.get(mdValue), repo.getCountOfMetaDataValue(mdValue));
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
