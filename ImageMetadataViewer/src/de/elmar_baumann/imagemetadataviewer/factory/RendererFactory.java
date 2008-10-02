package de.elmar_baumann.imagemetadataviewer.factory;

import de.elmar_baumann.imagemetadataviewer.resource.Panels;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.renderer.TableCellRendererExif;
import de.elmar_baumann.imagemetadataviewer.view.renderer.TableCellRendererIptc;
import de.elmar_baumann.imagemetadataviewer.view.renderer.TableCellRendererXmp;
import java.util.Vector;
import javax.swing.JTable;

/**
 * Erzeugt Renderer und verknüpft sie mit den GUI-Elementen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/29
 */
public class RendererFactory {

    private static RendererFactory instance = new RendererFactory();

    static RendererFactory getInstance() {
        return instance;
    }

    private RendererFactory() {
        createRenderer();
    }

    private void createRenderer() {
        AppPanel appPanel = Panels.getInstance().getAppPanel();
        TableCellRendererXmp rendererTableCellXmp = new TableCellRendererXmp();
        Vector<JTable> xmpTables = appPanel.getXmpTables();
        for (JTable table : xmpTables) {
            table.setDefaultRenderer(Object.class, rendererTableCellXmp);
        }
        appPanel.getTableIptc().setDefaultRenderer(Object.class, new TableCellRendererIptc());
        appPanel.getTableExif().setDefaultRenderer(Object.class, new TableCellRendererExif());
    }
}
