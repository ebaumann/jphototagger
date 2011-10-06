package org.jphototagger.program.module.keywords.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jphototagger.domain.metadata.keywords.Keyword;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.program.module.keywords.KeywordsHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.WaitDisplay;
import org.jphototagger.program.app.AppPanel;
import org.jphototagger.program.view.popupmenus.KeywordsTreePopupMenu;

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
                WaitDisplay.INSTANCE.show();

                AppPanel appPanel = GUI.getAppPanel();

                appPanel.displaySelKeywordsTree(AppPanel.SelectAlso.SEL_KEYWORDS_TAB);
                KeywordsHelper.selectNode(appPanel.getTreeSelKeywords(), keyword);
                WaitDisplay.INSTANCE.hide();
            }
        });
    }

    private void showImages(final String keyword) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                WaitDisplay.INSTANCE.show();

                AppPanel appPanel = GUI.getAppPanel();

                appPanel.displaySelKeywordsList(AppPanel.SelectAlso.SEL_KEYWORDS_TAB);
                ListUtil.select(appPanel.getListSelKeywords(), keyword, 0);
                WaitDisplay.INSTANCE.hide();
            }
        });
    }
}
