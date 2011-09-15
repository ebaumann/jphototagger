package org.jphototagger.domain.repository;

import java.awt.Image;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.domain.image.ImageFile;
import org.jphototagger.domain.timeline.Timeline;
import org.jphototagger.domain.xmp.FileXmp;
import org.jphototagger.domain.xmp.Xmp;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface ImageFilesRepository {

    int deleteImageFiles(List<File> imageFiles);

    void deleteDcSubject(String dcSubject);

    int deleteAbsentImageFiles(ProgressListener listener);

    int deleteAbsentXmp(ProgressListener listener);

    void deleteValueOfJoinedMetaDataValue(MetaDataValue mdValue, String value);

    public boolean existsImageFile(File imageFile);

    boolean existsDcSubject(String dcSubject);

    boolean existsExifDate(java.sql.Date date);

    boolean existsMetaDataValue(Object value, MetaDataValue mdValue);

    boolean existsXMPDateCreated(String date);

    Set<String> findAllDcSubjects();

    Set<String> findAllDistinctMetaDataValues(MetaDataValue value);

    List<File> findAllImageFiles();

    Set<File> findAllThumbnailFiles();

    List<String> findDcSubjectsOfImageFile(File imageFile);

    Exif findExifOfImageFile(File imageFile);

    Long findIdDcSubject(String dcSubject);

    boolean existsXmpDcSubjectsLink(long idXmp, long idDcSubject);

    ImageFile findImageFileOfFile(File file);

    Set<File> findImageFilesContainingAllDcSubjects(List<? extends String> dcSubjects);

    Set<File> findImageFilesContainingDcSubject(String dcSubject, boolean includeSynonyms);

    Set<File> findImageFilesContainingSomeOfDcSubjects(List<? extends String> dcSubjects);

    Set<File> findImageFilesContainingAllWordsInMetaDataValue(List<? extends String> words, MetaDataValue value);

    List<File> findImageFilesContainingAVauleInMetaDataValue(MetaDataValue value);

    long findImageFilesLastModifiedTimestamp(File imageFile);

    Set<File> findImageFilesOfDateTaken(int year, int month, int day);

    List<File> findImageFilesOfUnknownDateTaken();

    List<File> findImageFilesWhereMetaDataValueHasExactValue(MetaDataValue value, String exactValue);

    List<File> findImageFilesWithoutDataValue(MetaDataValue value);

    Set<String> findNotReferencedDcSubjects();

    Timeline findTimeline();

    long findXmpFilesLastModifiedTimestamp(File imageFile);

    Xmp findXmpOfImageFile(File imageFile);

    List<FileXmp> findXmpOfImageFiles(Collection<? extends File> imageFiles);

    boolean saveDcSubject(String dcSubject);

    boolean saveOrUpdateExif(File imageFile, Exif exif);

    boolean saveOrUpdateImageFile(ImageFile imageFile);

    boolean saveOrUpdateXmpOfImageFile(File imageFile, Xmp xmp);

    boolean isDcSubjectReferenced(String dcSubject);

    boolean setLastModifiedToXmpSidecarFileOfImageFile(File imageFile, long time);

    boolean updateImageFile(ImageFile imageFile);

    int updateRenameImageFile(File fromImageFile, File toImageFile);

    boolean updateThumbnail(File imageFile, Image thumbnail);

    int updateAllThumbnails(ProgressListener listener);

    int updateRenameFilenamesStartingWith(final String before, final String after, final ProgressListener progressListener);
}
