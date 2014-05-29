package org.jphototagger.exif;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.domain.metadata.exif.ExifInfo;
import org.jphototagger.domain.metadata.exif.ExifMakerNoteTags;
import org.jphototagger.exif.ExifTag.Properties;
import org.jphototagger.exif.tag.ExifGpsAltitude;
import org.jphototagger.exif.tag.ExifGpsLatitude;
import org.jphototagger.exif.tag.ExifGpsLongitude;
import org.jphototagger.exif.tag.ExifGpsMetadata;
import org.jphototagger.exif.tag.ExifGpsUtil;
import org.jphototagger.lib.util.Translation;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExifInfo.class)
public final class DefaultExifInfo implements ExifInfo {

    public static final Translation TAG_ID_TAGNAME_TRANSLATION = new Translation(DefaultExifInfo.class, "ExifTagIdTagNameTranslations");
    public static final Translation TAGNAME_TRANSLATION = new Translation(DefaultExifInfo.class, "ExifTagNameTranslations");
    private static final Logger LOGGER = Logger.getLogger(DefaultExifInfo.class.getName());

    @Override
    public double getRotationAngleOfEmbeddedThumbnail(File file) {
        ExifTags exifTags = ExifSupport.INSTANCE.getExifTagsPreferCached(file);
        if (exifTags != null) {
            ExifTag exifTag = exifTags.findExifTagByTagId(274);
            if (exifTag != null) {
                return ExifThumbnailUtil.getThumbnailRotationAngle(exifTag);
            }
        }
        return 0.0;
    }

    @Override
    public Collection<org.jphototagger.domain.metadata.exif.ExifTag> getExifTags(File file) {
        ExifTags exifTags = ExifSupport.INSTANCE.getExifTags(file);
        if (exifTags == null) {
            return Collections.emptyList();
        }
        return convertTags(exifTags);
    }

    @Override
    public Collection<org.jphototagger.domain.metadata.exif.ExifTag> getExifTagsPreferCached(File file) {
        ExifTags exifTags = ExifSupport.INSTANCE.getExifTagsPreferCached(file);
        if (exifTags == null) {
            return Collections.emptyList();
        }
        return convertTags(exifTags);
    }

    private Collection<org.jphototagger.domain.metadata.exif.ExifTag> convertTags(ExifTags exifTags) {
        List<org.jphototagger.domain.metadata.exif.ExifTag> tags = new ArrayList<>();
        try {
            addExifTags(exifTags.getExifTags(), tags);
            addExifTags(exifTags.getInteroperabilityTags(), tags);
            addGpsTags(exifTags, tags);
            addExifTags(exifTags.getMakerNoteTags(), tags);
            addMakerNoteTagsFromService(exifTags, tags);
        } catch (Throwable t) {
            Logger.getLogger(DefaultExifInfo.class.getName()).log(Level.SEVERE, null, t);
        }
        return tags;
    }

    private void addExifTags(Collection<? extends ExifTag> source, Collection<org.jphototagger.domain.metadata.exif.ExifTag> target) {
        List<ExifTag> displayableExifTags = ExifTagsToDisplay.getDisplayableExifTagsOf(source);
        if (!displayableExifTags.isEmpty()) {
            Collections.sort(displayableExifTags, ExifTagDisplayComparator.INSTANCE);
            for (ExifTag displayableExifTag : displayableExifTags) {
                String tagValue = displayableExifTag.getStringValue();
                if (tagValue.length() > 0) {
                    String nameString = getTagName(displayableExifTag).trim();
                    String valueString = ExifTagValueFormatter.format(displayableExifTag);
                    target.add(new org.jphototagger.domain.metadata.exif.ExifTag(nameString, valueString));
                }
            }
        }
    }

