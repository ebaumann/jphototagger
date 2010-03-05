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
package de.elmar_baumann.jpt.controller.keywords.tree;

import de.elmar_baumann.jpt.data.ImageFile;
import de.elmar_baumann.jpt.database.DatabaseKeywords;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.event.DatabaseImageFilesEvent;
import de.elmar_baumann.jpt.event.listener.DatabaseImageFilesListener;
import de.elmar_baumann.jpt.factory.ModelFactory;
import de.elmar_baumann.jpt.model.TreeModelKeywords;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Listens to database updates and adds not existing keywords.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-17
 */
public final class ControllerKeywordsDbUpdates implements DatabaseImageFilesListener {

    public ControllerKeywordsDbUpdates() {
        listen();
    }

    private void listen() {
        DatabaseImageFiles.INSTANCE.addListener(this);
    }

    @Override
    public void actionPerformed(DatabaseImageFilesEvent event) {
        if (event.isTextMetadataAffected()) {
            addNotExistingKeywords(event.getImageFile());
        }
    }

    @SuppressWarnings("unchecked")
    private void addNotExistingKeywords(ImageFile imageFile) {
        if (imageFile != null && imageFile.getXmp() != null) {
             Object o = imageFile.getXmp().getValue(ColumnXmpDcSubjectsSubject.INSTANCE);
             if (o instanceof List<?>) {
                for (String keyword : (List<String>) o) {
                   if (!DatabaseKeywords.INSTANCE.exists(keyword)) {
                       addKeyword(keyword);
                   }
                }
            }
        }
    }

    private void addKeyword(String keyword) {
        TreeModelKeywords model = ModelFactory.INSTANCE.getModel(TreeModelKeywords.class);

        model.insert((DefaultMutableTreeNode)model.getRoot(), keyword, true);
    }
}
