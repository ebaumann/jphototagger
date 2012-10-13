package org.jphototagger.iptc;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import org.jphototagger.lib.io.FileUtil;

/**
 * @author Elmar Baumann
 */
public final class IptcSupport {

    public static final IptcSupport INSTANCE = new IptcSupport();
    private static final Set<String> SUPPORTED_SUFFIXES_LOWERCASE = new HashSet<>();

    static {
        SUPPORTED_SUFFIXES_LOWERCASE.add("arw");
        SUPPORTED_SUFFIXES_LOWERCASE.add("crw");
        SUPPORTED_SUFFIXES_LOWERCASE.add("cr2");
        SUPPORTED_SUFFIXES_LOWERCASE.add("dcr");
        SUPPORTED_SUFFIXES_LOWERCASE.add("dng");
        SUPPORTED_SUFFIXES_LOWERCASE.add("jpg");
        SUPPORTED_SUFFIXES_LOWERCASE.add("jpeg");
        SUPPORTED_SUFFIXES_LOWERCASE.add("mrw");
        SUPPORTED_SUFFIXES_LOWERCASE.add("nef");
        SUPPORTED_SUFFIXES_LOWERCASE.add("thm");
        SUPPORTED_SUFFIXES_LOWERCASE.add("tif");
        SUPPORTED_SUFFIXES_LOWERCASE.add("tiff");
        SUPPORTED_SUFFIXES_LOWERCASE.add("srw");
    }

    public boolean canReadIptc(File file) {
        String suffix = FileUtil.getSuffix(file);
        String suffixLowerCase = suffix.toLowerCase();

        return SUPPORTED_SUFFIXES_LOWERCASE.contains(suffixLowerCase);
    }

    private IptcSupport() {
    }
}
