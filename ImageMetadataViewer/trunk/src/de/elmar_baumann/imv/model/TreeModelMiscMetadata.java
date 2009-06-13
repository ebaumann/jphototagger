package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifFocalLength;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifIsoSpeedRatings;
import de.elmar_baumann.imv.database.metadata.exif.ColumnExifRecordingEquipment;
import de.elmar_baumann.imv.resource.Bundle;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Misc metadata information.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/12
 */
public final class TreeModelMiscMetadata extends DefaultTreeModel {

    private static final DefaultMutableTreeNode ROOT = new DefaultMutableTreeNode(
            Bundle.getString("TreeModelMiscMetadata.Root.DisplayName"));
    private static final List<Column> exifColumns = new ArrayList<Column>();


    static {
        exifColumns.add(ColumnExifRecordingEquipment.INSTANCE);
        exifColumns.add(ColumnExifFocalLength.INSTANCE);
        exifColumns.add(ColumnExifIsoSpeedRatings.INSTANCE);
    }

    public TreeModelMiscMetadata() {
        super(ROOT);
        addExifInfo();
    }

    public static boolean containsExifColumn(Column column) {
        return exifColumns.contains(column);
    }

    private void addExifInfo() {
        DefaultMutableTreeNode exifNode = new DefaultMutableTreeNode(
                Bundle.getString("TreeModelMiscMetadata.ExifNode.DisplayName"));
        DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;
        for (Column column : exifColumns) {
            DefaultMutableTreeNode columnNode = new DefaultMutableTreeNode(
                    column);
            Set<String> data = db.getAllDistinctValues(column);
            addDataTo(data, columnNode);
            exifNode.add(columnNode);
        }
        ROOT.add(exifNode);
    }

    private void addDataTo(Set<String> data, DefaultMutableTreeNode node) {
        for (String s : data) {
            node.add(new DefaultMutableTreeNode(s));
        }
    }
}
