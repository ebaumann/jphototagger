package org.jphototagger.program.controller.keywords.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.database.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.xmp.Xmp;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.database.DatabaseKeywords;
import org.jphototagger.program.factory.ModelFactory;
import org.jphototagger.program.model.TreeModelKeywords;

/**
 * Listens to database updates and adds not existing keywords.
 *
 * @author Elmar Baumann
 */
public final class ControllerKeywordsDbUpdates {

    public ControllerKeywordsDbUpdates() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
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

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        addNotExistingKeywords(evt.getUpdatedXmp());
    }

    @EventSubscriber(eventClass = DcSubjectInsertedEvent.class)
    public void dcSubjectInserted(DcSubjectInsertedEvent evt) {
        addNotExistingKeywords(Collections.singleton(evt.getDcSubject()));
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        addNotExistingKeywords(evt.getXmp());
    }
}
