package org.jphototagger.program.module.keywords.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.KeywordsRepository;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.factory.ModelFactory;
import org.openide.util.Lookup;

/**
 * Listens to repository updates and adds not existing keywords.
 *
 * @author Elmar Baumann
 */
public final class KeywordsRepositoryUpdatesController {

    private final KeywordsRepository repo = Lookup.getDefault().lookup(KeywordsRepository.class);

    public KeywordsRepositoryUpdatesController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        if (isAutoInsertKeywords()) {
            addNotExistingKeywords(evt.getUpdatedXmp());
        }
    }

    @EventSubscriber(eventClass = DcSubjectInsertedEvent.class)
    public void dcSubjectInserted(DcSubjectInsertedEvent evt) {
        if (isAutoInsertKeywords()) {
            addNotExistingKeywords(Collections.singleton(evt.getDcSubject()));
        }
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        if (isAutoInsertKeywords()) {
            addNotExistingKeywords(evt.getXmp());
        }
    }

    private boolean isAutoInsertKeywords() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return !prefs.containsKey(KeywordsTreePreferencesKeys.KEY_AUTO_INSERT_UNKNOWN_KEYWORDS)
                || prefs.getBoolean(KeywordsTreePreferencesKeys.KEY_AUTO_INSERT_UNKNOWN_KEYWORDS);
    }

    @SuppressWarnings("unchecked")
    private void addNotExistingKeywords(Xmp xmp) {
        Object xmpValue = xmp.getValue(XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
        if (xmpValue instanceof List<?>) {
            addNotExistingKeywords((List<String>) xmpValue);
        }
    }

    private void addNotExistingKeywords(Collection<? extends String> keywords) {
        for (String keyword : keywords) {
            if (!repo.existsKeyword(keyword)) {
                addKeyword(keyword);
            }
        }
    }

    private void addKeyword(final String keyword) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {
            @Override
            public void run() {
                KeywordsTreeModel model = ModelFactory.INSTANCE.getModel(KeywordsTreeModel.class);
                model.insert((DefaultMutableTreeNode) model.getRoot(), keyword, true, false);
            }
        });
    }
    }
