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

import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Exif;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.image.metadata.exif.ExifMetadata;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.tasks.UserTasks;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Refreshes the EXIF metadata of all known imagesfiles whithout time stamp
 * check.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-02
 */
public final class RefreshExifInDbOfKnownFiles
        extends HelperThread implements ActionListener {

    private volatile boolean stop;

    public RefreshExifInDbOfKnownFiles() {
        setName("Refreshing EXIF in the database of known files @ " + getClass().getSimpleName());
        setInfo(Bundle.getString("RefreshExifInDbOfKnownFiles.Info"));
    }

    @Override
    public void run() {

        DatabaseImageFiles db        = DatabaseImageFiles.INSTANCE;
        List<String>       filenames = db.getAllImageFiles();
        int                fileCount = filenames.size();

        progressStarted(0, 0, fileCount, fileCount > 0 ? filenames.get(0) : null);

        for (int i = 0; !stop && i < fileCount; i++) {

            File imageFile = new File(filenames.get(i));
            Exif exif      = ExifMetadata.getExif(imageFile);

            if (exif != null) {
                db.insertOrUpdateExif(imageFile.getAbsolutePath(), exif);
            }
            progressPerformed(i + 1, imageFile);
        }
        progressEnded(null);
    }

    @Override
    protected void stopRequested() {
        stop = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (MessageDisplayer.confirmYesNo(null, "RefreshExifInDbOfKnownFiles.Confirm")) {

            RefreshExifInDbOfKnownFiles thread = new RefreshExifInDbOfKnownFiles();

            UserTasks.INSTANCE.add(thread);
        }
    }
}
