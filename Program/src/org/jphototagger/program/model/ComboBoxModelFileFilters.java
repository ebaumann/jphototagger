package org.jphototagger.program.model;

import java.awt.EventQueue;
import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilters;
import org.jphototagger.program.event.listener
    .DatabaseUserDefinedFileFiltersListener;
import org.jphototagger.program.UserSettings;

import javax.swing.DefaultComboBoxModel;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ComboBoxModelFileFilters extends DefaultComboBoxModel
        implements DatabaseUserDefinedFileFiltersListener {
    private static final long  serialVersionUID = -7792330718447905417L;
    public static final String SETTINGS_KEY_SEL_INDEX =
        "ComboBoxModelFileFilters.SelIndex";

    public ComboBoxModelFileFilters() {
        insertElements();
        DatabaseUserDefinedFileFilters.INSTANCE.addListener(this);
    }

    private void insertElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        addElement(AppFileFilters.ACCEPTED_IMAGE_FILENAMES);
        addElement(AppFileFilters.JPEG_FILENAMES);
        addElement(AppFileFilters.TIFF_FILENAMES);
        addElement(AppFileFilters.RAW_FILENAMES);
        addElement(AppFileFilters.DNG_FILENAMES);
        addElement(AppFileFilters.NO_XMP);
        addElement(AppFileFilters.XMP_RATING_1_STAR);
        addElement(AppFileFilters.XMP_RATING_2_STARS);
        addElement(AppFileFilters.XMP_RATING_3_STARS);
        addElement(AppFileFilters.XMP_RATING_4_STARS);
        addElement(AppFileFilters.XMP_RATING_5_STARS);

        for (UserDefinedFileFilter filter :
                DatabaseUserDefinedFileFilters.INSTANCE.getAll()) {
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

            @Override
    public void filterInserted(final UserDefinedFileFilter filter) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                insertFilter(filter);
            }
        });
    }

    @Override
    public void filterDeleted(final UserDefinedFileFilter filter) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                deleteFilter(filter);
            }
        });
    }

    @Override
    public void filterUpdated(final UserDefinedFileFilter filter) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateFilter(filter);
                }
        });
    }
}
