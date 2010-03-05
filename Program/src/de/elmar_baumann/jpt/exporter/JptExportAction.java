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
package de.elmar_baumann.jpt.exporter;

import de.elmar_baumann.jpt.view.dialogs.ExportImportDialog;
import de.elmar_baumann.jpt.view.panels.ExportImportPanel.Context;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-03
 */
public final class JptExportAction extends AbstractAction {

    private static final long            serialVersionUID = 2682169629889229733L;
    public static final  JptExportAction INSTANCE         = new JptExportAction();

    @Override
    public void actionPerformed(ActionEvent e) {
        new ExportImportDialog(Context.EXPORT).setVisible(true);
    }

    private JptExportAction() {
    }

}
