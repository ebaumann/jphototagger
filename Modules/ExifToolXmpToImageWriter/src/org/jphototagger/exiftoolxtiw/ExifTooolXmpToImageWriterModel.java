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
import org.jphototagger.lib.util.StringUtil;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExifTooolXmpToImageWriterModel {

    private final Settings settings = new Settings();
    private final Collection<File> imageFiles = new LinkedHashSet<File>();
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
        if (!checkCanExecute(true)) {
            return 0;
        }

        List<File> imageFilesCopy = new ArrayList<File>(imageFiles);
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
            Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.INFO, "Image file ''{0}'' does not have a XMP sidecar file. Skipping.", imageFile);
            return false;
        }

        Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.INFO, "Writing contents of XMP file ''{0}'' into image file ''{1}''", new Object[]{xmp, imageFile});

        String[] command = getCommand(xmp.getAbsolutePath(), imageFile.getAbsolutePath());
        Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.INFO, "Executing command {0}", toString(command));
        ProcessResult processResult = External.executeWaitForTermination(command, maxMillisecondsUntilInterrupt);

        boolean terminatedWithErrors = processResult == null || processResult.getExitValue() != 0;
        if (terminatedWithErrors) {
            logError(toString(command), processResult);
        } else {
            Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.INFO, "Successfully written contents of XMP file ''{0}'' into image file ''{1}''", new Object[]{xmp, imageFile});
        }

        return true;
    }

    private String toString(String[] command) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < command.length; i++) {
            String cmdToken = command[i];
            if (i == 0) {
                sb.append(cmdToken);
            } else {
                sb.append(" \"").append(cmdToken).append("\"");
            }
        }

        return sb.toString();
    }

    private boolean checkSuffix(File imageFile) {
        String filenameLc = FileUtil.getSuffix(imageFile).toLowerCase();

        return acceptedSuffixesLc.contains(filenameLc);
    }

    private void logError(String command, ProcessResult processResult) {
        Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.WARNING, "Error executing command  ''{0}'': {1}!", new Object[]{
                    command, (processResult == null)
                    ? "?"
                    : new String(processResult.getStdErrBytes())});
    }

    private String[] getCommand(String xmpFile, String imageFile) {
        List<String> cmdList = new ArrayList<String>();

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

    boolean checkCanExecute(boolean log) {
        if (!settings.isSelfResponsible()) {
            if (log) {
                Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.WARNING, "User does not take self-responsibility of modifying image files. Cancelling.");
            }
            return false;
        }

        if (getExifToolFilePath() == null) {
            if (log) {
                Logger.getLogger(ExifTooolXmpToImageWriterModel.class.getName()).log(Level.WARNING, "Exif tool excecutable not accessible: {0}. Cancelling.", settings.getExifToolFilePath());
            }
            return false;
        }

        return true;
    }

    private String getExifToolFilePath() {
        String exifToolFilePath = settings.getExifToolFilePath();
        if (!StringUtil.hasContent(exifToolFilePath)) {
            return null;
        }
        File et = new File(exifToolFilePath);
        return et.isFile()
                ? et.getAbsolutePath()
                : null;
    }
}
