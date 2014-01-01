package org.jphototagger.program.module.keywords.tree;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.thumbnails.ThumbnailsPanelSettings;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.module.keywords.list.ShowThumbnailsContainingAllKeywords2;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class KeywordsTreeItemSelectedController implements TreeSelectionListener {

    private final List<List<String>> selectedKeywordPaths = new ArrayList<>();

    public KeywordsTreeItemSelectedController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getSelKeywordsTree().getSelectionModel().addTreeSelectionListener(this);
    }

    private boolean isKeywordSelected() {
        return GUI.getSelKeywordsTree().getSelectionPaths() != null;
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        selectedKeywordPaths.clear();
        if (evt.isAddedPath()) {
            selectedKeywordPaths.addAll(getKeywordStringPaths());
            showThumbnailsOfSelectedKeywords(new ThumbnailsPanelSettings(new Point(0, 0), Collections.<Integer>emptyList()));
        }
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (isKeywordSelected() && evt.getOriginOfDisplayedThumbnails().isFilesMatchingAKeyword()) {
            showThumbnailsOfSelectedKeywords(evt.getThumbnailsPanelSettings());
        }
    }

    private void showThumbnailsOfSelectedKeywords(ThumbnailsPanelSettings settings) {
        EventQueueUtil.invokeInDispatchThread(new ShowThumbnailsContainingAllKeywords2(selectedKeywordPaths, settings));
    }

    private List<List<String>> getKeywordStringPaths() {
        List<List<String>> keywordPaths = new ArrayList<>();
        List<List<Keyword>> hkwp = getKeywordPaths();
        for (List<Keyword> kws : hkwp) {
            List<String> stringKeywords = new ArrayList<>();
            for (Keyword kw : kws) {
                stringKeywords.add(kw.getName());
            }
            keywordPaths.add(stringKeywords);
        }
        return keywordPaths;
    }

    private List<List<Keyword>> getKeywordPaths() {
        TreePath[] selPaths = GUI.getSelKeywordsTree().getSelectionPaths();
        List<List<Keyword>> paths = new ArrayList<>();
        for (TreePath selPath : selPaths) {
            DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            List<Keyword> kwPath = new ArrayList<>();
            for (Object userObject : selNode.getUserObjectPath()) {
                if (userObject instanceof Keyword) {
                    Keyword kw = (Keyword) userObject;
                    if (kw.isReal()) {
                        kwPath.add(kw);
                    }
                }
            }
            paths.add(kwPath);
        }
        return paths;
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        if (isKeywordSelected()
                && evt.getXmp().containsOneOf(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, getSelectedKeywords())) {
            showThumbnailsOfSelectedKeywords(createThumbnailsPanelSettings());
        }
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        if (isKeywordSelected()
                && evt.getXmp().containsOneOf(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, getSelectedKeywords())) {
            showThumbnailsOfSelectedKeywords(createThumbnailsPanelSettings());
        }
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        if (isKeywordSelected()) {
            List<String> selectedKeywords = getSelectedKeywords();
            if ((Xmp.valueDeleted(evt.getOldXmp(), evt.getUpdatedXmp(), XmpDcSubjectsSubjectMetaDataValue.INSTANCE, selectedKeywords)
                    || Xmp.valueInserted(evt.getOldXmp(), evt.getUpdatedXmp(), XmpDcSubjectsSubjectMetaDataValue.INSTANCE, selectedKeywords))) {
                showThumbnailsOfSelectedKeywords(createThumbnailsPanelSettings());
            }
        }
    }

    private List<String> getSelectedKeywords() {
        List<String> selKeywords = new ArrayList<>(selectedKeywordPaths.size());
        for (List<String> path : selectedKeywordPaths) {
            int pathsize = path.size();
            if (pathsize > 0) {
                selKeywords.add(path.get(pathsize - 1));
            }
        }
        return selKeywords;
    }

    public ThumbnailsPanelSettings createThumbnailsPanelSettings() {
        ThumbnailsPanelSettings settings = new ThumbnailsPanelSettings(GUI.getThumbnailsPanel().getViewPosition(), Collections.<Integer>emptyList());
        settings.setSelectedFiles(GUI.getThumbnailsPanel().getSelectedFiles());
        return settings;
}
}
