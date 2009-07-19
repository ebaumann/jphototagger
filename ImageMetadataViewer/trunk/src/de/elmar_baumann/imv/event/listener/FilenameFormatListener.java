package de.elmar_baumann.imv.event.listener;

/**
 * Listens while formatting files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-07
 */
public interface FilenameFormatListener {

    public enum Request {

        /**
         * A sequencial format shall restart it's sequence
         */
        RESTART_SEQUENCE
    }

    /**
     * Request from a filename formatter.
     *
     * @param request request
     */
    public void request(Request request);
}
