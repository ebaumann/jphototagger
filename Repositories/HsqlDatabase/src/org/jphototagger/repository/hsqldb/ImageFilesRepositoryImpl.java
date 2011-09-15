package org.jphototagger.repository.hsqldb;

import java.awt.Image;
import java.io.File;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.domain.image.ImageFile;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.domain.timeline.Timeline;
import org.jphototagger.domain.xmp.FileXmp;
import org.jphototagger.domain.xmp.Xmp;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ImageFilesRepository.class)
public final class ImageFilesRepositoryImpl implements ImageFilesRepository {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

    @Override
    public List<File> findAllImageFiles() {
        return db.getAllImageFiles();
    }

    @Override
    public long findImageFilesLastModifiedTimestamp(File imageFile) {
        return db.getImageFilesLastModifiedTimestamp(imageFile);
    }

    @Override
    public long findXmpFilesLastModifiedTimestamp(File imageFile) {
        return db.getXmpFilesLastModifiedTimestamp(imageFile);
    }

    @Override
    public int deleteImageFiles(List<File> imageFiles) {
        return db.deleteImageFiles(imageFiles);
    }

    @Override
    public void deleteDcSubject(String dcSubject) {
        db.deleteDcSubject(dcSubject);
    }

    @Override
    public int deleteAbsentImageFiles(ProgressListener listener) {
        return db.deleteAbsentImageFiles(listener);
    }

    @Override
    public int deleteAbsentXmp(ProgressListener listener) {
        return db.deleteAbsentXmp(listener);
    }

    @Override
    public void deleteValueOfJoinedMetaDataValue(MetaDataValue mdValue, String value) {
        db.deleteValueOfJoinedColumn(mdValue, value);
    }

    @Override
    public boolean existsImageFile(File imageFile) {
        return db.existsImageFile(imageFile);
    }

    @Override
    public boolean existsDcSubject(String dcSubject) {
        return db.existsDcSubject(dcSubject);
    }

    @Override
    public boolean existsExifDate(Date date) {
        return db.existsExifDate(date);
    }

    @Override
    public boolean existsMetaDataValue(Object value, MetaDataValue mdValue) {
        return db.existsValueInColumn(value, mdValue);
    }

    @Override
    public boolean existsXMPDateCreated(String date) {
        return db.existsXMPDateCreated(date);
    }

    @Override
    public Set<String> findAllDcSubjects() {
        return db.getAllDcSubjects();
    }

    @Override
    public Set<String> findAllDistinctMetaDataValues(MetaDataValue mdValue) {
        return db.getAllDistinctValuesOfColumn(mdValue);
    }

    @Override
    public Set<File> findAllThumbnailFiles() {
        return db.getAllThumbnailFiles();
    }

    @Override
    public List<String> findDcSubjectsOfImageFile(File imageFile) {
        return db.getDcSubjectsOfImageFile(imageFile);
    }

    @Override
    public Exif findExifOfImageFile(File imageFile) {
        return db.getExifOfImageFile(imageFile);
    }

    @Override
    public ImageFile findImageFileOfFile(File file) {
        return db.getImageFileOfFile(file);
    }

    @Override
    public Set<File> findImageFilesContainingAllDcSubjects(List<? extends String> dcSubjects) {
        return db.getImageFilesContainingAllDcSubjects(dcSubjects);
    }

    @Override
    public Set<File> findImageFilesContainingDcSubject(String dcSubject, boolean includeSynonyms) {
        return db.getImageFilesContainingDcSubject(dcSubject, includeSynonyms);
    }

    @Override
    public Set<File> findImageFilesContainingSomeOfDcSubjects(List<? extends String> dcSubjects) {
        return db.getImageFilesContainingSomeOfDcSubjects(dcSubjects);
    }

    @Override
    public Set<File> findImageFilesContainingAllWordsInMetaDataValue(List<? extends String> words, MetaDataValue mdValue) {
        return db.getImageFilesContainingAllWordsInColumn(words, mdValue);
    }

    @Override
    public List<File> findImageFilesContainingAVauleInMetaDataValue(MetaDataValue mdValue) {
        return db.getImageFilesContainingAVauleInColumn(mdValue);
    }

    @Override
    public Set<File> findImageFilesOfDateTaken(int year, int month, int day) {
        return db.getImageFilesOfDateTaken(year, month, day);
    }

    @Override
    public List<File> findImageFilesOfUnknownDateTaken() {
        return db.getImageFilesOfUnknownDateTaken();
    }

    @Override
    public List<File> findImageFilesWhereMetaDataValueHasExactValue(MetaDataValue mdValue, String exactValue) {
        return db.getImageFilesWhereColumnHasExactValue(mdValue, exactValue);
    }

    @Override
    public List<File> findImageFilesWithoutDataValue(MetaDataValue mdValue) {
        return db.getImageFilesWithoutMetadataInColumn(mdValue);
    }

    @Override
    public Set<String> findNotReferencedDcSubjects() {
        return db.getNotReferencedDcSubjects();
    }

    @Override
    public Timeline findTimeline() {
        return db.getTimeline();
    }

    @Override
    public Xmp findXmpOfImageFile(File imageFile) {
        return db.getXmpOfImageFile(imageFile);
    }

    @Override
    public List<FileXmp> findXmpOfImageFiles(Collection<? extends File> imageFiles) {
        return db.getXmpOfImageFiles(imageFiles);
    }

    @Override
    public boolean saveDcSubject(String dcSubject) {
        return db.insertDcSubject(dcSubject);
    }

    @Override
    public boolean saveOrUpdateExif(File imageFile, Exif exif) {
        return db.insertOrUpdateExif(imageFile, exif);
    }

    @Override
    public boolean saveOrUpdateImageFile(ImageFile imageFile) {
        return db.insertOrUpdateImageFile(imageFile);
    }

    @Override
    public boolean isDcSubjectReferenced(String dcSubject) {
        return db.isDcSubjectReferenced(dcSubject);
    }

    @Override
    public boolean setLastModifiedToXmpSidecarFileOfImageFile(File imageFile, long time) {
        return db.setLastModifiedToXmpSidecarFileOfImageFile(imageFile, time);
    }

    @Override
    public boolean updateImageFile(ImageFile imageFile) {
        return db.updateImageFile(imageFile);
    }

    @Override
    public int updateRenameImageFile(File fromImageFile, File toImageFile) {
        return db.updateRenameImageFile(fromImageFile, toImageFile);
    }

    @Override
    public boolean updateThumbnail(File imageFile, Image thumbnail) {
        return db.updateThumbnail(imageFile, thumbnail);
    }

    @Override
    public Long findIdDcSubject(String dcSubject) {
        return db.getIdDcSubject(dcSubject);
    }

    @Override
    public boolean existsXmpDcSubjectsLink(long idXmp, long idDcSubject) {
        return db.existsXmpDcSubjectsLink(idXmp, idDcSubject);
    }

    @Override
    public int updateAllThumbnails(ProgressListener listener) {
        return db.updateAllThumbnails(listener);
    }

    @Override
    public int updateRenameFilenamesStartingWith(String before, String after, ProgressListener progressListener) {
        return db.updateRenameFilenamesStartingWith(before, after, progressListener);
    }

    @Override
    public boolean saveOrUpdateXmpOfImageFile(File imageFile, Xmp xmp) {
        return db.insertOrUpdateXmpOfImageFile(imageFile, xmp);
    }
}
