package org.jphototagger.program.controller.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JRadioButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.JXList;
import org.jphototagger.api.storage.Storage;
import org.jphototagger.domain.thumbnails.TypeOfDisplayedImages;
import org.jphototagger.domain.thumbnails.event.ThumbnailsPanelRefreshEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class KeywordItemSelectedController implements ActionListener, ListSelectionListener {

    private static final String KEY_RADIO_BUTTON = "ControllerKeywordItemSelected.RadioButton";

    public KeywordItemSelectedController() {
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
            TypeOfDisplayedImages typeOfDisplayedImages = evt.getTypeOfDisplayedImages();

            if (TypeOfDisplayedImages.KEYWORD.equals(typeOfDisplayedImages)) {
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

        return KeywordsHelper.getSelectedKeywordsFromList(listSelKeywords);
    }

    private boolean isAllKeywords() {
        return getRadioButtonAllKeywords().isSelected();
    }

    private void readPersistent() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);
        boolean radioButtonAll = true;

        if (storage.containsKey(KEY_RADIO_BUTTON)) {
            radioButtonAll = storage.getInt(KEY_RADIO_BUTTON) == 0;
        }

        getRadioButtonAllKeywords().setSelected(radioButtonAll);
        getRadioButtonOneKeyword().setSelected(!radioButtonAll);
    }

    private void writePersistent() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);
        storage.setInt(KEY_RADIO_BUTTON, isAllKeywords()
                ? 0
                : 1);
    }
}
