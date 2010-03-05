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

package de.elmar_baumann.jpt.event.listener.impl;

import de.elmar_baumann.jpt.database.DatabaseImageCollections;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.DatabaseMetadataTemplates;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.jpt.database.DatabaseStatistics;
import de.elmar_baumann.jpt.event.DatabaseImageCollectionsEvent;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.DatabaseMetadataTemplatesEvent;
import de.elmar_baumann.jpt.event.DatabaseProgramsEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageCollectionsListener;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.event.listener.DatabaseMetadataTemplatesListener;
import de.elmar_baumann.jpt.event.listener.DatabaseProgramsListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

/**
 * Beobachtet Änderungen in der Datenbank und liefert die Anzahl aller Datensätze
 * danach.
 *
 * @author  Elmar Baumann
 * @version 2008-09-17
 */
public final class DatabaseTotalRecordCountListener
        implements DatabaseImageFilesListener, DatabaseProgramsListener,
                   DatabaseImageCollectionsListener,
                   DatabaseMetadataTemplatesListener {
    private final DatabaseStatistics db     = DatabaseStatistics.INSTANCE;
    private final List<JLabel>       labels = new ArrayList<JLabel>();
    private boolean                  listen = false;

    public DatabaseTotalRecordCountListener() {
        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addListener(this);
        DatabasePrograms.INSTANCE.addListener(this);
        DatabaseImageCollections.INSTANCE.addListener(this);
        DatabaseMetadataTemplates.INSTANCE.addListener(this);
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
    public void actionPerformed(DatabaseImageFilesEvent event) {
        setCount();
    }

    @Override
    public void actionPerformed(DatabaseProgramsEvent event) {
        setCount();
    }

    @Override
    public void actionPerformed(DatabaseImageCollectionsEvent event) {
        setCount();
    }

    @Override
    public void actionPerformed(DatabaseMetadataTemplatesEvent evt) {
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
}
