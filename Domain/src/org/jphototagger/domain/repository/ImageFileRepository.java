package org.jphototagger.domain.repository;

import java.awt.Image;
import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jphototagger.api.event.ProgressListener;
import org.jphototagger.domain.database.Column;
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
public interface ImageFileRepository {

    int deleteImageFiles(List<File> imageFiles);

    void deleteDcSubject(String dcSubject);

    int deleteAbsentImageFiles(ProgressListener listener);

    int deleteAbsentXmp(ProgressListener listener);

    void deleteValueOfJoinedColumn(Column column, String value);

    public boolean existsImageFile(File imageFile);

    boolean existsDcSubject(String dcSubject);

    boolean existsExifDate(java.sql.Date date);

    boolean existsValueInColumn(Object value, Column column);

    boolean existsXMPDateCreated(String date);

    Set<String> getAllDcSubjects();

    Set<String> getAllDistinctValuesOfColumn(Column column);

    List<File> getAllImageFiles();

    Set<File> getAllThumbnailFiles();

    List<String> getDcSubjectsOfImageFile(File imageFile);

    Exif getExifOfImageFile(File imageFile);

    ImageFile getImageFileOfFile(File file);

    Set<File> getImageFilesContainingAllDcSubjects(List<? extends String> dcSubjects);

    Set<File> getImageFilesContainingDcSubject(String dcSubject, boolean includeSynonyms);

    Set<File> getImageFilesContainingSomeOfDcSubjects(List<? extends String> dcSubjects);

    Set<File> getImageFilesContainingAllWordsInColumn(List<? extends String> words, Column column);

    List<File> getImageFilesContainingAVauleInColumn(Column column);

    long getImageFilesLastModifiedTimestamp(File imageFile);

    Set<File> getImageFilesOfDateTaken(int year, int month, int day);

    List<File> getImageFilesOfUnknownDateTaken();

    List<File> getImageFilesWhereColumnHasExactValue(Column column, String exactValue);

    List<File> getImageFilesWithoutMetadataInColumn(Column column);

    Set<String> getNotReferencedDcSubjects();

    Timeline getTimeline();

    long getXmpFilesLastModifiedTimestamp(File imageFile);

    Xmp getXmpOfImageFile(File imageFile);

    List<FileXmp> getXmpOfImageFiles(Collection<? extends File> imageFiles);

    boolean insertDcSubject(String dcSubject);

    boolean insertOrUpdateExif(File imageFile, Exif exif);

    boolean insertOrUpdateImageFile(ImageFile imageFile);

    boolean isDcSubjectReferenced(String dcSubject);

    boolean setLastModifiedToXmpSidecarFileOfImageFile(File imageFile, long time);

    boolean updateImageFile(ImageFile imageFile);

    int updateRenameImageFile(File fromImageFile, File toImageFile);

    boolean updateThumbnail(File imageFile, Image thumbnail);
}
