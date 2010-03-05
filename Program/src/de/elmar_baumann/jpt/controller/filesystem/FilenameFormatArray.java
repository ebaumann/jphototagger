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
package de.elmar_baumann.jpt.controller.filesystem;

import de.elmar_baumann.jpt.event.listener.FilenameFormatListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Array of {@link FilenameFormat} objects.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-13
 */
public final class FilenameFormatArray implements FilenameFormatListener {

    private final List<FilenameFormat> formats = new ArrayList<FilenameFormat>();

    /**
     * Adds a format. {@link #format()} returns the filename built in the
     * same order of the calls to this function.
     *
     * @param format  format
     */
    public void addFormat(FilenameFormat format) {
        synchronized (formats) {
            format.addFilenameFormatListener(this);
            formats.add(format);
        }
    }

    /**
     * Calls to every format {@link FilenameFormat#next()}
     */
    public void notifyNext() {
        synchronized (formats) {
            for (FilenameFormat format : formats) {
                format.next();
            }
        }
    }

    /**
     * Removes all Formats.
     */
    public void clear() {
        synchronized (formats) {
            for (FilenameFormat format : formats) {
                format.removeFilenameFormatListener(this);
            }
            formats.clear();
        }
    }

    /**
     * Returns the formatted filename: the appended strings of all formats
     * ({@link FilenameFormat#format()}).
     *
     * @return filename
     */
    public String format() {
        StringBuffer buffer = new StringBuffer();
        synchronized (formats) {
            for (FilenameFormat format : formats) {
                buffer.append(format.format());
            }
        }
        return buffer.toString();
    }

    /**
     * Sets a file to all formats.
     *
     * @param file file
     */
    public void setFile(File file) {
        synchronized (formats) {
            for (FilenameFormat format : formats) {
                format.setFile(file);
            }
        }
    }

    @Override
    public void request(Request request) {
        if (request.equals(FilenameFormatListener.Request.RESTART_SEQUENCE)) {
            restartSequenceFormatter();
        }
    }

    private void restartSequenceFormatter() {
        synchronized (formats) {
            for (FilenameFormat format : formats) {
                if (format instanceof FilenameFormatNumberSequence) {
                    ((FilenameFormatNumberSequence) format).restart();
                }
            }
        }
    }
}
