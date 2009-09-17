/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.tasks.UserTasks;
import de.elmar_baumann.jpt.view.panels.ProgressBarUserTasks;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JProgressBar;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class RenameXmpMetadata extends Thread
        implements ProgressListener {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
    private final List<String> filenames;
    private final Column column;
    private final String oldValue;
    private final String newValue;
    private final ProgressBarUserTasks progressBarRessource =
            ProgressBarUserTasks.INSTANCE;
    private JProgressBar progressBar;

    public static synchronized void update(
            List<String> filenames,
            Column column,
            String oldValue,
            String newValue) {

        UserTasks.INSTANCE.add(new RenameXmpMetadata(
                filenames, column, oldValue, newValue));
    }

    private RenameXmpMetadata(List<String> filenames, Column column,
            String oldValue, String newValue) {
        this.filenames = new ArrayList<String>(filenames);
        this.column = column;
        this.oldValue = oldValue;
        this.newValue = newValue;
        setName("Renaming XMP metadata " + column + " @ " + // NOI18N
                getClass().getName());
    }

    @Override
    public void run() {
        logRename(column.getName(), oldValue, newValue);
        db.renameXmpMetadata(filenames, column, oldValue, newValue, this);
    }

    private void logRename(String columnName, String oldValue, String newValue) {
        AppLog.logInfo(RenameXmpMetadata.class,
                "RenameXmpMetadata.Info.StartRename", // NOI18N
                columnName, oldValue, newValue);
    }

    @Override
    public void progressStarted(ProgressEvent evt) {
        progressBar = progressBarRessource.getResource(this);
        if (progressBar == null) {
            AppLog.logInfo(getClass(), "ProgressBar.Locked", getClass(), // NOI18N
                    progressBarRessource.getOwner());
        } else {
            progressBar.setMinimum(0);
            progressBar.setMaximum(evt.getMaximum());
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
            progressBar.setString(
                    Bundle.getString("RenameXmpMetadata.ProgressBar.String")); // NOI18N
        }
    }

    @Override
    public void progressPerformed(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(evt.getValue());
        }
    }

    @Override
    public void progressEnded(ProgressEvent evt) {
        if (progressBar != null) {
            progressBar.setValue(0);
            progressBar.setString(""); // NOI18N
            progressBar = null;
            progressBarRessource.releaseResource(this);
        }
    }
}
