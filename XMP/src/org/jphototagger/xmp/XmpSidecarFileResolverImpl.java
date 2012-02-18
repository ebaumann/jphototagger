package org.jphototagger.xmp;

import java.io.File;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.domain.metadata.xmp.XmpSidecarFileResolver;

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
        if (useLongXmpSidecarFilenames) {
            return getLongXmpSidecarFileOrNullIfNotExists(contentFile);
        } else {
            return getDefaultXmpSidecarFileOrNullIfNotExists(contentFile);
        }
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
        if (indexExtension > 0) {
            return new File(absolutePath.substring(0, indexExtension + 1) + "xmp");
        } else {
            return new File(absolutePath + ".xmp");
        }
    }

    @Override
    public File suggestLongSidecarFile(File contentFile) {
        if (contentFile == null) {
            throw new NullPointerException("contentFile == null");
        }
        return new File(contentFile.getAbsolutePath() + ".xmp");
    }

    @Override
    public File getDefaultXmpSidecarFileOrNullIfNotExists(File contentFile) {
        if (contentFile == null) {
            throw new NullPointerException("contentFile == null");
        }
        File sidecarFile = suggestDefaultSidecarFile(contentFile);
        return sidecarFile.isFile()
                ? sidecarFile
                : null;
    }

    @Override
    public File getLongXmpSidecarFileOrNullIfNotExists(File contentFile) {
        if (contentFile == null) {
            throw new NullPointerException("contentFile == null");
        }
        File sidecarFile = suggestLongSidecarFile(contentFile);
        return sidecarFile.isFile()
                ? sidecarFile
                : null;
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
