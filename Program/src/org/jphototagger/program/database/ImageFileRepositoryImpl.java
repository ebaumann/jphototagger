package org.jphototagger.program.database;

import java.awt.Image;
import java.io.File;
import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.jphototagger.api.event.ProgressListener;
import org.jphototagger.domain.database.Column;
import org.jphototagger.domain.exif.Exif;
import org.jphototagger.domain.image.ImageFile;
import org.jphototagger.domain.repository.ImageFileRepository;
import org.jphototagger.domain.timeline.Timeline;
import org.jphototagger.domain.xmp.FileXmp;
import org.jphototagger.domain.xmp.Xmp;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ImageFileRepository.class)
public final class ImageFileRepositoryImpl implements ImageFileRepository {

    private final DatabaseImageFiles db = DatabaseImageFiles.INSTANCE;

    @Override
    public List<File> getAllImageFiles() {
        return db.getAllImageFiles();
    }

    @Override
    public long getImageFilesLastModifiedTimestamp(File imageFile) {
        return db.getImageFilesLastModifiedTimestamp(imageFile);
    }

    @Override
    public long getXmpFilesLastModifiedTimestamp(File imageFile) {
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
    public void deleteValueOfJoinedColumn(Column column, String value) {
        db.deleteValueOfJoinedColumn(column, value);
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
    public boolean existsValueInColumn(Object value, Column column) {
        return db.existsValueInColumn(value, column);
    }

    @Override
    public boolean existsXMPDateCreated(String date) {
        return db.existsXMPDateCreated(date);
    }

    @Override
    public Set<String> getAllDcSubjects() {
        return db.getAllDcSubjects();
    }

    @Override
    public Set<String> getAllDistinctValuesOfColumn(Column column) {
        return db.getAllDistinctValuesOfColumn(column);
    }

    @Override
    public Set<File> getAllThumbnailFiles() {
        return db.getAllThumbnailFiles();
    }

    @Override
    public List<String> getDcSubjectsOfImageFile(File imageFile) {
        return db.getDcSubjectsOfImageFile(imageFile);
    }

    @Override
    public Exif getExifOfImageFile(File imageFile) {
        return db.getExifOfImageFile(imageFile);
    }

    @Override
    public ImageFile getImageFileOfFile(File file) {
        return db.getImageFileOfFile(file);
    }

    @Override
    public Set<File> getImageFilesContainingAllDcSubjects(List<? extends String> dcSubjects) {
        return db.getImageFilesContainingAllDcSubjects(dcSubjects);
    }

    @Override
    public Set<File> getImageFilesContainingDcSubject(String dcSubject, boolean includeSynonyms) {
        return db.getImageFilesContainingDcSubject(dcSubject, includeSynonyms);
    }

    @Override
    public Set<File> getImageFilesContainingSomeOfDcSubjects(List<? extends String> dcSubjects) {
        return db.getImageFilesContainingSomeOfDcSubjects(dcSubjects);
    }

    @Override
    public Set<File> getImageFilesContainingAllWordsInColumn(List<? extends String> words, Column column) {
        return db.getImageFilesContainingAllWordsInColumn(words, column);
    }

    @Override
    public List<File> getImageFilesContainingAVauleInColumn(Column column) {
        return db.getImageFilesContainingAVauleInColumn(column);
    }

    @Override
    public Set<File> getImageFilesOfDateTaken(int year, int month, int day) {
        return db.getImageFilesOfDateTaken(year, month, day);
    }

    @Override
    public List<File> getImageFilesOfUnknownDateTaken() {
        return db.getImageFilesOfUnknownDateTaken();
    }

    @Override
    public List<File> getImageFilesWhereColumnHasExactValue(Column column, String exactValue) {
        return db.getImageFilesWhereColumnHasExactValue(column, exactValue);
    }

    @Override
    public List<File> getImageFilesWithoutMetadataInColumn(Column column) {
        return db.getImageFilesWithoutMetadataInColumn(column);
    }

    @Override
    public Set<String> getNotReferencedDcSubjects() {
        return db.getNotReferencedDcSubjects();
    }

    @Override
    public Timeline getTimeline() {
        return db.getTimeline();
    }

    @Override
    public Xmp getXmpOfImageFile(File imageFile) {
        return db.getXmpOfImageFile(imageFile);
    }

    @Override
    public List<FileXmp> getXmpOfImageFiles(Collection<? extends File> imageFiles) {
        return db.getXmpOfImageFiles(imageFiles);
    }

    @Override
    public boolean insertDcSubject(String dcSubject) {
        return db.insertDcSubject(dcSubject);
    }

    @Override
    public boolean insertOrUpdateExif(File imageFile, Exif exif) {
        return db.insertOrUpdateExif(imageFile, exif);
    }

    @Override
    public boolean insertOrUpdateImageFile(ImageFile imageFile) {
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
}
