package org.jphototagger.repository.hsqldb;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.event.listener.ListenerSupport;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.DcSubjectsStatistics;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectDeletedEvent;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectRenamedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.lib.concurrent.SerialExecutor;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = DcSubjectsStatistics.class)
public final class DcSubjectsStatisticsImpl implements DcSubjectsStatistics {


    private static final ThreadFactory THREAD_FACTORY = new ThreadFactory() {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("JPhotoTagger: DC Subject change notification");
            return thread;
        }
    };

    private static final Map<String, Integer> IMAGE_COUNT_OF_DC_SUBJECTS = new HashMap<String, Integer>();
    private static final ListenerSupport<DcSubjectsStatistics.Listener> LS = new ListenerSupport<>();
    private static final SerialExecutor LISTENERS_EXECUTOR = new SerialExecutor(Executors.newFixedThreadPool(1, THREAD_FACTORY));
    private static volatile boolean INIT;

    private synchronized void checkInit() {
        if (!INIT) {
            IMAGE_COUNT_OF_DC_SUBJECTS.putAll(ImageFilesDatabase.INSTANCE.getImageCountOfDcSubjects());
            AnnotationProcessor.process(DcSubjectsStatisticsImpl.this);
            INIT = true;
        }
    }

    @Override
    public synchronized int getImageCountOfDcSubject(String dcSubject) {
        if (dcSubject == null) {
            return 0;
        }

        checkInit();

        Integer count = IMAGE_COUNT_OF_DC_SUBJECTS.get(dcSubject);

        return count == null
                ? 0
                : count;
    }

    // synchronized is not neccessary, because ListenerSupport handles this
    @Override
    public void addListener(Listener listener) {
        LS.add(listener);
    }

    // synchronized is not neccessary, because ListenerSupport handles this
    @Override
    public void removeListener(Listener listener) {
        LS.remove(listener);
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public synchronized void xmpInsertedEvent(XmpInsertedEvent evt) {
        notify(getDcSubjects(evt.getXmp()));
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public synchronized void xmpUpdated(final XmpUpdatedEvent evt) {
        Collection <String> dcSubjects = new HashSet<>(getDcSubjects(evt.getOldXmp()));
        dcSubjects.addAll(getDcSubjects(evt.getUpdatedXmp()));
        notify(dcSubjects);
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public synchronized void xmpDeletedEvent(XmpDeletedEvent evt) {
        notify(getDcSubjects(evt.getXmp()));
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getDcSubjects(Xmp xmp) {
        if (xmp == null) {
            return Collections.emptyList();
        }

        Object value = xmp.getValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);

        return value instanceof Collection
                ? (Collection<String>) value
                : Collections.<String>emptyList();
    }

    private void notify(Collection<String> dcSubjects) {
        for (String dcSubject : dcSubjects) {
            setImageCount(dcSubject);
        }
    }

    @EventSubscriber(eventClass = DcSubjectDeletedEvent.class)
    public synchronized void dcSubjectDeleted(final DcSubjectDeletedEvent evt) {
        setImageCount(evt.getDcSubject());
    }

    @EventSubscriber(eventClass = DcSubjectInsertedEvent.class)
    public synchronized void dcSubjectInserted(final DcSubjectInsertedEvent evt) {
        setImageCount(evt.getDcSubject());
    }

    @EventSubscriber(eventClass = DcSubjectRenamedEvent.class)
    public synchronized void dcSubjectRenamed(final DcSubjectRenamedEvent evt) {
        setImageCount(evt.getFromName());
        setImageCount(evt.getToName());
    }

    private synchronized void setImageCount(String dcSubject) {
        if (dcSubject != null) {
            int count = ImageFilesDatabase.INSTANCE.getImageCountOfDcSubject(dcSubject);
            IMAGE_COUNT_OF_DC_SUBJECTS.put(dcSubject, count);
            notifyListeners(dcSubject, count);
        }
    }

    private void notifyListeners(String dcSubject, int count) {
        CountNotification countNotification = new CountNotification(dcSubject, count);

        LISTENERS_EXECUTOR.execute(countNotification);
    }

    private static final class CountNotification implements Runnable {

        private final String dcSubject;
        private final int count;

        private CountNotification(String dcSubject, int count) {
            this.dcSubject = dcSubject;
            this.count = count;
        }

        @Override
        public void run() {
            for (Listener listener : LS.get()) {
                listener.imageCountContainingDcSubjectChanged(dcSubject, count);
            }
        }
    }
}
