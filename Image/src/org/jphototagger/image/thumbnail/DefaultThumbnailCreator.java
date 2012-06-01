package org.jphototagger.image.thumbnail;

import java.awt.Component;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.filetypes.UserDefinedFileType;
import org.jphototagger.domain.repository.UserDefinedFileTypesRepository;
import org.jphototagger.domain.thumbnails.ThumbnailCreator;
import org.jphototagger.image.ImageFileType;
import org.jphototagger.image.ImagePreferencesKeys;
import org.jphototagger.image.util.ThumbnailCreatorService;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.runtime.External;
import org.jphototagger.lib.runtime.FinishedProcessResult;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailCreator.class)
public final class DefaultThumbnailCreator implements ThumbnailCreator {

    private static final Logger LOGGER = Logger.getLogger(DefaultThumbnailCreator.class.getName());
    private static final Set<String> SUPPORTED_SUFFIXES_LOWERCASE = new HashSet<String>();
    private static final Set<String> RGB_IMAGE_SUFFIXES_LOWERCASE = new HashSet<String>();
    private static final Set<String> RAW_FORMAT_SUFFIXES_LOWERCASE = new HashSet<String>();
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private final UserDefinedFileTypesRepository userDefinedFileTypesRepository = Lookup.getDefault().lookup(UserDefinedFileTypesRepository.class);
    private final ThumbnailCreationStrategyProvider thumbnailCreationStrategyProvider = Lookup.getDefault().lookup(ThumbnailCreationStrategyProvider.class);

    static {
        RGB_IMAGE_SUFFIXES_LOWERCASE.add("jpeg"); // Joint Photographic Experts Group
        RGB_IMAGE_SUFFIXES_LOWERCASE.add("jpg");  // Joint Photographic Experts Group
        RGB_IMAGE_SUFFIXES_LOWERCASE.add("tif");  // Tagged Image File Format
        RGB_IMAGE_SUFFIXES_LOWERCASE.add("tiff"); // Tagged Image File Format

        RAW_FORMAT_SUFFIXES_LOWERCASE.add("arw"); // Sony (Alpha) RAW
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("cr2"); // Canon RAW 2
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("crw"); // Canon RAW
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("dcr"); // Kodak RAW
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("dng"); // Digital Negative
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("mrw"); // Minolta RAW
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("nef"); // Nikon RAW
        RAW_FORMAT_SUFFIXES_LOWERCASE.add("srw"); // Samsung RAW

        SUPPORTED_SUFFIXES_LOWERCASE.addAll(RGB_IMAGE_SUFFIXES_LOWERCASE);
        SUPPORTED_SUFFIXES_LOWERCASE.addAll(RAW_FORMAT_SUFFIXES_LOWERCASE);
    }

    @Override
    public Image createThumbnail(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (!file.exists()) {
            return null;
        }
        if (isUserDefinedFileType(file)) {
            return createUserDefinedThumbnail(file);
        }
        if (!canCreateThumbnail(file)) {
            return null;
        }
        Image thumbnail = null;
        int maxLength = ThumbnailCreatorService.readMaxThumbnailWidthFromPreferences();
        if (isCreateThumbnailWithExternalApplication()) {
            String createCommand = prefs.getString(ImagePreferencesKeys.KEY_THUMBNAIL_CREATION_EXTERNAL_COMMAND);
            thumbnail = createThumbnailWithExternalApplication(file, createCommand, maxLength);
        }
        if (thumbnail == null && ImageFileType.isJpegFile(file.getName())) {
            thumbnail = ThumbnailUtil.createThumbnailWithJavaImageIO(file, maxLength);
        }
        if (thumbnail == null && !isRawFile(file)) {
            thumbnail = ThumbnailUtil.createThumbnailWithImagero(file, maxLength);
        }
        return thumbnail;
    }

    private Image createUserDefinedThumbnail(File file) {
        String suffix = FileUtil.getSuffix(file);
        UserDefinedFileType fileType = userDefinedFileTypesRepository.findUserDefinedFileTypeBySuffix(suffix);
        if (fileType == null || !fileType.isExternalThumbnailCreator()) {
            return IconUtil.getIconImage("/org/jphototagger/program/resource/images/user_defined_file_type.jpg");
        } else {
            int maxLength = ThumbnailCreatorService.readMaxThumbnailWidthFromPreferences();
            String createCommand = prefs.getString(ImagePreferencesKeys.KEY_THUMBNAIL_CREATION_EXTERNAL_COMMAND);
            return createThumbnailWithExternalApplication(file, createCommand, maxLength);
        }
    }

