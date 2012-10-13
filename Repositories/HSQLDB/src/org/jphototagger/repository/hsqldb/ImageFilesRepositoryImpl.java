package org.jphototagger.repository.hsqldb;

import java.awt.Image;
import java.io.File;
import java.sql.Date;
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
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.timeline.Timeline;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ImageFilesRepository.class)
public final class ImageFilesRepositoryImpl implements ImageFilesRepository {

    @Override
    public long getFileCount() {
        return ImageFilesDatabase.INSTANCE.getFileCount();
    }

    @Override
    public List<File> findAllImageFiles() {
        return ImageFilesDatabase.INSTANCE.getAllImageFiles();
    }

    @Override
    public void eachImage(Functor<File> functor) {
        ImageFilesDatabase.INSTANCE.eachImage(functor);
    }

    @Override
    public long findImageFilesLastModifiedTimestamp(File imageFile) {
        return ImageFilesDatabase.INSTANCE.getImageFilesLastModifiedTimestamp(imageFile);
    }

    @Override
    public long findImageFilesSizeInBytes(File file) {
        return ImageFilesDatabase.INSTANCE.getImageFilesSizeInBytes(file);
    }

    @Override
    public long findXmpFilesLastModifiedTimestamp(File imageFile) {
        return ImageFilesDatabase.INSTANCE.getXmpFilesLastModifiedTimestamp(imageFile);
    }

    @Override
    public int deleteImageFiles(List<File> imageFiles) {
        return ImageFilesDatabase.INSTANCE.deleteImageFiles(imageFiles);
    }

    @Override
    public void deleteDcSubject(String dcSubject) {
        ImageFilesDatabase.INSTANCE.deleteDcSubject(dcSubject);
    }

    @Override
    public int deleteAbsentImageFiles(ProgressListener listener) {
        return ImageFilesDatabase.INSTANCE.deleteAbsentImageFiles(listener);
    }

    @Override
    public int deleteAbsentXmp(ProgressListener listener) {
        return ImageFilesDatabase.INSTANCE.deleteAbsentXmp(listener);
    }

    @Override
    public void deleteValueOfJoinedMetaDataValue(MetaDataValue mdValue, String value) {
        ImageFilesDatabase.INSTANCE.deleteValueOfJoinedColumn(mdValue, value);
    }

    @Override
    public boolean existsImageFile(File imageFile) {
        return ImageFilesDatabase.INSTANCE.existsImageFile(imageFile);
    }

    @Override
    public boolean existsDcSubject(String dcSubject) {
        return ImageFilesDatabase.INSTANCE.existsDcSubject(dcSubject);
    }

    @Override
    public boolean existsExifDate(Date date) {
        return ImageFilesDatabase.INSTANCE.existsExifDate(date);
    }

    @Override
    public boolean existsMetaDataValue(Object value, MetaDataValue mdValue) {
        return ImageFilesDatabase.INSTANCE.existsValueInColumn(value, mdValue);
    }

    @Override
    public boolean existsXMPDateCreated(String date) {
        return ImageFilesDatabase.INSTANCE.existsXMPDateCreated(date);
    }

    @Override
    public Set<String> findAllDcSubjects() {
        return ImageFilesDatabase.INSTANCE.getAllDcSubjects();
    }

    @Override
    public Set<String> findAllDistinctMetaDataValues(MetaDataValue mdValue) {
        return ImageFilesDatabase.INSTANCE.getAllDistinctValuesOfColumn(mdValue);
    }

    @Override
    public Set<File> findAllThumbnailFiles() {
        return ImageFilesDatabase.INSTANCE.getAllThumbnailFiles();
    }

    @Override
    public List<String> findDcSubjectsOfImageFile(File imageFile) {
        return ImageFilesDatabase.INSTANCE.getDcSubjectsOfImageFile(imageFile);
    }

    @Override
    public Exif findExifOfImageFile(File imageFile) {
        return ImageFilesDatabase.INSTANCE.getExifOfImageFile(imageFile);
    }

    @Override
    public ImageFile findImageFileOfFile(File file) {
        return ImageFilesDatabase.INSTANCE.getImageFileOfFile(file);
    }

    @Override
    public Set<File> findImageFilesContainingAllDcSubjects(List<? extends String> dcSubjects) {
        return ImageFilesDatabase.INSTANCE.getImageFilesContainingAllDcSubjects(dcSubjects);
    }

    @Override
    public Set<File> findImageFilesContainingDcSubject(String dcSubject, boolean includeSynonyms) {
        return ImageFilesDatabase.INSTANCE.getImageFilesContainingDcSubject(dcSubject, includeSynonyms);
    }

    @Override
    public Set<File> findImageFilesContainingSomeOfDcSubjects(List<? extends String> dcSubjects) {
        return ImageFilesDatabase.INSTANCE.getImageFilesContainingSomeOfDcSubjects(dcSubjects);
    }

    @Override
    public Set<File> findImageFilesContainingAllWordsInMetaDataValue(List<? extends String> words, MetaDataValue mdValue) {
        return ImageFilesDatabase.INSTANCE.getImageFilesContainingAllWordsInColumn(words, mdValue);
    }

