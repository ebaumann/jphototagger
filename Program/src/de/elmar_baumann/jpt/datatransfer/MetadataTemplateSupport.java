/*
 * @(#)MetadataTemplateSupport.java    2010-01-06
 *
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

package de.elmar_baumann.jpt.datatransfer;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.data.MetadataTemplate;
import de.elmar_baumann.jpt.data.TextEntry;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.view.panels.EditRepeatableTextEntryPanel;

import java.awt.Component;

import java.util.Collection;

import javax.swing.TransferHandler.TransferSupport;

/**
 *
 *
 * @author  Elmar Baumann
 */
final class MetadataTemplateSupport {
    @SuppressWarnings({ "unchecked", "unchecked" })
    public static void setTemplate(TransferSupport transferSupport) {
        try {
            Object[] selTemplates =
                (Object[]) transferSupport.getTransferable().getTransferData(
                    Flavor.METADATA_TEMPLATES);
            TextEntry textEntry =
                findParentTextEntry(transferSupport.getComponent());

            if ((selTemplates != null) && (textEntry != null)) {
                Column           column   = textEntry.getColumn();
                MetadataTemplate template = (MetadataTemplate) selTemplates[0];
                Object           value    = template.getValueOfColumn(column);

                if (value instanceof String) {
                    textEntry.setText((String) value);
                    textEntry.setDirty(true);
                } else if (value instanceof Collection<?>) {
                    EditRepeatableTextEntryPanel panel =
                        findRepeatableTextEntryPanel(
                            transferSupport.getComponent());

                    if (panel == null) {
                        return;
                    }

                    panel.setText((Collection<String>) value);
                    panel.setDirty(true);
                }
            }
        } catch (Exception ex) {
            AppLogger.logSevere(TransferHandlerDropEdit.class, ex);
        }
    }

    private static EditRepeatableTextEntryPanel findRepeatableTextEntryPanel(
            Component c) {
        Component parent = c.getParent();

        while (parent != null) {
            if (parent instanceof EditRepeatableTextEntryPanel) {
                return (EditRepeatableTextEntryPanel) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    private static TextEntry findParentTextEntry(Component c) {
        Component parent = c.getParent();

        while (parent != null) {
            if (parent instanceof TextEntry) {
                return (TextEntry) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    private MetadataTemplateSupport() {}
}
