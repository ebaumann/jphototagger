package org.jphototagger.xmp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.options.PropertyOptions;
import com.adobe.xmp.options.SerializeOptions;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.imagero.reader.iptc.IPTCEntryMeta;

import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.mapping.IptcEntryXmpPathStartMapping;
import org.jphototagger.domain.metadata.mapping.XmpMetaDataValueXmpArrayNameMapping;
import org.jphototagger.domain.metadata.mapping.XmpMetaDataValueXmpValueTypeMapping;
import org.jphototagger.domain.metadata.mapping.XmpMetaDataValueXmpValueTypeMapping.XmpValueType;
import org.jphototagger.domain.metadata.mapping.XmpMetaDataValuesNamespaceUriMapping;
import org.jphototagger.domain.metadata.xmp.XmpLastModifiedMetaDataValue;
import org.jphototagger.domain.repository.xmp.XmpToSaveInRepository;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.lib.io.FileLock;
import org.jphototagger.lib.io.FileUtil;

/**
 * Gets and sets XMP metadata from image files and XMP sidecar files and to
 * XMP sidecar files.
 *
 * @author Elmar Baumann, Tobias Stening
 */
public final class XmpMetadata {

    private static final Logger LOGGER = Logger.getLogger(XmpMetadata.class.getName());
    private static final List<String> KNOWN_NAMESPACES = new ArrayList<String>();

