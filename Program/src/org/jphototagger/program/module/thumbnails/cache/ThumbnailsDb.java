package org.jphototagger.program.module.thumbnails.cache;

import java.awt.Image;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.domain.thumbnails.ThumbnailsDirectoryProvider;
import org.jphototagger.image.util.ImageUtil;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailsDb {

    private static final Logger LOGGER = Logger.getLogger(ThumbnailsDb.class.getName());
    private static final DB THUMBNAILS_DB;
    private static final Map<String, Thumbnail> THUMBNAILS;

    static {
        ThumbnailsDirectoryProvider provider = Lookup.getDefault().lookup(ThumbnailsDirectoryProvider.class);
        File thumbnailsDirectory = provider.getThumbnailsDirectory();
        String thumbnailsDir = thumbnailsDirectory.getAbsolutePath();
        File thumbnailsFile = new File(thumbnailsDir + File.separator + "JPhotoTaggerTumbnailsDb");
        LOGGER.log(Level.INFO, "Opening thumbnails database ''{0}''", thumbnailsFile);
        THUMBNAILS_DB = DBMaker.newFileDB(thumbnailsFile)
                .closeOnJvmShutdown()
                .make();
        THUMBNAILS = THUMBNAILS_DB.getHashMap("thumbnails");
    }

    static boolean existsThumbnail(File imageFile) {
        try {
            return THUMBNAILS.containsKey(createKey(imageFile));
        } catch (Throwable t) {
            Logger.getLogger(ThumbnailsDb.class.getName()).log(Level.SEVERE, null, t);
            return false;
        }
    }

    static Image findThumbnail(File imageFile) {
        try {
            Thumbnail thumbnail = THUMBNAILS.get(createKey(imageFile));
            return thumbnail == null
                    ? null
                    : thumbnail.createImage();
        } catch (Throwable t) {
            Logger.getLogger(ThumbnailsDb.class.getName()).log(Level.SEVERE, null, t);
            return null;
        }
    }

    static boolean deleteThumbnail(File imageFile) {
        try {
            LOGGER.log(Level.FINE, "Deleting thumbnail for image file {0}", imageFile);
            if (THUMBNAILS.remove(createKey(imageFile)) != null) {
                THUMBNAILS_DB.commit();
            }
            return true;
        } catch (Throwable t) {
            Logger.getLogger(ThumbnailsDb.class.getName()).log(Level.SEVERE, null, t);
            rollback();
            return false;
        }
    }

    private static void rollback() {
        try {
            THUMBNAILS_DB.rollback();
        } catch (Throwable t) {
            Logger.getLogger(ThumbnailsDb.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    static void insertThumbnail(Image thumbnail, File imageFile) {
        byte[] imageBytes = ImageUtil.getByteArray(thumbnail, "jpeg");
        if (imageBytes != null) {
            LOGGER.log(Level.FINE, "Inserting thumbnail for image file {0}", imageFile);
            Thumbnail tn = new Thumbnail(imageBytes, imageFile.length(), imageFile.lastModified());
            THUMBNAILS.put(createKey(imageFile), tn);
            try {
                THUMBNAILS_DB.commit();
            } catch (Throwable t) {
                Logger.getLogger(ThumbnailsDb.class.getName()).log(Level.SEVERE, null, t);
                rollback();
            }
        }
    }

    static boolean hasUpToDateThumbnail(File imageFile) {
        try {
            Thumbnail thumbnail = THUMBNAILS.get(createKey(imageFile));
            return thumbnail == null
                    ? false
                    : thumbnail.getImageFileLength() == imageFile.length()
                        && thumbnail.getImageFileLastModified() == imageFile.lastModified();
        } catch (Throwable t) {
            Logger.getLogger(ThumbnailsDb.class.getName()).log(Level.SEVERE, null, t);
            return false;
        }
    }

    static boolean renameThumbnail(File fromImageFile, File toImageFile) {
        try {
            Thumbnail tn = THUMBNAILS.get(createKey(fromImageFile));
            if (tn != null) {
                LOGGER.log(Level.FINE, "Renaming Thumbnail from image file ''{0}'' to image file {1}", new Object[]{fromImageFile, toImageFile});
                THUMBNAILS.remove(createKey(fromImageFile));
                THUMBNAILS.put(createKey(toImageFile), new Thumbnail(tn));
                THUMBNAILS_DB.commit();
                return true;
            }
            return false;
        } catch (Throwable t) {
            Logger.getLogger(ThumbnailsDb.class.getName()).log(Level.SEVERE, null, t);
            rollback();
            return false;
        }
    }

    static Set<String> getImageFilenames() {
        return THUMBNAILS.keySet();
    }

    static void close() {
        THUMBNAILS_DB.close();
    }

    static void compact() {
        try {
            LOGGER.info("Compacting thumbnails database");
            THUMBNAILS_DB.compact();
        } catch (Throwable t) {
            Logger.getLogger(ThumbnailsDb.class.getName()).log(Level.SEVERE, null, t);
        }
    }

    private static String createKey(File imageFile) {
        return imageFile.getAbsolutePath();
    }

    private ThumbnailsDb() {
    }
}
