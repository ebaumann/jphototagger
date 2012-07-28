package org.jphototagger.xmp;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;
import org.jphototagger.lib.io.filefilter.FilenameIgnoreCaseFileFilter;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = XmpSidecarFileResolver.class)
public final class XmpSidecarFileResolverImpl implements XmpSidecarFileResolver {

    private boolean useLongXmpSidecarFilenames;

    public XmpSidecarFileResolverImpl() {
        setLongSidecarFilename();
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);
    }

    private void setLongSidecarFilename() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        if (prefs != null) {
            if (prefs.containsKey(XmpPreferences.KEY_USE_LONG_SIDECAR_FILENAMES)) {
                synchronized (this) {
                    useLongXmpSidecarFilenames = prefs.getBoolean(XmpPreferences.KEY_USE_LONG_SIDECAR_FILENAMES);
                }
            }
        }
    }

    @Override
    public synchronized File suggestXmpSidecarFile(File contentFile) {
        if (contentFile == null) {
            throw new NullPointerException("contentFile == null");
        }
        if (useLongXmpSidecarFilenames) {
            return suggestLongSidecarFile(contentFile);
        } else {
            return suggestDefaultSidecarFile(contentFile);
        }
    }

    @Override
    public File getXmpSidecarFileOrNullIfNotExists(File contentFile) {
        if (contentFile == null) {
            throw new NullPointerException("contentFile == null");
        }
        return useLongXmpSidecarFilenames
                ? getLongXmpSidecarFileOrNullIfNotExists(contentFile)
                : getDefaultXmpSidecarFileOrNullIfNotExists(contentFile);
        }

    @Override
    public boolean hasXmpSidecarFile(File contentFile) {
        if (contentFile == null) {
            throw new NullPointerException("contentFile == null");
        }
        File sidecarFile = getXmpSidecarFileOrNullIfNotExists(contentFile);
        return sidecarFile != null;
    }

    @Override
    public boolean isUseLongXmpSidecarFilenames() {
        return useLongXmpSidecarFilenames;
    }

    @Override
    public File suggestDefaultSidecarFile(File contentFile) {
        if (contentFile == null) {
            throw new NullPointerException("contentFile == null");
        }
        String absolutePath = contentFile.getAbsolutePath();
        int indexExtension = absolutePath.lastIndexOf('.');
        File suggestedSidecarFile = indexExtension > 0
                ? new File(absolutePath.substring(0, indexExtension + 1) + "xmp")
                : new File(absolutePath + ".xmp");
        File foundSidecarFile = findSidecarFile(suggestedSidecarFile);
        return foundSidecarFile == null
                ? suggestedSidecarFile
                : foundSidecarFile;
    }

    @Override
    public File suggestLongSidecarFile(File contentFile) {
        if (contentFile == null) {
            throw new NullPointerException("contentFile == null");
        }
        File suggestedSidecarFile = new File(contentFile.getAbsolutePath() + ".xmp");
        File foundSidecarFile = findSidecarFile(suggestedSidecarFile);
        return foundSidecarFile == null
                ? suggestedSidecarFile
                : foundSidecarFile;
    }

    @Override
    public File getDefaultXmpSidecarFileOrNullIfNotExists(File contentFile) {
        if (contentFile == null) {
            throw new NullPointerException("contentFile == null");
        }
        File sidecarFile = suggestDefaultSidecarFile(contentFile);
        return findSidecarFile(sidecarFile);
    }

    @Override
    public File findSidecarFile(File sidecarFile) {
        if (sidecarFile.isFile()) {
            return sidecarFile;
        }
        File directory = sidecarFile.getParentFile();
        if (directory == null) {
            return null;
        }
        try {
            File[] sidecarFiles = directory.listFiles(new FilenameIgnoreCaseFileFilter(sidecarFile.getName()));
            if (sidecarFiles != null && sidecarFiles.length > 1) {
                Logger.getLogger(XmpSidecarFileResolverImpl.class.getName()).log(Level.WARNING, "Sidecar file ''{0}'' is ambigious: {1}", new Object[]{sidecarFile, sidecarFiles});
            }
            return sidecarFiles == null
                    ? null
                    : sidecarFiles.length > 0
                    ? sidecarFiles[0]
                : null;
        } catch (Throwable t) {
            Logger.getLogger(XmpSidecarFileResolverImpl.class.getName()).log(Level.SEVERE, null, t);
            return null;
    }
    }

    @Override
    public File getLongXmpSidecarFileOrNullIfNotExists(File contentFile) {
        if (contentFile == null) {
            throw new NullPointerException("contentFile == null");
        }
        File sidecarFile = suggestLongSidecarFile(contentFile);
        return findSidecarFile(sidecarFile);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void preferencesChanged(PreferencesChangedEvent evt) {
        String key = evt.getKey();
        if (XmpPreferences.KEY_USE_LONG_SIDECAR_FILENAMES.equals(key)) {
            synchronized (this) {
                useLongXmpSidecarFilenames = (Boolean) evt.getNewValue();
            }
        }
    }
}
