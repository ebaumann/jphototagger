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
package de.elmar_baumann.imv.event.listener.impl;

import de.elmar_baumann.imv.database.DatabaseStatistics;
import de.elmar_baumann.imv.event.DatabaseImageCollectionEvent;
import de.elmar_baumann.imv.event.DatabaseImageEvent;
import de.elmar_baumann.imv.event.listener.DatabaseListener;
import de.elmar_baumann.imv.event.DatabaseProgramEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;

/**
 * Beobachtet Änderungen in der Datenbank und liefert die Anzahl aller Datensätze
 * danach.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-17
 */
public final class DatabaseListenerTotalRecordCount implements DatabaseListener {

    private final DatabaseStatistics db = DatabaseStatistics.INSTANCE;
    private final List<JLabel> labels = new ArrayList<JLabel>();
    boolean listen = false;

    public DatabaseListenerTotalRecordCount() {
        db.addDatabaseListener(this);
    }

    /**
     * Sets wheter to listen to the database.
     * 
     * @param listen  true if listen. Default: false
     */
    public void setListenToDatabase(boolean listen) {
        this.listen = listen;
    }

    /**
     * Fügt ein Label hinzu. In dieses wird die Anzahl der Datensätze nach
     * Modifikationen der Datenbank geschrieben.
     * 
     * @param label Label
     */
    public void addLabel(JLabel label) {
        labels.add(label);
    }

    /**
     * Entfernt ein Label.
     * 
     * @param label Label
     */
    public void removeLabel(JLabel label) {
        labels.remove(label);
    }

    @Override
    public void actionPerformed(DatabaseImageEvent event) {
        setCount();
    }

    @Override
    public void actionPerformed(DatabaseProgramEvent event) {
        setCount();
    }

    private void setCount() {
        if (listen) {
            long count = db.getTotalRecordCount();
            setLabels(count);
        }
    }

    private void setLabels(Long count) {
        for (JLabel label : labels) {
            label.setText(count.toString());
        }
    }

    @Override
    public void actionPerformed(DatabaseImageCollectionEvent event) {
        // ignore
    }
}