    @Override
    public List<File> findImageFilesContainingAVauleInMetaDataValue(MetaDataValue mdValue) {
        return ImageFilesDatabase.INSTANCE.getImageFilesContainingAVauleInColumn(mdValue);
    }

    @Override
    public Set<File> findImageFilesOfDateTaken(int year, int month, int day) {
        return ImageFilesDatabase.INSTANCE.getImageFilesOfDateTaken(year, month, day);
    }

    @Override
    public List<File> findImageFilesOfUnknownDateTaken() {
        return ImageFilesDatabase.INSTANCE.getImageFilesOfUnknownDateTaken();
    }

    @Override
    public List<File> findImageFilesWhereMetaDataValueHasExactValue(MetaDataValue mdValue, String exactValue) {
        return ImageFilesDatabase.INSTANCE.getImageFilesWhereColumnHasExactValue(mdValue, exactValue);
    }

    @Override
    public List<File> findImageFilesWithoutDataValue(MetaDataValue mdValue) {
        return ImageFilesDatabase.INSTANCE.getImageFilesWithoutMetadataInColumn(mdValue);
    }

    @Override
    public Set<String> findNotReferencedDcSubjects() {
        return ImageFilesDatabase.INSTANCE.getNotReferencedDcSubjects();
    }

    @Override
    public Timeline findTimeline() {
        return ImageFilesDatabase.INSTANCE.getTimeline();
    }

    @Override
    public Xmp findXmpOfImageFile(File imageFile) {
        return ImageFilesDatabase.INSTANCE.getXmpOfImageFile(imageFile);
    }

    @Override
    public List<FileXmp> findXmpOfImageFiles(Collection<? extends File> imageFiles) {
        return ImageFilesDatabase.INSTANCE.getXmpOfImageFiles(imageFiles);
    }

    @Override
    public boolean saveDcSubject(String dcSubject) {
        return ImageFilesDatabase.INSTANCE.insertDcSubject(dcSubject);
    }

    @Override
    public boolean saveOrUpdateExif(File imageFile, Exif exif) {
        return ImageFilesDatabase.INSTANCE.insertOrUpdateExif(imageFile, exif);
    }

    @Override
    public boolean saveOrUpdateImageFile(ImageFile imageFile) {
        return ImageFilesDatabase.INSTANCE.insertOrUpdateImageFile(imageFile);
    }

    @Override
    public boolean isDcSubjectReferenced(String dcSubject) {
        return ImageFilesDatabase.INSTANCE.isDcSubjectReferenced(dcSubject);
    }

    @Override
    public boolean setLastModifiedToXmpSidecarFileOfImageFile(File imageFile, long time) {
        return ImageFilesDatabase.INSTANCE.setLastModifiedToXmpSidecarFileOfImageFile(imageFile, time);
    }

    @Override
    public boolean updateImageFile(ImageFile imageFile) {
        return ImageFilesDatabase.INSTANCE.updateImageFile(imageFile);
    }

    @Override
    public int updateRenameImageFile(File fromImageFile, File toImageFile) {
        return ImageFilesDatabase.INSTANCE.updateRenameImageFile(fromImageFile, toImageFile);
    }

    @Override
    public boolean updateThumbnail(File imageFile, Image thumbnail) {
        return ImageFilesDatabase.INSTANCE.updateThumbnail(imageFile, thumbnail);
    }

    @Override
    public Long findIdDcSubject(String dcSubject) {
        return ImageFilesDatabase.INSTANCE.getIdDcSubject(dcSubject);
    }

    @Override
    public boolean existsXmpDcSubjectsLink(long idXmp, long idDcSubject) {
        return ImageFilesDatabase.INSTANCE.existsXmpDcSubjectsLink(idXmp, idDcSubject);
    }

    @Override
    public int updateAllThumbnails(ProgressListener listener) {
        return ImageFilesDatabase.INSTANCE.updateAllThumbnails(listener);
    }

    @Override
    public int updateRenameFilenamesStartingWith(String before, String after, ProgressListener progressListener) {
        return ImageFilesDatabase.INSTANCE.updateRenameFilenamesStartingWith(before, after, progressListener);
    }

    @Override
    public boolean saveOrUpdateXmpOfImageFile(File imageFile, Xmp xmp) {
        return ImageFilesDatabase.INSTANCE.insertOrUpdateXmpOfImageFile(imageFile, xmp);
    }

    @Override
    public boolean existsXmpForFile(File file) {
        return ImageFilesDatabase.INSTANCE.existsXmpForFile(file);
    }

    @Override
    public int deleteXmpOfFile(File file) {
        return ImageFilesDatabase.INSTANCE.deleteXmpOfFile(file);
    }

    @Override
    public String findXmpIptc4CoreDateCreated(File file) {
        return ImageFilesDatabase.INSTANCE.findXmpIptc4CoreDateCreated(file);
    }

    @Override
    public long findExifDateTimeOriginalTimestamp(File file) {
        return ImageFilesDatabase.INSTANCE.findExifDateTimeOriginalTimestamp(file);
    }
}
