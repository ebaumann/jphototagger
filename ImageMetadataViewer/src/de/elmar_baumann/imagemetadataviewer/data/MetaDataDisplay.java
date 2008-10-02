package de.elmar_baumann.imagemetadataviewer.data;

import de.elmar_baumann.imagemetadataviewer.model.TableModelExif;
import de.elmar_baumann.imagemetadataviewer.model.TableModelIptc;
import de.elmar_baumann.imagemetadataviewer.model.TableModelXmp;
import de.elmar_baumann.imagemetadataviewer.view.panels.AppPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.ImageFileThumbnailsPanel;
import de.elmar_baumann.imagemetadataviewer.view.panels.MetaDataEditPanelsArray;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 * Enthält Objekte zur Anzeige von Metadaten.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/25
 */
public class MetaDataDisplay {

    public Vector<JTable> metaDataTables;
    public Vector<JTable> xmpTables;
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
    public MetaDataEditPanelsArray editPanelsArray;
    public Vector<TableModelXmp> xmpTableModels;
    public JLabel labelMetadataFilename;
}
