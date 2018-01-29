package org.jphototagger.domain.repository;

import java.awt.Image;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jphototagger.api.applifecycle.generics.Functor;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.image.ImageFile;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.exif.Exif;
import org.jphototagger.domain.metadata.xmp.FileXmp;
import org.jphototagger.domain.metadata.xmp.Xmp;
import org.jphototagger.domain.timeline.Timeline;

/**
 * @author Elmar Baumann
 */
public interface ImageFilesRepository {

    long getFileCount();

    int deleteImageFiles(List<File> files);

    void deleteDcSubject(String dcSubject);

    int deleteAbsentImageFiles(ProgressListener listener);

    int deleteAbsentXmp(ProgressListener listener);

    void deleteValueOfJoinedMetaDataValue(MetaDataValue mdValue, String value);

    public boolean existsImageFile(File file);

    boolean existsDcSubject(String dcSubject);

    boolean existsExifDate(java.sql.Date date);

    boolean existsMetaDataValue(Object value, MetaDataValue mdValue);

    boolean existsXMPDateCreated(String date);

    boolean existsXmpForFile(File file);

    int deleteXmpOfFile(File file);

    Set<String> findAllDcSubjects();

    Set<String> findAllDistinctMetaDataValues(MetaDataValue value);

    List<File> findAllImageFiles();

    void eachImage(Functor<File> functor);

    List<String> findDcSubjectsOfImageFile(File file);

    Exif findExifOfImageFile(File file);

    long findExifDateTimeOriginalTimestamp(File file);

    Long findIdDcSubject(String dcSubject);

    boolean existsXmpDcSubjectsLink(long idXmp, long idDcSubject);

    ImageFile findImageFileOfFile(File file);

    Set<File> findImageFilesContainingAllDcSubjects(List<? extends String> dcSubjects);

    Set<File> findImageFilesContainingDcSubject(String dcSubject, boolean includeSynonyms);

    Set<File> findImageFilesContainingSomeOfDcSubjects(List<? extends String> dcSubjects);

    Set<File> findImageFilesContainingAllWordsInMetaDataValue(List<? extends String> words, MetaDataValue value);

    List<File> findImageFilesContainingAVauleInMetaDataValue(MetaDataValue value);

    long findImageFilesLastModifiedTimestamp(File file);

    long findImageFilesSizeInBytes(File file);

    Set<File> findImageFilesOfDateTaken(int year, int month, int day);

    List<File> findImageFilesOfUnknownDateTaken();

    List<File> findImageFilesWhereMetaDataValueHasExactValue(MetaDataValue value, String exactValue);

    List<File> findImageFilesWithoutDataValue(MetaDataValue value);

    Set<String> findNotReferencedDcSubjects();

    Timeline findTimeline();

    long findXmpFilesLastModifiedTimestamp(File file);

    Xmp findXmpOfImageFile(File file);

    String findXmpIptc4CoreDateCreated(File file);

    List<FileXmp> findXmpOfImageFiles(Collection<? extends File> files);

    boolean saveDcSubject(String dcSubject);

    boolean saveOrUpdateExif(File file, Exif exif);

    boolean saveOrUpdateImageFile(ImageFile imageFile);

    boolean saveOrUpdateXmpOfImageFile(File file, Xmp xmp);

    boolean isDcSubjectReferenced(String dcSubject);

    boolean setLastModifiedToXmpSidecarFileOfImageFile(File file, long time);

    boolean updateImageFile(ImageFile imageFile);

    int updateRenameImageFile(File fromImageFile, File toImageFile);

    boolean updateThumbnail(File file, Image thumbnail);

    int updateAllThumbnails(ProgressListener listener);

    int updateRenameFilenamesStartingWith(final String before, final String after, final ProgressListener progressListener);

    public int updateRenameAllDcSubjects(String fromName, String toName);
}
