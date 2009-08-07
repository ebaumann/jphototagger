package de.elmar_baumann.lib.io;

import de.elmar_baumann.lib.io.filefilter.DirectoryFilter;
import de.elmar_baumann.lib.resource.Bundle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utils für Dateien.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class FileUtil {

    /**
     * Returns the content of a file as string.
     * 
     * @param file file
     * @param encoding
     * @return     content or null when errors occur
     */
    public static String getFileContentAsString(File file, String encoding) {

        if (file == null)
            throw new NullPointerException("file == null"); // NOI18N
        if (encoding == null)
            throw new NullPointerException("encoding == null"); // NOI18N

        byte[] bytes = getFileContentAsBytes(file);
        if (bytes != null) {
            try {
                return new String(bytes, encoding); // NOI18N
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(
                        FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return null;
    }

    /**
     * Returns the content of a file als bytes.
     * 
     * @param file file
     * @return     content or null when errors occur
     */
    public static byte[] getFileContentAsBytes(File file) {

        if (file == null)
            throw new NullPointerException("file == null"); // NOI18N

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            int byteCount = fileInputStream.available();
            byte[] bytes = new byte[byteCount];
            fileInputStream.read(bytes);
            fileInputStream.close();
            return bytes;
        } catch (IOException ex) {
            Logger.getLogger(
                    FileUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(
                        FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    /**
     * Ensures that a file exists. Creates the file if it does not exist, even
     * necessary directories.
     * 
     * @param  file file
     * @return      true if the file exists
     */
    public static boolean ensureFileExists(File file) {

        if (file == null)
            throw new NullPointerException("file == null"); // NOI18N

        boolean exists = file.exists();
        if (!exists) {
            File directory = file.getParentFile();
            if (ensureDirectoryExists(directory)) {
                try {
                    file.createNewFile();
                    exists = true;
                } catch (IOException ex) {
                    Logger.getLogger(
                            FileUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return exists;
    }

    /**
     * Returns wheter a file exists and it's <em>not</em> a directory.
     * 
     * @param file file
     * @return     true if the file exists and it is not a directory
     */
    public static boolean existsFile(File file) {

        if (file == null)
            throw new NullPointerException("file == null"); // NOI18N

        return file.exists() && !file.isDirectory();
    }

    /**
     * Ensures that a directory exists. If it does not exists, this method
     * creates a new directory including it's parent directories if they does
     * not exist.
     * 
     * @param directory directory
     * @return          true if successful
     */
    public static boolean ensureDirectoryExists(File directory) {

        if (directory == null)
            throw new NullPointerException("directory == null"); // NOI18N

        boolean exists = existsDirectory(directory);
        if (!exists) {
            if (!directory.exists()) {
                exists = directory.mkdirs();
                if (!exists) {
                    Logger.getLogger(FileUtil.class.getName()).log(
                            Level.SEVERE,
                            null,
                            Bundle.getString(
                            "FileUtil.Error.CreateDirectoryFailed")); // NOI18N
                }
            }
        }
        return exists;
    }

    /**
     * Returns whether a directory exists and it's a directory.
     * 
     * @param  directory directory
     * @return           true if the directory exists and the file type is
     *                   a directory
     */
    public static boolean existsDirectory(File directory) {

        if (directory == null)
            throw new NullPointerException("directory == null"); // NOI18N

        return directory.exists() && directory.isDirectory();
    }

    /**
     * Copies a file (fast).
     * 
     * @param  source  source file
     * @param  target  target file
     * @throws         java.io.IOException on errors
     */
    public static void copyFile(File source, File target) throws IOException {

        if (source == null)
            throw new NullPointerException("source == null"); // NOI18N
        if (target == null)
            throw new NullPointerException("target == null"); // NOI18N

        if (source.equals(target)) {
            return;
        }
        FileChannel inChannel = new FileInputStream(source).getChannel();
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
                if (target.exists()) {
                    target.setLastModified(source.lastModified());
                }
            }
        }
    }

    /**
     * Liefert den Pfad einer Datei nach oben bis zur Wurzel.
     * 
     * @param  file Datei
     * @return Pfad, das oberste Element ist die Wurzel
     */
    public static Stack<File> getPathFromRoot(File file) {

        if (file == null)
            throw new NullPointerException("file == null"); // NOI18N

        if (file.getParent() == null) return new Stack<File>();

        Stack<File> path = new Stack<File>();
        File parent = file;
        do {
            parent = path.push(parent).getParentFile();
        } while (parent != null);
        return path;
    }

    /**
     * Liefert alle Unterverzeichnisse eines Verzeichnisses einschließlich
     * derer Unterverzeichnisse bis zur untersten Ebene.
     * 
     * @param  directory  Verzeichnis
     * @param  options    file filtering optins
     * @return Unterverzeichnisse
     */
    public static List<File> getSubdirectoriesRecursive(
            File directory,
            Set<DirectoryFilter.Option> options) {

        if (directory == null)
            throw new NullPointerException("directory == null"); // NOI18N
        if (options == null)
            throw new NullPointerException("options == null"); // NOI18N

        List<File> directories = new ArrayList<File>();
        if (directory.isDirectory()) {
            File[] subdirectories =
                    directory.listFiles(new DirectoryFilter(options));
            if (subdirectories != null && subdirectories.length > 0) {
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

    /**
     * Liefert die absoluten Pfadnamen mehrerer Dateien.
     * 
     * @param files Dateien
     * @return      Pfadnamen
     */
    public static List<String> getAbsolutePathnames(Collection<File> files) {

        if (files == null)
            throw new NullPointerException("files == null"); // NOI18N

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
    public static List<File> getAsFiles(Collection<? extends String> filenames) {

        if (filenames == null)
            throw new NullPointerException("filenames == null"); // NOI18N

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
    public static List<String> getAsFilenames(Collection<? extends File> files) {

        if (files == null)
            throw new NullPointerException("files == null"); // NOI18N

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
    public static List<File> objectCollectionToFileList(Collection objects) {

        if (objects == null)
            throw new NullPointerException("objects == null"); // NOI18N

        List<File> fileList = new ArrayList<File>(objects.size());
        for (Object o : objects) {
            assert o instanceof File : "Not a file: " + o; // NOI18N
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

        if (files == null)
            throw new NullPointerException("files == null"); // NOI18N

        File[] fileArray = new File[files.size()];
        int index = 0;
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
    public static List<File> filterDirectories(Collection<? extends File> files) {

        if (files == null)
            throw new NullPointerException("files == null"); // NOI18N

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
     * @param  directory directory
     * @return           true if successfully deleted
     */
    public static boolean deleteDirectoryRecursive(File directory) {

        if (directory == null)
            throw new NullPointerException("directory == null"); // NOI18N

        if (directory.exists()) {
            File[] files = directory.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryRecursive(file);
                } else {
                    file.delete();
                }
            }
        }
        return (directory.delete());
    }

    private FileUtil() {
    }
}
