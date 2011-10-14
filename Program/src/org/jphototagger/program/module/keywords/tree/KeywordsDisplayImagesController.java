package org.jphototagger.program.module.keywords.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.openide.util.Lookup;

import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.util.ListUtil;
import org.jphototagger.program.app.ui.AppPanel;
import org.jphototagger.program.module.keywords.KeywordsUtil;
import org.jphototagger.program.resource.GUI;

/**
 * Listens to the menu item
 * {@code KeywordsTreePopupMenu#getItemDisplayImages()} and on
 * action displays images with the selected keyword.
 *
 * @author Elmar Baumann
 */
public class KeywordsDisplayImagesController implements ActionListener {

    public KeywordsDisplayImagesController() {
        listen();
    }

    private void listen() {
        getItemDisplayImages().addActionListener(this);
        getItemDisplayImagesKw().addActionListener(this);
    }

    private JMenuItem getItemDisplayImages() {
        return KeywordsTreePopupMenu.INSTANCE.getItemDisplayImages();
    }

    private JMenuItem getItemDisplayImagesKw() {
        return KeywordsTreePopupMenu.INSTANCE.getItemDisplayImagesKw();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Keyword keyword = getKeyword();

        if (keyword == null) {
            return;
        }

        Object source = evt.getSource();

        if (source == getItemDisplayImages()) {
            showImages(keyword);
        } else if (source == getItemDisplayImagesKw()) {
            showImages(keyword.getName());
        }
    }

    private Keyword getKeyword() {
        TreePath treePath = KeywordsTreePopupMenu.INSTANCE.getTreePathAtMouseCursor();

        if (treePath == null) {
            return null;
        }

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath.getLastPathComponent();
        Object userObject = node.getUserObject();

        if (userObject instanceof Keyword) {
            return (Keyword) userObject;
        }

        return null;
    }

    private void showImages(final Keyword keyword) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                waitDisplayer.show();
                AppPanel appPanel = GUI.getAppPanel();

                appPanel.displaySelKeywordsTree(AppPanel.SelectAlso.SEL_KEYWORDS_TAB);
                KeywordsUtil.selectNode(appPanel.getTreeSelKeywords(), keyword);
                waitDisplayer.hide();
            }
        });
    }

    private void showImages(final String keyword) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
                waitDisplayer.show();
                AppPanel appPanel = GUI.getAppPanel();

                appPanel.displaySelKeywordsList(AppPanel.SelectAlso.SEL_KEYWORDS_TAB);
                ListUtil.select(appPanel.getListSelKeywords(), keyword, 0);
                waitDisplayer.hide();
            }
        });
    }
}
