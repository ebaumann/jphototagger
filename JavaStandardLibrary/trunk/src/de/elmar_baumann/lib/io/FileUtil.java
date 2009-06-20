package de.elmar_baumann.lib.io;

import de.elmar_baumann.lib.resource.Bundle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
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
     * Liefert den Inhalt einer Datei als String.
     * 
     * @param filename Dateiname
     * @return         Inhalt. Null bei Fehlern.
     */
    public static String getFileAsString(String filename) {
        if (filename == null)
            throw new NullPointerException("filename == null");

        byte[] bytes = getFileBytes(filename);
        if (bytes != null) {
            try {
                return new String(bytes, "UTF-8"); // NOI18N
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(FileUtil.class.getName()).
                        log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return null;
    }

    /**
     * Liefert den Inhalt einer Datei als Bytes.
     * 
     * @param filename Dateiname
     * @return         Inhalt. Null bei Fehlern.
     */
    public static byte[] getFileBytes(String filename) {
        if (filename == null)
            throw new NullPointerException("filename == null");

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filename);
            int byteCount = fileInputStream.available();
            byte[] bytes = new byte[byteCount];
            fileInputStream.read(bytes);
            fileInputStream.close();
            return bytes;
        } catch (IOException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null,
                    ex);
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
        }
        return null;
    }

    /**
     * Stellt sicher, dass eine bestimmte Datei existiert. Falls erforderlich,
     * werden fehlende Verzeichnisse angelegt.
     * 
     * @param filename Dateiname
     * @return         true bei Erfolg
     */
    public static boolean ensureFileExists(String filename) {
        if (filename == null)
            throw new NullPointerException("filename == null");

        File file = new File(filename);
        boolean exists = file.exists();
        if (!exists) {
            File directory = file.getParentFile();
            if (ensureDirectoryExists(directory.getAbsolutePath())) {
                try {
                    file.createNewFile();
                    exists = true;
                } catch (IOException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE,
                            null, ex);
                }
            }
        }
        return exists;
    }

    /**
     * Liefert, ob eine bestimmte Datei existiert.
     * 
     * @param filename Dateiname
     * @return         true, wenn die Datei existiert und kein Verzeichnis ist
     */
    public static boolean existsFile(String filename) {
        if (filename == null)
            throw new NullPointerException("filename == null");

        File file = new File(filename);
        return file.exists() && !file.isDirectory();
    }

    /**
     * Stellt sicher, dass ein Verzeichnis existiert. Bei Nichtexistenz wird
     * ein neues angelegt.
     * 
     * @param directoryname Verzeichnisname
     * @return              true bei Erfolg
     */
    public static boolean ensureDirectoryExists(String directoryname) {
        if (directoryname == null)
            throw new NullPointerException("directoryname == null");

        boolean exists = existsDirectory(directoryname);
        if (!exists) {
            File directory = new File(directoryname);
            if (!directory.exists()) {
                exists = directory.mkdirs();
                if (!exists) {
                    Logger.getLogger(FileUtil.class.getName()).log(
                            Level.SEVERE, null, Bundle.getString(
                            "FileUtil.ErrorMessage.CreateDirectoryFailed"));
                }
            }
        }
        return exists;
    }

    /**
     * Liefert, ob ein bestimmtes Verzeichnis existiert.
     * 
     * @param directoryname Verzeichnisname
     * @return              true wenn das Verzeichnis existiert
     */
    public static boolean existsDirectory(String directoryname) {
        if (directoryname == null)
            throw new NullPointerException("directoryname == null");

        File file = new File(directoryname);
        return file.exists() && file.isDirectory();
    }

    /**
     * Kopiert eine Datei.
     * 
     * @param  source  Quelldatei
     * @param  target  Zieldatei
     * @throws java.io.IOException
     */
    public static void copyFile(File source, File target) throws IOException {
        if (source == null)
            throw new NullPointerException("source == null");
        if (target == null)
            throw new NullPointerException("target == null");

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
     * Liefert den Dateinamen eines Pfads.
     * 
     * @param  path  Pfad
     * @return Dateiname
     */
    public static String getFilename(String path) {
        if (path == null)
            throw new NullPointerException("path == null");

        File file = new File(path);
        return file.getName();
    }

    /**
     * Liefert den Pfad einer Datei nach oben bis zur Wurzel.
     * 
     * @param  file Datei
     * @return Pfad, das oberste Element ist die Wurzel
     */
    public static Stack<File> getPathFromRoot(File file) {
        if (file == null)
            throw new NullPointerException("file == null");

        Stack<File> path = new Stack<File>();
        File parent = file;
        do {
            parent = path.push(parent).getParentFile();
        } while (parent != null);
        return path;
    }

    /**
     * Liefert die Zeit der letzten Modifikation einer Datei in Millisekunden
     * seit 01.01.1970.
     * 
     * @param  filename  Dateiname
     * @return Zeit
     */
    public static long getLastModified(String filename) {
        if (filename == null)
            throw new NullPointerException("filename == null");

        return new File(filename).lastModified();
    }

    /**
     * Liefert alle Unterverzeichnisse eines Verzeichnisses.
     * 
     * @param  directory  Verzeichnis
     * @param  options    file filtering optins
     * @return Unterverzeichnisse
     */
    public static List<File> getSubDirectories(File directory,
            Set<DirectoryFilter.Option> options) {
        if (directory == null)
            throw new NullPointerException("directory == null");
        if (options == null)
            throw new NullPointerException("options == null");

        List<File> directories = new ArrayList<File>();
        if (directory.isDirectory()) {
            File[] dirs = directory.listFiles(new DirectoryFilter(options));
            if (dirs != null && dirs.length > 0) {
                directories.addAll(Arrays.asList(dirs));
            }
        }
        return directories;
    }

    /**
     * Liefert die Namen aller Unterverzeichnisse eines Verzeichnisses.
     * 
     * @param  directoryName  Verzeichnisname
     * @param  options        file filtering optins
     * @return Namen der Unterverzeichnisse
     */
    public static List<String> getSubDirectoryNames(String directoryName,
            Set<DirectoryFilter.Option> options) {
        if (directoryName == null)
            throw new NullPointerException("directoryName == null");
        if (options == null)
            throw new NullPointerException("options == null");

        List<File> directories = getSubDirectories(new File(directoryName),
                options);
        List<String> subdirectories = new ArrayList<String>();
        for (File directory : directories) {
            subdirectories.add(directory.getAbsolutePath());
        }
        return subdirectories;
    }

    /**
     * Liefert alle Unterverzeichnisse eines Verzeichnisses einschließlich
     * derer Unterverzeichnisse bis zur untersten Ebene.
     * 
     * @param  directory  Verzeichnis
     * @param  options    file filtering optins
     * @return Unterverzeichnisse
     */
    public static List<File> getAllSubDirectories(File directory,
            Set<DirectoryFilter.Option> options) {
        if (directory == null)
            throw new NullPointerException("directory == null");
        if (options == null)
            throw new NullPointerException("options == null");

        List<File> directories = new ArrayList<File>();
        if (directory.isDirectory()) {
            File[] subdirectories = directory.listFiles(new DirectoryFilter(
                    options));
            if (subdirectories != null && subdirectories.length > 0) {
                List<File> subdirectoriesList = Arrays.asList(subdirectories);
                for (File dir : subdirectoriesList) {
                    directories.add(dir);
                    List<File> subdirectoriesSubDirs = getAllSubDirectories(
                            dir, options);
                    directories.addAll(subdirectoriesSubDirs);
                }
            }
        }

        return directories;
    }

    /**
     * Liefert die Namen aller Unterverzeichnisse eines Verzeichnisses
     * einschließlich die Namen derer Unterverzeichnisse bis zur untersten
     * Ebene.
     * 
     * @param  directoryName  Verzeichnisname
     * @param  options        file filtering optins
     * @return Namen der Unterverzeichnisse
     */
    public static List<String> getAllSubDirectoryNames(String directoryName,
            Set<DirectoryFilter.Option> options) {
        if (directoryName == null)
            throw new NullPointerException("directoryName == null");
        if (options == null)
            throw new NullPointerException("options == null");

        List<File> directories = getAllSubDirectories(new File(directoryName),
                options);
        List<String> subdirectories = new ArrayList<String>();
        for (File directory : directories) {
            subdirectories.add(directory.getAbsolutePath());
        }
        return subdirectories;
    }

    /**
     * Liefert gefiltert alle Dateien eines Verzeichnisses.
     * 
     * @param  directory   Verzeichnis
     * @param  fileFilter  Dateifilter
     * @return Dateien des Verzeichnisses, die der Filter akzeptiert
     *         oder Null, falls keine gefunden wurden
     */
    public static File[] getFiles(String directory, RegexFileFilter fileFilter) {
        if (directory == null)
            throw new NullPointerException("directory == null");

        File file = new File(directory);
        return file.listFiles(fileFilter);
    }

    /**
     * Liefert einen Dateinamen ohne Endung (das Präfix). Als Endung wird
     * betrachtet, was hinter dem letzten Punkt eines Dateinamens steht
     * einschließlich des Punkts.
     * 
     * @param filename Dateiname
     * @return         Präfix oder Dateiname, wenn kein Punkt in der Datei ist
     */
    public static String getPrefix(String filename) {
        if (filename == null)
            throw new NullPointerException("fileName == null");

        int index = filename.lastIndexOf('.');
        if (index > 0) {
            return filename.substring(0, index);
        }
        return filename;
    }

    /**
     * Returns the suffix of a filename - the substring after the last period
     * in the name, e.g. "jpg" if the filename is "image.jpg".
     *
     * @param  filename  filename
     * @return suffix or empty string if the filename has no suffix
     */
    public static String getSuffix(String filename) {
        if (filename == null)
            throw new NullPointerException("filename == null");

        int index = filename.lastIndexOf('.');
        int length = filename.length();
        if (index >= 0 && index < length - 1) {
            return filename.substring(index + 1, length);
        }
        return "";
    }

    /**
     * Liefert die absoluten Pfadnamen mehrerer Dateien.
     * 
     * @param files Dateien
     * @return      Pfadnamen
     */
    public static List<String> getAbsolutePathnames(List<File> files) {
        if (files == null)
            throw new NullPointerException("files == null");

        List<String> pathnames = new ArrayList<String>(files.size());
        for (File file : files) {
            pathnames.add(file.getAbsolutePath());
        }
        return pathnames;
    }

    /**
     * Returns an array of files from an array of filenames.
     * 
     * @param  filenames  filenames
     * @return files
     */
    public static List<File> getAsFiles(List<String> filenames) {
        if (filenames == null)
            throw new NullPointerException("filenames == null");

        List<File> files = new ArrayList<File>(filenames.size());
        for (String filename : filenames) {
            files.add(new File(filename));
        }
        return files;
    }

    /**
     * Returns an array of files from an set of filenames.
     * 
     * @param  filenames  filenames
     * @return files
     */
    public static List<File> getAsFiles(Set<String> filenames) {
        if (filenames == null)
            throw new NullPointerException("filenames == null");

        List<File> files = new ArrayList<File>(filenames.size());
        for (String filename : filenames) {
            files.add(new File(filename));
        }
        return files;
    }

    /**
     * Returns an array of filenames from an array of files.
     * 
     * @param  files  files
     * @return filenames
     */
    public static List<String> getAsFilenames(List<File> files) {
        if (files == null)
            throw new NullPointerException("files == null");

        List<String> filenames = new ArrayList<String>(files.size());
        for (File file : files) {
            filenames.add(file.getAbsolutePath());
        }
        return filenames;
    }

    /**
     * Returns a list of files from a list of object where every object is a file.
     * 
     * @param  files files as object
     * @return files
     */
    public static List<File> objectListToFileList(List files) {
        if (files == null)
            throw new NullPointerException("files == null");

        List<File> fileList = new ArrayList<File>(files.size());
        for (Object o : files) {
            fileList.add((File) o);
        }
        return fileList;
    }

    /**
     * Returns an array of files from a list of files.
     * 
     * @param  files  list
     * @return array
     */
    public static File[] fileListToFileArray(List<File> files) {
        if (files == null)
            throw new NullPointerException("files == null");

        int size = files.size();
        File[] fileArray = new File[size];
        for (int i = 0; i < size; i++) {
            fileArray[i] = files.get(i);
        }
        return fileArray;
    }

    /**
     * Returns the directories of a list of files.
     * 
     * @param  files  files
     * @return existing directories within <code>files</code>
     */
    public static List<File> getDirectories(List<File> files) {
        if (files == null)
            throw new NullPointerException("files == null");

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
     * @param  dir directory
     * @return     true if successfully deleted
     */
    public static boolean deleteDirectory(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return (dir.delete());
    }

    private FileUtil() {
    }
}
