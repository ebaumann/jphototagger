package org.jphototagger.program.module.editmetadata;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.applifecycle.AppWillExitEvent;
import org.jphototagger.api.concurrent.Cancelable;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.domain.DomainPreferencesKeys;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.event.EditMetadataPanelsEditDisabledEvent;
import org.jphototagger.domain.metadata.event.EditMetadataPanelsEditEnabledEvent;
import org.jphototagger.domain.metadata.xmp.FileXmp;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.domain.templates.MetadataTemplate;
import org.jphototagger.domain.text.TextEntry;
import org.jphototagger.domain.thumbnails.event.ThumbnailsSelectionChangedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.ExpandCollapseComponentPanel;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.program.misc.SaveXmp;
import org.jphototagger.program.module.keywords.tree.SuggestKeywords;
import org.jphototagger.program.module.wordsets.WordsetPreferences;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.program.view.ViewUtil;
import org.jphototagger.xmp.EditHints;
import org.jphototagger.xmp.EditableMetaDataValues;
import org.jphototagger.xmp.XmpMetadata;

/**
 * @author Elmar Baumann, Tobias Stening
 */
final class EditMetaDataPanels implements FocusListener {

    private static final Logger LOGGER = Logger.getLogger(EditMetaDataPanels.class.getName());
    private final List<TextEntry> textEntries = new ArrayList<TextEntry>();
    private final List<FileXmp> filesXmp = new CopyOnWriteArrayList<FileXmp>();
    private final Set<MetaDataValue> repeatableMetaDataValuesOfTextEntries = new HashSet<MetaDataValue>();
    private final Set<MetaDataValue> notRepeatableMetaDataValuesOfTextEntries = new HashSet<MetaDataValue>();
    private volatile boolean editable = true;
    private final WatchDifferentValues watchDifferentValues = new WatchDifferentValues();
    private SetFilesThread currentSetFilesThread;
    private final JComponent parentContainer;
    private final Object monitor = new Object();
    private final EditMetaDataActionsPanel editMetadataActionsPanel = new EditMetaDataActionsPanel(this);
    private final XmpSidecarFileResolver xmpSidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private EditRepeatableTextEntryPanel keywordsPanel;
    private Component lastFocussedEditControl;

