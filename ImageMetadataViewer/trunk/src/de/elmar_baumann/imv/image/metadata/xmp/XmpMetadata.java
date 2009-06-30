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
import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.data.TextEntry;
import de.elmar_baumann.imv.data.Xmp;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.database.metadata.mapping.IptcEntryXmpPathStartMapping;
import de.elmar_baumann.imv.database.metadata.mapping.XmpColumnNamespaceUriMapping;
import de.elmar_baumann.imv.database.metadata.mapping.XmpColumnXmpDataTypeMapping;
import de.elmar_baumann.imv.database.metadata.mapping.XmpColumnXmpDataTypeMapping.XmpValueType;
import de.elmar_baumann.imv.database.metadata.mapping.XmpColumnXmpPathStartMapping;
import de.elmar_baumann.imv.database.metadata.selections.EditColumns;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.image.metadata.xmp.XmpFileReader;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.template.Pair;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Gets and sets XMP metadata from image files and XMP sidecar files and to
 * XMP sidecar files.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008/10/05
 */
public final class XmpMetadata {

    private static final List<String> knownNamespaces = new ArrayList<String>();
    private static final String XMP_TOKEN_DELIMITER = "\\"; // NOI18N;


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
     * Options when updating XMP metadata in a sidecar file.
     */
    public enum UpdateOption {

        /**
         * Not existing values shall be <strong>added</strong> to existing
         * repeatable values
         */
        APPEND_TO_REPEATABLE_VALUES,
        /**
         * Delete existing values if the source does not contain corresponding
         * values. E.g. if a sidecar file has a Dublin Core description and the
         * metadata to set hasn't a dc description the dc description in the
         * sidecar file is to delete.
         */
        DELETE_IF_SOURCE_VALUE_IS_EMPTY
    }

    /**
     * Location of the XMP metadata.
     */
    private enum XmpLocation {

        /** The XMP metadta is embedded into the data file */
        EMBEDDED,
        /** The XMP metadta exists in a sidecar file */
        SIDECAR_FILE
    }

    /**
     * Returns the delimiter between multiple XMP tokens.
     * 
     * @return delimiter
     */
    public static final String getXmpTokenDelimiter() {
        return XMP_TOKEN_DELIMITER;
    }

    /**
     * Returns all {@link XMPPropertyInfo}s of a specific namespace.
     * 
     * @param propertyInfos XMP property infos of arbitrary namespaces
     * @param namespace     namespace
     * @return              property infos of <code>propertyInfos</code>
     *                      matching that namespace
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
     * Returns XMP metadata of an image file.
     * 
     * @param imageFilename name of the image file
     * @return              Metadata or null if the image file has no XMP
     *                      metadata or on errors while reading
     */
    public static List<XMPPropertyInfo> getPropertyInfosOfImageFile(
            String imageFilename) {
        if (imageFilename == null || !FileUtil.existsFile(imageFilename)) {
            return null;
        }
        return getPropertyInfosOfXmpString(getXmpAsStringFromImageFile(
                imageFilename));
    }

    private static List<XMPPropertyInfo> getPropertyInfosOfXmpString(String xmp) {
        List<XMPPropertyInfo> metadata = new ArrayList<XMPPropertyInfo>();
        try {
            if (xmp != null && xmp.length() > 0) {
                XMPMeta xmpMeta = XMPMetaFactory.parseFromString(xmp);
                if (xmpMeta != null) {
                    addXmpPropertyInfo(xmpMeta, metadata);
                }
            }
        } catch (XMPException ex) {
            metadata = null;
            AppLog.logWarning(XmpMetadata.class, ex);
        } catch (Exception ex) {
            metadata = null;
            AppLog.logWarning(XmpMetadata.class, ex);
        }
        return metadata;
    }

