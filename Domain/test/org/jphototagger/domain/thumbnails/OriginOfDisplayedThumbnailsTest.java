package org.jphototagger.domain.thumbnails;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * @author Elmar Baumann
 */
public class OriginOfDisplayedThumbnailsTest {

    @Test
    public void testCanDeleteImagesFromFileSystem() {
        for (OriginOfDisplayedThumbnails origin : OriginOfDisplayedThumbnails.values()) {
            assertTrue(origin.canDeleteImagesFromFileSystem());
        }
    }

    @Test
    public void testCanInsertImagesFromFileSystem() {
        assertTrue(OriginOfDisplayedThumbnails.FILES_IN_SAME_DIRECTORY.canDeleteImagesFromFileSystem());
        assertTrue(OriginOfDisplayedThumbnails.FILES_IN_SAME_FAVORITE_DIRECTORY.canDeleteImagesFromFileSystem());
    }

    @Test
    public void testIsUniqueFileSystemDirectory() {
        assertTrue(OriginOfDisplayedThumbnails.FILES_IN_SAME_DIRECTORY.isInSameFileSystemDirectory());
        assertTrue(OriginOfDisplayedThumbnails.FILES_IN_SAME_FAVORITE_DIRECTORY.isInSameFileSystemDirectory());
    }

    @Test
    public void testIsSortable() {
        for (OriginOfDisplayedThumbnails origin : OriginOfDisplayedThumbnails.values()) {
            if (OriginOfDisplayedThumbnails.FILES_OF_AN_IMAGE_COLLECTION.equals(origin)) {
                assertFalse(origin.isSortable());
            } else {
                assertTrue(origin.isSortable());
            }
        }
    }

    @Test
    public void testIsFilterable() {
        for (OriginOfDisplayedThumbnails origin : OriginOfDisplayedThumbnails.values()) {
            if (OriginOfDisplayedThumbnails.FILES_OF_AN_IMAGE_COLLECTION.equals(origin)) {
                assertFalse(origin.isFilterable());
            } else {
                assertTrue(origin.isFilterable());
            }
        }
    }
}
