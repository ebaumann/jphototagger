package org.jphototagger.program.database.metadata.selections;

import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.column.ColumnXmpDcCreator;
import org.jphototagger.domain.database.column.ColumnXmpDcRights;
import org.jphototagger.domain.database.column.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.column.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCity;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCountry;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCredit;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopSource;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopState;
import java.util.ArrayList;
import java.util.List;

/**
 * All columns with autocomplete enabled, for other columns autocomplete should
 * be disabled.
 *
 * @author Elmar Baumann
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
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        return COLUMNS.contains(column);
    }

    public static List<Column> get() {
        return new ArrayList<Column>(COLUMNS);
    }
}