    private static void addXmpPropertyInfo(XMPMeta xmpMeta,
            List<XMPPropertyInfo> xmpPropertyInfos) {
        try {
            for (XMPIterator it = xmpMeta.iterator(); it.hasNext();) {
                XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) it.next();
                if (hasContent(xmpPropertyInfo)) {
                    xmpPropertyInfos.add(xmpPropertyInfo);
                }
            }
        } catch (XMPException ex) {
            AppLog.logWarning(XmpMetadata.class, ex);
        }
    }

    private static boolean hasContent(XMPPropertyInfo xmpPropertyInfo) {
        return !xmpPropertyInfo.getOptions().isQualifier() &&
                xmpPropertyInfo.getPath() != null &&
                xmpPropertyInfo.getValue() != null &&
                xmpPropertyInfo.getValue().toString().length() > 0;
    }

    /**
     * Suggests a sidecar filename for an image. If the image already has a
     * sidecar file it's name will be returned. If the image does'nt have a
     * sidecar file, the sidecar file has the same name as the image file with
     * exception of the suffix: it's <code>xmp</code>. When the image file has
     * the name <code>dog.jpg</code> the sidecar name is <code>dog.xmp</code>.
     * 
     * @param  imageFilename name of the image file
     * @return               sidecar filename
     */
    public static String suggestSidecarFilenameForImageFile(String imageFilename) {
        String existingFilename =
                getSidecarFilenameOfImageFileIfExists(imageFilename);
        if (existingFilename != null) {
            return existingFilename;
        }
        int indexExtension = imageFilename.lastIndexOf("."); // NOI18N
        if (indexExtension > 0) {
            return imageFilename.substring(0, indexExtension + 1) + "xmp"; // NOI18N
        } else {
            return imageFilename + ".xmp"; // NOI18N
        }
    }

    /**
     * Returns the name of an image's sidecar file (only) if the image has
     * a sidecar file.
     * 
     * @param imageFilename image filename
     * @return              name of the sidecar file or null, if the image
     *                      hasn't a sidecar file
     */
    public static String getSidecarFilenameOfImageFileIfExists(
            String imageFilename) {
        int indexExtension = imageFilename.lastIndexOf("."); // NOI18N
        if (indexExtension > 0) {
            String sidecarFilename = imageFilename.substring(0, indexExtension +
                    1) +
                    "xmp"; // NOI18N
            File sidecarFile = new File(sidecarFilename);
            if (sidecarFile.exists()) {
                return sidecarFile.getAbsolutePath();
            }
        }
        return null;
    }

    /**
     * Returns the XMP sidecar file of an image file (only) if the image has a
     * sidecar file.
     * 
     * @param  imageFile image file
     * @return           sidecar file or null if the image hasn't a sidecar file
     */
    public static File getSidecarFileOfImageFileIfExists(File imageFile) {
        String sidecarFilename = getSidecarFilenameOfImageFileIfExists(
                imageFile.getAbsolutePath());
        return sidecarFilename == null
               ? null
               : new File(sidecarFilename);
    }

    private static String getXmpAsStringFromImageFile(String imageFilename) {
        if (!FileUtil.existsFile(imageFilename)) {
            return null;
        }
        String xmpString = null;
        String sidecarFilename =
                getSidecarFilenameOfImageFileIfExists(imageFilename);
        if (sidecarFilename == null) {
            AppLog.logInfo(XmpMetadata.class, Bundle.getString(
                    "XmpMetadata.Info.ReadEmbeddedXmp", imageFilename)); // NOI18N
            xmpString = XmpFileReader.readFile(imageFilename);
        } else {
            AppLog.logInfo(XmpMetadata.class, Bundle.getString(
                    "XmpMetadata.Info.ReadSidecarFile", // NOI18N
                    sidecarFilename, imageFilename));
            xmpString = FileUtil.getFileAsString(sidecarFilename);
        }
        return xmpString;
    }

    /**
     * Returns wheter a string is a known XMP namespace.
     * 
     * @param string string
     * @return       true if that string is a known namespace
     */
    public static boolean isKnownNamespace(String string) {
        return knownNamespaces.contains(string);
    }

    /**
     * Returns all {@link XMPPropertyInfo}s matching a {@link IPTCEntryMeta}.
     * 
     * @param  iptcEntryMeta IPTC entry metadata
     * @param  propertyInfos arbitrary property infos
     * @return               property infos of <code>propertyInfos</code>
     *                       matching that metadata
     */
    public static List<XMPPropertyInfo> getFilteredPropertyInfosOfIptcEntryMeta(
            IPTCEntryMeta iptcEntryMeta, List<XMPPropertyInfo> propertyInfos) {

        List<XMPPropertyInfo> filteredPropertyInfos =
                new ArrayList<XMPPropertyInfo>();
        String startsWith = IptcEntryXmpPathStartMapping.
                getXmpPathStartOfIptcEntryMeta(iptcEntryMeta);

        for (XMPPropertyInfo propertyInfo : propertyInfos) {
            if (propertyInfo.getPath().startsWith(startsWith)) {
                filteredPropertyInfos.add(propertyInfo);
            }
        }
        return filteredPropertyInfos;
    }

    /**
     * Returns whether an image has a sidecar file and the sidecar file exists
     * in the same directory as the image file.
     * 
     * @param  imageFilename name of the image file
     * @return               true if the image has a sidecar file
     */
    public static boolean hasImageASidecarFile(String imageFilename) {
        String sidecarFilename = getSidecarFilenameOfImageFileIfExists(
                imageFilename);
        return sidecarFilename == null
               ? false
               : new File(sidecarFilename).exists();
    }

    /**
     * Returns whether a sidecar file can be written to the file system. This
     * is true if the sidecar file exists an is writable or if it does not
     * exist and the directory is writable.
     * 
     * @param  imageFilename  name of the image file
     * @return                true if possible
     */
    public static boolean canWriteSidecarFileForImageFile(String imageFilename) {
        if (imageFilename != null) {
            File directory = new File(imageFilename).getParentFile();
            String sidecarFilename = getSidecarFilenameOfImageFileIfExists(
                    imageFilename);
            if (sidecarFilename != null) {
                return new File(sidecarFilename).canWrite();
            } else if (directory != null) {
                return directory.canWrite();
            }
        }
        return false;
    }

    /**
     * Writes the values of a {@link Xmp} instance as or into a XMP sidecar
     * file.
     * 
     * @param  sidecarFilename  name of the sidecar file
     * @param  metadata         metadata
     * @return                  true if successfully written
     */
    public static boolean writeMetadataToSidecarFile(
            String sidecarFilename, Xmp metadata) {
        try {
            XMPMeta xmpMeta = getXmpMetaOfSidecarFile(sidecarFilename);
            writeSidecarFileDeleteItems(xmpMeta);
            writeMetadata(xmpMeta, metadata);
            return writeSidecarFile(sidecarFilename, xmpMeta);
        } catch (XMPException ex) {
            AppLog.logWarning(XmpMetadata.class, ex);
            return false;
        }
    }

    private static void writeMetadata(XMPMeta xmpMeta, Xmp metadata) throws
            XMPException {
        Set<Column> xmpColumns = EditColumns.getColumns();
        for (Column column : xmpColumns) {
            String namespaceUri = XmpColumnNamespaceUriMapping.
                    getNamespaceUriOfColumn(column);
            String propertyName = XmpColumnXmpPathStartMapping.
                    getXmpPathStartOfColumn(column);
            writeSidecarFileSetMetadata(column, metadata,
                    xmpMeta, namespaceUri, propertyName);
        }
    }

    /**
     * Writes the values of a {@link TextEntry} instance into a sidecar file.
     * 
     * @param  sidecarFilename  name of the sidecar file
     * @param  textEntries      text entries to write
     * @param  writeOptions     options
     * @return                  true when successfully written
     */
    public static boolean writeMetadataToSidecarFile(String sidecarFilename,
            List<TextEntry> textEntries, EnumSet<UpdateOption> writeOptions) {
        try {
            XMPMeta xmpMeta = getXmpMetaOfSidecarFile(sidecarFilename);
            writeSidecarFileDeleteItems(xmpMeta, textEntries, writeOptions);
            for (TextEntry entry : textEntries) {
                Column xmpColumn = entry.getColumn();
                String namespaceUri = XmpColumnNamespaceUriMapping.
                        getNamespaceUriOfColumn(xmpColumn);
                String name = XmpColumnXmpPathStartMapping.
                        getXmpPathStartOfColumn(xmpColumn);
                String entryText = entry.getText().trim();
                if (!entryText.isEmpty()) {
                    writeSidecarFileSetTextEntry(xmpColumn, entryText,
                            xmpMeta, namespaceUri, name);
                }
            }
            return writeSidecarFile(sidecarFilename, xmpMeta);
        } catch (XMPException ex) {
            AppLog.logWarning(XmpMetadata.class, ex);
            return false;
        }
    }

    private static XMPMeta getXmpMetaOfSidecarFile(String sidecarFilename)
            throws XMPException {
        if (FileUtil.existsFile(sidecarFilename)) {
            return XMPMetaFactory.parseFromString(FileUtil.getFileAsString(
                    sidecarFilename));
        } else {
            return XMPMetaFactory.create();
        }
    }

    private static void writeSidecarFileDeleteItems(XMPMeta xmpMeta,
            List<TextEntry> textEntries, EnumSet<UpdateOption> options) {
        for (TextEntry textEntry : textEntries) {
            Column xmpColumn = textEntry.getColumn();
            String namespaceUri = XmpColumnNamespaceUriMapping.
                    getNamespaceUriOfColumn(xmpColumn);
            String name = XmpColumnXmpPathStartMapping.getXmpPathStartOfColumn(
                    xmpColumn);
            boolean textEntryIsEmpty = textEntry.getText().trim().isEmpty();
            boolean deleteProperty =
                    (!textEntryIsEmpty && !options.contains(
                    UpdateOption.APPEND_TO_REPEATABLE_VALUES)) // !textEntryIsEmpty: empty must not be deleted
                    || (textEntryIsEmpty && options.contains(
                    UpdateOption.DELETE_IF_SOURCE_VALUE_IS_EMPTY));
            if (deleteProperty) {
                xmpMeta.deleteProperty(namespaceUri, name);
            }
        }
    }

    private static void writeSidecarFileDeleteItems(XMPMeta xmpMeta) {
        Set<Column> xmpColumns = EditColumns.getColumns();
        for (Column column : xmpColumns) {
            String namespaceUri = XmpColumnNamespaceUriMapping.
                    getNamespaceUriOfColumn(column);
            String name = XmpColumnXmpPathStartMapping.getXmpPathStartOfColumn(
                    column);
            xmpMeta.deleteProperty(namespaceUri, name);
        }
    }

    private static void writeSidecarFileSetTextEntry(Column xmpColumn,
            String entryText, XMPMeta xmpMeta, String namespaceUri,
            String propertyName) throws XMPException {
        if (XmpColumnXmpDataTypeMapping.isText(xmpColumn)) {
            xmpMeta.setProperty(namespaceUri, propertyName, entryText);
        } else if (XmpColumnXmpDataTypeMapping.isLanguageAlternative(xmpColumn)) {
            xmpMeta.setLocalizedText(namespaceUri, propertyName, "", "x-default", // NOI18N
                    entryText);
        } else if (XmpColumnXmpDataTypeMapping.isArray(xmpColumn)) {
            List<String> items = ArrayUtil.stringTokenToList(
                    entryText, getXmpTokenDelimiter());
            for (String item : items) {
                item = item.trim();
                if (!doesArrayItemExist(xmpMeta, namespaceUri, propertyName,
                        item)) {
                    xmpMeta.appendArrayItem(namespaceUri, propertyName,
                            getArrayPropertyOptions(xmpColumn), item, null);
                }
            }
        }
    }

    private static void writeSidecarFileSetMetadata(Column column, Xmp metadata,
            XMPMeta xmpMeta, String namespaceUri, String propertyName)
            throws XMPException {
        Object o = metadata.getValue(column);
        if (o != null) {
            if (o instanceof String) {
                String value = (String) o;
                if (XmpColumnXmpDataTypeMapping.isText(column)) {
                    xmpMeta.setProperty(namespaceUri, propertyName, value);
                } else if (XmpColumnXmpDataTypeMapping.isLanguageAlternative(
                        column)) {
                    xmpMeta.setLocalizedText(namespaceUri, propertyName, "", // NOI18N
                            "x-default", // NOI18N
                            value);
                }
            } else if (o instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) o;
                for (String value : values) {
                    value = value.trim();
                    if (!doesArrayItemExist(
                            xmpMeta, namespaceUri, propertyName, value)) {
                        xmpMeta.appendArrayItem(namespaceUri, propertyName,
                                getArrayPropertyOptions(column), value, null);
                    }
                }
            } else {
                AppLog.logWarning(XmpMetadata.class, Bundle.getString(
                        "XmpMetadata.ErrorMessage.WriteSetMetadata") + o. // NOI18N
                        toString());
            }
        }
    }

    private static boolean doesArrayItemExist(XMPMeta xmpMeta,
            String namespaceUri, String propertyName, String item)
            throws XMPException {
        if (xmpMeta.doesPropertyExist(namespaceUri, propertyName)) {
            for (XMPIterator it = xmpMeta.iterator(namespaceUri, propertyName,
                    new IteratorOptions());
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
        XmpValueType valueType = XmpColumnXmpDataTypeMapping.
                getXmpValueTypeOfColumn(xmpColumn);
        if (valueType.equals(XmpValueType.BAG_TEXT)) {
            return new PropertyOptions().setArray(true);
        } else if (valueType.equals(XmpValueType.SEQ_PROPER_NAME)) {
            return new PropertyOptions().setArrayOrdered(true);
        } else if (valueType.equals(XmpValueType.LANG_ALT)) {
            return new PropertyOptions().setArrayAlternate(true);
        } else {
            assert false : valueType;
            return null;
        }
    }

    private static boolean writeSidecarFile(String sidecarFilename, XMPMeta meta) {
        try {
            FileOutputStream out = new FileOutputStream(
                    new File(sidecarFilename));
            XMPMetaFactory.serialize(meta, out,
                    new SerializeOptions().setPadding(10).setOmitPacketWrapper(
                    true));
            out.close();
            return true;
        } catch (XMPException ex) {
            AppLog.logWarning(XmpMetadata.class, ex);
            return false;
        } catch (IOException ex) {
            AppLog.logWarning(XmpMetadata.class, ex);
            return false;
        }
    }

    /**
     * Returns XMP metadata of a image file. If the image has a sidecar file,
     * it's metadata will be read. If the image hasn't a sidecar file but
     * embedded XMP metadata, the embedded XMP metadata will be read.
     * 
     * @param  imageFilename name of the image file
     * @return               XMP metadata of the image file or null if the image
     *                       file has no XMP metadata or on errors while reading
     *                       the file
     */
    public static Xmp getXmpOfImageFile(String imageFilename) {
        XmpLocation xmpType = hasImageASidecarFile(imageFilename)
                              ? XmpLocation.SIDECAR_FILE
                              : XmpLocation.EMBEDDED;
        return getXmp(xmpType, imageFilename,
                getPropertyInfosOfImageFile(imageFilename));
    }

    /**
     * Returns XMP embedded in the image file.
     *
     * @param   imageFilename name of the image file
     * @return                embedded XMP metadata or null if no XMP is
     *                        embedded or when errors occur while reading the
     *                        file
     */
    public static Xmp getEmbeddedXmp(String imageFilename) {
        String xmpString = getXmpAsStringFromImageFile(imageFilename);
        return xmpString == null
               ? null
               : getXmp(XmpLocation.EMBEDDED, imageFilename,
                getPropertyInfosOfXmpString(xmpString));
    }

    private static Xmp getXmp(XmpLocation xmpType, String imageFilename,
            List<XMPPropertyInfo> xmpPropertyInfos) {
        Xmp xmp = null;
        if (xmpPropertyInfos != null) {
            xmp = new Xmp();
            for (XMPPropertyInfo xmpPropertyInfo : xmpPropertyInfos) {
                if (xmpPropertyInfo.getPath().startsWith("dc:creator")) { // NOI18N
                    xmp.setDcCreator(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("dc:subject")) { // NOI18N
                    xmp.addDcSubject(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:SupplementalCategories")) { // NOI18N
                    xmp.addPhotoshopSupplementalCategory(xmpPropertyInfo.
                            getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("dc:description")) { // NOI18N
                    xmp.setDcDescription(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("dc:rights")) { // NOI18N
                    xmp.setDcRights(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("dc:title")) { // NOI18N
                    xmp.setDcTitle(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "Iptc4xmpCore:CountryCode")) { // NOI18N
                    xmp.setIptc4xmpcoreCountrycode(xmpPropertyInfo.getValue().
                            toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "Iptc4xmpCore:Location")) { // NOI18N
                    xmp.setIptc4xmpcoreLocation(xmpPropertyInfo.getValue().
                            toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:AuthorsPosition")) { // NOI18N
                    xmp.setPhotoshopAuthorsposition(xmpPropertyInfo.getValue().
                            toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:CaptionWriter")) { // NOI18N
                    xmp.setPhotoshopCaptionwriter(xmpPropertyInfo.getValue().
                            toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:Category")) { // NOI18N
                    xmp.setPhotoshopCategory(
                            xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith("photoshop:City")) { // NOI18N
                    xmp.setPhotoshopCity(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:Country")) { // NOI18N
                    xmp.setPhotoshopCountry(
                            xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:Credit")) { // NOI18N
                    xmp.setPhotoshopCredit(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:Headline")) { // NOI18N
                    xmp.setPhotoshopHeadline(
                            xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:Instructions")) { // NOI18N
                    xmp.setPhotoshopInstructions(xmpPropertyInfo.getValue().
                            toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:Source")) { // NOI18N
                    xmp.setPhotoshopSource(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:State")) { // NOI18N
                    xmp.setPhotoshopState(xmpPropertyInfo.getValue().toString());
                } else if (xmpPropertyInfo.getPath().startsWith(
                        "photoshop:TransmissionReference")) { // NOI18N
                    xmp.setPhotoshopTransmissionReference(xmpPropertyInfo.
                            getValue().toString());
                }
            }
            setLastModified(xmpType, xmp, imageFilename);
        }
        return xmp;
    }

    private static void setLastModified(
            XmpLocation xmpType, Xmp xmp, String imageFilename) {
        String sidecarFilename =
                getSidecarFilenameOfImageFileIfExists(imageFilename);
        if (xmpType.equals(XmpLocation.SIDECAR_FILE) && sidecarFilename != null) {
            xmp.setLastModified(FileUtil.getLastModified(sidecarFilename));
        } else if (xmpType.equals(XmpLocation.EMBEDDED) && FileUtil.existsFile(
                imageFilename)) {
            xmp.setLastModified(FileUtil.getLastModified(imageFilename));
        }
    }

    /**
     * Returns pairs of image files and their sidecar files.
     *
     * @param  imageFiles image files
     * @return            pairs of image files with their corresponding sidecar
     *                    files.
     *                    {@link de.elmar_baumann.lib.template.Pair#getFirst()}
     *                    returns a reference to an image file object in the
     *                    <code>imageFiles</code> list.
     *                    {@link de.elmar_baumann.lib.template.Pair#getSecond()}
     *                    returns the sidecar file of the referenced image file
     *                    or null if the image file has no sidecar file.
     */
    public static List<Pair<File, File>> getImageFilesWithSidecarFiles(
            List<File> imageFiles) {
        List<Pair<File, File>> filePairs = new ArrayList<Pair<File, File>>();
        for (File imageFile : imageFiles) {
            filePairs.add(new Pair<File, File>(imageFile, getSidecarFileOfImageFileIfExists(
                    imageFile)));
        }
        return filePairs;
    }

    private XmpMetadata() {
    }
}
