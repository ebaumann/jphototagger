package org.jphototagger.program.database.metadata.mapping;

import com.adobe.xmp.XMPConst;

import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcCreator;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcDescription;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcRights;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcTitle;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4XmpCoreDateCreated;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCity;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopSource;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopState;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpRating;

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

    private XmpColumnNamespaceUriMapping() {}
}
