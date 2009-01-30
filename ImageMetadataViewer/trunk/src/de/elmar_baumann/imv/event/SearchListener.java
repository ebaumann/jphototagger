package de.elmar_baumann.imv.event;

/**
 * Beobachtet Suchen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public interface SearchListener {

    /**
     * Ereignis fand statt.
     * 
     * @param evt Ereignis
     */
    public void actionPerformed(SearchEvent evt);
}
