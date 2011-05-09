package org.jphototagger.lib.io;

import org.jphototagger.lib.io.filefilter.DirectoryFilter;


import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import java.nio.channels.FileChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;


/**
 * Utilities for Files.
 *
 * @author Elmar Baumann
 */
public final class FileUtil {

    /**
     * Returns the content of a file as string.
     * <p>
     * Usage for text files.
     *
     * @param file     file
     * @param encoding encoding of the file's text content
     * @return         content
     * @throws         IOException
     */
    public static String getContentAsString(File file, String encoding) throws IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (encoding == null) {
            throw new NullPointerException("encoding == null");
        }

        return new String(getContentAsBytes(file), encoding);
    }

    public static void writeStringAsFile(String string, File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (string == null) {
            throw new NullPointerException("string == null");
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            fos.write(string.getBytes());
        } finally {
            IoUtil.close(fos);
        }
    }

    /**
     * Returns the content of a file als byte array.
     *
     * @param file file
     * @return     content
     * @throws     IOException
     */
    public static byte[] getContentAsBytes(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);

            int byteCount = fileInputStream.available();
            byte[] bytes = new byte[byteCount];

            fileInputStream.read(bytes);

            return bytes;
        } finally {
            IoUtil.close(fileInputStream);
        }
    }

    /**
     * Ensures that a file exists. Creates the file and it's not existing parent
     * files (directories).
     *
     * @param  file file
     * @throws      IOException
     */
    public static void ensureFileExists(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (file.exists()) {
            return;
        }

        File directory = file.getParentFile();

        ensureDirectoryExists(directory);

        if (!file.createNewFile()) {
            throw new IOException("File couldn't be created: " + file);
        }
    }

    /**
     * Returns whether a file exists and is <em>not</em> a directory (weaker
     * than {@link File#isFile()}).
     *
     * @param file file
     * @return     true if the file exists and is not a directory
     */
    public static boolean existsFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return file.exists() &&!file.isDirectory();
    }

    /**
     * Ensures that a directory exists. If it does not exist, this method
     * creates a new directory including its parent directories.
     *
     * @param  directory directory
     * @throws           IOException
     */
    public static void ensureDirectoryExists(File directory) throws IOException {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (directory.isDirectory()) {
            return;
        }

        if (!directory.mkdirs()) {
            throw new IOException("Directory couldn't be created: " + directory);
        }
    }

    /**
     * Returns whether a file is a writable directory.
     *
     * @param  directory directory
     * @return true if the file is a writable directory
     */
    public static boolean isWritableDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        return directory.isDirectory() && directory.canWrite();
    }

    /**
     * Copies a file (fast).
     *
     * @param  sourceFile source file
     * @param  targetFile target file
     * @throws        java.io.IOException
     */
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        if (sourceFile == null) {
            throw new NullPointerException("sourceFile == null");
        }

        if (targetFile == null) {
            throw new NullPointerException("targetFile == null");
        }

        if (sourceFile.equals(targetFile)) {
            return;
        }

        FileChannel inChannel = null;
        FileChannel outChannel = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(sourceFile);
            inChannel = fis.getChannel();
            fos = new FileOutputStream(targetFile);
            outChannel = fos.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            IoUtil.close(inChannel);
            IoUtil.close(outChannel);
            IoUtil.close(fis);
            IoUtil.close(fos);

            if (!targetFile.setLastModified(sourceFile.lastModified())) {
                throw new IOException("Last modified couldn't be set to " + targetFile + " from " + sourceFile);
            }
        }
    }

    /**
     * Returns the path to a file up to it's root file.
     * <p>
     * Every stack element obove another stack element is the parent file
     * ({@link File#getParentFile()}) of the element below it. The top of the
     * stack is the root file.
     *
     * @param  file file
     * @return      path
     */
    public static Stack<File> getPathFromRoot(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (file.getParent() == null) {
            return new Stack<File>();
        }

        Stack<File> path = new Stack<File>();
        File parent = file;

        do {
            parent = path.push(parent).getParentFile();
        } while (parent != null);

        return path;
    }

    /**
     * Returns recursive all subdirectories of a parent directory.
     *
     * @param  directory     parent directory
     * @param  cancelRequest cancel request or null
     * @param  options       options
     * @return               subdirectories or empty list
     */
    public static List<File> getSubDirectoriesRecursive(File directory, CancelRequest cancelRequest, DirectoryFilter.Option... options) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (options == null) {
            throw new NullPointerException("options == null");
        }

        List<File> allSubDirs = new ArrayList<File>();
        boolean isCancel = cancelRequest != null && cancelRequest.isCancel();

        if (isCancel) {
            return allSubDirs;
        }

        if (directory.isDirectory()) {
            File[] subDirs = directory.listFiles(new DirectoryFilter(options));

            if (subDirs != null) {
                for (File dir : subDirs) {
                    isCancel = cancelRequest != null && cancelRequest.isCancel();

                    if (isCancel) {
                        return Collections.emptyList();
                    }

                    allSubDirs.add(dir);

                    List<File> subSubDirs = getSubDirectoriesRecursive(dir, cancelRequest, options);

                    allSubDirs.addAll(subSubDirs);
                }
            }
        }

        return allSubDirs;
    }

    /**
     * Returns the absolute path names ({@link File#getAbsolutePath()} of a
     * collection of files.
     *
     * @param  files files
     * @return        path names of the files
     */
    public static List<String> getAbsolutePathnames(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        List<String> pathnames = new ArrayList<String>(files.size());

        for (File file : files) {
            pathnames.add(file.getAbsolutePath());
        }

        return pathnames;
    }

    /**
     * Returns a list of files from a collection of path names.
     *
     * @param  pathnames path names
     * @return           files
     */
    public static List<File> getStringsAsFiles(Collection<? extends String> pathnames) {
        if (pathnames == null) {
            throw new NullPointerException("pathnames == null");
        }

        List<File> files = new ArrayList<File>(pathnames.size());

        for (String pathname : pathnames) {
            files.add(new File(pathname));
        }

        return files;
    }

    public static List<File> filterFiles(Collection<? extends File> files, FileFilter filter) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        if (filter == null) {
            throw new NullPointerException("filter == null");
        }

        List<File> filteredFiles = new ArrayList<File>(files.size());

        for (File file : files) {
            if (filter.accept(file)) {
                filteredFiles.add(file);
            }
        }

        return filteredFiles;
    }

    /**
     * Deletes a directory and all it's contents: files and subdirectories.
     *
     * @param  directory directory
     * @throws           IOException
     */
    public static void deleteDirectoryRecursive(File directory) throws IOException {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (directory.isDirectory()) {
            File[] files = directory.listFiles();

            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursive(file);
                } else {
                    if (!file.delete()) {
                        throw new IOException("File couldn't be deleted: " + file);
                    }
                }
            }
        }

        if (!directory.delete()) {
            throw new IOException("Directory couldn't be deleted: " + directory);
        }
    }

    /**
     * Returns a not existing file.
     *
     * @param  file suggested file
     * @return      file in the same path with a unique number prepending the
     *              suffix
     */
    public static File getNotExistingFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        File newFile = file;
        int index = 0;

        while (newFile.exists()) {
            String pathname = file.getPath();
            int suffixPos = pathname.lastIndexOf('.');

            if (suffixPos > 0) {
                String suffix = pathname.substring(suffixPos + 1);
                String prefix = pathname.substring(0, suffixPos);

                pathname = prefix + "_" + Integer.valueOf(index++) + "." + suffix;
            } else {
                pathname += "_" + Integer.valueOf(index++);
            }

            newFile = new File(pathname);
        }

        return newFile;
    }

    /**
     * Returns the root of a file.
     * <p>
     * The Windows file name <code>"C:\Programs\MyProg\Readme.txt"</code>
     * has the root name <code>"C:\"</code>, the Unix file name
     * <code>"/home/me/Readme.txt"</code> has the root name <code>"/"</code>.
     *
     * @param  file file
     * @return      root file of the file or the file itself if it has no
     *              parents
     */
    public static File getRoot(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        File root = file.getParentFile();
        File parent = root;

        while (parent != null) {
            if (parent != null) {
                root = parent;
                parent = parent.getParentFile();
            }
        }

        return (root == null)
               ? file
               : root;
    }

    /**
     * Returns the directory path of a file.
     * <p>
     * The directory path is path of the parent file without the root.
     *
     * @param  file file
     * @return      directory path
     */
    public static String getDirectoryPath(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        String parent = file.getParent();

        if (parent == null) {
            return "";
        }

        String root = getRoot(file).getAbsolutePath();

        if (parent.startsWith(root) && (parent.length() > root.length())) {
            return parent.substring(root.length());
        }

        return "";
    }

    /**
     * Returns the suffix of a file path.
     * <p>
     * The suffix is the substring after the last dot in the filename.
     *
     * @param  file file
     * @return      suffix or empty string if the filename has no dot or the dot
     *              is the first or last character within the filename
     */
    public static String getSuffix(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        String pathname = file.getName();

        if (hasSuffix(pathname)) {
            return pathname.substring(pathname.lastIndexOf('.') + 1);
        }

        return "";
    }

    private static boolean hasSuffix(String pathname) {
        int index = pathname.lastIndexOf('.');
        int len = pathname.length();

        return (index > 0) && (index < len - 1);
    }

    /**
     * Returns the prefix of a filename.
     * <p>
     * The prefix is the substring before the last dot in a filename. If the
     * last dot is the first or last character within the string, the prefix is
     * equal to the filename.
     *
     * @param  file file
     * @return      prefix
     */
    public static String getPrefix(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        
        String filename = file.getName();

        int index = filename.lastIndexOf('.');
        int len = filename.length();

        if ((index == 0) || (index == len - 1)) {
            return filename;
        }

        return filename.substring(0, filename.lastIndexOf('.'));
    }

    /**
     * Ensures that a file has a specific suffix. Ignores the case
     *
     * @param file   file
     * @param suffix suffix - a dot will <em>not</em> be prepended - e.g.
     *               <code>".xml"</code>
     * @return       file or a new file with the old file path but the suffix.
     *               E.g. if the suffix is <code>".xml"</code> and the file name
     *               is <code>"file.xml"</code> or the file name is
     *               <code>"file"</code> in both cases a file named
     *               <code>"file.xml"</code> will be returned.
     */
    public static File ensureSuffix(File file, String suffix) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (suffix == null) {
            throw new NullPointerException("suffix == null");
        }

        if (file.getName().toLowerCase().endsWith(suffix.toLowerCase())) {
            return file;
        }

        return new File(file.getPath() + suffix);
    }


    /**
     * Changes the last modification time of a file.
     *
     * @param fileToTouch   file
     * @param referenceFile file from which the time shall be taken
     *                      or null if the current system time shall be used
     */
    public static void touch(File fileToTouch, File referenceFile) {
        if (fileToTouch == null) {
            throw new NullPointerException("fileToTouch == null");
        }

        long reference = referenceFile == null
                ? System.currentTimeMillis()
                : referenceFile.lastModified();

        fileToTouch.setLastModified(reference);
    }

    private FileUtil() {}
}
