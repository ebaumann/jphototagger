package org.jphototagger.image.thumbnail;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.jphototagger.domain.thumbnails.ThumbnailCreator;
import org.jphototagger.image.util.ThumbnailCreatorService;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailCreator.class)
public final class PngThumbnailCreator implements ThumbnailCreator {

    private static final Set<String> SUPPORTED_FILE_TYPES = new HashSet<>(Arrays.asList("png"));

    @Override
    public Image createThumbnail(File file) {
        int maxLength = ThumbnailCreatorService.readMaxThumbnailWidthFromPreferences();
        return ThumbnailUtil.createThumbnailWithJavaImageIO(file, maxLength);
    }

    @Override
    public boolean canCreateThumbnail(File file) {
        return file.getName().toLowerCase().endsWith(".png");
    }

    @Override
    public Set<String> getAllSupportedFileTypeSuffixes() {
        return Collections.unmodifiableSet(SUPPORTED_FILE_TYPES);
    }

    @Override
    public Set<String> getSupportedRawFormatFileTypeSuffixes() {
        return Collections.<String>emptySet();
    }

    @Override
    public Component getSettingsComponent() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(PngThumbnailCreator.class, "PngThumbnailCreator.Displayname");
    }

    @Override
    public int getPriority() {
        return 50;
    }

    @Override
    public int getPosition() {
        return 500;
    }
}
