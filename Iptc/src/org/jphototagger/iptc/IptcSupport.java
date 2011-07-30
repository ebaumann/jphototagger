package org.jphototagger.iptc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.jphototagger.lib.io.FileUtil;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class IptcSupport {

    public static final IptcSupport INSTANCE = new IptcSupport();
    private static final Set<String> SUPPORTED_SUFFIXES = new HashSet<String>();

    static {
        SUPPORTED_SUFFIXES.add("arw");
        SUPPORTED_SUFFIXES.add("crw");
        SUPPORTED_SUFFIXES.add("cr2");
        SUPPORTED_SUFFIXES.add("dcr");
        SUPPORTED_SUFFIXES.add("dng");
        SUPPORTED_SUFFIXES.add("jpg");
        SUPPORTED_SUFFIXES.add("jpeg");
        SUPPORTED_SUFFIXES.add("mrw");
        SUPPORTED_SUFFIXES.add("nef");
        SUPPORTED_SUFFIXES.add("thm");
        SUPPORTED_SUFFIXES.add("tif");
        SUPPORTED_SUFFIXES.add("tiff");
        SUPPORTED_SUFFIXES.add("srw");
    }

    public boolean canReadIptc(File file) {
        String suffix = FileUtil.getSuffix(file);

        return SUPPORTED_SUFFIXES.contains(suffix);
    }

    private IptcSupport() {
    }
}
