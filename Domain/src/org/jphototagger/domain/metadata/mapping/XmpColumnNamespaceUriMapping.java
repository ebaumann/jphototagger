package org.jphototagger.domain.metadata.mapping;

import com.adobe.xmp.XMPConst;
import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.database.column.ColumnXmpDcCreator;
import org.jphototagger.domain.database.column.ColumnXmpDcDescription;
import org.jphototagger.domain.database.column.ColumnXmpDcRights;
import org.jphototagger.domain.database.column.ColumnXmpDcSubjectsSubject;
import org.jphototagger.domain.database.column.ColumnXmpDcTitle;
import org.jphototagger.domain.database.column.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.domain.database.column.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCity;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCountry;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopCredit;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopHeadline;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopInstructions;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopSource;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopState;
import org.jphototagger.domain.database.column.ColumnXmpPhotoshopTransmissionReference;
import org.jphototagger.domain.database.column.ColumnXmpRating;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping zwischen
 * {@link org.jphototagger.program.database.metadata.Column} und
 * einem Namespace-URI
 *
 * @author Elmar Baumann
 */
public final class XmpColumnNamespaceUriMapping {

    private static final Map<Column, String> NAMESPACE_URI_OF_COLUMN = new HashMap<Column, String>();

    static {
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpDcCreator.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpDcDescription.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpDcRights.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpDcSubjectsSubject.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpDcTitle.INSTANCE, XMPConst.NS_DC);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, XMPConst.NS_IPTCCORE);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpIptc4XmpCoreDateCreated.INSTANCE, XMPConst.NS_IPTCCORE);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopCity.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopCountry.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopCredit.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopHeadline.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopInstructions.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopSource.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopState.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, XMPConst.NS_PHOTOSHOP);
        NAMESPACE_URI_OF_COLUMN.put(ColumnXmpRating.INSTANCE, XMPConst.NS_XMP);
    }

    /**
     * Liefert den Namespace-URI für eine Spalte.
     *
     * @param  column  Spalte
     * @return Namespace-URI oder null bei unbekannter Spalte
     */
    public static String getNamespaceUriOfColumn(Column column) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        return NAMESPACE_URI_OF_COLUMN.get(column);
    }

    private XmpColumnNamespaceUriMapping() {
    }
}
