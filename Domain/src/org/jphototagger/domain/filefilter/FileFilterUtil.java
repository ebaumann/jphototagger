package org.jphototagger.domain.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.repository.FileExcludePatternsRepository;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.util.RegexUtil;

/**
 * @author Elmar Baumann
 */
public final class FileFilterUtil {

    public static List<File> getImageFilesOfDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        AppFileFilterProvider provider = Lookup.getDefault().lookup(AppFileFilterProvider.class);
        File[] filteredFiles = directory.listFiles(provider.getAcceptedImageFilesFileFilter());
        FileExcludePatternsRepository repo = Lookup.getDefault().lookup(FileExcludePatternsRepository.class);
        List<String> excludePatterns = repo.findAllFileExcludePatterns();
        List<File> files = new ArrayList<File>();

        if (filteredFiles != null) {
            for (int index = 0; index < filteredFiles.length; index++) {
                File file = filteredFiles[index];

                if (!RegexUtil.containsMatch(excludePatterns, file.getAbsolutePath())) {
                    files.add(file);
                }
            }
        }

        return files;
    }

    public static List<File> getImageFilesOfDirectories(List<File> directories) {
        if (directories == null) {
            throw new NullPointerException("directories == null");
        }

        List<File> files = new ArrayList<File>();

        for (File directory : directories) {
            files.addAll(getImageFilesOfDirectory(directory));
        }

        return files;
    }

    public static List<File> getImageFilesOfDirAndSubDirs(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }

        List<File> dirAndSubdirs = FileUtil.getSubDirectoriesRecursive(dir, null, getDirFilterOptionShowHiddenFiles());

        dirAndSubdirs.add(dir);

        return getImageFilesOfDirectories(dirAndSubdirs);
    }

    private static DirectoryFilter.Option getDirFilterOptionShowHiddenFiles() {
        return isAcceptHiddenDirectories()
                ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                : DirectoryFilter.Option.NO_OPTION;
    }

    private static boolean isAcceptHiddenDirectories() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? storage.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    public static List<File> filterImageFiles(Collection<File> arbitraryFiles) {
        if (arbitraryFiles == null) {
            throw new NullPointerException("arbitraryFiles == null");
        }

        List<File> imageFiles = new ArrayList<File>();
        AppFileFilterProvider provider = Lookup.getDefault().lookup(AppFileFilterProvider.class);
        FileFilter filter = provider.getAcceptedImageFilesFileFilter();

        for (File file : arbitraryFiles) {
            if (filter.accept(file)) {
                imageFiles.add(file);
            }
        }

        return imageFiles;
    }

    public static boolean isImageFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        AppFileFilterProvider provider = Lookup.getDefault().lookup(AppFileFilterProvider.class);
        return provider.isAcceptedImageFile(file);
    }

    public static List<File> getImageFiles(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        List<File> imageFiles = new ArrayList<File>(files.size());

        for (File file : files) {
            if (isImageFile(file)) {
                imageFiles.add(file);
            }
        }

        return imageFiles;
    }

    private FileFilterUtil() {
    }
}
