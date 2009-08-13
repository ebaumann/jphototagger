package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.app.MessageDisplayer;
import de.elmar_baumann.imv.data.HierarchicalKeyword;
import de.elmar_baumann.imv.model.TreeModelHierarchicalKeywords;
import de.elmar_baumann.imv.view.panels.HierarchicalKeywordsPanel;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;

/**
 * Listens to the menu item {@link HierarchicalKeywordsPanel#getMenuItemToggleReal()}
 * and toggles the real property of a keyword.
 *
 * @author  Martin Pohlack <martinp@gmx.de>
 * @version 2009-07-13
 */
public class ControllerToggleRealHierarchicalKeyword
        extends ControllerHierarchicalKeywords
        implements ActionListener, KeyListener {

    public ControllerToggleRealHierarchicalKeyword(
            HierarchicalKeywordsPanel _panel) {
        super(_panel);
    }

    @Override
    protected boolean myKey(KeyEvent e) {
        return KeyEventUtil.isControl(e, KeyEvent.VK_R);
    }

    @Override
    protected void localAction(DefaultMutableTreeNode node) {
        Object userObject = node.getUserObject();
        if (userObject instanceof HierarchicalKeyword) {
            HierarchicalKeyword keyword = (HierarchicalKeyword) userObject;
            TreeModel tm = getHKPanel().getTree().getModel();
            if (tm instanceof TreeModelHierarchicalKeywords) {
                keyword.setReal(!keyword.isReal());
                ((TreeModelHierarchicalKeywords) tm).changed(node, keyword);
            } else {
                AppLog.logWarning(
                        ControllerToggleRealHierarchicalKeyword.class,
                        "ControllerToggleRealHierarchicalKeyword.Error.Model"); // NOI18N
                }
        } else {
            MessageDisplayer.error(getHKPanel().getTree(),
                    "ControllerToggleRealHierarchicalKeyword.Error.Node", // NOI18N
                    node);
        }
    }
}