    EditMetaDataPanels(JComponent parentContainer) {
        if (parentContainer == null) {
            throw new NullPointerException("parentContainer == null");
        }
        this.parentContainer = parentContainer;
        createEditPanels();
        addEditPanelsToParentContainer();
        requestFocusToFirstEditField();
        enableAutocomplete();
        setEditable(false);
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private boolean isDirty() {
        // Access not from EDT considered as ok
        for (TextEntry textEntry : textEntries) {
            if (textEntry.isDirty()) {
                return true;
            }
        }
        return false;
    }

    private void setDirty(final boolean dirty) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                for (TextEntry textEntry : textEntries) {
                    textEntry.setDirty(dirty);
                }
            }
        });
    }

    private void saveIfDirty() {
        if (isDirty()) {
            save();
        }
    }

    private void save() {
        synchronized (monitor) {
            SaveXmp.save(filesXmp);
        }
        setDirty(false);
    }

    private void setEditable(final boolean editable) {
        synchronized (monitor) {
            this.editable = editable;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                for (TextEntry textEntry : textEntries) {
                    textEntry.setEditable(editable);
                }

                EventBus.publish(editable
                        ? new EditMetadataPanelsEditEnabledEvent(this)
                        : new EditMetadataPanelsEditDisabledEvent(this));
            }
        });
    }

    boolean isEditable() {
        synchronized (monitor) {
            return editable;
        }
    }

    @EventSubscriber(eventClass = ThumbnailsSelectionChangedEvent.class)
    public void thumbnailsSelectionChanged(final ThumbnailsSelectionChangedEvent evt) {
        List<File> selFiles = evt.getSelectedFiles();
        if (evt.isAFileSelected()) {
            boolean canEdit = canEdit(selFiles);
            setEditable(canEdit);
            setFiles(selFiles);
        } else {
            clear();
            setEditable(false);
        }
    }

    boolean canEdit(List<File> selectedFiles) {
        for (File selFile : selectedFiles) {
            if (!XmpMetadata.canWriteSidecarFileForImageFile(selFile)) {
                return false;
            }
        }

        return selectedFiles.size() > 0;
    }

    private void setFiles(final Collection<File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        synchronized (monitor) {
            setEditable(false);
            parentContainer.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            saveIfDirty();
            removeXmpAsListenerFromAllTextEntries(getXmpOfFilesXmp(filesXmp));
            emptyAllEditPanels();
            watchDifferentValues.setListen(false);
            watchDifferentValues.setEntries(new ArrayList<TextEntry>());
            if (currentSetFilesThread != null) {
                currentSetFilesThread.cancel();
            }
            currentSetFilesThread = new SetFilesThread(files);
            currentSetFilesThread.start();
        }
    }

    private List<Xmp> getXmpOfFilesXmp(Collection<? extends FileXmp> filesXmp) {
        List<Xmp> xmps = new ArrayList<Xmp>(filesXmp.size());
        for (FileXmp fileXmp : filesXmp) {
            Xmp xmp = fileXmp.getXmp();

            xmps.add(xmp);
        }
        return xmps;
    }

    private class SetFilesThread extends Thread implements Cancelable {

        private volatile boolean cancelled;
        private final Collection<File> threadFiles;
        private final List<FileXmp> threadFilesXmp;

        private SetFilesThread(Collection<File> files) {
            super("JPhotoTagger: Set File's XMP to Edit Panel");
            threadFiles = new ArrayList<File>(files);
            threadFilesXmp = new ArrayList<FileXmp>(files.size());
            LOGGER.log(Level.INFO, "Setting XMP to edit panel from that files: {0}", files);
        }

        @Override
        public void run() {
            setFilesXmp();
            if (!cancelled) {
                synchronized (monitor) {
                    List<Xmp> xmps = getXmpOfFilesXmp(threadFilesXmp);
                    setXmpToEditPanels(xmps);
                    addXmpAsListenerToAllTextEntries(xmps);
                    filesXmp.clear();
                    filesXmp.addAll(threadFilesXmp);
                    setDirty(false);
                    setEditable(!threadFiles.isEmpty());
                    parentContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
            synchronized (monitor) {
                if (currentSetFilesThread == this) {
                    currentSetFilesThread = null;
                }
            }
        }

        // Profiling detected that the reading of the XMP files is the most time consuming task
        private void setFilesXmp() {
            for (File file : threadFiles) {
                if (cancelled) {
                    return;
                }
                Xmp xmp = null;
                if (xmpSidecarFileResolver.hasXmpSidecarFile(file)) {
                    try {
                        xmp = XmpMetadata.getXmpFromSidecarFileOf(file);
                    } catch (Throwable t) {
                        LOGGER.log(Level.SEVERE, null, t);
                    }
                }
                if (xmp == null) {
                    xmp = new Xmp();
                }
                FileXmp fileXmp = new FileXmp(file, xmp);
                threadFilesXmp.add(fileXmp);
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
            LOGGER.log(Level.INFO, "Cancelled setting XMP to edit panel from that files: {0}", threadFiles);
        }
    }

    private void setXmpToEditPanels(final List<? extends Xmp> xmps) {
        if (xmps.isEmpty()) {
            return;
        }
        final Map<MetaDataValue, Collection<String>> commonXmpOfRepeatableMetaDataValues = getCommonXmpOfRepeatableMetaDataValues(xmps);
        final Map<MetaDataValue, String> commonXmpOfNotRepeatableMetaDataValues = getCommonXmpOfNotRepeatableMetaDataValues(xmps);

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                List<TextEntry> textEntriesWithDifferentValues = new ArrayList<TextEntry>();
                boolean containsMultipleFilesForEdit = xmps.size() > 1;
                for (TextEntry textEntry : textEntries) {
                    MetaDataValue metaDataValue = textEntry.getMetaDataValue();
                    if (textEntry instanceof EditRepeatableTextEntryPanel) {
                        EditRepeatableTextEntryPanel panel = (EditRepeatableTextEntryPanel) textEntry;
                        Collection<String> commonXmp = commonXmpOfRepeatableMetaDataValues.get(metaDataValue);
                        panel.setTexts(commonXmp);
                    } else {
                        String commonText = commonXmpOfNotRepeatableMetaDataValues.get(metaDataValue);
                        textEntry.setText(commonText);
                        if (containsMultipleFilesForEdit && commonText.isEmpty() && oneOfXmpsContainsMetaDataValue(xmps, metaDataValue)) {
                            textEntriesWithDifferentValues.add(textEntry);
                        }
                    }
                    textEntry.setDirty(false);
                }
                if (containsMultipleFilesForEdit && !textEntriesWithDifferentValues.isEmpty()) {
                    watchDifferentValues.setEntries(textEntriesWithDifferentValues);
                    watchDifferentValues.setListen(true);
                }
            }
        });
    }

    private Map<MetaDataValue, Collection<String>> getCommonXmpOfRepeatableMetaDataValues(List<? extends Xmp> xmps) {
        Map<MetaDataValue, Collection<String>> commonXmp = new HashMap<MetaDataValue, Collection<String>>();
        for (MetaDataValue metaDataValue : repeatableMetaDataValuesOfTextEntries) {
            Collection<String> commonXmpAsStrings = getCommonXmpValuesAsStrings(xmps, metaDataValue);
            commonXmp.put(metaDataValue, commonXmpAsStrings);
        }
        return commonXmp;
    }

    private Map<MetaDataValue, String> getCommonXmpOfNotRepeatableMetaDataValues(List<? extends Xmp> xmps) {
        Map<MetaDataValue, String> commonXmp = new HashMap<MetaDataValue, String>();
        for (MetaDataValue metaDataValue : notRepeatableMetaDataValuesOfTextEntries) {
            String commonXmpAsString = getCommonXmpString(xmps, metaDataValue);
            commonXmp.put(metaDataValue, commonXmpAsString);
        }
        return commonXmp;
    }

    @SuppressWarnings("unchecked")
    private Collection<String> getCommonXmpValuesAsStrings(List<? extends Xmp> xmps, MetaDataValue metaDataValue) {
        if (xmps.size() == 1) {
            Xmp xmp = xmps.get(0);
            Object currentXmpValue = xmp.getValue(metaDataValue);
            if (currentXmpValue instanceof List<?>) {
                return (List<String>) currentXmpValue;
            } else {
                return new ArrayList<String>(1);
            }
        }
        // more then 1 file
        Stack<List<String>> lists = new Stack<List<String>>();
        for (Xmp xmp : xmps) {
            Object xmpValue = xmp.getValue(metaDataValue);
            if (xmpValue instanceof List<?>) {
                lists.push((List<String>) xmpValue);
            }
        }
        if (lists.size() != xmps.size()) {
            // 1 ore more files without metadata
            return new ArrayList<String>(1);
        }
        List<String> coll = lists.pop();
        while (!lists.isEmpty() && (coll.size() > 0)) {
            coll.retainAll(lists.pop());
        }
        return coll;
    }

    private String getCommonXmpString(List<? extends Xmp> xmps, MetaDataValue metaDataValue) {
        if (xmps.size() == 1) {
            Xmp xmp = xmps.get(0);
            Object currentXmpValue = xmp.getValue(metaDataValue);
            String currentXmpValueAsString = valueToString(currentXmpValue);
            return (currentXmpValueAsString == null)
                    ? ""
                    : currentXmpValueAsString.trim();
        }
        // more then 1 file
        Stack<String> strings = new Stack<String>();
        for (Xmp xmp : xmps) {
            Object xmpValue = xmp.getValue(metaDataValue);
            String xmpValueAsString = valueToString(xmpValue);
            if (xmpValueAsString != null) {
                strings.push(xmpValueAsString.trim());
            }
        }
        if (strings.size() != xmps.size()) {
            return "";
        }
        String string = strings.pop();
        while (!strings.empty()) {
            if (!strings.pop().equalsIgnoreCase(string)) {
                return "";
            }
        }
        return string;
    }

    private boolean oneOfXmpsContainsMetaDataValue(Collection<? extends Xmp> xmps, MetaDataValue metaDataValue) {
        for (Xmp xmp : xmps) {
            Object xmpValue = xmp.getValue(metaDataValue);
            String xmpValueAsString = valueToString(xmpValue);
            if (StringUtil.hasContent(xmpValueAsString)) {
                return true;
            }
        }
        return false;
    }

    private String valueToString(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Long) {
            return Long.toOctalString((Long) value);
        } else {
            throw new IllegalArgumentException("No string conversion implemented for " + value + " Class " + value.getClass());
        }
    }

    private void addXmpAsListenerToAllTextEntries(Collection<? extends Xmp> xmps) {
        for (Xmp xmp : xmps) {
            addXmpAsListenerToAllTextEntries(xmp);
        }
    }

    private void addXmpAsListenerToAllTextEntries(final Xmp xmp) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                for (TextEntry textEntry : textEntries) {
                    textEntry.addTextEntryListener(xmp);
                }
            }
        });
    }

    private void removeXmpAsListenerFromAllTextEntries(Collection<? extends Xmp> xmps) {
        for (Xmp xmp : xmps) {
            removeXmpAsListenerFromAllTextEntries(xmp);
        }
    }

    private void removeXmpAsListenerFromAllTextEntries(final Xmp xmp) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                for (TextEntry textEntry : textEntries) {
                    textEntry.removeTextEntryListener(xmp);
                }
            }
        });
    }

    JPanel getEditPanelForMetaDataValue(MetaDataValue metaDataValue) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }
        for (TextEntry textEntry : textEntries) {
            if (!(textEntry instanceof JPanel)) {
                throw new IllegalStateException("Unexpected type of TextEntry: " + textEntry.getClass());
            }
            MetaDataValue textEntryMetaDataValue = textEntry.getMetaDataValue();
            if (textEntryMetaDataValue.equals(metaDataValue)) {
                return (JPanel) textEntry;
            }
        }
        return null;
    }

    void setOrAddText(final MetaDataValue metaDataValue, final String text) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }
        if (text == null) {
            throw new NullPointerException("text == null");
        }
        synchronized (monitor) {
            if (!editable) {
                return;
            }
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                TextEntry textEntryForAdd = null;
                int size = textEntries.size();
                for (int i = 0; textEntryForAdd == null && i < size; i++) {
                    TextEntry textEntry = textEntries.get(i);
                    MetaDataValue textEntryMetaDataValue = textEntry.getMetaDataValue();

                    if (textEntryMetaDataValue.equals(metaDataValue)) {
                        textEntryForAdd = textEntry;
                    }
                }
                if (textEntryForAdd instanceof EditRepeatableTextEntryPanel) {
                    EditRepeatableTextEntryPanel editRepeatableTextEntryPanel = (EditRepeatableTextEntryPanel) textEntryForAdd;
                    editRepeatableTextEntryPanel.addText(text);
                } else if (textEntryForAdd instanceof TextEntry) {
                    textEntryForAdd.setText(text);
                    textEntryForAdd.setDirty(true);
                }
                saveIfDirtyAndInputIsSaveEarly();
            }
        });
    }

    void removeText(final MetaDataValue metaDataValue, final String text) {
        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }
        if (text == null) {
            throw new NullPointerException("text == null");
        }
        synchronized (monitor) {
            if (!editable) {
                return;
            }
        }
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                TextEntry textEntryForRemove = null;
                int size = textEntries.size();
                for (int i = 0; textEntryForRemove == null && i < size; i++) {
                    TextEntry textEntry = textEntries.get(i);
                    MetaDataValue textEntryMetaDataValue = textEntry.getMetaDataValue();
                    if (textEntryMetaDataValue.equals(metaDataValue)) {
                        textEntryForRemove = textEntry;
                    }
                }
                if (textEntryForRemove instanceof EditRepeatableTextEntryPanel) {
                    EditRepeatableTextEntryPanel editRepeatableTextEntryPanel = (EditRepeatableTextEntryPanel) textEntryForRemove;
                    editRepeatableTextEntryPanel.removeText(text);
                } else if (textEntryForRemove instanceof TextEntry) {
                    textEntryForRemove.setText("");
                    textEntryForRemove.setDirty(true);
                }
                saveIfDirtyAndInputIsSaveEarly();
            }
        });
    }

    Xmp createXmpFromInput() {
        if (!EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Not called in EDT");
        }
        Xmp xmp = new Xmp();
        for (TextEntry textEntry : textEntries) {
            if (textEntry instanceof EditTextEntryPanel) {
                EditTextEntryPanel panel = (EditTextEntryPanel) textEntry;
                MetaDataValue metaDataValue = panel.getMetaDataValue();
                String text = panel.getText();
                xmp.setValue(metaDataValue, text);
            } else if (textEntry instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel panel = (EditRepeatableTextEntryPanel) textEntry;
                MetaDataValue metaDataValue = panel.getMetaDataValue();
                String text = panel.getText();
                xmp.setValue(metaDataValue, text);

                for (String repetableText : panel.getRepeatableText()) {
                    xmp.setValue(metaDataValue, repetableText);
                }
            } else if (textEntry instanceof RatingSelectionPanel) {
                RatingSelectionPanel panel = (RatingSelectionPanel) textEntry;

                try {
                    // Will be reached only once (only one panel of this type is in the parent container),
                    // so try catch within a loop is ok
                    String text = panel.getText();
                    if (text != null && !text.isEmpty()) {
                        Long rating = Long.getLong(text);
                        xmp.setValue(XmpRatingMetaDataValue.INSTANCE, rating);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(EditMetaDataPanels.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return xmp;
    }

    void setXmp(final Xmp xmp) {
        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }
        if (!editable) {
            return;
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                for (TextEntry textEntry : textEntries) {
                    if (textEntry instanceof EditTextEntryPanel) {
                        EditTextEntryPanel panel = (EditTextEntryPanel) textEntry;
                        MetaDataValue metaDataValue = panel.getMetaDataValue();
                        Object xmpValue = xmp.getValue(metaDataValue);

                        if (xmpValue != null) {
                            String text = xmpValue.toString();
                            panel.setText(text);
                            panel.setDirty(true);
                        }
                    } else if (textEntry instanceof EditRepeatableTextEntryPanel) {
                        EditRepeatableTextEntryPanel panel = (EditRepeatableTextEntryPanel) textEntry;
                        MetaDataValue metaDataValue = panel.getMetaDataValue();
                        Object xmpValue = xmp.getValue(metaDataValue);

                        if (xmpValue instanceof Collection<?>) {
                            Collection<?> collection = (Collection<?>) xmpValue;

                            for (Object o : collection) {
                                panel.addText(o.toString());
                            }
                        }
                    } else if (textEntry instanceof RatingSelectionPanel) {
                        RatingSelectionPanel panel = (RatingSelectionPanel) textEntry;
                        Long rating = xmp.contains(XmpRatingMetaDataValue.INSTANCE)
                                ? (Long) xmp.getValue(XmpRatingMetaDataValue.INSTANCE)
                                : null;

                        if (rating != null) {
                            String ratingAsString = Long.toString(rating);
                            panel.setText(ratingAsString);
                            panel.setDirty(true);
                        }
                    }
                }

                saveIfDirtyAndInputIsSaveEarly();
            }
        });
    }

    void setRating(final Long rating) {
        if (rating == null) {
            throw new NullPointerException("rating == null");
        }
        if (!editable) {
            return;
        }
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                TextEntry textEntryForSet = null;
                int size = textEntries.size();

                for (int i = 0; textEntryForSet == null && i < size; i++) {
                    TextEntry textEntry = textEntries.get(i);
                    MetaDataValue metaDataValue = textEntry.getMetaDataValue();

                    if (metaDataValue.equals(XmpRatingMetaDataValue.INSTANCE)) {
                        textEntryForSet = textEntry;
                    }
                }

                if (textEntryForSet instanceof RatingSelectionPanel) {
                    RatingSelectionPanel ratingPanel = (RatingSelectionPanel) textEntryForSet;
                    String ratingAsString = Long.toString(rating);

                    ratingPanel.setTextAndNotify(ratingAsString);
                }

                saveIfDirtyAndInputIsSaveEarly();
            }
        });
    }

    void setMetadataTemplate(final MetadataTemplate template) {
        if (template == null) {
            throw new NullPointerException("template == null");
        }
        synchronized (monitor) {
            if (!editable) {
                return;
            }
        }

        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                for (TextEntry textEntry : textEntries) {
                    MetaDataValue metaDataValue = textEntry.getMetaDataValue();
                    Object templateValue = template.getMetaDataValue(metaDataValue);
                    if (templateValue instanceof String) {
                        String string = (String) templateValue;
                        if (!string.isEmpty()) {
                            textEntry.setText(string);
                            textEntry.setDirty(true);
                        }
                    } else if (templateValue instanceof Collection<?>) {
                        @SuppressWarnings("unchecked") Collection<String> strings = (Collection<String>) templateValue;
                        EditRepeatableTextEntryPanel repeatableTextEntry = (EditRepeatableTextEntryPanel) textEntry;
                        repeatableTextEntry.setTexts(strings);
                        repeatableTextEntry.setDirty(true);
                    }
                }
            }
        });
    }

    MetadataTemplate createMetadataTemplateFromInput() {
        if (!EventQueue.isDispatchThread()) {
            throw new IllegalStateException("Not called in EDT");
        }
        MetadataTemplate template = new MetadataTemplate();
        for (TextEntry textEntry : textEntries) {
            if (textEntry instanceof EditRepeatableTextEntryPanel) {
                EditRepeatableTextEntryPanel repeatableEntry = (EditRepeatableTextEntryPanel) textEntry;
                MetaDataValue metaDataValue = textEntry.getMetaDataValue();
                Collection<String> repeatableText = repeatableEntry.getRepeatableText();
                template.setMetaDataValue(metaDataValue, repeatableText);
            } else {
                String text = textEntry.getText();
                if (text != null && !text.trim().isEmpty()) {
                    MetaDataValue metaDataValue = textEntry.getMetaDataValue();
                    template.setMetaDataValue(metaDataValue, text.trim());
                }
            }
        }
        return template;
    }

    private void addEditPanelsToParentContainer() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                parentContainer.removeAll();
                parentContainer.setLayout(new GridBagLayout());
                List<Component> excludeFromAutoMnemonicComponents = new LinkedList<Component>();
                int size = textEntries.size();
                for (int i = 0; i < size; i++) {
                    GridBagConstraints constraints = createGridBagConstraints();
                    if (i == size - 1) {
                        constraints.insets.bottom += 10;
                    }
                    TextEntry textEntry = textEntries.get(i);
                    excludeFromAutoMnemonicComponents.addAll(textEntry.getExcludeFromAutoMnemonicComponents());
                    ExpandCollapseComponentPanel panel = new ExpandCollapseComponentPanel((Component) textEntry);
                    parentContainer.add(panel, constraints);
                    panel.readExpandedState();
                }
                setMnemonics(excludeFromAutoMnemonicComponents);
                addActionPanel();    // After setMnemonics()!
            }
        });
    }

    private void setMnemonics(final Collection<? extends Component> excludeFromAutoMnemonicComponents) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                List<Character> mnemonicChars = editMetadataActionsPanel.getButtonsMnemonicChars();
                ViewUtil.setDisplayedMnemonicsToLabels(parentContainer,
                        excludeFromAutoMnemonicComponents,
                        mnemonicChars.toArray(new Character[]{}));
            }
        });
    }

    private GridBagConstraints createGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.weightx = 1;
        return gbc;
    }

    private void addActionPanel() {
        GridBagConstraints gbc = createGridBagConstraints();
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        parentContainer.add(editMetadataActionsPanel, gbc);
        editMetadataActionsPanel.tabbedPane.addFocusListener(this);
    }

    private void requestFocusToFirstEditField() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                if (textEntries.size() > 0) {
                    TextEntry textEntry = textEntries.get(0);
                    textEntry.requestFocus();
                    lastFocussedEditControl = (Component) textEntries.get(0);
                }
            }
        });
    }

    void setFocusToLastFocussedEditControl() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                if (lastFocussedEditControl != null) {
                    lastFocussedEditControl.requestFocus();
                } else {
                    requestFocusToFirstEditField();
                }
            }
        });
    }

    private void createEditPanels() {
        if (!textEntries.isEmpty()) {
            throw new IllegalStateException("Panels already created");
        }
        for (MetaDataValue metaDataValue : EditableMetaDataValues.get()) {
            EditHints editHints = EditableMetaDataValues.getEditHints(metaDataValue);
            boolean isRepeatable = editHints.isRepeatable();
            if (isRepeatable) {
                EditRepeatableTextEntryPanel panel = new EditRepeatableTextEntryPanel(metaDataValue);
                panel.textAreaInput.addFocusListener(this);
                if (metaDataValue.equals(XmpDcSubjectsSubjectMetaDataValue.INSTANCE)) {
                    keywordsPanel = panel;
                    panel.setSuggest(new SuggestKeywords());
                    panel.setBundleKeyPosRenameDialog("EditMetadataPanels.Keywords.RenameDialog.Pos");
                    if (WordsetPreferences.isDisplayWordsetsEditPanel()) {
                        panel.addWordsetsPanel();
                    }
                }
                textEntries.add(panel);
                repeatableMetaDataValuesOfTextEntries.add(metaDataValue);
            } else {
                if (metaDataValue.equals(XmpRatingMetaDataValue.INSTANCE)) {
                    RatingSelectionPanel panel = new RatingSelectionPanel(metaDataValue);
                    for (Component component : panel.getInputComponents()) {
                        component.addFocusListener(this);
                    }
                    textEntries.add(panel);
                } else {
                    EditTextEntryPanel panel = new EditTextEntryPanel(metaDataValue);
                    panel.textAreaEdit.addFocusListener(this);
                    textEntries.add(panel);
                }
                notRepeatableMetaDataValuesOfTextEntries.add(metaDataValue);
            }
        }
    }

    private void enableAutocomplete() {
        if (isAutocompleteEnabled()) {
            for (TextEntry textEntry : textEntries) {
                textEntry.enableAutocomplete();
            }
        }
    }

    private boolean isAutocompleteEnabled() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                ? prefs.getBoolean(DomainPreferencesKeys.KEY_ENABLE_AUTOCOMPLETE)
                : true;
    }

    private void clear() {
        synchronized (monitor) {
            setFiles(Collections.<File>emptyList());
        }
    }

    void emptyAllEditPanels() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                for (TextEntry textEntry : textEntries) {
                    textEntry.empty();
                }
            }
        });
    }

    @Override
    public void focusGained(FocusEvent evt) {
        Component sourceComponent = (Component) evt.getSource();
        if (isEditComponent(sourceComponent) && !(sourceComponent instanceof RatingSelectionPanel)) {
            lastFocussedEditControl = sourceComponent;
        }
        scrollToVisible(sourceComponent);
    }

    @Override
    public void focusLost(FocusEvent evt) {
        Component oppositeComponent = evt.getOppositeComponent();
        if (isEditComponent(oppositeComponent)) {
            saveIfDirtyAndInputIsSaveEarly();
        }
    }

    private boolean isEditComponent(Component c) {
        return c instanceof JTextArea || c instanceof JTextField || c instanceof RatingSelectionPanel;
    }

    private void scrollToVisible(final Component component) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                Component parent = getParentNextToContainer(component);
                if (parent != null) {
                    parentContainer.scrollRectToVisible(parent.getBounds());
                }
            }
        });
    }

    private Component getParentNextToContainer(Component component) {
        Component parent = component;
        while (parent != null) {
            Container parentsParent = parent.getParent();
            if (parentsParent == parentContainer) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    @EventSubscriber(eventClass=PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        String key = evt.getKey();
        if (AppPreferencesKeys.KEY_UI_DISPLAY_WORD_SETS_EDIT_PANEL.equals(key) && keywordsPanel != null) {
            boolean  display = (Boolean) evt.getNewValue();
            if (display) {
                keywordsPanel.addWordsetsPanel();
            } else {
                keywordsPanel.removeWordsetsPanel();
            }
        }
    }

    @EventSubscriber(eventClass = AppWillExitEvent.class)
    public void appWillExit(AppWillExitEvent evt) {
        saveIfDirty();
    }

    void saveIfDirtyAndInputIsSaveEarly() {
        if (!editable || !isSaveInputEarly() || !isDirty()) {
            return;
        }
        save();
    }

    private boolean isSaveInputEarly() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        return prefs.containsKey(AppPreferencesKeys.KEY_SAVE_INPUT_EARLY)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_SAVE_INPUT_EARLY)
                : true;
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(XmpInsertedEvent evt) {
        insertValuesOfExternalUpdatedXmp(evt.getImageFile(), evt.getXmp());
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(XmpUpdatedEvent evt) {
        insertValuesOfExternalUpdatedXmp(evt.getImageFile(), evt.getUpdatedXmp());
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(XmpDeletedEvent evt) {
        insertValuesOfExternalUpdatedXmp(evt.getImageFile(), evt.getXmp());
    }

    private void insertValuesOfExternalUpdatedXmp(final File file, final Xmp xmp) {
        synchronized (monitor) {
            if (!isAsSingleFileInEdit(file) || isDirty()) {
                return;
            }
            FileXmp currentFileXmpInEdit = filesXmp.get(0);
            filesXmp.set(0, new FileXmp(file, xmp));
            Xmp currentXmpInEdit = currentFileXmpInEdit.getXmp();
            removeXmpAsListenerFromAllTextEntries(currentXmpInEdit);
            addXmpAsListenerToAllTextEntries(xmp);
            setXmpToEditPanels(Arrays.asList(xmp));
        }
    }

    private boolean isAsSingleFileInEdit(File file) {
        synchronized (monitor) {
            if (!editable || !containsExactlyOneFileForEdit()) {
                return false;
            }
            FileXmp currentFileXmp = filesXmp.get(0);
            File currentFile = currentFileXmp.getFile();
            return file.equals(currentFile);
        }
    }

    private boolean containsExactlyOneFileForEdit() {
        synchronized (monitor) {
            return filesXmp.size() == 1;
        }
    }

    private class WatchDifferentValues extends MouseAdapter {

        private final List<TextEntry> entries = new ArrayList<TextEntry>();
        private final Set<TextEntry> releasedEntries = new HashSet<TextEntry>();
        private volatile boolean listen;
        public synchronized void setListen(boolean listen) {
            if (listen) {
                listenToEntries();
            }
            this.listen = listen;
        }

        private void listenToEntries() {
            for (TextEntry entry : entries) {
                if (entry instanceof RatingSelectionPanel) {
                    // Text not parsable as number leads to an exception
                } else {
                    entry.setText(Bundle.getString(EditMetaDataPanels.class, "EditMetadataPanels.DisableIfMultipleValues.Info.TextEntry"));
                }
                entry.addMouseListenerToInputComponents(this);
                entry.setDirty(false);
                entry.setEditable(false);
            }
        }

        private void releaseAllEntries() {
            for (TextEntry entry : entries) {
                if (!releasedEntries.contains(entry)) {
                    releaseEntry(entry);
                }
            }
        }

        private void releaseEntry(TextEntry entry) {
            entry.removeMouseListenerFromInputComponents(this);
            entry.setEditable(true);
            entry.setText("");
            entry.setDirty(false);
            releasedEntries.add(entry);
        }

        public synchronized void setEntries(Collection<TextEntry> entries) {
            if (entries == null) {
                throw new NullPointerException("entries == null");
            }
            releaseAllEntries();
            this.releasedEntries.clear();
            this.entries.clear();
            this.entries.addAll(entries);
        }

        @Override
        public void mousePressed(MouseEvent evt) {
            synchronized (this) {
                if (!editable || !listen) {
                    return;
                }
                TextEntry entry = getTextEntry(evt.getSource());
                if (enableEdit(entry) && (entry instanceof RatingSelectionPanel)) {
                    ((RatingSelectionPanel) entry).repeatLastClick();
                }
            }
        }

        private TextEntry getTextEntry(Object o) {
            Object obj = o;
            if (obj instanceof TextEntry) {
                return (TextEntry) obj;
            }
            while (obj != null) {
                if (obj instanceof Component) {
                    obj = ((Component) obj).getParent();
                    if (obj instanceof TextEntry) {
                        return (TextEntry) obj;
                    }
                } else {
                    return null;
                }
            }
            return null;
        }

        private boolean enableEdit(TextEntry entry) {
            if (entry == null) {
                throw new NullPointerException("entry == null");
            }
            String message = Bundle.getString(WatchDifferentValues.class, "EditMetadataPanels.DisableIfMultipleValues.Confirm.Edit");
            if (MessageDisplayer.confirmYesNo(null, message)) {
                releaseEntry(entry);
                return true;
            }
            return false;
        }
    }
}
