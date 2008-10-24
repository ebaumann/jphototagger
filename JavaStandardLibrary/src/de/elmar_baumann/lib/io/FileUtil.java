package de.elmar_baumann.lib.io;

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
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class FileUtil {

    /**
     * Liefert den Inhalt einer Datei als String.
     * 
     * @param filename Dateiname
     * @return         Inhalt. Null bei Fehlern.
     */
    public static String getFileAsString(String filename) {
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
                Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
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
        File file = new File(filename);
        boolean exists = file.exists();
        if (!exists) {
            File directory = file.getParentFile();
            if (ensureDirectoryExists(directory.getAbsolutePath())) {
                try {
                    file.createNewFile();
                    exists = true;
                } catch (IOException ex) {
                    Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
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
        boolean exists = directoryname == null || existsDirectory(directoryname);
        if (!exists) {
            File directory = new File(directoryname);
            if (!directory.exists()) {
                exists = directory.mkdirs();
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
    public static void copyFile(File source, File target)
        throws IOException {
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
        return new File(filename).lastModified();
    }

    /**
     * Liefert alle Unterverzeichnisse eines Verzeichnisses.
     * 
     * @param  directory      Verzeichnis
     * @param  accecptHidden  true, wenn versteckte Verzeichnisse akzeptiert
     *                         werden sollen
     * @return Unterverzeichnisse
     */
    public static List<File> getSubDirectories(File directory, boolean accecptHidden) {
        List<File> directories = new ArrayList<File>();
        if (directory.isDirectory()) {
            File[] dirs = directory.listFiles(new DirectoryFilter(accecptHidden));
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
     * @param  accecptHidden  true, wenn versteckte Verzeichnisse akzeptiert
     *                        werden sollen
     * @return Namen der Unterverzeichnisse
     */
    public static List<String> getSubDirectoryNames(String directoryName, boolean accecptHidden) {
        List<File> directories = getSubDirectories(new File(directoryName), accecptHidden);
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
     * @param  directory      Verzeichnis
     * @param  accecptHidden  true, wenn versteckte Verzeichnisse akzeptiert
     *                        werden sollen
     * @return Unterverzeichnisse
     */
    public static List<File> getAllSubDirectories(File directory, boolean accecptHidden) {
        List<File> directories = new ArrayList<File>();
        if (directory.isDirectory()) {
            File[] subdirectories = directory.listFiles(new DirectoryFilter(accecptHidden));
            if (subdirectories != null && subdirectories.length > 0) {
                List<File> subdirectoriesList = Arrays.asList(subdirectories);
                for (File dir : subdirectoriesList) {
                    directories.add(dir);
                    List<File> subdirectoriesSubDirs = getAllSubDirectories(
                        dir, accecptHidden);
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
     * @param  directoryName   Verzeichnisname
     * @param  accecptHidden   true, wenn versteckte Verzeichnisse akzeptiert
     *                         werden sollen
     * @return Namen der Unterverzeichnisse
     */
    public static List<String> getAllSubDirectoryNames(String directoryName, boolean accecptHidden) {
        List<File> directories = getAllSubDirectories(new File(directoryName), accecptHidden);
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
    public static File[] getFiles(String directory, FileFilter fileFilter) {
        File file = new File(directory);
        return file.listFiles(fileFilter);
    }

    /**
     * Liefert einen Dateinamen ohne Endung (das Präfix). Als Endung wird
     * betrachtet, was hinter dem letzten Punkt eines Dateinamens steht
     * einschließlich des Punkts.
     * 
     * @param fileName Dateiname
     * @return         Präfix oder Dateiname, wenn kein Punkt in der Datei ist
     */
    public static String getPrefix(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            return fileName.substring(0, index);
        }
        return fileName;
    }

    /**
     * Liefert die absoluten Pfadnamen mehrerer Dateien.
     * 
     * @param files Dateien
     * @return      Pfadnamen
     */
    public static List<String> getAbsolutePathnames(List<File> files) {
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
        List<File> fileList = new ArrayList<File>(files.size());
        for (Object o : files) {
            fileList.add((File) o);
        }
        return fileList;
    }

    /**
     * Returns the directories of a list of files.
     * 
     * @param  files  files
     * @return existing directories within <code>files</code>
     */
    public static List<File> getDirectories(List<File> files) {
        List<File> directories = new ArrayList<File>();
        for (File file : files) {
            if (file.exists() && file.isDirectory()) {
                directories.add(file);
            }
        }
        return directories;
    }
}
