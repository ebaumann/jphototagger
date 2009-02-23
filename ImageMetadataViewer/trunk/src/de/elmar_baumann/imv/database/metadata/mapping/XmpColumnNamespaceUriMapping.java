package de.elmar_baumann.imv.database.metadata.mapping;

import com.adobe.xmp.XMPConst;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imv.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping zwischen 
 * {@link de.elmar_baumann.imv.database.metadata.Column} und
 * einem Namespace-URI
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/19
 */
public final class XmpColumnNamespaceUriMapping {

    private static final Map<Column, String> namespaceUriOfColumn = new HashMap<Column, String>();

    static  {
        namespaceUriOfColumn.put(ColumnXmpDcCreator.INSTANCE, XMPConst.NS_DC);
        namespaceUriOfColumn.put(ColumnXmpDcDescription.INSTANCE, XMPConst.NS_DC);
        namespaceUriOfColumn.put(ColumnXmpDcRights.INSTANCE, XMPConst.NS_DC);
        namespaceUriOfColumn.put(ColumnXmpDcSubjectsSubject.INSTANCE, XMPConst.NS_DC);
        namespaceUriOfColumn.put(ColumnXmpDcTitle.INSTANCE, XMPConst.NS_DC);
        namespaceUriOfColumn.put(ColumnXmpIptc4xmpcoreCountrycode.INSTANCE, XMPConst.NS_IPTCCORE);
        namespaceUriOfColumn.put(ColumnXmpIptc4xmpcoreLocation.INSTANCE, XMPConst.NS_IPTCCORE);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopAuthorsposition.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopCaptionwriter.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopCategory.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopCity.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopCountry.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopCredit.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopHeadline.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopInstructions.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopSource.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopState.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.INSTANCE, XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopTransmissionReference.INSTANCE, XMPConst.NS_PHOTOSHOP);
    }

    /**
     * Liefert den Namespace-URI für eine Spalte.
     * 
     * @param  column  Spalte
     * @return Namespace-URI oder null bei unbekannter Spalte
     */
    public static String getNamespaceUriOfColumn(Column column) {
        return namespaceUriOfColumn.get(column);
    }

    private XmpColumnNamespaceUriMapping() {
    }
}