    private Image createThumbnailWithExternalApplication(File file, String command, int maxLength) {
        LOGGER.log(Level.INFO, "Creating thumbnail of file ''{0}'' with external program. The length of the thumbnail''s width will be{1} pixels", new Object[]{file, maxLength});
        String cmd = command.replace("%s", file.getAbsolutePath()).replace("%i", Integer.toString(maxLength));
        Image image = null;
        LOGGER.log(Level.FINEST, "Creating thumbnail with external application. Command: ''{0}''", cmd);
        FinishedProcessResult output = External.executeWaitForTermination(cmd, getMaxSecondsToTerminateExternalPrograms() * 1000);
        if (output == null) {
            return null;
        }
        byte[] stdout = output.getStdOutBytes();
        if (stdout != null) {
            try {
                image = javax.imageio.ImageIO.read(new ByteArrayInputStream(stdout));
            } catch (Exception ex) {
                Logger.getLogger(ThumbnailUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (output.getStdErrBytes() != null) {
            logErrorStream(file, output);
        }
        return image;
    }

    private int getMaxSecondsToTerminateExternalPrograms() {
        int seconds = prefs.containsKey(ImagePreferencesKeys.KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
                ? prefs.getInt(ImagePreferencesKeys.KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS)
                : 60;
        int minSeconds = 10;

        return seconds > minSeconds
                ? seconds
                : minSeconds;
    }

    private static void logErrorStream(File imageFile, FinishedProcessResult output) {
        byte[] errorStreamBytes = output.getStdErrBytes();
        String errorMessage = ((errorStreamBytes == null)
                ? ""
                : new String(errorStreamBytes).trim());
        if (!errorMessage.isEmpty()) {
            LOGGER.log(Level.WARNING, "Program error message while creating a thumbnail of file ''{0}'': ''{1}''", new Object[]{imageFile, errorMessage});
        }
    }

    private boolean isUserDefinedFileType(File file) {
        String suffix = FileUtil.getSuffix(file);
        return userDefinedFileTypesRepository.existsUserDefinedFileTypeWithSuffix(suffix);
    }

    private boolean isRawFile(File file) {
        String suffix = FileUtil.getSuffix(file).toLowerCase();
        return RAW_FORMAT_SUFFIXES_LOWERCASE.contains(suffix);
    }

    private boolean isCreateThumbnailWithExternalApplication() {
        ThumbnailCreationStrategy strategy = thumbnailCreationStrategyProvider.getThumbnailCreationStrategy();
        return ThumbnailCreationStrategy.EXTERNAL_APP.equals(strategy);
    }

    @Override
    public boolean canCreateThumbnail(File file) {
        return externalAppCreatesThumbnails() || suffixIsSupported(file) || isUserDefinedFileType(file);
    }

    private boolean externalAppCreatesThumbnails() {
        ThumbnailCreationStrategy strategy = thumbnailCreationStrategyProvider.getThumbnailCreationStrategy();
        return ThumbnailCreationStrategy.EXTERNAL_APP.equals(strategy);
    }

    private boolean suffixIsSupported(File file) {
        String suffix = FileUtil.getSuffix(file);
        String suffixLowerCase = suffix.toLowerCase();
        return RGB_IMAGE_SUFFIXES_LOWERCASE.contains(suffixLowerCase);
    }

    @Override
    public Set<String> getAllSupportedFileTypeSuffixes() {
        return Collections.unmodifiableSet(SUPPORTED_SUFFIXES_LOWERCASE);
    }

    @Override
    public Set<String> getSupportedRawFormatFileTypeSuffixes() {
        return Collections.unmodifiableSet(RAW_FORMAT_SUFFIXES_LOWERCASE);
    }

    @Override
    public Component getSettingsComponent() {
        return null;
    }

    @Override
    public int getPosition() {
        return 0;
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(DefaultThumbnailCreator.class, "ThumbnailCreatorImpl.DisplayName");
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
