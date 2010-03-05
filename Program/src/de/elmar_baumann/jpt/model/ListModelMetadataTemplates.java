/*
 * JPhotoTagger tags and finds images fast
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
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.database.DatabaseMetadataTemplates;
import de.elmar_baumann.jpt.event.DatabaseMetadataTemplatesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseMetadataTemplatesListener;
import javax.swing.DefaultListModel;

/**
 * Elements are {@link MetadataTemplate}s retrieved through
 * {@link DatabaseMetadataTemplates#getAll()}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-05
 */
public final class ListModelMetadataTemplates
        extends    DefaultListModel
        implements DatabaseMetadataTemplatesListener {

    private static final long serialVersionUID = -1726658041913008196L;

    public ListModelMetadataTemplates() {
        addElements();
        DatabaseMetadataTemplates.INSTANCE.addListener(this);
    }

    private void addElements() {
        for (MetadataTemplate t : DatabaseMetadataTemplates.INSTANCE.getAll()) {
            addElement(t);
        }
    }

    @Override
    public void actionPerformed(DatabaseMetadataTemplatesEvent evt) {
        if (evt.wasAdded()) {
            addElement(evt.getTemplate());
        } else if (evt.wasDeleted()) {
            removeElement(evt.getTemplate());
        } else if (evt.wasUpdated()) {
            int index = indexOf(evt.getOldTemplate());
            if (index >= 0) {
                remove(index);
                add(index, evt.getTemplate());
            }
        }
    }

}
