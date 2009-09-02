package de.elmar_baumann.imv.controller.keywords;

import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.event.listener.RefreshListener;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ThumbnailsPanel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-25
 */
public final class ControllerKeywordItemSelected implements
        ListSelectionListener, RefreshListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList listKeywords = appPanel.getListKeywords();
    private final ThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();

    public ControllerKeywordItemSelected() {
        listen();
    }

    private void listen() {
        listKeywords.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.KEYWORD);
    }

    @Override
    public void refresh() {
        if (listKeywords.getSelectedIndex() >= 0) {
            update();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && listKeywords.getSelectedIndex() >= 0) {
            update();
        }
    }

    private void update() {
        List<String> selKeywords = getSelectedKeywords();
        SwingUtilities.invokeLater(new ShowThumbnailsContainingAllKeywords(selKeywords));
    }

    private List<String> getSelectedKeywords() {
        Object[] selValues = listKeywords.getSelectedValues();
        List<String> keywords = new ArrayList<String>();
        for (Object selValue : selValues) {
            if (selValue instanceof String) {
                keywords.add((String) selValue);
            }
        }
        assert keywords.size() == selValues.length :
                "Not all keywords are strings: " + keywords;
        return keywords;
    }
}
