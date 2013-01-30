package org.jphototagger.program.module.keywords.tree;

import java.util.ArrayList;
import java.util.List;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.module.keywords.list.ShowThumbnailsContainingAllKeywords2;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class KeywordsSelectionController implements TreeSelectionListener {

    public KeywordsSelectionController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getSelKeywordsTree().getSelectionModel().addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            showThumbnailsOfSelKeywords();
        }
    }

    private void showThumbnailsOfSelKeywords() {
        EventQueueUtil.invokeInDispatchThread(new ShowThumbnailsContainingAllKeywords2(getKeywordStringPaths()));
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
}
