package de.elmar_baumann.imagemetadataviewer.view.renderer;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Table;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.AllTables;
import de.elmar_baumann.imagemetadataviewer.database.metadata.selections.TableIcons;
import java.util.HashMap;
import javax.swing.JLabel;

/**
 * Renderd eine Tabellenspalte.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/17
 */
public class RendererTableColumn {

    private static HashMap<Column, String> textOfColumn = new HashMap<Column, String>();
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
     * Removes from column descriptions which have the standard definition
     * at the end of the description in "[" brackets.
     * 
     * @param  column  column
     * @return Description without " [.*"
     */
    public static String getDescriptionWithoutDefinition(Column column) {
        String description = column.getDescription();
        int index = description.indexOf(" [");
        if (index > 0) {
            return description.substring(0, index);
        }
        return description;
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
        String text = "<html><strong>" + column.getDescription() + "</strong>"; // NOI18N
        text = text.replace("[", "<font color=\"#aaaaaa\">[").replace("]", "]</font>"); // NOI18N
        return text + "</html>"; // NOI18N
    }
}
