package org.jphototagger.image.thumbnail;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.domain.thumbnails.ExternalThumbnailCreationCommand;
import org.jphototagger.domain.thumbnails.ThumbnailCreator;
import org.jphototagger.image.ImagePreferencesKeys;
import org.jphototagger.image.util.ThumbnailCreatorService;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.runtime.External;
import org.jphototagger.lib.runtime.FinishedProcessResult;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.SystemUtil;

import com.imagero.reader.IOParameterBlock;
import com.imagero.reader.ImageProcOptions;
import com.imagero.reader.ImageReader;
import com.imagero.reader.Imagero;

/**
 * @author Elmar Baumann
 */
@ServiceProviders({
    @ServiceProvider(service = ThumbnailCreator.class),
    @ServiceProvider(service = ExternalThumbnailCreationCommand.class)
})
public final class DcrawThumbnailCreator implements ThumbnailCreator, ExternalThumbnailCreationCommand {

    private static final Set<String> SUPPORTED_SUFFIXES_LOWERCASE = new HashSet<String>();
    private static final Logger LOGGER = Logger.getLogger(DcrawThumbnailCreator.class.getName());
    private final Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
    private boolean dcrawResolved;
    private File dcraw;

    static {
        // HOOK RAW filename suffixes
        SUPPORTED_SUFFIXES_LOWERCASE.addAll(Arrays.asList(
                "arw", // Sony (Alpha) RAW
                "cr2", // Canon RAW 2
                "crw", // Canon RAW
                "dcr", // Kodak RAW
                "dng", // Digital Negative
                "mrw", // Minolta RAW
                "nef", // Nikon RAW
                "raw", // Pansonic (some models like DMC-FZ50)
                "rw2", // Pansonic (some models like DMC-GH2)
                "srw"  // Samsung RAW
                ));
    }

    public DcrawThumbnailCreator() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    @Override
    public Image createThumbnail(File file) {
        if (!canCreateThumbnail(file) || !file.exists()) {
            return null;
        }
        resolveDcraw();
        if (dcraw == null) {
            return null;
        }
        FinishedProcessResult output = getExternalOutput(file);
        if (output != null && !output.hasStdErrBytes() && output.hasStdOutBytes()) {
            try {
                int maxLength = ThumbnailCreatorService.readMaxThumbnailWidthFromPreferences();
                IOParameterBlock ioParamBlock = new IOParameterBlock();
                ImageProcOptions procOptions = new ImageProcOptions();
                ioParamBlock.setSource(output.getStdOutBytes());
                procOptions.setSource(ioParamBlock);
                procOptions.setScale(maxLength);
                Image image = Imagero.readImage(procOptions);
                ImageReader reader = procOptions.getImageReader();
                if (reader != null) {
                    reader.close();
                }
                return image;
            } catch (Throwable t) {
                LOGGER.log(Level.SEVERE, null, t);
            }
        }
        return null;
    }

    private FinishedProcessResult getExternalOutput(File file) {
        String command = "\"" + dcraw.getAbsolutePath() + "\" -c -h -T \"" + file.getAbsolutePath() + "\"";
        LOGGER.log(Level.INFO, "Creating thumbnail with dcraw; command: {0}", command);
        return External.executeWaitForTermination(command, getMaxMillisecondsToTerminate());
    }


    @Override
    public String getThumbnailCreationCommand() {
        if (dcraw == null) {
            return null;
        }
        return "\"" + dcraw.getAbsolutePath() + "\" \"%s\" %i";
    }

    @Override
    public boolean isEnabled() {
        resolveDcraw();
        return dcraw != null;
    }

    private long getMaxMillisecondsToTerminate() {
        int maxSeconds = prefs.getInt(ImagePreferencesKeys.KEY_MAX_SECONDS_TO_TERMINATE_EXTERNAL_PROGRAMS);
        if (maxSeconds < 60) {
            maxSeconds = 60;
        }
        return maxSeconds * 1000;
    }

    private void resolveDcraw() {
        if (dcraw != null || dcrawResolved) {
            return;
        }
        LOGGER.info("Trying to resolve dcraw...");
        resolveDcrawFromPreferences();
        if (dcraw == null) {
            resolveDcrawFromFileJptDir();
        }
        dcrawResolved = true;
    }

