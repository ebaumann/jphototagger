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

import de.elmar_baumann.jpt.app.AppLifeCycle;
import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.tasks.UserTasks;
import de.elmar_baumann.jpt.view.panels.ProgressBar;
import de.elmar_baumann.lib.generics.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import javax.swing.JProgressBar;

/**
 * Saves edited metadata.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class SaveEditedMetadata extends Thread {

    private static final String                        PROGRESSBAR_STRING = Bundle.getString("SaveEditedMetadata.ProgressBar.String");
    private final        Collection<Pair<String, Xmp>> filenamesXmp;
    private              JProgressBar                  progressBar;

    public synchronized static void saveMetadata(Collection<Pair<String, Xmp>> filenamesXmp) {

        final int fileCount = filenamesXmp.size();

        if (fileCount >= 1) {
            SaveEditedMetadata updater = new SaveEditedMetadata(filenamesXmp);
            UserTasks.INSTANCE.add(updater);
        } else {
            AppLog.logWarning(SaveEditedMetadata.class, "SaveEditedMetadata.Error.NoImageFilesSelected");
        }
    }

    private SaveEditedMetadata(Collection<Pair<String, Xmp>> filenamesXmp) {

        AppLifeCycle.INSTANCE.addSaveObject(this);
        this.filenamesXmp = new ArrayList<Pair<String, Xmp>>(filenamesXmp);
        setName("Saving edited metadata @ " + getClass().getSimpleName());
    }

    @Override
    public void run() {
        // Ignore isInterrupted() because saving user input has high priority
        int fileIndex = 0;
        for (Pair<String, Xmp> pair : filenamesXmp) {
            String filename        = pair.getFirst();
            Xmp    xmp             = pair.getSecond();
            String sidecarFilename = XmpMetadata.suggestSidecarFilenameForImageFile(filename);

            if (XmpMetadata.writeMetadataToSidecarFile(sidecarFilename, xmp)) {
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
                EnumSet.of(Insert.XMP));
        updater.run();
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
