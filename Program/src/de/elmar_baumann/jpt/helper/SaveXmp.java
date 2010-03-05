/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.app.AppLifeCycle;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.tasks.UserTasks;
import de.elmar_baumann.jpt.view.panels.ProgressBar;
import de.elmar_baumann.lib.generics.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.JProgressBar;

/**
 * Writes {@link Xmp} objects to XMP files and inserts or updates them into the
 * database.
 *
 * @author  Elmar Baumann
 * @version 2008-10-05
 */
public final class SaveXmp extends Thread {

    private static final String                        PROGRESSBAR_STRING = JptBundle.INSTANCE.getString("SaveXmp.ProgressBar.String");
    private final        Collection<Pair<String, Xmp>> filenamesXmp;
    private              JProgressBar                  progressBar;

    private SaveXmp(Collection<Pair<String, Xmp>> filenamesXmp) {
        AppLifeCycle.INSTANCE.addSaveObject(this);
        this.filenamesXmp = new ArrayList<Pair<String, Xmp>>(filenamesXmp);
        setName("Saving XMP @ " + getClass().getSimpleName());
    }

    public synchronized static void save(Collection<Pair<String, Xmp>> filenamesXmp) {

        final int fileCount = filenamesXmp.size();

        if (fileCount >= 1) {
            UserTasks.INSTANCE.add(new SaveXmp(filenamesXmp));
        }
    }

    @Override
    public void run() {
        int fileIndex = 0;
        // Ignore isInterrupted() because saving user input has high priority
        for (Pair<String, Xmp> pair : filenamesXmp) {
            String filename        = pair.getFirst();
            Xmp    xmp             = pair.getSecond();
            String sidecarFilename = XmpMetadata.suggestSidecarFilename(filename);

            if (XmpMetadata.writeXmpToSidecarFile(xmp, sidecarFilename)) {
                updateDatabase(filename);
            }
            updateProgressBar(++fileIndex);
        }
        releaseProgressBar();
        AppLifeCycle.INSTANCE.removeSaveObject(this);
    }

    private void updateDatabase(String filename) {
        InsertImageFilesIntoDatabase updater = new InsertImageFilesIntoDatabase(
                                                        Arrays.asList(filename),
                                                        Insert.XMP);

        updater.run(); // Starting not a separate thread
    }

    private void getProgressBar() {
        if (progressBar != null) return;
        progressBar = ProgressBar.INSTANCE.getResource(this);
    }

    private void updateProgressBar(int value) {
        getProgressBar();
        if (progressBar != null) {
            progressBar.setMinimum(0);
            progressBar.setMaximum(filenamesXmp.size());
            progressBar.setValue(value);
            if (!progressBar.isStringPainted()) {
                progressBar.setStringPainted(true);
            }
            if (!PROGRESSBAR_STRING.equals(progressBar.getString())) {
                progressBar.setString(PROGRESSBAR_STRING);
            }
        }
    }

    private void releaseProgressBar() {
        if (progressBar != null) {
            if (progressBar.isStringPainted()) {
                progressBar.setString("");
            }
            progressBar.setValue(0);
        }
        ProgressBar.INSTANCE.releaseResource(this);
    }
}
