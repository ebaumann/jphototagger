package org.jphototagger.program.module.thumbnails;

import javax.swing.DefaultComboBoxModel;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterDeletedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterInsertedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterUpdatedEvent;
import org.jphototagger.domain.repository.event.userdefinedfiletypes.UserDefinedFileTypeDeletedEvent;
import org.jphototagger.domain.repository.event.userdefinedfiletypes.UserDefinedFileTypeInsertedEvent;
import org.jphototagger.domain.repository.event.userdefinedfiletypes.UserDefinedFileTypeUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.program.filefilter.AppFileFilters;

/**
 * @author Elmar Baumann
 */
public final class FileFiltersComboBoxModel extends DefaultComboBoxModel {

    private static final long serialVersionUID = 1L;
    static final String PERSISTED_SELECTED_ITEM_KEY = "ComboBoxModelFileFilters.SelIndex";
    private RegexFileFilter allAcceptedFileImageFilesFilter;
    private RegexFileFilter userDefinedFileTypesFilter;
    private final UserDefinedFileFiltersRepository udffRepo = Lookup.getDefault().lookup(UserDefinedFileFiltersRepository.class);

    public FileFiltersComboBoxModel() {
        insertElements();
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private void insertElements() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);

        if (repo == null || !repo.isInit()) {
            return;
        }

        allAcceptedFileImageFilesFilter = AppFileFilters.INSTANCE.getAllAcceptedImageFilesFilter();

        addElement(allAcceptedFileImageFilesFilter);
        addElement(AppFileFilters.INSTANCE.getAcceptedJpegFilesFilter());
        addElement(AppFileFilters.INSTANCE.getAcceptedTiffFilesFilter());
        addElement(AppFileFilters.INSTANCE.getAcceptedRawFilesFilter());
        addElement(AppFileFilters.INSTANCE.getAcceptedDngFilesFilter());

        userDefinedFileTypesFilter = AppFileFilters.INSTANCE.getUserDefinedFileTypesFilter();

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
    }

    void selectPersistedItem() {
        Preferences references = Lookup.getDefault().lookup(Preferences.class);

        if (references.containsKey(PERSISTED_SELECTED_ITEM_KEY)) {
            int index = references.getInt(PERSISTED_SELECTED_ITEM_KEY);

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
    public void userDefinedFileFilterInserted(final UserDefinedFileFilterInsertedEvent evt) {
        insertFilter(evt.getFilter());
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterDeletedEvent.class)
    public void userDefinedFileFilterDeleted(final UserDefinedFileFilterDeletedEvent evt) {
        deleteFilter(evt.getFilter());
    }

    @EventSubscriber(eventClass = UserDefinedFileFilterUpdatedEvent.class)
    public void userDefinedFileFilterUpdated(final UserDefinedFileFilterUpdatedEvent evt) {
        updateFilter(evt.getFilter());
    }

    @EventSubscriber(eventClass = UserDefinedFileTypeInsertedEvent.class)
    public void userDefinedFileTypeUpdated(UserDefinedFileTypeInsertedEvent evt) {
        replaceUserDefinedFileTypesFilter();
    }

    @EventSubscriber(eventClass = UserDefinedFileTypeUpdatedEvent.class)
    public void userDefinedFileTypeUpdated(UserDefinedFileTypeUpdatedEvent evt) {
        replaceUserDefinedFileTypesFilter();
    }

    @EventSubscriber(eventClass = UserDefinedFileTypeDeletedEvent.class)
    public void userDefinedFileTypeUpdated(UserDefinedFileTypeDeletedEvent evt) {
        replaceUserDefinedFileTypesFilter();
    }

    private void replaceUserDefinedFileTypesFilter() {
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                int index = userDefinedFileTypesFilter == null ? -1 : getIndexOf(userDefinedFileTypesFilter);
                RegexFileFilter newUserDefinedFileTypesFilter = AppFileFilters.INSTANCE.createUserDefinedFileFilter();

                if (index >= 0) {
                    if (newUserDefinedFileTypesFilter == null) {
                        removeElementAt(index);
                    } else {
                        userDefinedFileTypesFilter.set(newUserDefinedFileTypesFilter);
                    }
                    fireContentsChanged(this, index, index);
                } else {
                    if (newUserDefinedFileTypesFilter != null) {
                        userDefinedFileTypesFilter = newUserDefinedFileTypesFilter;
                        addElement(userDefinedFileTypesFilter);
                    }
                }

                index = getIndexOf(allAcceptedFileImageFilesFilter);
                allAcceptedFileImageFilesFilter.set(AppFileFilters.INSTANCE.createAllAcceptedImagesFileFilter());
                fireContentsChanged(this, index, index);
            }
        });
    }
}
