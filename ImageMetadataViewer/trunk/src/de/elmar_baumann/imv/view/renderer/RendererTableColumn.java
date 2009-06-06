package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.selections.TableIcons;
import javax.swing.JLabel;

/**
 * Renderd eine Tabellenspalte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/17
 */
public final class RendererTableColumn {

    /**
     * Setzt Icon und Text eines Labels, das eine Tabellenspalte darstellt.
     * 
     * @param label   Label
     * @param column  Tabellenspalte
     */
    public static void setLabelText(JLabel label, Column column) {
        label.setIcon(TableIcons.getIcon(column.getTable()));
        label.setText(column.getDescription());
    }

    protected RendererTableColumn() {
    }
}
