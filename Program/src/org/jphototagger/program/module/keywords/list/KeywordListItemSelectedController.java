package org.jphototagger.program.module.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JRadioButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.jdesktop.swingx.JXList;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.module.keywords.KeywordsUtil;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class KeywordListItemSelectedController implements ActionListener, ListSelectionListener {

    private static final String KEY_RADIO_BUTTON = "ControllerKeywordItemSelected.RadioButton";

    public KeywordListItemSelectedController() {
        readPersistent();
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
        GUI.getSelKeywordsList().addListSelectionListener(this);
        getRadioButtonAllKeywords().addActionListener(this);
        getRadioButtonOneKeyword().addActionListener(this);
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

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (GUI.getSelKeywordsList().getSelectedIndex() >= 0) {
            OriginOfDisplayedThumbnails typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

            if (OriginOfDisplayedThumbnails.FILES_MATCHING_A_KEYWORD.equals(typeOfDisplayedImages)) {
                update(evt);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting() && (GUI.getSelKeywordsList().getSelectedIndex() >= 0)) {
            update(null);
        }
    }

    private void update(ThumbnailsPanelRefreshEvent evt) {
        List<String> selKeywords = getSelectedKeywords();

        EventQueueUtil.invokeInDispatchThread(isAllKeywords()
                ? new ShowThumbnailsContainingAllKeywords(selKeywords, (evt == null)
                ? null
                : evt.getThumbnailsPanelSettings())
                : new ShowThumbnailsContainingKeywords(selKeywords, (evt == null)
                ? null
                : evt.getThumbnailsPanelSettings()));
    }

    private List<String> getSelectedKeywords() {
        JXList listSelKeywords = GUI.getSelKeywordsList();

        return KeywordsUtil.getSelectedKeywordsFromList(listSelKeywords);
    }

    private boolean isAllKeywords() {
        return getRadioButtonAllKeywords().isSelected();
    }

    private void readPersistent() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        boolean radioButtonAll = true;

        if (prefs.containsKey(KEY_RADIO_BUTTON)) {
            radioButtonAll = prefs.getInt(KEY_RADIO_BUTTON) == 0;
        }

        getRadioButtonAllKeywords().setSelected(radioButtonAll);
        getRadioButtonOneKeyword().setSelected(!radioButtonAll);
    }

    private void writePersistent() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setInt(KEY_RADIO_BUTTON, isAllKeywords()
                ? 0
                : 1);
    }
}
