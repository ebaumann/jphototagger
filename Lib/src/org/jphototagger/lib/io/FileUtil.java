package org.jphototagger.lib.io;

import org.jphototagger.lib.dialog.FileChooserExt;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.util.Settings;

import java.awt.Component;
import java.awt.Dimension;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import java.net.URISyntaxException;
import java.net.URL;

import java.nio.channels.FileChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.Stack;

import javax.swing.JFileChooser;

/**
 * Utilities for Files.
 *
 * @author Elmar Baumann
 */
public final class FileUtil {
    private FileUtil() {}

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

    public static void writeStringAsFile(File file, String content) throws IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (content == null) {
            throw new NullPointerException("content == null");
        }

        FileOutputStream fos = null;

        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes());
        } finally {
            close(fos);
        }
    }

    /**
     * Calls {@link #getContentAsString(java.io.File, java.lang.String)}.
     *
     * @param pathname path name
     * @param encoding encoding of the file's text content
     * @return         content
     * @throws         IOException
     */
    public static String getContentAsString(String pathname, String encoding) throws IOException {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        if (encoding == null) {
            throw new NullPointerException("encoding == null");
        }

        return getContentAsString(new File(pathname), encoding);
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
            close(fileInputStream);
        }
    }

    /**
     * Calls {@link #getContentAsBytes(java.io.File)}.
     *
     * @param  pathname path name
     * @return          content
     * @throws          IOException
     */
    public static byte[] getContentAsBytes(String pathname) throws IOException {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return getContentAsBytes(new File(pathname));
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
     * Calls {@link #ensureFileExists(java.io.File)}.
     *
     * @param pathname path name
     * @throws         IOException
     */
    public static void ensureFileExists(String pathname) throws IOException {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        ensureFileExists(new File(pathname));
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
     * Calls {@link #existsFile(java.io.File)}.
     *
     * @param pathname path name
     * @return         true if the file exists and is not a directory
     */
    public static boolean existsFile(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return existsFile(new File(pathname));
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
     * Calls {@link #ensureDirectoryExists(java.io.File)}.
     *
     * @param pathname path name
     * @throws         IOException
     */
    public static void ensureDirectoryExists(String pathname) throws IOException {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        ensureDirectoryExists(new File(pathname));
    }

    /**
     * Returns whether a directory exists.
     *
     * @param pathname path name
     * @return         true if the directory exists
     */
    public static boolean existsDirectory(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return new File(pathname).isDirectory();
    }

    /**
     * Returns whether a file is a writable directory.
     *
     * @param  dir directory
     * @return     true if the file is a writable directory
     */
    public static boolean isWritableDirectory(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }

        return dir.isDirectory() && dir.canWrite();
    }

    /**
     * Calls {@link #isWritableDirectory(java.io.File)}.
     *
     * @param  pathname path name
     * @return          true if the directory is writable
     */
    public static boolean isWritableDirectory(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return isWritableDirectory(new File(pathname));
    }

    /**
     * Copies a file (fast).
     *
     * @param  source source file
     * @param  target target file
     * @throws        java.io.IOException
     */
    public static void copyFile(File source, File target) throws IOException {
        if (source == null) {
            throw new NullPointerException("source == null");
        }

        if (target == null) {
            throw new NullPointerException("target == null");
        }

        if (source.equals(target)) {
            return;
        }

        FileChannel inChannel = null;
        FileChannel outChannel = null;
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(source);
            inChannel = fis.getChannel();
            fos = new FileOutputStream(target);
            outChannel = fos.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            close(inChannel);
            close(outChannel);
            close(fis);
            close(fos);

            if (!target.setLastModified(source.lastModified())) {
                throw new IOException("Last modified couldn't be set to " + target + " from " + source);
            }
        }
    }

    /**
     * Calls {@link #copyFile(java.io.File, java.io.File)}.
     *
     * @param  sourcePath path to the source file
     * @param  targetPath path to the target file
     * @throws            IOException
     */
    public static void copyFile(String sourcePath, String targetPath) throws IOException {
        if (sourcePath == null) {
            throw new NullPointerException("sourcePath == null");
        }

        if (targetPath == null) {
            throw new NullPointerException("targetPath == null");
        }

        copyFile(new File(sourcePath), new File(targetPath));
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
     * Calls {@link #getPathFromRoot(java.io.File)}.
     *
     * @param  pathname path name
     * @return          path
     */
    public static Stack<File> getPathFromRoot(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return getPathFromRoot(new File(pathname));
    }

    /**
     * Returns recursive all subdirectories of a parent directory.
     *
     * @param  directory parent directory
     * @param  options   options
     * @return           subdirectories or empty list
     */
    public static List<File> getSubDirsRecursive(File directory, DirectoryFilter.Option... options) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (options == null) {
            throw new NullPointerException("options == null");
        }

        List<File> allSubDirs = new ArrayList<File>();

        if (directory.isDirectory()) {
            File[] subDirs = directory.listFiles(new DirectoryFilter(options));

            if (subDirs != null) {
                for (File dir : subDirs) {
                    allSubDirs.add(dir);

                    List<File> subSubDirs = getSubDirsRecursive(dir, options);

                    allSubDirs.addAll(subSubDirs);
                }
            }
        }

        return allSubDirs;
    }

    public static List<File> getSubDirsRecursive(String pathname, DirectoryFilter.Option... options) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        if (options == null) {
            throw new NullPointerException("options == null");
        }

        return getSubDirsRecursive(new File(pathname), options);
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
    public static List<File> getAsFiles(Collection<? extends String> pathnames) {
        if (pathnames == null) {
            throw new NullPointerException("pathnames == null");
        }

        List<File> files = new ArrayList<File>(pathnames.size());

        for (String pathname : pathnames) {
            files.add(new File(pathname));
        }

        return files;
    }

    /**
     * Returns the files from a collection of arbitrary objects.
     *
     * @param  objects arbitrary objects
     * @return         files of that objects
     */
    public static List<File> getFiles(Collection<Object> objects) {
        if (objects == null) {
            throw new NullPointerException("objects == null");
        }

        List<File> files = new ArrayList<File>(objects.size());

        for (Object o : objects) {
            if (o instanceof File) {
                files.add((File) o);
            }
        }

        return files;
    }

    /**
     * Returns <em>existing</em> directories of a list of arbitrary files.
     *
     * @param  files arbitrary files
     * @return       existing directories within <code>files</code>
     */
    public static List<File> filterDirectories(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        List<File> directories = new ArrayList<File>();

        for (File file : files) {
            if (file.isDirectory()) {
                directories.add(file);
            }
        }

        return directories;
    }

    /**
     * Filters files.
     *
     * @param  files  files
     * @param  filter filter
     * @return        files where {@link FileFilter#accept(java.io.File)} is
     *                true
     */
    public static List<File> filter(Collection<? extends File> files, FileFilter filter) {
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
     * Calls {@link #deleteDirectoryRecursive(java.io.File)}.
     *
     * @param  pathname path name
     * @throws          IOException
     */
    public static void deleteDirectoryRecursive(String pathname) throws IOException {
        if (pathname == null) {
            throw new NullPointerException("directoryname == null");
        }

        deleteDirectoryRecursive(new File(pathname));
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
     * Calls {@link #getNotExistingFile(java.io.File)}.
     *
     * @param  pathname path name
     * @return          file in the same path with a unique number prepending
     *                  the suffix
     */
    public static File getNotExistingFile(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return getNotExistingFile(new File(pathname));
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
     * Calls {@link #getRoot(java.io.File)}.
     *
     * @param  pathname path name
     * @return          root file of the file or the file itself if it has no
     *                  parents
     */
    public static File getRoot(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return getRoot(new File(pathname));
    }

    /**
     * Returns the absolute path name of a file.
     * <p>
     * Shorthand for creating a file and calling {@link #getRoot(java.io.File)}.
     *
     * @param  pathname path name
     * @return          absolute path name of the file's root
     */
    public static String getRootName(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return getRoot(pathname).getAbsolutePath();
    }

    /**
     * Returns the directory path of a file.
     * <p>
     * The directory path is path of the parent file without the root.
     *
     * @param  file file
     * @return      directory path
     */
    public static String getDirPath(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        String parent = file.getParent();

        if (parent == null) {
            return "";
        }

        String root = getRootName(file.getAbsolutePath());

        if (parent.startsWith(root) && (parent.length() > root.length())) {
            return parent.substring(root.length());
        }

        return "";
    }

    /**
     * Calls {@link #getDirPath(java.io.File)}.
     *
     * @param  pathname path name
     * @return          directory path
     */
    public static String getDirPath(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return getDirPath(new File(pathname));
    }

    /**
     * Returns the suffix of a file path.
     * <p>
     * The suffix is the substring after the last dot in the filename.
     *
     * @param  pathname path name
     * @return          suffix or empty string if the filename has no dot or the
     *                  dot is the first or last character within the file path
     */
    public static String getSuffix(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        if (hasSuffix(pathname)) {
            return pathname.substring(pathname.lastIndexOf('.') + 1);
        }

        return "";
    }

    /**
     * Calls {@link #getSuffix(java.lang.String)}.
     *
     * @param  file file
     * @return      suffix or empty string if the filename has no dot or the dot
     *              is the first or last character within the filename
     */
    public static String getSuffix(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return getSuffix(file.getName());
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
     * @param  pathname path name
     * @return          prefix
     */
    public static String getPrefix(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("path == null");
        }

        int index = pathname.lastIndexOf('.');
        int len = pathname.length();

        if ((index == 0) || (index == len - 1)) {
            return pathname;
        }

        return pathname.substring(0, pathname.lastIndexOf('.'));
    }

    /**
     * Calls {@link #getPrefix(java.lang.String)}.
     *
     * @param  file file
     * @return      prefix
     */
    public static String getPrefix(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return getPrefix(file.getName());
    }

    /**
     * Returns the file's last modification timestamp in milliseconds since
     * 1970.
     * <p>
     * Calls {@code File#lastModified()}.
     *
     * @param  pathname path name
     * @return          timestamp or 0L if the file does not exist or on errors
     */
    public static long getLastModified(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return new File(pathname).lastModified();
    }

    /**
     * Returns the location a class' source.
     *
     * @param  clazz class
     * @return       source code path
     */
    public static URL getSourceLocation(Class<?> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }

        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }

    /**
     * Returns the path of a file in a package.
     *
     * @param  classInPackgage class residing in the same package as the file
     * @param  filename        file name without any parents
     * @return                 file
     */
    public static File getFileOfPackage(Class<?> classInPackgage, String filename) {
        if (classInPackgage == null) {
            throw new NullPointerException("classInPackgage == null");
        }

        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        String packagePath = classInPackgage.getName();
        int index = packagePath.lastIndexOf('.');

        if (index > 0) {
            packagePath = packagePath.substring(0, index);
        }

        packagePath = packagePath.replace('.', File.separatorChar);

        File dir = null;

        try {
            dir = new File(getSourceLocation(classInPackgage).toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (dir == null) {
            return new File("");
        } else {
            return new File(dir.getPath() + File.separator + packagePath + File.separator + filename);
        }
    }

    /**
     * Finds in a file a byte pattern and returns it's file offset.
     *
     * @param file    file. It will be searched from the current file pointer
     *                position and the file pointer will be moved forwards
     *                either behind the end of the file or to the first byte
     *                behind the found byte pattern.
     * @param search  byte pattern to find
     * @return        file offset of the first byte where <code>search</code>
     *                starts or -1 if <code>search</code> was not found
     * @throws IOException on errors while accessing the file
     */
    public static long getIndexOf(RandomAccessFile file, byte[] search) throws IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (search == null) {
            throw new NullPointerException("search == null");
        }

        int matched = 0;
        int b = (file.getFilePointer() < file.length() - 1)
                ? 0
                : -1;

        while ((matched < search.length) && (b >= 0)) {
            b = file.read();

            if ((byte) b == search[matched]) {
                matched++;
            } else {
                matched = (byte) b == search[0]
                          ? 1
                          : 0;
            }
        }

        return (matched == search.length)
               ? file.getFilePointer() - search.length
               : -1;
    }

    /**
     * Closes an input stream and catches a possible exception.
     * <p>
     * Use this method, if the {@link IOException} thrown by the stream shall
     * not be handled.
     *
     * @param is input stream, can be null
     */
    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Closes an output stream and catches a possible exception.
     * <p>
     * Use this method, if the {@link IOException} thrown by the stream shall
     * not be handled.
     *
     * @param os stream, can be null
     */
    public static void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Closes a file channel and catches a possible exception.
     * <p>
     * Use this method, if the {@link IOException} thrown by the stream shall
     * not be handled.
     *
     * @param fc file channel, can be null
     */
    public static void close(FileChannel fc) {
        if (fc != null) {
            try {
                fc.close();
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static boolean deleteFile(String pathname) {
        if (pathname == null) {
            throw new NullPointerException("pathname == null");
        }

        return new File(pathname).delete();
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
     * Properties for using a {@link FileChooserExt}.
     *
     * @author Elmar Baumann
     */
    public static final class FileChooserProperties {
        private String propertyKeyPrefix;
        private Properties properties;
        private Component parent;
        private String currentDirectoryPath;
        private String dialogTitle;
        private javax.swing.filechooser.FileFilter fileFilter;
        private boolean multiSelectionEnabled;
        private boolean open;
        private boolean confirmOverwrite;
        private String saveFilenameExtension;

        /**
         * Sets the prefix of the key for storing size and current directory
         * path in {@link #getProperties()}.
         *
         * @param  propertyKeyPrefix prefix or null
         * @return                   this instance (for cascade settings)
         */
        public FileChooserProperties propertyKeyPrefix(String propertyKeyPrefix) {
            this.propertyKeyPrefix = propertyKeyPrefix;

            return this;
        }

        /**
         * Sets the properties for applying and storing size and current
         * directory path.
         *
         * @param  properties properties or null
         * @return            this instance (for cascade settings)
         */
        public FileChooserProperties properties(Properties properties) {
            this.properties = properties;

            return this;
        }

        /**
         * Sets the parent component of the file chooser dialog.
         *
         * @param  parent parent component or null
         * @return        this instance (for cascade settings)
         */
        public FileChooserProperties parent(Component parent) {
            this.parent = parent;

            return this;
        }

        /**
         * Sets the path of the directory to display in the file chooser.
         *
         * @param  currentDirectoryPath directory path or null
         * @return                      this instance (for cascade settings)
         */
        public FileChooserProperties currentDirectoryPath(String currentDirectoryPath) {
            this.currentDirectoryPath = currentDirectoryPath;

            return this;
        }

        /**
         * Sets the dialog title of the file chooser.
         *
         * @param  dialogTitle dialog title or null
         * @return             this instance (for cascade settings)
         */
        public FileChooserProperties dialogTitle(String dialogTitle) {
            this.dialogTitle = dialogTitle;

            return this;
        }

        /**
         * Sets the file filter for the file chooser.
         *
         * @param  fileFilter file filter or null
         * @return            this instance (for cascade settings)
         */
        public FileChooserProperties fileFilter(javax.swing.filechooser.FileFilter fileFilter) {
            this.fileFilter = fileFilter;

            return this;
        }

        /**
         * Sets whether the user can select multiple files.
         *
         * @param  multiSelectionEnabled true if the user can select multiple
         *                               files.
         *                               Default: false.
         * @return                       this instance (for cascade settings)
         */
        public FileChooserProperties multiSelectionEnabled(boolean multiSelectionEnabled) {
            this.multiSelectionEnabled = multiSelectionEnabled;

            return this;
        }

        /**
         * Sets whether an open or save dialog shall be displayed.
         *
         * @param  open true if an open dialog shall be displayed and false, if
         *              a save dialog shall be displayed.
         *              Default: false (save mode).
         * @return      this instance (for cascade settings)
         */
        public FileChooserProperties open(boolean open) {
            this.open = open;

            return this;
        }

        /**
         * Sets whether a user has to confirm overwriting existing files in save
         * mode.
         *
         * @param  confirmOverwrite true if the user has to confirm overwriting
         *                          existing files in save mode
         * @return                  this instance (for cascade settings)
         * @see    FileChooserExt#saveFilenameExtension(java.lang.String)
         */
        public FileChooserProperties confirmOverwrite(boolean confirmOverwrite) {
            this.confirmOverwrite = confirmOverwrite;

            return this;
        }

        /**
         * Sets in save mode a file extension which sall be added to files if
         * the user didn't input that extension.
         *
         * @param   saveFilenameExtension extension, e.g. <code>".txt"</code>
         * @return                        this instance (for cascade settings)
         * @see     FileChooserExt#saveFilenameExtension(java.lang.String)
         */
        public FileChooserProperties saveFilenameExtension(String saveFilenameExtension) {
            this.saveFilenameExtension = saveFilenameExtension;

            return this;
        }
    }


    /**
     * Displays a {@link FileChooserExt} for choosing files to open or save.
     * <p>
     * If <code>fcProperties</code> containing a {@link Properties} object and
     * a key prefix, the size and current directory of the file chooser will be
     * applied before displaying the file chooser and written into the
     * properties object after displaying the file chooser.
     * <p>
     * <strong>Example:</strong>
     * <pre>
     * Properties            props = new Properties();
     * FileChooserProperties p     =
     *          new FileChooserProperties()
     *              .properties(props)
     *              .propertyKeyPrefix("SaveMyDoc")
     *              .dialogTitle("Saving my document")
     *              .confirmOverwrite(true)
     *              ;
     *  System.out.println("Files: " + chooseFiles(p));
     * </pre>
     *
     * @param  fcProperties properties or null. If null, an open dialog will be
     *                      displayed.
     * @return              selected files or empty list
     */
    public static List<File> chooseFiles(FileChooserProperties fcProperties) {
        List<File> files = new ArrayList<File>();
        FileChooserExt fc = getFileChooser(fcProperties);

        applySize(fc, fcProperties);

        boolean open = isOpen(fcProperties);

        if (open && open(fc, fcProperties)) {
            files.addAll(Arrays.asList(fc.getSelectedFiles()));
        } else if (!open && save(fc, fcProperties)) {
            files.addAll(Arrays.asList(fc.getSelectedFiles()));
        }

        writeSize(fc, fcProperties);
        writeCurrentDirectoryPath(files, fcProperties);

        return files;
    }

    private static boolean open(JFileChooser fc, FileChooserProperties p) {
        return fc.showOpenDialog(getParent(p)) == JFileChooser.APPROVE_OPTION;
    }

    private static boolean save(JFileChooser fc, FileChooserProperties p) {
        return fc.showSaveDialog(getParent(p)) == JFileChooser.APPROVE_OPTION;
    }

    private static FileChooserExt getFileChooser(FileChooserProperties p) {
        FileChooserExt fc = new FileChooserExt(getCurrentDirectoryPath(p));

        if (p != null) {
            if (p.dialogTitle != null) {
                fc.setDialogTitle(p.dialogTitle);
            }

            if (p.fileFilter != null) {
                fc.setFileFilter(p.fileFilter);
            }

            if (p.saveFilenameExtension != null) {
                fc.setSaveFilenameExtension(p.saveFilenameExtension);
            }

            fc.setMultiSelectionEnabled(p.multiSelectionEnabled);
            fc.setConfirmOverwrite(p.confirmOverwrite);
        }

        return fc;
    }

    private static Component getParent(FileChooserProperties p) {
        return (p == null)
               ? null
               : p.parent;
    }

    private static boolean isOpen(FileChooserProperties p) {
        return (p == null)
               ? true
               : p.open;
    }

    private static void writeCurrentDirectoryPath(List<File> files, FileChooserProperties p) {
        if (canWriteProperties(p) &&!files.isEmpty()) {
            p.properties.setProperty(getKeyCurrentDirectoryPath(p), files.get(0).getAbsolutePath());
        }
    }

    private static String getCurrentDirectoryPath(FileChooserProperties p) {
        if (p == null) {
            return "";
        }

        if (p.currentDirectoryPath != null) {
            return p.currentDirectoryPath;
        }

        if (canWriteProperties(p)) {
            String dp = p.properties.getProperty(getKeyCurrentDirectoryPath(p));

            if (dp != null) {
                return dp;
            }
        }

        return "";
    }

    private static String getKeyCurrentDirectoryPath(FileChooserProperties p) {
        assert canWriteProperties(p);

        return p.propertyKeyPrefix + ".CurrentDirectoryPath";
    }

    private static void applySize(JFileChooser fc, FileChooserProperties p) {
        if (canWriteProperties(p)) {
            Settings settings = new Settings(p.properties);
            int width = settings.getInt(p.propertyKeyPrefix + Settings.KEY_POSTFIX_WIDTH);
            int height = settings.getInt(p.propertyKeyPrefix + Settings.KEY_POSTFIX_HEIGHT);

            if ((width > 0) && (height > 0)) {
                fc.setPreferredSize(new Dimension(width, height));
            }
        }
    }

    private static void writeSize(JFileChooser fc, FileChooserProperties p) {
        if (canWriteProperties(p)) {
            Settings settings = new Settings(p.properties);

            settings.set(fc.getWidth(), p.propertyKeyPrefix + Settings.KEY_POSTFIX_WIDTH);
            settings.set(fc.getHeight(), p.propertyKeyPrefix + Settings.KEY_POSTFIX_HEIGHT);
        }
    }

    private static boolean canWriteProperties(FileChooserProperties p) {
        return (p != null) && (p.propertyKeyPrefix != null) && (p.properties != null);
    }

    /**
     * Calls {@link #chooseFiles(FileChooserProperties)}, where the user can
     * select only one file.
     *
     * @param  fcProperties properties or null
     * @return              selected file or null if no file has been selected
     */
    public static File chooseFile(FileChooserProperties fcProperties) {
        if (fcProperties != null) {
            fcProperties.multiSelectionEnabled = false;
        }

        List<File> files = chooseFiles(fcProperties);

        return files.isEmpty()
               ? null
               : files.get(0);
    }
}
