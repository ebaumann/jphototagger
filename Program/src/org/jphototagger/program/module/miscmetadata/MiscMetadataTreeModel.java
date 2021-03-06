package org.jphototagger.program.module.miscmetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.exif.ExifFocalLengthMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifIsoSpeedRatingsMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifLensMetaDataValue;
import org.jphototagger.domain.metadata.exif.ExifRecordingEquipmentMetaDataValue;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.metadata.xmp.XmpDcCreatorMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcRightsMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpIptc4xmpcoreLocationMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCityMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopCountryMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopSourceMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpPhotoshopStateMetaDataValue;
import org.jphototagger.domain.metadata.xmp.XmpRatingMetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectDeletedEvent;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifDeletedEvent;
import org.jphototagger.domain.repository.event.exif.ExifInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifUpdatedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.lib.swing.util.TreeUtil;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * This model contains distinct values of specific EXIF and XMP repository metadata values.
 *
 * Elements are {@code DefaultMutableTreeNode}s with the user objects listed below.
 *
 * <ul>
 * <li>The root user object is a {@code String}</li>
 * <li>User objects direct below the root are {@code MetaDataValue}s</li>
 * <li>User objects below the columns having the data type of the column
 *    ({@code MetaDataValue#getValueType()}</li>
 * </ul>
 *
 * @author Elmar Baumann
 */
public final class MiscMetadataTreeModel extends DefaultTreeModel {

    private static final Object EXIF_USER_OBJECT = Bundle.getString(MiscMetadataTreeModel.class, "MiscMetadataTreeModel.ExifNode.DisplayName");
    private static final long serialVersionUID = 1L;
    private static final Object XMP_USER_OBJECT = Bundle.getString(MiscMetadataTreeModel.class, "MiscMetadataTreeModel.XmpNode.DisplayName");
    private static final Set<MetaDataValue> XMP_META_DATA_VALUES = new LinkedHashSet<>();
    private static final Set<MetaDataValue> EXIF_META_DATA_VALUES = new LinkedHashSet<>();

    static {
        EXIF_META_DATA_VALUES.add(ExifRecordingEquipmentMetaDataValue.INSTANCE);
        EXIF_META_DATA_VALUES.add(ExifFocalLengthMetaDataValue.INSTANCE);
        EXIF_META_DATA_VALUES.add(ExifLensMetaDataValue.INSTANCE);
        EXIF_META_DATA_VALUES.add(ExifIsoSpeedRatingsMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUES.add(XmpIptc4xmpcoreLocationMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUES.add(XmpPhotoshopCityMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUES.add(XmpPhotoshopStateMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUES.add(XmpPhotoshopCountryMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUES.add(XmpDcCreatorMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUES.add(XmpDcRightsMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUES.add(XmpPhotoshopSourceMetaDataValue.INSTANCE);
        XMP_META_DATA_VALUES.add(XmpRatingMetaDataValue.INSTANCE);
    }
    private final boolean onlyXmp;
    private final DefaultMutableTreeNode ROOT;
    private final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    public MiscMetadataTreeModel(boolean onlyXmp) {
        super(new DefaultMutableTreeNode(Bundle.getString(MiscMetadataTreeModel.class, "MiscMetadataTreeModel.Root.DisplayName")));
        this.onlyXmp = onlyXmp;
        this.ROOT = (DefaultMutableTreeNode) getRoot();

        if (!onlyXmp) {
            addMetaDataValueNodes(EXIF_USER_OBJECT, EXIF_META_DATA_VALUES);
        }

        addMetaDataValueNodes(XMP_USER_OBJECT, XMP_META_DATA_VALUES);
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    public boolean isOnlyXmp() {
        return onlyXmp;
    }

    public static Set<MetaDataValue> getExifMetaDataValues() {
        return new LinkedHashSet<>(EXIF_META_DATA_VALUES);
    }

    public static Set<MetaDataValue> getXmpMetaDataValues() {
        return new LinkedHashSet<>(XMP_META_DATA_VALUES);
    }

    private void addMetaDataValueNodes(Object userObject, Set<MetaDataValue> metaDataValues) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(userObject);

        for (MetaDataValue mdValue : metaDataValues) {
            DefaultMutableTreeNode mdValueNode = new DefaultMutableTreeNode(mdValue);

            addChildren(mdValueNode, repo.findAllDistinctMetaDataValues(mdValue), mdValue.getValueType());
            node.add(mdValueNode);
        }

        ROOT.add(node);
    }

    private void addChildren(DefaultMutableTreeNode parentNode, Set<String> data, MetaDataValue.ValueType dataType) {
        for (String string : getTrimmedStrigns(data)) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode();

            if (dataType.equals(MetaDataValue.ValueType.STRING)) {
                node.setUserObject(string);
            } else if (dataType.equals(MetaDataValue.ValueType.SMALLINT)) {
                node.setUserObject(Short.valueOf(string));
            } else if (dataType.equals(MetaDataValue.ValueType.REAL)) {
                node.setUserObject(Double.valueOf(string));
            } else if (dataType.equals(MetaDataValue.ValueType.BIGINT)) {
                node.setUserObject(Long.valueOf(string));
            } else {
                assert false : "Unregognized data type: " + dataType;
            }

            parentNode.add(node);
        }
    }

    private Collection<String> getTrimmedStrigns(Collection<? extends String> strings) {
        Set<String> trimmedStrings = new LinkedHashSet<>();
        for (String string : strings) {
            trimmedStrings.add(string.trim());
        }
        return trimmedStrings;
    }

    private void checkDeleted(Xmp xmp) {
        for (MetaDataValue xmpMdValue : XMP_META_DATA_VALUES) {
            Object value = xmp.getValue(xmpMdValue);

            if (value != null) {
                checkDeleted(xmpMdValue, value);
            }
        }
    }

    private void checkDeleted(Exif exif) {
        String recordingEquipment = exif.getRecordingEquipment();

        if (recordingEquipment != null) {
            checkDeleted(ExifRecordingEquipmentMetaDataValue.INSTANCE, recordingEquipment);
        }

        short iso = exif.getIsoSpeedRatings();

        if (iso > 0) {
            checkDeleted(ExifIsoSpeedRatingsMetaDataValue.INSTANCE, Short.valueOf(iso));
        }

        double f = exif.getFocalLength();

        if (f > 0) {
            checkDeleted(ExifFocalLengthMetaDataValue.INSTANCE, Double.valueOf(f));
        }

        String lens = exif.getLens();

        if (lens != null) {
            checkDeleted(ExifLensMetaDataValue.INSTANCE, lens);
        }
    }

    private void checkDeleted(MetaDataValue mdValue, Object userObject) {
        DefaultMutableTreeNode node = findNodeWithUserObject(ROOT, mdValue);

        if ((node != null) && !repo.existsMetaDataValue(userObject, mdValue)) {
            DefaultMutableTreeNode child = findNodeWithUserObject(node, userObject);

            if (child != null) {
                int index = node.getIndex(child);

                node.remove(index);
                nodesWereRemoved(node, new int[]{index}, new Object[]{child});
            }
        }
    }

    private void checkInserted(Xmp xmp) {
        for (MetaDataValue xmpMdValue : XMP_META_DATA_VALUES) {
            Object value = xmp.getValue(xmpMdValue);

            if (value != null) {
                checkInserted(xmpMdValue, value);
            }
        }
    }

    private void checkInserted(Exif exif) {
        String recordingEquipment = exif.getRecordingEquipment();

        if (recordingEquipment != null) {
            checkInserted(ExifRecordingEquipmentMetaDataValue.INSTANCE, recordingEquipment);
        }

        short iso = exif.getIsoSpeedRatings();

        if (iso > 0) {
            checkInserted(ExifIsoSpeedRatingsMetaDataValue.INSTANCE, Short.valueOf(iso));
        }

        double f = exif.getFocalLength();

        if (f > 0) {
            checkInserted(ExifFocalLengthMetaDataValue.INSTANCE, Double.valueOf(f));
        }

        String lens = exif.getLens();

        if (lens != null) {
            checkInserted(ExifLensMetaDataValue.INSTANCE, lens);
        }
    }

    private void checkInserted(MetaDataValue mdValue, Object userObject) {
        DefaultMutableTreeNode node = findNodeWithUserObject(ROOT, mdValue);

        if (node != null) {
            DefaultMutableTreeNode child = findNodeWithUserObject(node, userObject);

            if (child == null) {
                DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(userObject);

                node.add(newChild);
                nodesWereInserted(node, new int[]{node.getIndex(newChild)});
            }
        }
    }

    private DefaultMutableTreeNode findNodeWithUserObject(DefaultMutableTreeNode rootNode, Object userObject) {
        List<DefaultMutableTreeNode> foundNodes = new ArrayList<>(1);

        TreeUtil.addNodesUserWithObject(foundNodes, rootNode, userObject, 1);

        return (foundNodes.size() > 0)
                ? foundNodes.get(0)
                : null;
    }

    @EventSubscriber(eventClass = XmpUpdatedEvent.class)
    public void xmpUpdated(final XmpUpdatedEvent evt) {
        checkDeleted(evt.getOldXmp());
        checkInserted(evt.getUpdatedXmp());
    }

    @EventSubscriber(eventClass = XmpInsertedEvent.class)
    public void xmpInserted(final XmpInsertedEvent evt) {
        checkInserted(evt.getXmp());
    }

    @EventSubscriber(eventClass = XmpDeletedEvent.class)
    public void xmpDeleted(final XmpDeletedEvent evt) {
        checkDeleted(evt.getXmp());
    }

    @EventSubscriber(eventClass = DcSubjectDeletedEvent.class)
    public void dcSubjectDeleted(final DcSubjectDeletedEvent evt) {
        checkDeleted(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, evt.getDcSubject());
    }

    @EventSubscriber(eventClass = DcSubjectInsertedEvent.class)
    public void dcSubjectInserted(final DcSubjectInsertedEvent evt) {
        checkInserted(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, evt.getDcSubject());
    }

    @EventSubscriber(eventClass = ExifInsertedEvent.class)
    public void exifInserted(final ExifInsertedEvent evt) {
        checkInserted(evt.getExif());
    }

    @EventSubscriber(eventClass = ExifUpdatedEvent.class)
    public void exifUpdated(final ExifUpdatedEvent evt) {
        checkDeleted(evt.getOldExif());
        checkInserted(evt.getUpdatedExif());
    }

    @EventSubscriber(eventClass = ExifDeletedEvent.class)
    public void exifDeleted(final ExifDeletedEvent evt) {
        checkDeleted(evt.getExif());
    }
}
