package de.elmar_baumann.jpt.database.metadata.selections;

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.jpt.database.metadata.xmp
    .ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.jpt.database.metadata.xmp.ColumnXmpPhotoshopState;

import java.util.ArrayList;
import java.util.List;

/**
 * All columns with autocomplete enabled, for other columns autocomplete should
 * be disabled.
 *
 * @author  Elmar Baumann
 * @version 2010-03-16
 */
public final class AutocompleteColumns {
    private static final List<Column> COLUMNS = new ArrayList<Column>();

    static {
        COLUMNS.add(ColumnXmpDcCreator.INSTANCE);
        COLUMNS.add(ColumnXmpDcRights.INSTANCE);
        COLUMNS.add(ColumnXmpIptc4xmpcoreLocation.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopAuthorsposition.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCaptionwriter.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCity.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCountry.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopCredit.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopSource.INSTANCE);
        COLUMNS.add(ColumnXmpPhotoshopState.INSTANCE);
        COLUMNS.add(ColumnXmpDcSubjectsSubject.INSTANCE);
    }

    private AutocompleteColumns() {}

    public static boolean contains(Column column) {
        return COLUMNS.contains(column);
    }

    public static List<Column> get() {
        return new ArrayList<Column>(COLUMNS);
    }
}
