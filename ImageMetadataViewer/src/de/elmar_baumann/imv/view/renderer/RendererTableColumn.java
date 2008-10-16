package de.elmar_baumann.imv.view.renderer;

import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.Table;
import de.elmar_baumann.imv.database.metadata.selections.AllTables;
import de.elmar_baumann.imv.database.metadata.selections.TableIcons;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;

/**
 * Renderd eine Tabellenspalte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/17
 */
public class RendererTableColumn {

    private static Map<Column, String> textOfColumn = new HashMap<Column, String>();
    private static TableIcons tableIcons = TableIcons.getInstance();
    

    static {
        for (Table table : AllTables.get()) {
            for (Column column : table.getColumns()) {
                String description = column.getDescription();
                if (!column.isPrimaryKey() && !column.isForeignKey() &&
                    !description.isEmpty()) {
                    textOfColumn.put(column, getLabelText(column));
                }
            }
        }
    }
    
    /**
     * Setzt Icon und Text eines Labels, das eine Tabellenspalte darstellt.
     * 
     * @param label   Label
     * @param column  Tabellenspalte
     */
    public static void setLabelText(JLabel label, Column column) {
        label.setIcon(tableIcons.getIcon(column.getTable()));
        label.setText(textOfColumn.get(column));
    }

    private static String getLabelText(Column column) {
        return "<html><strong>" + column.getDescription() + "</strong></html>"; // NOI18N
    }
}
