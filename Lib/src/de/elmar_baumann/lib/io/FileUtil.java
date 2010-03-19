/*
 * @(#)FileUtil.java    Created on 2008-10-05
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.lib.io;

import de.elmar_baumann.lib.io.filefilter.DirectoryFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.util.Stack;

/**
 * Utils für Dateien.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann
 */
public final class FileUtil {

    /**
     * Returns the content of a file as string.
     *
     * @param file     file
     * @param encoding encoding of the file
     * @return         content or null when errors occur
     */
    public static String getFileContentAsString(File file, String encoding) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (encoding == null) {
            throw new NullPointerException("encoding == null");
        }

        byte[] bytes = getFileContentAsBytes(file);

        if (bytes != null) {
            try {
                return new String(bytes, encoding);
            } catch (Exception ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE,
                                 null, ex);

                return null;
            }
        }

        return null;
    }

    /**
     * Calls {@link #getFileContentAsString(java.io.File, java.lang.String)}.
     *
     * @param  filename name of the file
     * @param  encoding encoding
     * @return          content or null when errors occur
     */
    public static String getFileContentAsString(String filename,
            String encoding) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        return getFileContentAsString(new File(filename), encoding);
    }

    /**
     * Returns the content of a file als bytes.
     *
     * @param file file
     * @return     content or null when errors occur
     */
    public static byte[] getFileContentAsBytes(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);

            int    byteCount = fileInputStream.available();
            byte[] bytes     = new byte[byteCount];

            fileInputStream.read(bytes);
            fileInputStream.close();

            return bytes;
        } catch (Exception ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null,
                             ex);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (Exception ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE,
                                 null, ex);
            }
        }

        return null;
    }

    /**
     * Calls {@link #getFileContentAsBytes(java.io.File)}.
     *
     * @param  filename name of the file
     * @return          content or null when errors occur
     */
    public static byte[] getFileContentAsBytes(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        return getFileContentAsBytes(new File(filename));
    }

    /**
     * Ensures that a file exists. Creates the file if it does not exist, even
     * necessary directories.
     *
     * @param  file file
     * @throws IOException if the file couldn't be created if it does not exist
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
     * @param filename     filename
     * @throws IOException if the file does not exist and couldn't be created
     */
    public static void ensureFileExists(String filename) throws IOException {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        ensureFileExists(new File(filename));
    }

    /**
     * Returns wheter a file exists and it's <em>not</em> a directory.
     *
     * @param file file
     * @return     true if the file exists and it is not a directory
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
     * @param filename file name
     * @return         true if the file exists and it is not a directory
     */
    public static boolean existsFile(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        return existsFile(new File(filename));
    }

    /**
     * Ensures that a directory exists. If it does not exists, this method
     * creates a new directory including it's parent directories if they does
     * not exist.
     *
     * @param directory    directory
     * @throws IOException if the directory couldn't be created
     */
    public static void ensureDirectoryExists(File directory)
            throws IOException {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (existsDirectory(directory)) {
            return;
        }

        if (!directory.mkdirs()) {
            throw new IOException("Directory couldn't be created: "
                                  + directory);
        }
    }

    /**
     * Calls {@link #ensureDirectoryExists(java.io.File)}.
     *
     * @param directoryname name of the directory
     * @throws IOException  if the directory couldn't be created
     */
    public static void ensureDirectoryExists(String directoryname)
            throws IOException {
        if (directoryname == null) {
            throw new NullPointerException("directoryname == null");
        }

        ensureDirectoryExists(new File(directoryname));
    }

    /**
     * Returns whether a directory exists and it's a directory.
     *
     * @param  directory directory
     * @return           true if the directory exists and the file type is
     *                   a directory
     */
    public static boolean existsDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        return directory.exists() && directory.isDirectory();
    }

    /**
     * Calls {@link #existsDirectory(java.io.File)}.
     *
     * @param directoryname name of the directory
     * @return              true if the directory exists and the file type is
     *                      a directory
     */
    public static boolean existsDirectory(String directoryname) {
        if (directoryname == null) {
            throw new NullPointerException("directoryname == null");    // NOI18N
        }

        return existsDirectory(new File(directoryname));
    }

    /**
     * Copies a file (fast).
     *
     * @param  source  source file
     * @param  target  target file
     * @throws         java.io.IOException on errors
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

        FileChannel inChannel  = new FileInputStream(source).getChannel();
        FileChannel outChannel = new FileOutputStream(target).getChannel();

        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            throw e;
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }

            if (outChannel != null) {
                outChannel.close();

                if (!target.setLastModified(source.lastModified())) {
                    throw new IOException("Last modified couldn't be set to "
                                          + target + " from " + source);
                }
            }
        }
    }

    /**
     * Calls {@link #copyFile(java.io.File, java.io.File)}.
     *
     * @param  sourceFilename name of the source file
     * @param  targetFilename name of the target file
     * @throws IOException    on errors
     */
    public static void copyFile(String sourceFilename, String targetFilename)
            throws IOException {
        if (sourceFilename == null) {
            throw new NullPointerException("sourceFilename == null");    // NOI18N
        }

        if (targetFilename == null) {
            throw new NullPointerException("targetFilename == null");    // NOI18N
        }

        copyFile(new File(sourceFilename), new File(targetFilename));
    }

    /**
     * Liefert den Pfad einer Datei nach oben bis zur Wurzel.
     *
     * @param  file Datei
     * @return Pfad, das oberste Element ist die Wurzel
     */
    public static Stack<File> getPathFromRoot(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (file.getParent() == null) {
            return new Stack<File>();
        }

        Stack<File> path   = new Stack<File>();
        File        parent = file;

        do {
            parent = path.push(parent).getParentFile();
        } while (parent != null);

        return path;
    }

    /**
     * Calls {@link #getPathFromRoot(java.io.File)}.
     *
     * @param  filename name of the file
     * @return          path where the top of the stack is the root directory
     */
    public static Stack<File> getPathFromRoot(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");    // NOI18N
        }

        return getPathFromRoot(new File(filename));
    }

    /**
     * Returns recursive all subdirectories of a parent directory.
     *
     * @param  directory  parent directory
     * @param  options    options
     * @return            subdirectories or empty list
     */
    public static List<File> getSubdirectoriesRecursive(File directory,
            DirectoryFilter.Option... options) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (options == null) {
            throw new NullPointerException("options == null");
        }

        List<File> directories = new ArrayList<File>();

        if (directory.isDirectory()) {
            File[] subdirectories =
                directory.listFiles(new DirectoryFilter(options));

            if ((subdirectories != null) && (subdirectories.length > 0)) {
                List<File> subdirectoriesList = Arrays.asList(subdirectories);

                for (File dir : subdirectoriesList) {
                    directories.add(dir);

                    List<File> subdirectoriesSubDirs =
                        getSubdirectoriesRecursive(dir, options);

                    directories.addAll(subdirectoriesSubDirs);
                }
            }
        }

        return directories;
    }

    public static List<File> getSubdirectoriesRecursive(String directoryname,
            DirectoryFilter.Option... options) {
        if (directoryname == null) {
            throw new NullPointerException("directoryname == null");    // NOI18N
        }

        return getSubdirectoriesRecursive(new File(directoryname), options);
    }

    /**
     * Liefert die absoluten Pfadnamen mehrerer Dateien.
     *
     * @param files Dateien
     * @return      Pfadnamen
     */
    public static List<String> getAbsolutePathnames(
            Collection<? extends File> files) {
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
     * Returns a list of files from an array of filenames.
     *
     * @param  filenames filenames
     * @return files
     */
    public static List<File> getAsFiles(
            Collection<? extends String> filenames) {
        if (filenames == null) {
            throw new NullPointerException("filenames == null");
        }

        List<File> files = new ArrayList<File>(filenames.size());

        for (String filename : filenames) {
            files.add(new File(filename));
        }

        return files;
    }

    /**
     * Returns an array of filenames (absolute path) from an array of files.
     *
     * @param  files  files
     * @return filenames
     */
    public static List<String> getAsFilenames(
            Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        List<String> filenames = new ArrayList<String>(files.size());

        for (File file : files) {
            filenames.add(file.getAbsolutePath());
        }

        return filenames;
    }

    /**
     * Returns a list of files from a list of object where every object is a file.
     *
     * @param  objects files as object
     * @return files
     */
    public static List<File> objectCollectionToFileList(
            Collection<Object> objects) {
        if (objects == null) {
            throw new NullPointerException("objects == null");
        }

        List<File> fileList = new ArrayList<File>(objects.size());

        for (Object o : objects) {
            if (o instanceof File) {
                fileList.add((File) o);
            }
        }

        return fileList;
    }

    /**
     * Returns an array of files from a collection of files.
     *
     * @param  files collection
     * @return array
     */
    public static File[] fileCollectionToFileArray(
            Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        File[] fileArray = new File[files.size()];
        int    index     = 0;

        for (File file : files) {
            fileArray[index++] = file;
        }

        return fileArray;
    }

    /**
     * Returns the directories of a list of files.
     *
     * @param  files    files
     * @return existing directories within <code>files</code>
     */
    public static List<File> filterDirectories(
            Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        List<File> directories = new ArrayList<File>();

        for (File file : files) {
            if (file.exists() && file.isDirectory()) {
                directories.add(file);
            }
        }

        return directories;
    }

    /**
     * Deletes a directory and all it's contents: files and subdirectories.
     *
     * @param  directory   directory
     * @throws IOException if a file couldn't be deleted
     */
    public static void deleteDirectoryRecursive(File directory)
            throws IOException {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (directory.exists()) {
            File[] files = directory.listFiles();

            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursive(file);
                } else {
                    if (!file.delete()) {
                        throw new IOException("File couldn't be deleted: "
                                              + file);
                    }
                }
            }
        }

        if (!directory.delete()) {
            throw new IOException("Directory couldn't be deleted: "
                                  + directory);
        }
    }

    /**
     * Calls {@link #deleteDirectoryRecursive(java.io.File)}.
     *
     * @param  directoryname name of the directory
     * @throws IOException   if the directory couldn't be deleted
     */
    public static void deleteDirectoryRecursive(String directoryname)
            throws IOException {
        if (directoryname == null) {
            throw new NullPointerException("directoryname == null");    // NOI18N
        }

        deleteDirectoryRecursive(new File(directoryname));
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
            throw new NullPointerException("file == null");    // NOI18N
        }

        File newFile = file;
        int  index   = 0;

        while (newFile.exists()) {
            String path      = file.getAbsolutePath();
            int    suffixPos = path.lastIndexOf(".");

            if (suffixPos > 0) {
                String suffix = path.substring(suffixPos + 1);
                String prefix = path.substring(0, suffixPos);

                path = prefix + "_" + Integer.valueOf(index++) + "." + suffix;
            } else {
                path += "_" + Integer.valueOf(index++);
            }

            newFile = new File(path);
        }

        return newFile;
    }

    /**
     * Calls {@link #getNotExistingFile(java.io.File)}.
     *
     * @param  filename name of the file
     * @return          file in the same path with a unique number prepending
     *                  the suffix
     */
    public static File getNotExistingFile(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");    // NOI18N
        }

        return getNotExistingFile(new File(filename));
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
            throw new NullPointerException("file == null");    // NOI18N
        }

        File root   = file.getParentFile();
        File parent = root;

        while (parent != null) {
            if (parent != null) {
                root   = parent;
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
     * @param  filename name of the file
     * @return          root file of the file or the file itself if it has no
     *                  parents
     */
    public static File getRoot(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");    // NOI18N
        }

        return getRoot(new File(filename));
    }

    /**
     * Returns the absolute path name of a filename.
     * <p>
     * Shorthand for creating a file and calling {@link #getRoot(java.io.File)}.
     *
     * @param  filename name of the file
     * @return          name of the file's root
     */
    public static String getRootName(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");    // NOI18N
        }

        return getRoot(filename).getAbsolutePath();
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
            throw new NullPointerException("file == null");    // NOI18N
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
     * @param  filename name of the file
     * @return          directory path
     */
    public static String getDirPath(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");    // NOI18N
        }

        return getDirPath(new File(filename));
    }

    /**
     * Returns the suffix of a filename.
     * <p>
     * The suffix is the substring after the last dot in the filename.
     *
     * @param  filename filename without path, e.g. <code>"info.txt"</code>
     * @return          suffix or empty string if the filename has no dot or
     *                  the dot is the first or last character within the
     *                  filename
     */
    public static String getSuffix(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");    // NOI18N
        }

        if (hasSuffix(filename)) {
            return filename.substring(filename.lastIndexOf(".") + 1);
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
            throw new NullPointerException("file == null");    // NOI18N
        }

        return getSuffix(file.getName());
    }

    private static boolean hasSuffix(String filename) {
        int index = filename.lastIndexOf(".");
        int len   = filename.length();

        return (index > 0) && (index < len - 1);
    }

    /**
     * Returns the prefix of a filename.
     * <p>
     * The prefix is the substring before the last dot in a filename. If the
     * last dot is the first or last character within the string, the prefix is
     * equal to the filename.
     *
     * @param  filename filename without path, e.g. <code>"info.txt"</code>
     * @return          prefix
     */
    public static String getPrefix(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");    // NOI18N
        }

        int index = filename.lastIndexOf(".");
        int len   = filename.length();

        if ((index == 0) || (index == len - 1)) {
            return filename;
        }

        return filename.substring(0, filename.lastIndexOf("."));
    }

    /**
     * Calls {@link #getPrefix(java.lang.String)}.
     *
     * @param  file file
     * @return      prefix
     */
    public static String getPrefix(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");    // NOI18N
        }

        return getPrefix(file.getName());
    }

    /**
     * Returns the file's last modification timestamp in milliseconds since
     * 1970.
     *
     * Calls {@code File#lastModified()}.
     *
     * @param  filename filename
     * @return          timestamp or 0L if the file does not exist or on errors
     */
    public static long getLastModified(String filename) {
        if (filename == null) {
            throw new NullPointerException("filename == null");    // NOI18N
        }

        return new File(filename).lastModified();
    }

    /**
     * Returns the location a class' source.
     *
     * @param  clazz class
     * @return       source code path
     */
    public static URL getSourceLocation(Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation();
    }

    /**
     * Returns the path of a file in a package.
     *
     * @param  classInPackgage class residing in the same package as the file
     * @param  filename        name of the file without any parents
     * @return                 file
     */
    public static File getFileOfPackage(Class<?> classInPackgage,
            String filename) {
        String packagePath = classInPackgage.getName();
        int    index       = packagePath.lastIndexOf(".");

        if (index > 0) {
            packagePath = packagePath.substring(0, index);
        }

        packagePath = packagePath.replace('.', File.separatorChar);

        File dir = null;

        try {
            dir = new File(getSourceLocation(classInPackgage).toURI());
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null,
                             ex);
        }

        if (dir == null) {
            return new File("");
        } else {
            return new File(dir.getAbsolutePath() + File.separator
                            + packagePath + File.separator + filename);
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
    public static long getIndexOf(RandomAccessFile file, byte[] search)
            throws IOException {
        int matched = 0;
        int b       = (file.getFilePointer() < file.length() - 1)
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
     * Returns a file with an added suffix to its name.
     * <p>
     * Ignores case, both suffixes treated as equals: ".HTML" and ".html".
     *
     * @param  file   file
     * @param  suffix suffix, e.g. <code>".xml"</code>
     * @return        file with suffix or the file itself if it already has
     *                that suffix
     */
    public static File getWithSuffixIgnoreCase(File file, String suffix) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        if (suffix == null) {
            throw new NullPointerException("suffix == null");
        }

        if (file.getName().toLowerCase().endsWith(suffix.toLowerCase())) {
            return file;
        }

        return new File(file.getAbsolutePath() + suffix);
    }

    private FileUtil() {}
}
