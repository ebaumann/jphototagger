package org.jphototagger.exif;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.metadata.exif.ExifInfo;
import org.jphototagger.exif.ExifTag.Id;
import org.jphototagger.exif.tag.ExifGpsAltitude;
import org.jphototagger.exif.tag.ExifGpsLatitude;
import org.jphototagger.exif.tag.ExifGpsLongitude;
import org.jphototagger.exif.tag.ExifGpsMetadata;
import org.jphototagger.exif.tag.ExifGpsUtil;
import org.jphototagger.lib.util.Translation;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ExifInfo.class)
public final class ExifInfoImpl implements ExifInfo {

    public static final Translation TAG_ID_TAGNAME_TRANSLATION = new Translation(ExifInfoImpl.class, "ExifTagIdTagNameTranslations");
    public static final Translation TAGNAME_TRANSLATION = new Translation(ExifInfoImpl.class, "ExifTagNameTranslations");
    private static final Logger LOGGER = Logger.getLogger(ExifInfoImpl.class.getName());

    @Override
    public double getRotationAngleOfEmbeddedThumbnail(File file) {
        ExifTags exifTags = ExifMetadata.getExifTagsPreferCached(file);

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
        ExifTags exifTags = ExifMetadata.getExifTags(file);

        if (exifTags == null) {
            return Collections.emptyList();
        }

        return convertTags(exifTags);
    }

    @Override
    public Collection<org.jphototagger.domain.metadata.exif.ExifTag> getExifTagsPreferCached(File file) {
        ExifTags exifTags = ExifMetadata.getExifTagsPreferCached(file);

        if (exifTags == null) {
            return Collections.emptyList();
        }

        return convertTags(exifTags);
    }

    private Collection<org.jphototagger.domain.metadata.exif.ExifTag> convertTags(ExifTags exifTags) {
        List<org.jphototagger.domain.metadata.exif.ExifTag> tags = new ArrayList<org.jphototagger.domain.metadata.exif.ExifTag>();

        addExifTags(exifTags.getExifTags(), tags);
        addExifTags(exifTags.getInteroperabilityTags(), tags);
        addGpsTags(exifTags, tags);
        addExifTags(exifTags.getMakerNoteTags(), tags);

        return tags;
    }

    private void addExifTags(Collection<? extends ExifTag> source, List<org.jphototagger.domain.metadata.exif.ExifTag> target) {
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

    private void addGpsTags(ExifTags source, List<org.jphototagger.domain.metadata.exif.ExifTag> target) {
        ExifGpsMetadata exifGpsMetadata = ExifGpsUtil.createGpsMetadataFromExifTags(source);
        ExifGpsLatitude latitude = exifGpsMetadata.getLatitude();
        ExifGpsLongitude longitude = exifGpsMetadata.getLongitude();
        ExifGpsAltitude altitude = exifGpsMetadata.getAltitude();

        if (latitude != null) {
            String tagId = Integer.toString(ExifTag.Id.GPS_LATITUDE.getTagId());
            String nameString = TAG_ID_TAGNAME_TRANSLATION.translate(tagId, tagId);
            String valueString = exifGpsMetadata.getLatitude().getLocalizedString();
            org.jphototagger.domain.metadata.exif.ExifTag exifTag = new org.jphototagger.domain.metadata.exif.ExifTag(nameString, valueString);

            target.add(exifTag);
        }

        if (longitude != null) {
            String tagId = Integer.toString(ExifTag.Id.GPS_LONGITUDE.getTagId());
            String tagName = TAG_ID_TAGNAME_TRANSLATION.translate(tagId, tagId);
            String valueString = exifGpsMetadata.getLongitude().toLocalizedString();
            org.jphototagger.domain.metadata.exif.ExifTag exifTag = new org.jphototagger.domain.metadata.exif.ExifTag(tagName, valueString);

            target.add(exifTag);
        }

        if (altitude != null) {
            String tagId = Integer.toString(ExifTag.Id.GPS_ALTITUDE.getTagId());
            String nameString = TAG_ID_TAGNAME_TRANSLATION.translate(tagId, tagId);
            String valueString = exifGpsMetadata.getAltitude().getLocalizedString();
            org.jphototagger.domain.metadata.exif.ExifTag exifTag = new org.jphototagger.domain.metadata.exif.ExifTag(nameString, valueString);

            target.add(exifTag);
        }

        if (longitude != null && latitude != null) {
            String nameString = org.jphototagger.domain.metadata.exif.ExifTag.NAME_GOOGLE_MAPS_URL;
            String valueString = ExifGpsUtil.getGoogleMapsUrl(exifGpsMetadata.getLongitude(), exifGpsMetadata.getLatitude());
            org.jphototagger.domain.metadata.exif.ExifTag exifTag = new org.jphototagger.domain.metadata.exif.ExifTag(nameString, valueString);

            target.add(exifTag);
        }
    }

    private static String getTagName(ExifTag exifTag) {
        String tagName = exifTag.getName();
        ExifIfdType ifdType = exifTag.getIfdType();
        boolean isMakerNoteIfd = ifdType.equals(ExifIfdType.MAKER_NOTE);

        if (isMakerNoteIfd) {
            return tagName;
        }

        Id exifTagId = exifTag.convertTagIdToEnumId();
        int tagId = exifTagId.getTagId();
        int makerNoteTagId = ExifTag.Id.MAKER_NOTE.getTagId();
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
}
