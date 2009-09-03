package de.elmar_baumann.imv.controller.hierarchicalkeywords;

import de.elmar_baumann.imv.helper.InsertHierarchicalKeywords;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.lib.componentutil.ListUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

/**
 * Inserts the (flat) keywords and categories into the hierarchical keywords
 * root.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-02
 */
public final class ControllerInsertIntoHierarchicalKeywords
        implements ActionListener {

    private final JMenuItem menuItemCategories = GUI.INSTANCE.getAppFrame().
            getMenuItemCopyCategoriesToHierarchicalKeywords();
    private final JMenuItem menuItemKeywords = GUI.INSTANCE.getAppFrame().
            getMenuItemCopyKeywordsToHierarchicalKeywords();

    public ControllerInsertIntoHierarchicalKeywords() {
        listen();
    }

    private void listen() {
        menuItemCategories.addActionListener(this);
        menuItemKeywords.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        List<String> keywords = getKeywords(e.getSource());
        if (keywords.size() > 0) {
            SwingUtilities.invokeLater(new InsertHierarchicalKeywords(keywords));
        }
    }

    private List<String> getKeywords(Object source) {
        if (source == menuItemCategories) {
            return ListUtil.toStringList(
                    GUI.INSTANCE.getAppPanel().getListCategories().getModel());
        } else if (source == menuItemKeywords) {
            return ListUtil.toStringList(
                    GUI.INSTANCE.getAppPanel().getListKeywords().getModel());
        } else {
            assert false : "Invalid source: " + source; // NOI18N
            return new ArrayList<String>();
        }
    }
}
