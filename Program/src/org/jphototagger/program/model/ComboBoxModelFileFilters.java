package org.jphototagger.program.model;

import java.io.FileFilter;

import javax.swing.DefaultComboBoxModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.core.Storage;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterDeletedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterInsertedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.app.AppFileFilters;
import org.openide.util.Lookup;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelFileFilters extends DefaultComboBoxModel {

    private static final long serialVersionUID = -7792330718447905417L;
    public static final String SETTINGS_KEY_SEL_INDEX = "ComboBoxModelFileFilters.SelIndex";
    private final UserDefinedFileFiltersRepository udffRepo = Lookup.getDefault().lookup(UserDefinedFileFiltersRepository.class);

    public ComboBoxModelFileFilters() {
        insertElements();
        AnnotationProcessor.process(this);
    }

    private void insertElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo == null || !repo.isInit()) {
            return;
        }

        addElement(AppFileFilters.INSTANCE.getAllAcceptedImageFilesFilter());
        addElement(AppFileFilters.INSTANCE.getAcceptedJpegFilesFilter());
        addElement(AppFileFilters.INSTANCE.getAcceptedTiffFilesFilter());
        addElement(AppFileFilters.INSTANCE.getAcceptedRawFilesFilter());
        addElement(AppFileFilters.INSTANCE.getAcceptedDngFilesFilter());

        FileFilter userDefinedFileTypesFilter = AppFileFilters.INSTANCE.getUserDefinedFileTypesFilter();

        if (userDefinedFileTypesFilter != null) {
            addElement(userDefinedFileTypesFilter);
        }

        addElement(AppFileFilters.NO_XMP);
        addElement(AppFileFilters.XMP_RATING_1_STAR);
        addElement(AppFileFilters.XMP_RATING_2_STARS);
        addElement(AppFileFilters.XMP_RATING_3_STARS);
        addElement(AppFileFilters.XMP_RATING_4_STARS);
        addElement(AppFileFilters.XMP_RATING_5_STARS);

        for (UserDefinedFileFilter filter : udffRepo.findAllUserDefinedFileFilters()) {
            addElement(filter);
        }

        selectItem();
    }

    private void selectItem() {
        Storage storage = Lookup.getDefault().lookup(Storage.class);

        if (storage.containsKey(SETTINGS_KEY_SEL_INDEX)) {
            int index = storage.getInt(SETTINGS_KEY_SEL_INDEX);

            if ((index >= 0) && (index < getSize())) {
                setSelectedItem(getElementAt(index));
            }
        }
    }

    private void updateFilter(UserDefinedFileFilter filter) {
        int index = getIndexOf(filter);

        if (index >= 0) {
            ((UserDefinedFileFilter) getElementAt(index)).set(filter);
            fireContentsChanged(this, index, index);
        }
    }

    private void deleteFilter(UserDefinedFileFilter filter) {
        removeElement(filter);
    }

    private void insertFilter(UserDefinedFileFilter filter) {
        addElement(filter);
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterInsertedEvent.class)
    public void filterInserted(final UserDefinedFileFilterInsertedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                insertFilter(evt.getFilter());
            }
        });
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterDeletedEvent.class)
    public void filterDeleted(final UserDefinedFileFilterDeletedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                deleteFilter(evt.getFilter());
            }
        });
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterUpdatedEvent.class)
    public synchronized void filterUpdated(final UserDefinedFileFilterUpdatedEvent evt) {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                updateFilter(evt.getFilter());
            }
        });
    }
}
