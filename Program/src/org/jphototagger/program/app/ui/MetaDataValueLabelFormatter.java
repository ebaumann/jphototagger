package org.jphototagger.program.app.ui;

import javax.swing.JLabel;

import org.jphototagger.domain.metadata.MetaDataValue;

/**
 * @author Elmar Baumann
 */
public final class MetaDataValueLabelFormatter {

    public static void setLabelText(JLabel label, MetaDataValue metaDataValue) {
        if (label == null) {
            throw new NullPointerException("label == null");
        }

        if (metaDataValue == null) {
            throw new NullPointerException("metaDataValue == null");
        }

        label.setIcon(TableIcons.getIcon(metaDataValue.getCategory()));
        label.setText(metaDataValue.getDescription());
    }

    protected MetaDataValueLabelFormatter() {
    }
}
