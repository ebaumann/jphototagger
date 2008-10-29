package de.elmar_baumann.imv.data;

import de.elmar_baumann.imv.model.TableModelExif;
import de.elmar_baumann.imv.model.TableModelIptc;
import de.elmar_baumann.imv.model.TableModelXmp;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imv.view.panels.EditMetadataPanelsArray;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * Enthält Objekte zur Anzeige von Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public class MetadataDisplay {

    public List<JTable> metadataTables;
    public List<JTable> xmpTables;
    public TableModelIptc iptcTableModel;
    public TableModelExif exifTableModel;
    public TableModelXmp xmpTableModelDc;
    public TableModelXmp xmpTableModelExif;
    public TableModelXmp xmpTableModelIptc;
    public TableModelXmp xmpTableModelLightroom;
    public TableModelXmp xmpTableModelPhotoshop;
    public TableModelXmp xmpTableModelTiff;
    public TableModelXmp xmpTableModelCameraRawSettings;
    public TableModelXmp xmpTableModelXap;
    public AppPanel appPanel;
    public ImageFileThumbnailsPanel thumbnailsPanel;
    public EditMetadataPanelsArray editPanelsArray;
    public List<TableModelXmp> xmpTableModels;
    public JLabel labelMetadataFilename;
}
