package de.elmar_baumann.imv.factory;

import de.elmar_baumann.imv.resource.Panels;
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
 * @version 2008/09/29
 */
public final class RendererFactory {

    static final RendererFactory INSTANCE = new RendererFactory();
    private boolean init = false;

    synchronized void init() {
        Util.checkInit(RendererFactory.class, init);
        if (!init) {
            init = true;
            AppPanel appPanel = Panels.getInstance().getAppPanel();
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
