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
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
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

    private boolean isKeywordSelected() {
        return GUI.getSelKeywordsList().getSelectedIndex() >= 0;
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (isKeywordSelected()) {
            writePersistent();
            update(null);
        }
    }

    @EventSubscriber(eventClass = ThumbnailsPanelRefreshEvent.class)
    public void refresh(ThumbnailsPanelRefreshEvent evt) {
        if (isKeywordSelected()) {
            OriginOfDisplayedThumbnails origin = evt.getOriginOfDisplayedThumbnails();
            if (origin.isFilesMatchingAKeyword()) {
                update(evt);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            selectedKeywords.clear();
            if (isKeywordSelected()) {
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
        if (!isKeywordSelected()) {
            return;
        }
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

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        if (isKeywordSelected()
                && evt.getXmp().containsOneOf(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, selectedKeywords)) {
            update(null);
        }
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        if (isKeywordSelected()
                && evt.getXmp().containsOneOf(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, selectedKeywords)) {
            update(null);
        }
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        if (isKeywordSelected()
                && (Xmp.valueDeleted(evt.getOldXmp(), evt.getUpdatedXmp(), XmpDcSubjectsSubjectMetaDataValue.INSTANCE, selectedKeywords)
                || Xmp.valueInserted(evt.getOldXmp(), evt.getUpdatedXmp(), XmpDcSubjectsSubjectMetaDataValue.INSTANCE, selectedKeywords))) {
            update(null);
        }
    }
}