    private void resolveDcrawFromPreferences() {
        LOGGER.info("Looking for dcraw path in preferences key '" + ImagePreferencesKeys.KEY_DCRAW_FILEPATH + "'");
        String path = prefs.getString(ImagePreferencesKeys.KEY_DCRAW_FILEPATH).trim();
        if (path.isEmpty()) {
            LOGGER.info("Preferences do not contain a key named '" + ImagePreferencesKeys.KEY_DCRAW_FILEPATH + "'");
        } else {
            File candidate = new File(path);
            if (candidate.isFile()) {
                dcraw = candidate;
                LOGGER.log(Level.INFO, "Using dcraw ''{0}''", candidate);
            } else {
                LOGGER.log(Level.INFO, "''{0}'' is not a file", candidate);
            }
        }
    }

    // The dcraw executable should be installed below the JAR of this module,
    // usually <JPhotoTagger installation dir>/lib/dcraw/<osdependend>,
    // e.g. /usr/opt/JPhotoTagger/lib/dcraw/linux32
    private void resolveDcrawFromFileJptDir() {
        LOGGER.info("Looking for dcraw within JPhotoTagger's directory");
        File jarDir = FileUtil.getJarDirectory(DcrawThumbnailCreator.class);
        if (jarDir != null) {
            String architecture = SystemUtil.guessVmArchitecture();
            if (!architecture.isEmpty()) {
                String dcrawSuffix = getSupportedOsDcrawSuffix(architecture);
                if (!dcrawSuffix.isEmpty()) {
                    File candidate = new File(jarDir.getAbsolutePath() + File.separator + dcrawSuffix);
                    if (candidate.isFile()) {
                        dcraw = candidate;
                        LOGGER.log(Level.INFO, "Using dcraw ''{0}''", candidate);
                    } else {
                        LOGGER.log(Level.INFO, "''{0}'' is not a file", candidate);
                    }
                } else {
                    LOGGER.log(Level.INFO, "JPhotoTagger does not have a dcraw executable for {0}", System.getProperty("os.name"));
                }
            } else {
                LOGGER.info("Can't resolve Architecture of the Java Virtual Machine to get dcraw subdirectory");
            }
        } else {
            LOGGER.info("Can't resolve directory of this JAR");
        }
    }

    // HOOK Compiled system dependend dcraw executable, if OS count > 3 Map instead of if-else-switch
    private String getSupportedOsDcrawSuffix(String architecture) {
        String osNameLc = System.getProperty("os.name").toLowerCase();
        String dcrawSubdir = "dcraw" + File.separator;
        if (osNameLc.contains("windows")) {
            return dcrawSubdir + "win" + architecture + File.separator + "dcraw.exe";
        } else if (osNameLc.contains("linux")) {
            return dcrawSubdir + "linux" + architecture + File.separator + "dcraw";
        } else if (osNameLc.contains("mac")) {
            return dcrawSubdir + "mac" + architecture + File.separator + "dcraw";
        } else {
            return "";
        }
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        if (ImagePreferencesKeys.KEY_DCRAW_FILEPATH.equals(evt.getKey())) {
            dcraw = null;
            resolveDcraw();
        }
    }

    @Override
    public boolean canCreateThumbnail(File file) {
        String suffix = FileUtil.getSuffix(file);
        return !suffix.isEmpty() && SUPPORTED_SUFFIXES_LOWERCASE.contains(suffix.toLowerCase());
    }

    @Override
    public Set<String> getAllSupportedFileTypeSuffixes() {
        return getSupportedRawFormatFileTypeSuffixes();
    }

    @Override
    public Set<String> getSupportedRawFormatFileTypeSuffixes() {
        return Collections.unmodifiableSet(SUPPORTED_SUFFIXES_LOWERCASE);
    }

    @Override
    public Component getSettingsComponent() {
        return new DcrawThumbnailCreatorSettingsPanel();
    }

    @Override
    public int getPosition() {
        return 100;
    }

    @Override
    public String getDisplayName() {
        return Bundle.getString(DcrawThumbnailCreator.class, "DcrawThumbnailCreator.DisplayName");
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
