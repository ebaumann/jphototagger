package org.jphototagger.program.controller.keywords.tree;

import org.jphototagger.program.controller.keywords.list.ShowThumbnailsContainingAllKeywords2;
import org.jphototagger.program.data.Keyword;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.AppPanel;

import java.awt.EventQueue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Listens to {@link AppPanel#getTreeSelKeywords()} and on selection
 * shows thumbnails of the selected keyword and all it's
 *
 * @author Elmar Baumann
 */
public final class ControllerKeywordsSelection implements TreeSelectionListener {
    public ControllerKeywordsSelection() {
        listen();
    }

    private void listen() {
        GUI.getSelKeywordsTree().getSelectionModel().addTreeSelectionListener(this);
    }

    @Override
    public void valueChanged(TreeSelectionEvent evt) {
        if (evt.isAddedPath()) {
            showThumbnailsOfSelKeywords();
        }
    }

    private void showThumbnailsOfSelKeywords() {
        EventQueue.invokeLater(new ShowThumbnailsContainingAllKeywords2(getKeywordStringPaths()));
    }

    private List<List<String>> getKeywordStringPaths() {
        List<List<String>> keywordPaths = new ArrayList<List<String>>();
        List<List<Keyword>> hkwp = getKeywordPaths();

        for (List<Keyword> kws : hkwp) {
            List<String> stringKeywords = new ArrayList<String>();

            for (Keyword kw : kws) {
                stringKeywords.add(kw.getName());
            }

            keywordPaths.add(stringKeywords);
        }

        return keywordPaths;
    }

    private List<List<Keyword>> getKeywordPaths() {
        TreePath[] selPaths = GUI.getSelKeywordsTree().getSelectionPaths();
        List<List<Keyword>> paths = new ArrayList<List<Keyword>>();

        for (TreePath selPath : selPaths) {
            DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            List<Keyword> kwPath = new ArrayList<Keyword>();

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