    static {
        KNOWN_NAMESPACES.add("Iptc4xmpCore");
        KNOWN_NAMESPACES.add("aux");
        KNOWN_NAMESPACES.add("crs");
        KNOWN_NAMESPACES.add("dc");
        KNOWN_NAMESPACES.add("exif");
        KNOWN_NAMESPACES.add("lr");
        KNOWN_NAMESPACES.add("photoshop");
        KNOWN_NAMESPACES.add("tiff");
        KNOWN_NAMESPACES.add("xap");
        KNOWN_NAMESPACES.add("xapRights");
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

    private XmpMetadata() {
    }

    /**
     * Returns all {@code XMPPropertyInfo}s of a specific namespace.
     *
     * @param propertyInfos XMP property infos of arbitrary namespaces
     * @param namespace     namespace
     * @return              property infos of <code>propertyInfos</code>
     *                      matching that namespace
     */
    public static List<XMPPropertyInfo> filterPropertyInfosOfNamespace(List<XMPPropertyInfo> propertyInfos,
            String namespace) {
        if (propertyInfos == null) {
            throw new NullPointerException("propertyInfos == null");
        }

        if (namespace == null) {
            throw new NullPointerException("namespace == null");
        }

        List<XMPPropertyInfo> propertyInfosNs = new ArrayList<XMPPropertyInfo>();

        for (XMPPropertyInfo propertyInfo : propertyInfos) {
            if (propertyInfo.getNamespace().equals(namespace)) {
                propertyInfosNs.add(propertyInfo);
            }
        }

        return propertyInfosNs;
    }

    /**
     * Returns XMP metadata embedded in an image file.
     *
     * @param imageFile image file or null
     * @return          Metadata or null if the image file has no XMP metadata
     *                  or on errors while reading
     */
    static List<XMPPropertyInfo> getEmbeddedPropertyInfos(File imageFile) {
        if ((imageFile == null) || !imageFile.exists()) {
            return null;
        }

        return getPropertyInfosOfXmpString(getEmbeddedXmpAsString(imageFile));
    }

    /**
     * Returns XMP metadata of a sidecar file.
     *
     * @param sidecarFile sidecar file. Can be null.
     * @return            Metadata or null if the sidecar file is null or on
     *                    errors while reading
     * @throws IOException
     */
    public static List<XMPPropertyInfo> getPropertyInfosOfSidecarFile(File sidecarFile) throws IOException {
        if ((sidecarFile == null) || !sidecarFile.exists()) {
            return null;
        }

        return getPropertyInfosOfXmpString(getXmpAsStringOfSidecarFile(sidecarFile));
    }

    /**
     *
     * @param xmpAsString can be null
     * @return l
     */
    public static List<XMPPropertyInfo> getPropertyInfosOfXmpString(String xmpAsString) {
        List<XMPPropertyInfo> propertyInfos = new ArrayList<XMPPropertyInfo>();

        try {
            if ((xmpAsString != null) && (xmpAsString.length() > 0)) {
                XMPMeta xmpMeta = XMPMetaFactory.parseFromString(xmpAsString);

                if (xmpMeta != null) {
                    addXmpPropertyInfosTo(xmpMeta, propertyInfos);
                }
            }
        } catch (Exception ex) {
            propertyInfos = null;
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return propertyInfos;
    }

    /**
     * Adds XMP property infos of XMP metadata to a list of property infos.
     *
     * @param toXmpMeta          XMP metadata
     * @param toXmpPropertyInfos list of property infos to retrieve the
     *                           properties from XMP metadata
     */
    private static void addXmpPropertyInfosTo(XMPMeta fromXmpMeta, List<XMPPropertyInfo> toXmpPropertyInfos) {
        try {
            for (XMPIterator it = fromXmpMeta.iterator(); it.hasNext();) {
                XMPPropertyInfo xmpPropertyInfo = (XMPPropertyInfo) it.next();

                if (hasContent(xmpPropertyInfo)) {
                    toXmpPropertyInfos.add(xmpPropertyInfo);
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private static boolean hasContent(XMPPropertyInfo xmpPropertyInfo) {
        return !xmpPropertyInfo.getOptions().isQualifier() && (xmpPropertyInfo.getPath() != null)
                && (xmpPropertyInfo.getValue() != null) && (xmpPropertyInfo.getValue().toString().length() > 0);
    }

    /**
     * Suggests a sidecar file for an image.
     * <p>
     * If the image already has a sidecar file it will be returned. If the image
     * doesn't have a sidecar file, the a sidecar file will be created with the
     * same name as the image file with exception of the suffix: it's
     * <code>xmp</code>. If the image file has the name <code>dog.jpg</code>,
     * the sidecar file name is <code>dog.xmp</code>.
     *
     * @param  imageFile image file or null
     * @return           sidecar file
     */
    public static File suggestSidecarFile(File imageFile) {
        File existingFile = getSidecarFile(imageFile);

        if (existingFile != null) {
            return existingFile;
        }

        String absolutePath = imageFile.getAbsolutePath();
        int indexExtension = absolutePath.lastIndexOf('.');

        if (indexExtension > 0) {
            return new File(absolutePath.substring(0, indexExtension + 1) + "xmp");
        } else {
            return new File(absolutePath + ".xmp");
        }
    }

    /**
     * Returns an image's sidecar file (only) if the image has a sidecar file.
     *
     * @param imageFile image file or null
     * @return          sidecar file or null, if the image doesn't have a
     *                  sidecar file
     */
    public static File getSidecarFile(File imageFile) {
        if (imageFile == null) {
            return null;
        }

        String absolutePath = imageFile.getAbsolutePath();
        int indexExtension = absolutePath.lastIndexOf('.');

        // > 0: ".suffix" is not a file of type "suffix"
        if (indexExtension > 0) {
            File sidecarFile = new File(absolutePath.substring(0, indexExtension + 1) + "xmp");

            if (sidecarFile.exists() && sidecarFile.isFile()) {
                return sidecarFile;
            }
        }

        return null;
    }

    /**
     *
     * @param  imageFile can be null
     * @return           may be null
     */
    public static String getEmbeddedXmpAsString(File imageFile) {
        if ((imageFile == null) || !imageFile.exists()) {
            return null;
        }

        LOGGER.log(Level.INFO, "Reading embedded XMP from image file ''{0}'', size {1} Bytes", new Object[]{imageFile, imageFile.length()});

        return XmpFileReader.readFile(imageFile);
    }

    public static String getXmpAsStringOfSidecarFile(File sidecarFile) throws IOException {
        if ((sidecarFile == null) || !sidecarFile.exists()) {
            return null;
        }

        LOGGER.log(Level.INFO, "Reading XMP metadata in sidecar file ''{0}''", sidecarFile);

        return FileUtil.getContentAsString(sidecarFile, "UTF-8");
    }

    /**
     * Returns wheter a string is a known XMP namespace.
     *
     * @param string string
     * @return       true if that string is a known namespace
     */
    public static boolean isKnownNamespace(String string) {
        if (string == null) {
            throw new NullPointerException("string == null");
        }

        return KNOWN_NAMESPACES.contains(string);
    }

    /**
     * Returns all {@code XMPPropertyInfo}s matching a {@code IPTCEntryMeta}.
     *
     * @param  matchingIptcEntryMeta IPTC entry metadata
     * @param  propertyInfos      arbitrary property infos
     * @return                    property infos of <code>propertyInfos</code>
     *                            matching that metadata
     */
    public static List<XMPPropertyInfo> filterPropertyInfosOfIptcEntryMeta(List<XMPPropertyInfo> propertyInfos,
            IPTCEntryMeta matchingIptcEntryMeta) {
        if (propertyInfos == null) {
            throw new NullPointerException("propertyInfos == null");
        }

        if (matchingIptcEntryMeta == null) {
            throw new NullPointerException("matchingIptcEntryMeta == null");
        }

        List<XMPPropertyInfo> filteredPropertyInfos = new ArrayList<XMPPropertyInfo>();
        String startsWith = IptcEntryXmpPathStartMapping.getXmpPathStartOfIptcEntryMeta(matchingIptcEntryMeta);

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
     * @param  imageFile image file or null
     * @return           true if the image has a sidecar file
     */
    public static boolean hasImageASidecarFile(File imageFile) {
        return getSidecarFile(imageFile) != null;
    }

    /**
     * Returns whether a sidecar file can be written to the file system.
     * <p>
     * This is true if the sidecar file exists and is writable or if it does not
     * exist and the directory is writable.
     *
     * @param  imageFile image file or null
     * @return           true if possible
     */
    public static boolean canWriteSidecarFileForImageFile(File imageFile) {
        if (imageFile != null) {
            File directory = imageFile.getParentFile();
            File sidecarFile = getSidecarFile(imageFile);

            if (sidecarFile != null) {
                return sidecarFile.canWrite();
            } else if (directory != null) {
                return directory.canWrite();
            }
        }

        return false;
    }

    /**
     * Writes the values of a {@code Xmp} instance as or into a XMP sidecar
     * file.
     *
     * @param  fromXmp       XMP metadata
     * @param  toSidecarFile sidecar file
     * @return               true if successfully written
     */
    public static boolean writeXmpToSidecarFile(Xmp fromXmp, File toSidecarFile) {
        if (fromXmp == null) {
            throw new NullPointerException("fromXmp == null");
        }

        if (toSidecarFile == null) {
            throw new NullPointerException("toSidecarFile == null");
        }

        try {
            XMPMeta toXmpMeta = getXmpMetaOfSidecarFile(toSidecarFile);

            deleteAllEditableMetadataFrom(toXmpMeta);
            setMetadata(fromXmp, toXmpMeta);

            return writeSidecarFile(toXmpMeta, toSidecarFile);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);

            return false;
        }
    }

    private static void setMetadata(Xmp fromXmp, XMPMeta toXmpMeta) throws XMPException {
        for (MetaDataValue metaDataValue : EditMetaDataValues.get()) {
            String namespaceUri = XmpMetaDataValuesNamespaceUriMapping.getNamespaceUriOfXmpMetaDataValue(metaDataValue);
            String arrayName = XmpMetaDataValueXmpArrayNameMapping.getXmpArrayNameOfXmpMetaDataValue(metaDataValue);

            copyMetadata(fromXmp, toXmpMeta, metaDataValue, namespaceUri, arrayName);
        }
    }

    private static XMPMeta getXmpMetaOfSidecarFile(File sidecarFile) throws XMPException, IOException {
        if (sidecarFile.exists()) {
            String xmp = FileUtil.getContentAsString(sidecarFile, "UTF-8");

            if ((xmp != null) && !xmp.trim().isEmpty()) {
                return XMPMetaFactory.parseFromString(xmp);
            }
        }

        return XMPMetaFactory.create();
    }

    /**
     * Deletes from an <code>XMPMeta</code> instance all data an user can edit.
     *
     * @param toXmpMeta XMP metadata
     */
    private static void deleteAllEditableMetadataFrom(XMPMeta xmpMeta) {
        List<MetaDataValue> editableXmpMetaDataValues = EditMetaDataValues.get();

        for (MetaDataValue editableValue : editableXmpMetaDataValues) {
            String namespaceUri = XmpMetaDataValuesNamespaceUriMapping.getNamespaceUriOfXmpMetaDataValue(editableValue);
            String propertyName = XmpMetaDataValueXmpArrayNameMapping.getXmpArrayNameOfXmpMetaDataValue(editableValue);

            xmpMeta.deleteProperty(namespaceUri, propertyName);
        }
    }

    /**
     * Sets metadata of a {@code Xmp} instance to a {@code XMPMeta} instance.
     *
     * @param mdValue        part of data to set
     * @param fromXmp       <code>Xmp</code> metadata to set from
     * @param toXmpMeta     <code>XMPMeta</code> metadata to set to
     * @param namespaceUri  URI of namespase in <code>XMPMeta</code> to set
     * @param propertyName     array name to set within the URI
     * @throws XMPException if the namespace or uri or data is invalid
     */
    private static void copyMetadata(Xmp fromXmp, XMPMeta toXmpMeta, MetaDataValue mdValue, String namespaceUri, String arrayName)
            throws XMPException {
        Object xmpValue = fromXmp.getValue(mdValue);

        if (xmpValue != null) {
            if (xmpValue instanceof String) {
                String value = (String) xmpValue;

                // 2009-08-02: No side effects if value is clear
                // ("orphaned data"), because previous metadata was deleted
                if (XmpMetaDataValueXmpValueTypeMapping.isText(mdValue) && !value.trim().isEmpty()) {
                    toXmpMeta.setProperty(namespaceUri, arrayName, value);
                } else if (XmpMetaDataValueXmpValueTypeMapping.isLanguageAlternative(mdValue)) {
                    toXmpMeta.setLocalizedText(namespaceUri, arrayName, "", "x-default", value);
                }
            } else if (xmpValue instanceof List<?>) {
                @SuppressWarnings("unchecked") List<String> values = (List<String>) xmpValue;

                Collections.sort(values);

                for (String value : values) {
                    String trimmedValue = value.trim();

                    if (!doesArrayItemExist(toXmpMeta, namespaceUri, arrayName, trimmedValue)) {
                        toXmpMeta.appendArrayItem(namespaceUri, arrayName, getArrayPropertyOptionsOf(mdValue),
                                trimmedValue, null);
                    }
                }
            } else if (xmpValue instanceof Long) {
                Long value = (Long) xmpValue;

                toXmpMeta.setProperty(namespaceUri, arrayName, Long.toString(value));
            } else {
                LOGGER.log(Level.INFO, "No rule to write ''{0}''", xmpValue.getClass());
            }
        }
    }

    private static boolean doesArrayItemExist(XMPMeta xmpMeta, String namespaceUri, String propertyName, String item)
            throws XMPException {
        if (xmpMeta.doesPropertyExist(namespaceUri, propertyName)) {
            for (XMPIterator it = xmpMeta.iterator(namespaceUri, propertyName, new IteratorOptions()); it.hasNext();) {
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

    private static PropertyOptions getArrayPropertyOptionsOf(MetaDataValue mdValue) {
        XmpValueType valueType = XmpMetaDataValueXmpValueTypeMapping.getXmpValueType(mdValue);

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

    private static boolean writeSidecarFile(XMPMeta fromXmpMeta, File toSidecarFile) {
        FileOutputStream out = null;

        if (!FileLock.INSTANCE.lockLogWarning(toSidecarFile, XmpMetadata.class)) {
            return false;
        }

        try {
            out = new FileOutputStream(toSidecarFile);
            out.getChannel().lock();
            XMPMetaFactory.serialize(fromXmpMeta, out,
                    new SerializeOptions().setPadding(10).setOmitPacketWrapper(true));

            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);

            return false;
        } finally {
            FileLock.INSTANCE.unlock(toSidecarFile, XmpMetadata.class);

            if (out != null) {
                try {
                    out.close();
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Returns XMP metadata of a image file.
     * <p>
     * If the image has a sidecar file, it's metadata will be read. If the image
     * hasn't a sidecar file but embedded XMP metadata, the embedded XMP
     * metadata will be read.
     *
     * @param  imageFile image file or null
     * @return           XMP metadata of the image file or null
     * @throws           IOException
     */
    public static Xmp getXmpFromSidecarFileOf(File imageFile) throws IOException {
        if ((imageFile == null) || !hasImageASidecarFile(imageFile)) {
            return null;
        }

        return getXmp(getPropertyInfosOfSidecarFile(getSidecarFile(imageFile)), imageFile, XmpLocation.SIDECAR_FILE);
    }

    /**
     * Returns XMP embedded in the image file.
     *
     * @param   imageFile image file
     * @return            embedded XMP metadata or null if no XMP is embedded or
     *                    when errors occur while reading the file
     */
    public static Xmp getEmbeddedXmp(File imageFile) {
        if (imageFile == null) {
            throw new NullPointerException("imageFile == null");
        }

        String xmpString = EmbeddedXmpCache.INSTANCE.getCachedXmp(imageFile);

        return (xmpString == null)
                ? null
                : getXmp(getPropertyInfosOfXmpString(xmpString), imageFile, XmpLocation.EMBEDDED);
    }

    /**
     * Puts into a map property infos where the map key is a string of
     * {@code XmpToSaveInRepository#getPathPrefixes()}. The values are
     * {@code XMPPropertyInfo} instances with path prefixes matching the key.
     *
     * @param  xmpPropertyInfos unordered property infos
     * @return                   ordered property infos
     */
    public static Map<String, List<XMPPropertyInfo>> getOrderedPropertyInfos(List<XMPPropertyInfo> xmpPropertyInfos) {
        if (xmpPropertyInfos == null) {
            throw new NullPointerException("xmpPropertyInfos == null");
        }

        Map<String, List<XMPPropertyInfo>> propertyInfoWithPathStart = new HashMap<String, List<XMPPropertyInfo>>();
        Set<String> pathPrefixes = XmpToSaveInRepository.getPathPrefixes();

        for (String pathPrefix : pathPrefixes) {
            for (XMPPropertyInfo propertyInfo : xmpPropertyInfos) {
                if (propertyInfo.getPath().startsWith(pathPrefix)) {
                    List<XMPPropertyInfo> infos = propertyInfoWithPathStart.get(pathPrefix);

                    if (infos == null) {
                        infos = new ArrayList<XMPPropertyInfo>();
                        infos.add(propertyInfo);
                        propertyInfoWithPathStart.put(pathPrefix, infos);
                    } else {
                        infos.add(propertyInfo);
                    }
                }
            }
        }

        return propertyInfoWithPathStart;
    }

    private static Xmp getXmp(List<XMPPropertyInfo> xmpPropertyInfos, File areFromXmpImageFile, XmpLocation xmpLocation) {
        Xmp xmp = null;

        if (xmpPropertyInfos != null) {
            xmp = new Xmp();

            for (XMPPropertyInfo xmpPropertyInfo : xmpPropertyInfos) {
                String path = xmpPropertyInfo.getPath();
                Object value = xmpPropertyInfo.getValue();
                MetaDataValue mdValue = XmpMetaDataValueXmpArrayNameMapping.findXmpMetaDataValue(path);

                if ((value != null) && (mdValue != null) && (mdValue.getValueType() != null)) {
                    try {
                        xmp.setValue(mdValue, mdValue.getValueType().parseString(value.toString()));
                    } catch (Exception ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                    }
                }
            }

            setLastModified(xmpLocation, xmp, areFromXmpImageFile);
        }

        return xmp;
    }

    private static void setLastModified(XmpLocation xmpType, Xmp xmp, File imageFile) {
        File sidecarFile = getSidecarFile(imageFile);

        if (xmpType.equals(XmpLocation.SIDECAR_FILE) && (sidecarFile != null)) {
            xmp.setValue(XmpLastModifiedMetaDataValue.INSTANCE, sidecarFile.lastModified());
        } else if (xmpType.equals(XmpLocation.EMBEDDED) && imageFile.exists()) {
            xmp.setValue(XmpLastModifiedMetaDataValue.INSTANCE, imageFile.lastModified());
        }
    }

    /**
     * Returns image files and their sidecar files.
     *
     * @param  imageFiles image files
     * @return            image files with their corresponding sidecar files.
     *                    {@code ImageFileSidecarFile#getSidecarFile()}
     *                    returns null if the image file has no sidecar file.
     */
    public static List<ImageFileSidecarFile> getImageFilesWithSidecarFiles(List<File> imageFiles) {
        if (imageFiles == null) {
            throw new NullPointerException("imageFiles == null");
        }

        List<ImageFileSidecarFile> imageFilesSidecarFiles = new ArrayList<ImageFileSidecarFile>();

        for (File imageFile : imageFiles) {
            File sidecarFile = getSidecarFile(imageFile);

            imageFilesSidecarFiles.add(new ImageFileSidecarFile(imageFile, (sidecarFile == null)
                    ? null
                    : sidecarFile));
        }

        return imageFilesSidecarFiles;
    }
}
