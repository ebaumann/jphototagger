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

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.Iptc;
import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.event.ProgressEvent;
import de.elmar_baumann.jpt.event.listener.ProgressListener;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.image.metadata.iptc.IptcMetadata;
import de.elmar_baumann.jpt.image.metadata.xmp.XmpMetadata;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Erzeugt XMP-Daten anhand bestehender IPTC-Daten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ConvertIptcToXmp implements Runnable {

    private final   List<ProgressListener> progressListeners = new ArrayList<ProgressListener>();
    private final   List<String>           filenames;
    private boolean                        stop;

    public ConvertIptcToXmp(List<String> filenames) {
        this.filenames = new ArrayList<String>(filenames);
    }

    public synchronized void addProgressListener(ProgressListener listener) {
        progressListeners.add(listener);
    }

    public void stop() {
        stop = true;
    }

    @Override
    public void run() {
        notifyStart();
        int size = filenames.size();
        int index = 0;
        for (index = 0; !stop && index < size; index++) {
            String imageFilename = filenames.get(index);
            String xmpFilename   = XmpMetadata.suggestSidecarFilename(imageFilename);
            Iptc   iptc          = IptcMetadata.getIptc(new File(imageFilename));
            if (iptc != null) {
                Xmp xmp = XmpMetadata.getXmpFromSidecarFileOf(imageFilename);
                if (xmp == null) {
                    xmp = new Xmp();
                }
                xmp.setIptc(iptc, Xmp.SetIptc.DONT_CHANGE_EXISTING_VALUES);
                logWriteXmpFile(imageFilename);
                if (XmpMetadata.writeXmpToSidecarFile(xmp, xmpFilename)) {
                    updateDatabase(imageFilename);
                }
            }
            notifyPerformed(index);
        }
        notifyEnd(index);
    }

    private void updateDatabase(String imageFilename) {
        InsertImageFilesIntoDatabase insert = new InsertImageFilesIntoDatabase(
                                       Arrays.asList(imageFilename), Insert.XMP);
        insert.run(); // Shall run in this thread!
    }

    private void checkStopEvent(ProgressEvent event) {
        if (event.isStop()) {
            stop();
        }
    }

    private void logWriteXmpFile(String imageFilename) {
        AppLogger.logInfo(ConvertIptcToXmp.class,
                "IptcToXmp.Info.StartWriteXmpFile", imageFilename);
    }

    private synchronized void notifyStart() {
        int count = filenames.size();
        ProgressEvent event = new ProgressEvent(this, 0, count, 0,
                filenames.size() > 0
                ? filenames.get(0)
                : "");
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressStarted(event);
            checkStopEvent(event);
        }
    }

    private synchronized void notifyPerformed(int index) {
        ProgressEvent event = new ProgressEvent(this, 0, filenames.size(), index + 1, filenames.get(index));
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressPerformed(event);
            checkStopEvent(event);
        }
    }

    private synchronized void notifyEnd(int index) {
        ProgressEvent event = new ProgressEvent(this, 0, filenames.size(), index + 1, "");
        for (ProgressListener progressListener : progressListeners) {
            progressListener.progressEnded(event);
        }
    }
}
