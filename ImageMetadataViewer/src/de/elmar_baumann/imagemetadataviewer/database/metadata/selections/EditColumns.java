package de.elmar_baumann.imagemetadataviewer.database.metadata.selections;

import de.elmar_baumann.imagemetadataviewer.database.metadata.Column;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcByLinesByLine;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcByLinesTitlesByLineTitle;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCaptionAbstract;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCity;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcContentLocationCodesContentLocationCode;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcContentLocationNamesContentLocationName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCopyrightNotice;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCountryPrimaryLocationName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcCredit;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcHeadline;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcKeywordsKeyword;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcObjectName;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcOriginalTransmissionReference;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcProvinceState;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcSource;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcSpecialInstructions;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcSupplementalCategoriesSupplementalCategory;
import de.elmar_baumann.imagemetadataviewer.database.metadata.iptc.ColumnIptcWritersEditorsWriterEditor;
import de.elmar_baumann.imagemetadataviewer.database.metadata.mapping.IptcXmpMapping;
import de.elmar_baumann.lib.template.Pair;
import java.util.Vector;

/**
 * Bearbeitbare Spalten: Der Spalteninhalt kann in Bilddateien geschrieben
 * werden.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/18
 */
public class EditColumns {

    private static Vector<Pair<Column, EditColumnHints>> iptcColumns = new Vector<Pair<Column, EditColumnHints>>();
    private static Vector<Column> xmpColumns = new Vector<Column>();
    private static EditColumns instance = new EditColumns();
    

    static {
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcKeywordsKeyword.getInstance(), new EditColumnHints(true, EditColumnHints.SizeEditField.large)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcObjectName.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcHeadline.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.large)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcCaptionAbstract.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.large)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcWritersEditorsWriterEditor.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcContentLocationNamesContentLocationName.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcContentLocationCodesContentLocationCode.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcCategory.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcSupplementalCategoriesSupplementalCategory.getInstance(), new EditColumnHints(true, EditColumnHints.SizeEditField.large)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcCopyrightNotice.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcByLinesByLine.getInstance(), new EditColumnHints(true, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcByLinesTitlesByLineTitle.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcCity.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcProvinceState.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcCountryPrimaryLocationName.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcOriginalTransmissionReference.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcSpecialInstructions.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.large)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcCredit.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));
        iptcColumns.add(new Pair<Column, EditColumnHints>(
            ColumnIptcSource.getInstance(), new EditColumnHints(false, EditColumnHints.SizeEditField.small)));

        IptcXmpMapping mapping = IptcXmpMapping.getInstance();
        for (Pair<Column, EditColumnHints> pair : iptcColumns) {
            xmpColumns.add(mapping.getXmpColumnOfIptcColumn(pair.getFirst()));
        }
    }

    public static EditColumns getInstance() {
        return instance;
    }

    /**
     * Liefert die bearbeitbaren IPTC-Spalten. Die dazugehörigen XMP-Spalten
     * liefert
     * {@link de.elmar_baumann.imagemetadataviewer.database.metadata.mapping.IptcXmpMapping#getXmpColumnOfIptcColumn(de.elmar_baumann.imagemetadataviewer.database.metadata.Column)}
     * 
     * @return IPTC-Spalten
     */
    public Vector<Pair<Column, EditColumnHints>> getIptcColumns() {
        return iptcColumns;
    }

    /**
     * Liefert die zugehörigen XMP-Spalten. Nutzt
     * {@link de.elmar_baumann.imagemetadataviewer.database.metadata.mapping.IptcXmpMapping#getXmpColumnOfIptcColumn(de.elmar_baumann.imagemetadataviewer.database.metadata.Column)}
     * 
     * @return XMP-Spalten
     */
    public Vector<Column> getXmpColumns() {
        return xmpColumns;
    }

    private EditColumns() {
    }
}
