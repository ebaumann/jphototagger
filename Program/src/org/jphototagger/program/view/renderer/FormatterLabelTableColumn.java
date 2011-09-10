package org.jphototagger.program.view.renderer;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.program.database.metadata.selections.TableIcons;
import javax.swing.JLabel;

/**
 * Renderd eine Tabellenspalte.
 *
 * @author Elmar Baumann
 */
public final class FormatterLabelTableColumn {

    /**
     * Setzt Icon und Text eines Labels, das eine Tabellenspalte darstellt.
     *
     * @param label   Label
     * @param column  Tabellenspalte
     */
    public static void setLabelText(JLabel label, MetaDataValue column) {
        if (label == null) {
            throw new NullPointerException("label == null");
        }

        if (column == null) {
            throw new NullPointerException("column == null");
        }

        label.setIcon(TableIcons.getIcon(column.getCategory()));
        label.setText(column.getDescription());
    }

    protected FormatterLabelTableColumn() {}
}
