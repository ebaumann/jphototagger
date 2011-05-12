package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.UserSettings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.List;
import java.util.Properties;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JRadioButton;
import org.jdesktop.swingx.JXList;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.helper.KeywordsHelper;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerKeywordItemSelected implements ActionListener, ListSelectionListener, RefreshListener {
    private static final String KEY_RADIO_BUTTON = "ControllerKeywordItemSelected.RadioButton";

    public ControllerKeywordItemSelected() {
        readPersistent();
        listen();
    }

    private void listen() {
        GUI.getSelKeywordsList().addListSelectionListener(this);
        getRadioButtonAllKeywords().addActionListener(this);
        getRadioButtonOneKeyword().addActionListener(this);
        GUI.getThumbnailsPanel().addRefreshListener(this, Content.KEYWORD);
    }

    private JRadioButton getRadioButtonAllKeywords() {
        return GUI.getAppPanel().getRadioButtonSelKeywordsMultipleSelAll();
    }

    private JRadioButton getRadioButtonOneKeyword() {
        return GUI.getAppPanel().getRadioButtonSelKeywordsMultipleSelOne();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (GUI.getSelKeywordsList().getSelectedIndex() >= 0) {
            writePersistent();
            update(null);
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        if (GUI.getSelKeywordsList().getSelectedIndex() >= 0) {
            update(evt);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting() && (GUI.getSelKeywordsList().getSelectedIndex() >= 0)) {
            update(null);
        }
    }

    private void update(RefreshEvent evt) {
        List<String> selKeywords = getSelectedKeywords();

        EventQueueUtil.invokeInDispatchThread(isAllKeywords()
                               ? new ShowThumbnailsContainingAllKeywords(selKeywords, (evt == null)
                ? null
                : evt.getSettings())
                               : new ShowThumbnailsContainingKeywords(selKeywords, (evt == null)
                ? null
                : evt.getSettings()));
    }

    private List<String> getSelectedKeywords() {
        JXList listSelKeywords = GUI.getSelKeywordsList();

        return KeywordsHelper.getSelectedKeywordsFromList(listSelKeywords);
    }

    private boolean isAllKeywords() {
        return getRadioButtonAllKeywords().isSelected();
    }

    private void readPersistent() {
        Properties properties = UserSettings.INSTANCE.getProperties();
        boolean radioButtonAll = true;

        if (properties.containsKey(KEY_RADIO_BUTTON)) {
            radioButtonAll = UserSettings.INSTANCE.getSettings().getInt(KEY_RADIO_BUTTON) == 0;
        }

        getRadioButtonAllKeywords().setSelected(radioButtonAll);
        getRadioButtonOneKeyword().setSelected(!radioButtonAll);
    }

    private void writePersistent() {
        UserSettings.INSTANCE.getSettings().set(isAllKeywords()
                ? 0
                : 1, KEY_RADIO_BUTTON);
        UserSettings.INSTANCE.writeToFile();
    }
}
