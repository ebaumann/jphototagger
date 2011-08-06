package org.jphototagger.program.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.database.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.repository.event.DcSubjectDeletedEvent;
import org.jphototagger.domain.repository.event.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.XmpUpdatedEvent;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.DatabaseStatistics;

/**
 *
 * @author Elmar Baumann
 */
public final class ListModelKeywords extends DefaultListModel {

    private static final long serialVersionUID = -9181622876402951455L;

    public ListModelKeywords() {
        addElements();
        AnnotationProcessor.process(this);
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
        for (String keyword : keywords) {
            if (!contains(keyword)) {
                addElement(keyword);
            }
        }
    }

    private void removeKeywordsNotInDb(Collection<? extends String> keywords) {
        for (String keyword : keywords) {
            if (contains(keyword) && !databaseHasKeyword(keyword)) {
                removeElement(keyword);
            }
        }
    }

    boolean databaseHasKeyword(String keyword) {
        return DatabaseStatistics.INSTANCE.existsValueIn(ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
    }

    @SuppressWarnings("unchecked")
    private List<String> getKeywords(Xmp xmp) {
        List<String> keywords = new ArrayList<String>();

        if (xmp.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
            keywords.addAll((List<String>) xmp.getValue(ColumnXmpDcSubjectsSubject.INSTANCE));
        }

        return keywords;
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(final XmpInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                addNewKeywords(getKeywords(evt.getXmp()));
            }
        });
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(final XmpDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                removeKeywordsNotInDb(getKeywords(evt.getXmp()));
            }
        });
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(final XmpUpdatedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                addNewKeywords(getKeywords(evt.getUpdatedXmp()));
                removeKeywordsNotInDb(getKeywords(evt.getOldXmp()));
            }
        });
    }

    @EventSubscriber(eventClass = DcSubjectDeletedEvent.class)
    public void dcSubjectDeleted(final DcSubjectDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                removeKeywordsNotInDb(Collections.singleton(evt.getDcSubject()));
            }
        });
    }

    @EventSubscriber(eventClass = DcSubjectInsertedEvent.class)
    public void dcSubjectInserted(final DcSubjectInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                addNewKeywords(Collections.singleton(evt.getDcSubject()));
            }
        });
    }
}
