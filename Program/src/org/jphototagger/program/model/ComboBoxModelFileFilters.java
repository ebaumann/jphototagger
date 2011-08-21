package org.jphototagger.program.model;

import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilters;
import org.jphototagger.program.UserSettings;
import java.io.FileFilter;
import javax.swing.DefaultComboBoxModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterDeletedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterInsertedEvent;
import org.jphototagger.domain.repository.event.userdefinedfilefilters.UserDefinedFileFilterUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelFileFilters extends DefaultComboBoxModel {

    private static final long serialVersionUID = -7792330718447905417L;
    public static final String SETTINGS_KEY_SEL_INDEX = "ComboBoxModelFileFilters.SelIndex";

    public ComboBoxModelFileFilters() {
        insertElements();
        AnnotationProcessor.process(this);
    }

    private void insertElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
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

        for (UserDefinedFileFilter filter : DatabaseUserDefinedFileFilters.INSTANCE.getAll()) {
            addElement(filter);
        }

        selectItem();
    }

    private void selectItem() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        if (settings.containsKey(SETTINGS_KEY_SEL_INDEX)) {
            int index = settings.getInt(SETTINGS_KEY_SEL_INDEX);

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
