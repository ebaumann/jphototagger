package org.jphototagger.program.module.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JRadioButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.JXList;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.thumbnails.OriginOfDisplayedThumbnails;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.module.keywords.KeywordsUtil;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class KeywordListItemSelectedController implements ActionListener, ListSelectionListener {

    private static final String KEY_RADIO_BUTTON = "ControllerKeywordItemSelected.RadioButton";
    private final List<String> selectedKeywords = new ArrayList<>();

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

    private boolean keywordsSelected() {
        return GUI.getSelKeywordsList().getSelectedIndex() >= 0;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (keywordsSelected()) {
            writePersistent();
            update(null);
        }
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (keywordsSelected()) {
            OriginOfDisplayedThumbnails origin = evt.getOriginOfDisplayedThumbnails();
            if (OriginOfDisplayedThumbnails.FILES_MATCHING_A_KEYWORD.equals(origin)) {
                update(evt);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            selectedKeywords.clear();
            if (keywordsSelected()) {
                selectedKeywords.addAll(getSelectedKeywords());
            }
            update(null);
        }
    }

    private List<String> getSelectedKeywords() {
        JXList listSelKeywords = GUI.getSelKeywordsList();
        return KeywordsUtil.getSelectedKeywordsFromList(listSelKeywords);
    }

    private void update(ThumbnailsPanelRefreshEvent evt) {
        EventQueueUtil.invokeInDispatchThread(isAllKeywords()
                ? new ShowThumbnailsContainingAllKeywords(selectedKeywords, (evt == null)
                ? null
                : evt.getThumbnailsPanelSettings())
                : new ShowThumbnailsContainingKeywords(selectedKeywords, (evt == null)
                ? null
                : evt.getThumbnailsPanelSettings()));
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