    private void addGpsTags(ExifTags source, Collection<org.jphototagger.domain.metadata.exif.ExifTag> target) {
        ExifGpsMetadata exifGpsMetadata = ExifGpsUtil.createGpsMetadataFromExifTags(source);
        ExifGpsLatitude latitude = exifGpsMetadata.getLatitude();
        ExifGpsLongitude longitude = exifGpsMetadata.getLongitude();
        ExifGpsAltitude altitude = exifGpsMetadata.getAltitude();
        if (latitude != null) {
            String tagId = Integer.toString(ExifTag.Properties.GPS_LATITUDE.getTagId());
            String nameString = TAG_ID_TAGNAME_TRANSLATION.translate(tagId, tagId);
            String valueString = exifGpsMetadata.getLatitude().getLocalizedString();
            org.jphototagger.domain.metadata.exif.ExifTag exifTag = new org.jphototagger.domain.metadata.exif.ExifTag(nameString, valueString);
            target.add(exifTag);
        }
        if (longitude != null) {
            String tagId = Integer.toString(ExifTag.Properties.GPS_LONGITUDE.getTagId());
            String tagName = TAG_ID_TAGNAME_TRANSLATION.translate(tagId, tagId);
            String valueString = exifGpsMetadata.getLongitude().toLocalizedString();
            org.jphototagger.domain.metadata.exif.ExifTag exifTag = new org.jphototagger.domain.metadata.exif.ExifTag(tagName, valueString);
            target.add(exifTag);
        }
        if (altitude != null) {
            String tagId = Integer.toString(ExifTag.Properties.GPS_ALTITUDE.getTagId());
            String nameString = TAG_ID_TAGNAME_TRANSLATION.translate(tagId, tagId);
            String valueString = exifGpsMetadata.getAltitude().getLocalizedString();
            org.jphototagger.domain.metadata.exif.ExifTag exifTag = new org.jphototagger.domain.metadata.exif.ExifTag(nameString, valueString);
            target.add(exifTag);
        }
        if (longitude != null && latitude != null) {
            addGoogleMapsUrl(target, exifGpsMetadata);
            addOpenStreetMapUrl(target, exifGpsMetadata);
        }
    }

    private static void addGoogleMapsUrl(Collection<org.jphototagger.domain.metadata.exif.ExifTag> target, ExifGpsMetadata exifGpsMetadata) {
        String nameString = org.jphototagger.domain.metadata.exif.ExifTag.NAME_GOOGLE_MAPS_URL;
        String valueString = ExifGpsUtil.getGoogleMapsUrl(exifGpsMetadata.getLongitude(), exifGpsMetadata.getLatitude());
        org.jphototagger.domain.metadata.exif.ExifTag exifTag = new org.jphototagger.domain.metadata.exif.ExifTag(nameString, valueString);
        target.add(exifTag);
    }

    private static void addOpenStreetMapUrl(Collection<org.jphototagger.domain.metadata.exif.ExifTag> target, ExifGpsMetadata exifGpsMetadata) {
        String nameString = org.jphototagger.domain.metadata.exif.ExifTag.NAME_OPEN_STREET_MAP_URL;
        String valueString = ExifGpsUtil.getOpenStreetMapUrl(exifGpsMetadata.getLongitude(), exifGpsMetadata.getLatitude());
        org.jphototagger.domain.metadata.exif.ExifTag exifTag = new org.jphototagger.domain.metadata.exif.ExifTag(nameString, valueString);
        target.add(exifTag);
    }

    private static String getTagName(ExifTag exifTag) {
        String tagName = exifTag.getName();
        ExifIfd ifdType = exifTag.getIfd();
        boolean isMakerNoteIfd = ifdType.equals(ExifIfd.MAKER_NOTE);
        if (isMakerNoteIfd) {
            return tagName;
        }
        Properties exifTagId = exifTag.parseProperties();
        int tagId = exifTagId.getTagId();
        int makerNoteTagId = ExifTag.Properties.MAKER_NOTE.getTagId();
        boolean isMakerNoteTag = tagId >= makerNoteTagId;
        if (isMakerNoteTag) {
            boolean canTranslate = TAGNAME_TRANSLATION.canTranslate(tagName);
            if (!canTranslate) {
                LOGGER.log(Level.INFO, "EXIF tag name suggested for translation: ''{0}''", tagName);
            }
            return canTranslate
                    ? TAGNAME_TRANSLATION.translate(tagName)
                    : tagName;
        }
        return TAG_ID_TAGNAME_TRANSLATION.translate(Integer.toString(exifTag.getTagId()), tagName);
    }

    private void addMakerNoteTagsFromService(ExifTags source, Collection<org.jphototagger.domain.metadata.exif.ExifTag> target) {
        ExifTag makerNoteTag = source.findExifTagByTagId(ExifTag.Properties.MAKER_NOTE.getTagId());
        if (makerNoteTag == null) {
            return;
        }
        byte[] makerNoteRawValue = makerNoteTag.getRawValue();
        ExifTag modelTag = source.findExifTagByTagId(ExifTag.Properties.MODEL.getTagId());
        String modelString = modelTag.getStringValue();
        ExifTag makeTag = source.findExifTagByTagId(ExifTag.Properties.MAKE.getTagId());
        String makeString = makeTag.getStringValue();
        Collection<? extends ExifMakerNoteTags> makerNoteExifInfos = Lookup.getDefault().lookupAll(ExifMakerNoteTags.class);
        for (ExifMakerNoteTags makerNoteTags : makerNoteExifInfos) {
            target.addAll(makerNoteTags.getMakerNoteTags(makeString, modelString, makerNoteRawValue));
        }
    }

    @Override
    public long getTimeTakenInMillis(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        return ExifSupport.INSTANCE.getTimeTakenInMillis(file);
    }
}
