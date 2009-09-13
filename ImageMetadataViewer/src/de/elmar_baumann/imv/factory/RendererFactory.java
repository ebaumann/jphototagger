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
package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.renderer.TableCellRendererExif;
import de.elmar_baumann.imv.view.renderer.TableCellRendererIptc;
import de.elmar_baumann.imv.view.renderer.TableCellRendererXmp;
import java.util.List;
import javax.swing.JTable;

/**
 * Erzeugt Renderer und verknüpft sie mit den GUI-Elementen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-29
 */
public final class RendererFactory {

    static final RendererFactory INSTANCE = new RendererFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(RendererFactory.class, init);
        if (!init) {
            init = true;
            AppPanel appPanel = GUI.INSTANCE.getAppPanel();
            TableCellRendererXmp rendererTableCellXmp = new TableCellRendererXmp();
            List<JTable> xmpTables = appPanel.getXmpTables();
            for (JTable table : xmpTables) {
                table.setDefaultRenderer(Object.class, rendererTableCellXmp);
            }
            appPanel.getTableIptc().setDefaultRenderer(Object.class, new TableCellRendererIptc());
            appPanel.getTableExif().setDefaultRenderer(Object.class, new TableCellRendererExif());
        }
    }
}
