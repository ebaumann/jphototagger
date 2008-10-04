package de.elmar_baumann.imagemetadataviewer.database.metadata.mapping;

import com.adobe.xmp.XMPConst;
import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcCreator;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcDescription;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcRights;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpDcTitle;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpIptc4xmpcoreCountrycode;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpIptc4xmpcoreLocation;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopAuthorsposition;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCaptionwriter;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCity;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCountry;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopCredit;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopHeadline;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopInstructions;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopSource;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopState;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.xmp.ColumnXmpPhotoshopTransmissionReference;
import java.util.HashMap;

/**
 * Mapping zwischen 
 * {@link de.elmar_baumann.imagemetadataviewer.database.metadata.Column} und
 * einem Namespace-URI
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/19
 */
public class XmpColumnNamespaceUriMapping {

    private static HashMap<Column, String> namespaceUriOfColumn = new HashMap<Column, String>();
    private static XmpColumnNamespaceUriMapping instance = new XmpColumnNamespaceUriMapping();

    static  {
        namespaceUriOfColumn.put(ColumnXmpDcCreator.getInstance(), XMPConst.NS_DC);
        namespaceUriOfColumn.put(ColumnXmpDcDescription.getInstance(), XMPConst.NS_DC);
        namespaceUriOfColumn.put(ColumnXmpDcRights.getInstance(), XMPConst.NS_DC);
        namespaceUriOfColumn.put(ColumnXmpDcSubjectsSubject.getInstance(), XMPConst.NS_DC);
        namespaceUriOfColumn.put(ColumnXmpDcTitle.getInstance(), XMPConst.NS_DC);
        namespaceUriOfColumn.put(ColumnXmpIptc4xmpcoreCountrycode.getInstance(), XMPConst.NS_IPTCCORE);
        namespaceUriOfColumn.put(ColumnXmpIptc4xmpcoreLocation.getInstance(), XMPConst.NS_IPTCCORE);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopAuthorsposition.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopCaptionwriter.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopCategory.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopCity.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopCountry.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopCredit.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopHeadline.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopInstructions.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopSource.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopState.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopSupplementalcategoriesSupplementalcategory.getInstance(), XMPConst.NS_PHOTOSHOP);
        namespaceUriOfColumn.put(ColumnXmpPhotoshopTransmissionReference.getInstance(), XMPConst.NS_PHOTOSHOP);
    }

    /**
     * Liefert die einzige Klasseninstanz.
     * 
     * @return Instanz
     */
    public static XmpColumnNamespaceUriMapping getInstance() {
        return instance;
    }

    /**
     * Liefert den Namespace-URI für eine Spalte.
     * 
     * @param  column  Spalte
     * @return Namespace-URI oder null bei unbekannter Spalte
     */
    public String getNamespaceUriOfColumn(Column column) {
        return namespaceUriOfColumn.get(column);
    }

    private XmpColumnNamespaceUriMapping() {
    }
}
