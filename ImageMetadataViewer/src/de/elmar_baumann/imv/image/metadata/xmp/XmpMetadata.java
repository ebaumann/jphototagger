package de.elmar_baumann.imv.image.metadata.xmp;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.options.SerializeOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.imagero.reader.iptc.IPTCEntryMeta;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.mapping.IptcEntryXmpPathStartMapping;
import de.elmar_baumann.imv.database.metadata.mapping.XmpColumnNamespaceUriMapping;
import de.elmar_baumann.imv.database.metadata.mapping.XmpColumnXmpDataTypeMapping;
import de.elmar_baumann.imv.database.metadata.mapping.XmpColumnXmpDataTypeMapping.XmpValueType;
import de.elmar_baumann.imv.database.metadata.mapping.XmpColumnXmpPathStartMapping;
import de.elmar_baumann.imv.database.metadata.selections.EditColumns;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils für XMP.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class XmpMetadata {

    private static final List<String> knownNamespaces = new ArrayList<String>();
    private static XmpColumnNamespaceUriMapping mappingNamespaceUri = XmpColumnNamespaceUriMapping.getInstance();
    private static XmpColumnXmpPathStartMapping mappingName = XmpColumnXmpPathStartMapping.getInstance();
    private static XmpColumnXmpDataTypeMapping mappingDataType = XmpColumnXmpDataTypeMapping.getInstance();
    

    static {
        knownNamespaces.add("Iptc4xmpCore"); // NOI18N
        knownNamespaces.add("aux"); // NOI18N
        knownNamespaces.add("crs"); // NOI18N
        knownNamespaces.add("dc"); // NOI18N
        knownNamespaces.add("exif"); // NOI18N
        knownNamespaces.add("lr"); // NOI18N
        knownNamespaces.add("photoshop"); // NOI18N
        knownNamespaces.add("tiff"); // NOI18N
        knownNamespaces.add("xap"); // NOI18N
        knownNamespaces.add("xapRights"); // NOI18N
    }

    /**
     * Liefert das Begrenzungszeichen für die einzelnen Werte in Arrays.
     * 
     * @return Trennzeichen
     */
    public static final String getArrayItemDelimiter() {
        return "\\"; // NOI18N
    }

    /**
     * Liefert alle XMP-Property-Infos eines bestimmten Namensraums.
     * 
     * @param propertyInfos XMP-Property-Infos beliebiger Namensräume
     * @param namespace     Namensraum
     * @return              Property-Infos dieses Namensraums
     */
    public static List<XMPPropertyInfo> getPropertyInfosOfNamespace(
        List<XMPPropertyInfo> propertyInfos, String namespace) {
        List<XMPPropertyInfo> propertyInfosNs = new ArrayList<XMPPropertyInfo>();
        for (XMPPropertyInfo propertyInfo : propertyInfos) {
            if (propertyInfo.getNamespace().equals(namespace)) {
                propertyInfosNs.add(propertyInfo);
            }
        }
        return propertyInfosNs;
    }

    /**
     * Liefert die XMP-Metadaten einer Bilddatei.
     * 
     * @param filename Dateiname
     * @return         Metadaten oder null bei Lesefehlern
     */
    public List<XMPPropertyInfo> getPropertyInfosOfFile(String filename) {
        if (filename == null || !FileUtil.existsFile(filename)) {
            return null;
        }
        List<XMPPropertyInfo> metadata = new ArrayList<XMPPropertyInfo>();
        try {
            String xmp = getXmpAsString(filename);
            if (xmp != null && xmp.length() > 0) {
                XMPMeta xmpMeta = XMPMetaFactory.parseFromString(xmp);
                if (xmpMeta != null) {
                    addXmpPropertyInfo(xmpMeta, metadata);
                }
            }
        } catch (XMPException ex) {
            metadata = null;
            de.elmar_baumann.imv.Logging.logWarning(getClass(), ex);
        } catch (Exception ex) {
            metadata = null;
            de.elmar_baumann.imv.Logging.logWarning(getClass(), ex);
        }
        return metadata;
    }

    private void addXmpPropertyInfo(XMPMeta xmpMeta,
        List<XMPPropertyInfo> xmpPropertyInfos) {
        try {
            for (XMPIterator it = xmpMeta.iterator(); it.hasNext();) {
                XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) it.next();
                if (hasContent(xmpPropertyInfo)) {
                    xmpPropertyInfos.add(xmpPropertyInfo);
                }
            }
        } catch (XMPException ex) {
            de.elmar_baumann.imv.Logging.logWarning(getClass(), ex);
        }
    }

    private static boolean hasContent(XMPPropertyInfo xmpPropertyInfo) {
        return !xmpPropertyInfo.getOptions().isQualifier() &&
            xmpPropertyInfo.getPath() != null &&
            xmpPropertyInfo.getValue() != null &&
            xmpPropertyInfo.getValue().toString().length() > 0;
    }

    /**
     * Schlägt einen Filialdateinamen vor für eine Bilddatei.
     * 
     * @param  filename  Name der Bilddatei
     * @return Namensvorschlag
     */
    public static String suggestSidecarFilename(String filename) {
        String existingFilename = getSidecarFilename(filename);
        if (existingFilename != null) {
            return existingFilename;
        }
        int indexExtension = filename.lastIndexOf("."); // NOI18N
        if (indexExtension > 0) {
            return filename.substring(0, indexExtension + 1) + "xmp"; // NOI18N
        } else {
            return filename + ".xmp"; // NOI18N
        }
    }

    /**
     * Liefert den Namen einer XMP-Filialdatei, falls existent.
     * 
     * @param filename Dateiname
     * @return         Name der Filialdatei oder null, falls nicht existent
     */
    public static String getSidecarFilename(String filename) {
        int indexExtension = filename.lastIndexOf("."); // NOI18N
        if (indexExtension > 0) {
            String sidecarFilename = filename.substring(0, indexExtension + 1) + "xmp"; // NOI18N
            File sidecarFile = new File(sidecarFilename);
            if (sidecarFile.exists()) {
                return sidecarFile.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * Returns the XMP sidecar file of a file.
     * 
     * @param  file  file
     * @return sidecar file or null if not found
     */
    public static File getSidecarFile(File file) {
        String sidecarFilename = getSidecarFilename(file.getAbsolutePath());
        return sidecarFilename == null ? null : new File(sidecarFilename);
    }

    private static String getXmpAsString(String filename) {
        if (!FileUtil.existsFile(filename)) {
            return null;
        }
        String xmp = null;
        String sidecarFilename = getSidecarFilename(filename);
        if (sidecarFilename != null) {
            xmp = FileUtil.getFileAsString(sidecarFilename);
        }
        return xmp;
    }

    /**
     * Liefert, ob ein String ein bekannter Namensraum ist.
     * 
     * @param string String
     * @return       true, wenn bekannt
     */
    public static boolean isKnownNamespace(String string) {
        return knownNamespaces.contains(string);
    }

    /**
     * Liefert alle Property-Infos, die auf ein IPTC-Entry-Meta-Datum passen.
     * 
     * @param  iptcEntryMeta IPTC-Entry-Meta-Datum
     * @param  propertyInfos Beliebige Property-Infos
     * @return Gefilterte Property-Infos
     */
    public List<XMPPropertyInfo> getFilteredPropertyInfosOfIptcEntryMeta(
        IPTCEntryMeta iptcEntryMeta, List<XMPPropertyInfo> propertyInfos) {

        List<XMPPropertyInfo> filteredPropertyInfos = new ArrayList<XMPPropertyInfo>();
        IptcEntryXmpPathStartMapping mapping = IptcEntryXmpPathStartMapping.getInstance();
        String startsWith = mapping.getXmpPathStartOfIptcEntryMeta(iptcEntryMeta);

        for (XMPPropertyInfo propertyInfo : propertyInfos) {
            if (propertyInfo.getPath().startsWith(startsWith)) {
                filteredPropertyInfos.add(propertyInfo);
            }
        }
        return filteredPropertyInfos;
    }

    /**
     * Liefert, ob für eine Datei eine XMP-Filialdatei existiert.
     * 
     * @param  filename  Dateiname
     * @return true, wenn existent
     */
    public static boolean existsSidecarFile(String filename) {
        String sidecarFilename = getSidecarFilename(filename);
        if (sidecarFilename != null) {
            return new File(sidecarFilename).exists();
        }
        return false;
    }

    /**
     * Liefert, ob für eine Datei eine XMP-Filialdatei geschrieben werden kann.
     * 
     * @param  name  Dateiname
     * @return true, wenn möglich
     */
    public static boolean canWriteSidecarFile(String name) {
        if (name != null) {
            File directory = new File(name).getParentFile();
            String sidecarFilename = getSidecarFilename(name);
            if (sidecarFilename != null) {
                return new File(sidecarFilename).canWrite();
            } else if (directory != null) {
                return directory.canWrite();
            }
        }
        return false;
    }

    /**
     * Schreibt XMP-Metadaten in eine Filialdatei.
     * 
     * @param  sidecarFilename  Name der Filialdatei
     * @param  metadata         Metadaten
     * @return true bei Erfolg
     */
    public boolean writeMetadataToSidecarFile(String sidecarFilename, Xmp metadata) {
        try {
            XMPMeta xmpMeta = getXmpMetaOfSidecarFile(sidecarFilename);
            writeSidecarFileDeleteItems(xmpMeta);
            writeMetadata(xmpMeta, metadata);
            return writeSidecarFile(sidecarFilename, xmpMeta);
        } catch (XMPException ex) {
            de.elmar_baumann.imv.Logging.logWarning(getClass(), ex);
            return false;
        }
    }

    private void writeMetadata(XMPMeta xmpMeta, Xmp metadata) throws XMPException {
        Set<Column> xmpColumns = EditColumns.getColumns();
        for (Column column : xmpColumns) {
            String namespaceUri = mappingNamespaceUri.getNamespaceUriOfColumn(column);
            String propertyName = mappingName.getXmpPathStartOfColumn(column);
            writeSidecarFileSetMetadata(column, metadata,
                xmpMeta, namespaceUri, propertyName);
        }
    }

    /**
     * Schreibt Metadaten in eine XMP-Filialdatei.
     * 
     * @param  sidecarFilename  Name der Filialdatei
     * @param  textEntries      Zu schreibende Texteinträge
     * @param  deleteEmpty      true, wenn in einer existierenden XMP-Datei
     *                          Einträge gelöscht werden sollen, wenn das
     *                          zugehörige Textfeld leer ist
     * @param  append           true, wenn existierende Einträge um nicht
     *                          existierende ergänzt werden sollen und nicht
     *                          gelöscht
     * @return true bei Erfolg
     */
    public boolean writeMetadataToSidecarFile(String sidecarFilename,
        List<TextEntry> textEntries, boolean deleteEmpty, boolean append) {
        try {
            XMPMeta xmpMeta = getXmpMetaOfSidecarFile(sidecarFilename);
            writeSidecarFileDeleteItems(xmpMeta, textEntries, deleteEmpty, append);
            for (TextEntry entry : textEntries) {
                Column xmpColumn = entry.getColumn();
                String namespaceUri = mappingNamespaceUri.getNamespaceUriOfColumn(xmpColumn);
                String name = mappingName.getXmpPathStartOfColumn(xmpColumn);
                String entryText = entry.getText().trim();
                if (!entryText.isEmpty()) {
                    writeSidecarFileSetTextEntry(xmpColumn, entryText,
                        xmpMeta, namespaceUri, name);
                }
            }
            return writeSidecarFile(sidecarFilename, xmpMeta);
        } catch (XMPException ex) {
            de.elmar_baumann.imv.Logging.logWarning(getClass(), ex);
            return false;
        }
    }

    private XMPMeta getXmpMetaOfSidecarFile(String sidecarFilename) throws XMPException {
        if (FileUtil.existsFile(sidecarFilename)) {
            return XMPMetaFactory.parseFromString(FileUtil.getFileAsString(sidecarFilename));
        } else {
            return XMPMetaFactory.create();
        }
    }

    private void writeSidecarFileDeleteItems(XMPMeta xmpMeta,
        List<TextEntry> textEntries, boolean deleteEmpty, boolean append) {
        for (TextEntry textEntry : textEntries) {
            Column xmpColumn = textEntry.getColumn();
            String namespaceUri = mappingNamespaceUri.getNamespaceUriOfColumn(xmpColumn);
            String name = mappingName.getXmpPathStartOfColumn(xmpColumn);
            boolean textEntryIsEmpty = textEntry.getText().trim().isEmpty();
            if ((!textEntryIsEmpty && !append) || (textEntryIsEmpty && deleteEmpty)) {
                xmpMeta.deleteProperty(namespaceUri, name);
            }
        }
    }

    private void writeSidecarFileDeleteItems(XMPMeta xmpMeta) {
        Set<Column> xmpColumns = EditColumns.getColumns();
        for (Column column : xmpColumns) {
            String namespaceUri = mappingNamespaceUri.getNamespaceUriOfColumn(column);
            String name = mappingName.getXmpPathStartOfColumn(column);
            xmpMeta.deleteProperty(namespaceUri, name);
        }
    }

    private void writeSidecarFileSetTextEntry(Column xmpColumn, String entryText,
        XMPMeta xmpMeta, String namespaceUri, String propertyName) throws XMPException {
        if (mappingDataType.isText(xmpColumn)) {
            xmpMeta.setProperty(namespaceUri, propertyName, entryText);
        } else if (mappingDataType.isLanguageAlternative(xmpColumn)) {
            xmpMeta.setLocalizedText(namespaceUri, propertyName, "", "x-default", // NOI18N
                entryText);
        } else if (mappingDataType.isArray(xmpColumn)) {
            List<String> items = ArrayUtil.stringTokenToList(
                entryText, getArrayItemDelimiter());
            for (String item : items) {
                item = item.trim();
                if (!doesArrayItemExist(xmpMeta, namespaceUri, propertyName, item)) {
                    xmpMeta.appendArrayItem(namespaceUri, propertyName,
                        getArrayPropertyOptions(xmpColumn), item, null);
                }
            }
        }
    }

    private void writeSidecarFileSetMetadata(Column column, Xmp metadata,
        XMPMeta xmpMeta, String namespaceUri, String propertyName) throws XMPException {
        Object o = metadata.getValue(column);
        if (o != null) {
            if (o instanceof String) {
                String value = (String) o;
                if (mappingDataType.isText(column)) {
                    xmpMeta.setProperty(namespaceUri, propertyName, value);
                } else if (mappingDataType.isLanguageAlternative(column)) {
                    xmpMeta.setLocalizedText(namespaceUri, propertyName, "", "x-default", // NOI18N
                        value);
                }
            }
            if (o instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) o;
                for (String value : values) {
                    value = value.trim();
                    if (!doesArrayItemExist(xmpMeta, namespaceUri, propertyName, value)) {
                        xmpMeta.appendArrayItem(namespaceUri, propertyName,
                            getArrayPropertyOptions(column), value, null);
                    }
                }
            }
        }
    }

    private boolean doesArrayItemExist(XMPMeta xmpMeta, String namespaceUri,
        String propertyName, String item) throws XMPException {
        if (xmpMeta.doesPropertyExist(namespaceUri, propertyName)) {
            for (XMPIterator it = xmpMeta.iterator(namespaceUri, propertyName, new IteratorOptions());
                it.hasNext();) {
                XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) it.next();
                if (xmpPropertyInfo != null) {
                    Object value = xmpPropertyInfo.getValue();
                    if (value != null) {
                        String itemValue = value.toString().trim();
                        if (itemValue.equals(item)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private static PropertyOptions getArrayPropertyOptions(Column xmpColumn) {
        XmpValueType valueType = XmpColumnXmpDataTypeMapping.getInstance().
            getXmpValueTypeOfColumn(xmpColumn);
        if (valueType.equals(XmpValueType.BagText)) {
            return new PropertyOptions().setArray(true);
        } else if (valueType.equals(XmpValueType.SeqProperName)) {
            return new PropertyOptions().setArrayOrdered(true);
        } else if (valueType.equals(XmpValueType.LangAlt)) {
            return new PropertyOptions().setArrayAlternate(true);
        } else {
            assert false : valueType;
            return null;
        }
    }

    private boolean writeSidecarFile(String sidecarFilename, XMPMeta meta) {
        try {
            FileOutputStream out = new FileOutputStream(new File(sidecarFilename));
            XMPMetaFactory.serialize(meta, out,
                new SerializeOptions().setPadding(10).setOmitPacketWrapper(true));
            out.close();
            return true;
        } catch (XMPException ex) {
            de.elmar_baumann.imv.Logging.logWarning(getClass(), ex);
            return false;
        } catch (IOException ex) {
            de.elmar_baumann.imv.Logging.logWarning(getClass(), ex);
            return false;
        }
    }

    /**
     * Liefert die XMP-Daten einer Datei.
     * 
     * @param  filename  Dateiname
     * @return Daten oder null bei Fehlern
     */
    public static Xmp getXmp(String filename) {
        Xmp xmp = null;
        XmpMetadata xmpMetadata = new XmpMetadata();
        List<XMPPropertyInfo> xmpPropertyInfos = xmpMetadata.getPropertyInfosOfFile(filename);
        if (xmpPropertyInfos != null) {
            xmp = new Xmp();
            for (XMPPropertyInfo xmpPropertyInfo : xmpPropertyInfos) {
                if (xmpPropertyInfo.getPath().startsWith("dc:creator")) { // NOI18N
                    xmp.setDcCreator(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("dc:subject")) { // NOI18N
                    xmp.addDcSubject(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:SupplementalCategories")) { // NOI18N
                    xmp.addPhotoshopSupplementalCategory(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("dc:description")) { // NOI18N
                    xmp.setDcDescription(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("dc:rights")) { // NOI18N
                    xmp.setDcRights(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("dc:title")) { // NOI18N
                    xmp.setDcTitle(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("Iptc4xmpCore:CountryCode")) { // NOI18N
                    xmp.setIptc4xmpcoreCountrycode(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("Iptc4xmpCore:Location")) { // NOI18N
                    xmp.setIptc4xmpcoreLocation(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:AuthorsPosition")) { // NOI18N
                    xmp.setPhotoshopAuthorsposition(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:CaptionWriter")) { // NOI18N
                    xmp.setPhotoshopCaptionwriter(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:Category")) { // NOI18N
                    xmp.setPhotoshopCategory(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:City")) { // NOI18N
                    xmp.setPhotoshopCity(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:Country")) { // NOI18N
                    xmp.setPhotoshopCountry(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:Credit")) { // NOI18N
                    xmp.setPhotoshopCredit(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:Headline")) { // NOI18N
                    xmp.setPhotoshopHeadline(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:Instructions")) { // NOI18N
                    xmp.setPhotoshopInstructions(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:Source")) { // NOI18N
                    xmp.setPhotoshopSource(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:State")) { // NOI18N
                    xmp.setPhotoshopState(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:TransmissionReference")) { // NOI18N
                    xmp.setPhotoshopTransmissionReference(xmpPropertyInfo.getValue().toString());
                }
            }
            setLastModified(xmp, filename);
        }
        return xmp;
    }

    private static void setLastModified(Xmp xmp, String filename) {
        String xmpFilename = getSidecarFilename(filename);
        if (xmpFilename != null) {
            xmp.setLastModified(FileUtil.getLastModified(xmpFilename));
        }
    }
}
