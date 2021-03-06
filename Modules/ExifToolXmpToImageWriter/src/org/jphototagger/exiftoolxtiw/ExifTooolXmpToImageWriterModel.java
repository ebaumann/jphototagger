package org.jphototagger.exiftoolxtiw;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.domain.event.listener.ProgressListenerSupport;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.runtime.External;
import org.jphototagger.lib.runtime.ProcessResult;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExifTooolXmpToImageWriterModel {

    private final Settings settings = new Settings();
    private final Collection<File> imageFiles = new LinkedHashSet<>();
    private final XmpSidecarFileResolver sidecarFileResolver = Lookup.getDefault().lookup(XmpSidecarFileResolver.class);
    private final Collection<String> acceptedSuffixesLc = settings.getFileSuffixesLcNoDot();
    private final ProgressListenerSupport pls = new ProgressListenerSupport();
    private final ProgressEvent progressEvent = new ProgressEvent.Builder().source(this).build();
    private long maxMillisecondsUntilInterrupt = 60000;
    private volatile boolean cancel;
    private File xmpFile;

    public void addProgressListener(ProgressListener pl) {
        Objects.requireNonNull(this, "this == null");

        pls.add(pl);
    }

    public void removeProgressListener(ProgressListener pl) {
        Objects.requireNonNull(pl, "pl == null");

        pls.remove(pl);
    }

    public void setMaxMillisecondsUntilInterrupt(long milliseconds) {
        this.maxMillisecondsUntilInterrupt = milliseconds;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void setImageFiles(Collection<? extends File> imageFiles) {
        Objects.requireNonNull(imageFiles, "imageFiles == null");

        this.imageFiles.clear();
        this.imageFiles.addAll(imageFiles);
    }

    /**
     * Sets the XMP file when only one image file shall be processed. Not
     * required (auto resolution).
     *
     * @param xmpFile XMP file
     */
    public void setXmpFile(File xmpFile) {
        this.xmpFile = xmpFile;
    }

    private void notifyStarted(int count) {
        progressEvent.setMinimum(0);
        progressEvent.setMaximum(count);
        progressEvent.setValue(0);
        pls.notifyStarted(progressEvent);
    }

    private void notifyPerformed(int current, int count, File file) {
        String info = "[" + current + "/" + count + "] " + file.getAbsolutePath();
        Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.INFO, "Performing {0}", info);
        progressEvent.setValue(current);
        progressEvent.setStringToPaint(info);
        pls.notifyPerformed(progressEvent);
    }

    private void notifyEnded(int count) {
        Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.INFO, "Processed {0} images", count);
        pls.notifyEnded(progressEvent);
    }

    /**
     * @return count of processed files
     */
    public int execute() {
        if (!ExifToolCommon.checkCanExecute(settings, true)) {
            return 0;
        }

        List<File> imageFilesCopy = new ArrayList<>(imageFiles);
        final int count = imageFilesCopy.size();
        int current = 1;
        int countProcessed = 0;

        try {
            notifyStarted(count);
            for (File imageFile : imageFilesCopy) {
                if (cancel) {
                    Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.INFO, "Cancelling processing image files");
                    break;
                }
                notifyPerformed(current, count, imageFile);
                if (processImageFile(imageFile)) {
                    countProcessed++;
                }
                current++;
            }
        } finally {
            notifyEnded(current);
        }

        return countProcessed;
    }

    private boolean processImageFile(File imageFile) {
        if (!checkSuffix(imageFile)) {
            Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.INFO, "Image file does not have a defined suffix: ''{0}''. Skipping.", imageFile);
            return false;
        }

        File xmp = xmpFile == null || imageFiles.size() > 1
                ? sidecarFileResolver.getXmpSidecarFileOrNullIfNotExists(imageFile)
                : xmpFile;

        if (xmp == null) {
            Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.WARNING, "Image file ''{0}'' does not have a XMP sidecar file. Skipping.", imageFile);
            return false;
        }

        Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.INFO, "Writing contents of XMP file ''{0}'' into image file ''{1}''", new Object[]{xmp, imageFile});

        String[] command = getCommand(xmp.getAbsolutePath(), imageFile.getAbsolutePath());
        Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.INFO, "Executing command {0}", ExifToolCommon.toString(command));
        ProcessResult processResult = External.executeWaitForTermination(command, maxMillisecondsUntilInterrupt);

        boolean terminatedWithErrors = processResult == null || processResult.getExitValue() != 0;
        if (terminatedWithErrors) {
            ExifToolCommon.logError(command, processResult);
        } else {
            ExifToolCommon.logSuccess(command);
        }

        return true;
    }

    private boolean checkSuffix(File imageFile) {
        String filenameLc = FileUtil.getSuffix(imageFile).toLowerCase();

        return acceptedSuffixesLc.contains(filenameLc);
    }

    private String[] getCommand(String xmpFile, String imageFile) {
        List<String> cmdList = new ArrayList<>();

        cmdList.add(settings.getExifToolFilePath());
        cmdList.add("-tagsfromfile");
        cmdList.add(xmpFile);
        cmdList.add("-codedcharacterset=utf8");
        cmdList.add("-mwg:all");

        if (!settings.isCreateBackupFile()) {
            cmdList.add("-overwrite_original");
        }

        cmdList.add(imageFile);

        return cmdList.toArray(new String[cmdList.size()]);
    }
}
