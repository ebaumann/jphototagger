/*
 * @(#)ComboBoxModelFileFilters.java    Created on 2010-03-29
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseUserDefinedFileFilters;
import org.jphototagger.program.event.listener
    .DatabaseUserDefinedFileFiltersListener;
import org.jphototagger.program.UserSettings;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;

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

    @Override
    public void filterInserted(final UserDefinedFileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                addElement(filter);
            }
        });
    }

    @Override
    public void filterDeleted(final UserDefinedFileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                removeElement(filter);
            }
        });
    }

    @Override
    public void filterUpdated(final UserDefinedFileFilter filter) {
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        final Object src = this;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int index = getIndexOf(filter);

                if (index >= 0) {
                    ((UserDefinedFileFilter) getElementAt(index)).set(filter);
                    fireContentsChanged(src, index, index);
                }
            }
        });
    }
}
