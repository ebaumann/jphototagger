package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.event.listener.FilenameFormatListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Array of {@link FilenameFormat} objects.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/13
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
