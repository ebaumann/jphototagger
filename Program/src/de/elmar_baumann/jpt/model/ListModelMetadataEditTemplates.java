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
package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.data.MetadataEditTemplate;
import de.elmar_baumann.jpt.database.DatabaseMetadataEditTemplates;
import de.elmar_baumann.jpt.event.MetadataEditTemplateEvent;
import de.elmar_baumann.jpt.event.listener.MetadataEditTemplateEventListener;
import javax.swing.DefaultListModel;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-01-05
 */
public final class ListModelMetadataEditTemplates
        extends    DefaultListModel
        implements MetadataEditTemplateEventListener {

    public ListModelMetadataEditTemplates() {
        addItems();
        DatabaseMetadataEditTemplates.INSTANCE.addMetadataEditTemplateEventListener(this);
    }

    private void addItems() {
        for (MetadataEditTemplate t : DatabaseMetadataEditTemplates.INSTANCE.getMetadataEditTemplates()) {
            addElement(t);
        }
    }

    @Override
    public void actionPerformed(MetadataEditTemplateEvent evt) {

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
