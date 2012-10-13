package org.jphototagger.lib.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.concurrent.CancelRequest;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.util.CollectionUtil;
import org.jphototagger.lib.util.Md5Util;
import org.jphototagger.lib.util.ObjectUtil;
import org.jphototagger.lib.util.StringUtil;

/**
 * Utilities for Files.
 *
 * @author Elmar Baumann
 */
public final class FileUtil {

    /**
     * Returns the content of a file as string. <p> Usage for text files.
     *
     * @param file file
     * @param encoding encoding of the file's text content
     * @return content
     * @throws IOException
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

    public static void writeStringAsFile(String string, String charsetName, File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (charsetName == null) {
            throw new NullPointerException("charsetName == null");
        }
        if (string == null) {
            throw new NullPointerException("string == null");
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(string.getBytes(charsetName));
        } finally {
            IoUtil.close(fos);
        }
    }

    /**
     * Returns the content of a file als byte array.
     *
     * @param file file
     * @return content
     * @throws IOException
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
     * Ensures that a file exists. Creates the file and it's not existing parent files (directories).
     *
     * @param file file
     * @throws IOException
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
     * Returns whether a file exists and is <em>not</em> a directory (weaker than {@code File#isFile()}).
     *
     * @param file file
     * @return true if the file exists and is not a directory
     */
    public static boolean existsFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        return file.exists() && !file.isDirectory();
    }

    /**
     * Ensures that a directory exists. If it does not exist, this method creates a new directory including its parent
     * directories.
     *
     * @param directory directory
     * @throws IOException
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
     * @param directory directory
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
     * @param sourceFile source file
     * @param targetFile target file
     * @throws java.io.IOException
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
     * Returns the path to a file up to it's root file. <p> Every stack element obove another stack element is the
     * parent file
     * ({@code File#getParentFile()}) of the element below it. The top of the stack is the root file.
     *
     * @param file file
     * @return path
     */
    public static Stack<File> getPathFromRoot(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (file.getParent() == null) {
            return new Stack<>();
        }
        Stack<File> path = new Stack<>();
        File parent = file;
        do {
            parent = path.push(parent).getParentFile();
        } while (parent != null);
        return path;
    }

    /**
     * Returns recursive all subdirectories of a parent directory.
     *
     * @param directory parent directory
     * @param cancelRequest cancel request or null
     * @param options options
     * @return subdirectories or empty list
     */
    public static List<File> getSubDirectoriesRecursive(File directory, CancelRequest cancelRequest, DirectoryFilter.Option... options) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }
        if (options == null) {
            throw new NullPointerException("options == null");
        }
        List<File> allSubDirs = new ArrayList<>();
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
     * Returns the absolute path names ({@code File#getAbsolutePath()} of a collection of files.
     *
     * @param files files
     * @return path names of the files
     */
    public static List<String> getAbsolutePathnames(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        List<String> pathnames = new ArrayList<>(files.size());
        for (File file : files) {
            pathnames.add(file.getAbsolutePath());
        }
        return pathnames;
    }

    /**
     * Returns a list of files from a collection of path names.
     *
     * @param pathnames path names
     * @return files
     */
    public static List<File> getStringsAsFiles(Collection<? extends String> pathnames) {
        if (pathnames == null) {
            throw new NullPointerException("pathnames == null");
        }
        List<File> files = new ArrayList<>(pathnames.size());
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
        List<File> filteredFiles = new ArrayList<>(files.size());
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
     * @param directory directory
     * @throws IOException
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
     * @param file suggested file
     * @return file in the same path with a unique number prepending the suffix
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
                pathname = prefix + "_" + Integer.valueOf(index) + "." + suffix;
            } else {
                pathname += "_" + Integer.valueOf(index);
            }
            index++;
            newFile = new File(pathname);
        }
        return newFile;
    }

    /**
     * Returns the root of a file. <p> The Windows file name
     * <code>"C:\Programs\MyProg\Readme.txt"</code> has the root name
     * <code>"C:\"</code>, the Unix file name
     * <code>"/home/me/Readme.txt"</code> has the root name
     * <code>"/"</code>.
     *
     * @param file file
     * @return root file of the file or the file itself if it has no parents
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
     * Returns the directory path of a file. <p> The directory path is path of the parent file without the root.
     *
     * @param file file
     * @return directory path
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
     * Returns the suffix of a file path. <p> The suffix is the substring after the last dot in the filename.
     *
     * @param file file
     * @return suffix or empty string if the filename has no dot or the dot is the first or last character within the
     * filename
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
        return index > 0 && index < len - 1;
    }

    /**
     * @param files
     * @return key is a suffix in lower case, values are files with that suffix ignoring case taken from {@code files}
     */
    public static Map<String, List<File>> getFilesWithSuffixIgnoreCase(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        Map<String, List<File>> filesOfSuffix = new HashMap<>();
        for (File file : files) {
            String suffixLowerCase = getSuffix(file).toLowerCase();
            List<File> fos = filesOfSuffix.get(suffixLowerCase);
            if (fos == null) {
                fos = new LinkedList<>();
                filesOfSuffix.put(suffixLowerCase, fos);
            }
            fos.add(file);
        }
        return filesOfSuffix;
    }

    /**
     * Returns the prefix of a filename. <p> The prefix is the substring before the last dot in a filename. If the last
     * dot is the first or last character within the string, the prefix is equal to the filename.
     *
     * @param file file
     * @return prefix
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
     *
     * @param absolutePath p
     * @return "/a/b/c.txt" &gt; "/a/b/c"
     */
    public static String getAbsolutePathnamePrefix(String absolutePath) {
        if (absolutePath == null) {
            throw new NullPointerException("absolutePath == null");
        }
        int index = absolutePath.lastIndexOf('.');
        return index > 0
                ? absolutePath.substring(0, index)
                : absolutePath;
    }

    /**
     *
     * @param files f
     * @param excludeSuffix empty if no suffix shall be excluded
     * @return f
     */
    static public List<File> getFilesWithEqualBasenames(Collection<File> files, String excludeSuffix) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        if (excludeSuffix == null) {
            throw new NullPointerException("excludeSuffix == null");
        }
        Map<String, List<File>> filesWithEqualPrefix = new HashMap<>();
        String excludeSuffixLowercase = excludeSuffix.toLowerCase();
        for (File file : files) {
            String filePathLowercase = file.getAbsolutePath();
            if (excludeSuffixLowercase.isEmpty() || !filePathLowercase.endsWith(excludeSuffixLowercase)) {
                String absolutePath = file.getAbsolutePath();
                String filePathPrefix = getAbsolutePathnamePrefix(absolutePath);
                String filePathPrefixLowercase = filePathPrefix.toLowerCase();
                List<File> fWithEqualPrefix = filesWithEqualPrefix.get(filePathPrefixLowercase);
                if (fWithEqualPrefix == null) {
                    fWithEqualPrefix = new ArrayList<>(3);
                    filesWithEqualPrefix.put(filePathPrefixLowercase, fWithEqualPrefix);
                }
                fWithEqualPrefix.add(file);
            }
        }
        List<File> filesWithEqualBasenames = new ArrayList<>();
        for (List<File> fWithEqualBasenames : filesWithEqualPrefix.values()) {
            int size = fWithEqualBasenames.size();
            if (size > 1) {
                for (File filepath : fWithEqualBasenames) {
                    filesWithEqualBasenames.add(filepath);
                }
            }
        }
        return filesWithEqualBasenames;
    }

    /**
     * Ensures that a file has a specific suffix. Ignores the case
     *
     * @param file file
     * @param suffix suffix - a dot will <em>not</em> be prepended - e.g.
     * <code>".xml"</code>
     * @return file or a new file with the old file path but the suffix. E.g. if the suffix is
     * <code>".xml"</code> and the file name is
     * <code>"file.xml"</code> or the file name is
     * <code>"file"</code> in both cases a file named
     * <code>"file.xml"</code> will be returned.
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
     * @param fileToTouch file
     * @param referenceFile file from which the time shall be taken or null if the current system time shall be used
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

    public static String getMd5FilenameOfAbsolutePath(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);

            return null;
        }
        md5.reset();
        md5.update(("file://" + file.getAbsolutePath()).getBytes());
        byte[] result = md5.digest();
        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            if ((result[i] & 0xff) == 0) {
                hex.append("00");
            } else if ((result[i] & 0xff) < 0x10) {
                hex.append("0").append(Integer.toHexString(0xFF & result[i]));
            } else {
                hex.append(Integer.toHexString(0xFF & result[i]));
            }
        }
        return hex.toString();
    }

    // http://www.rgagnon.com/javadetails/java-0416.html
    public static String getMd5OfFileContent(File file) throws IOException, NoSuchAlgorithmException {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (!file.exists()) {
            throw new IllegalStateException("File does not exist: " + file);
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return Md5Util.getMd5FromStream(is);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        } finally {
            IoUtil.close(is);
        }
    }

    public static String toStringWithMaximumLength(File file, int maximumLength) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }
        if (maximumLength < 0) {
            throw new IllegalArgumentException("Negative maximum length: " + maximumLength);
        }
        if (maximumLength == 0) {
            return "";
        }
        String filepath = file.getAbsolutePath();
        if (filepath.length() <= maximumLength) {
            return filepath;
        }
        String fill = "...";
        if (maximumLength <= fill.length()) {
            return StringUtil.getNTimesRepeated(".", maximumLength);
        }
        String filename = file.getName();
        StringBuilder sb = new StringBuilder();
        int filenameWithFillLength = fill.length() + File.separator.length() + filename.length();
        if (maximumLength >= filenameWithFillLength) { // Min. ".../Filename"
            String filepathRemainder = filepath.substring(0, maximumLength - filenameWithFillLength);
            sb.append(filepathRemainder).append(fill).append(File.separator).append(filename);
        } else {
            String filenameRemainder = filename.substring(filename.length() - maximumLength + fill.length());
            sb.append(fill).append(filenameRemainder);
        }
        return sb.toString();

    }

    /**
     * @param classWithinJar
     * @return existing directory or null
     */
    public static File getJarDirectory(Class<?> classWithinJar) {
        if (classWithinJar == null) {
            throw new NullPointerException("classWithinJar == null");
        }
        try {
            ProtectionDomain protectionDomain = classWithinJar.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            URL locationURL = codeSource.getLocation();
            URI locationURI = locationURL.toURI();
            File jarPath = new File(locationURI);
            File parentFile = jarPath.getParentFile();
            if (parentFile != null && parentFile.isDirectory()) {
                return parentFile;
            }
        } catch (Throwable t) {
            Logger.getLogger(FileUtil.class.getName()).log(Level.SEVERE, null, t);
        }
        return null;

    }

    /**
     * @param files
     * @return true if all files having the same parent or the collection of files is empty
     */
    public static boolean inSameDirectory(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        if (files.isEmpty()) {
            return true;
        }
        File prevParent = CollectionUtil.getFirstElement(files).getParentFile();
        for (File file : files) {
            File parent = file.getParentFile();
            if (!ObjectUtil.equals(parent, prevParent)) {
                return false;
            }
            prevParent = parent;
        }
        return true;
    }

    /**
     * @param parentFile
     * @param filter maybe null (then unused)
     * @return In all cases a not null object (empty if {@link File#listFiles() or {@link File#listFiles(java.io.FileFilter)}
     * returning null)
     */
    public static List<File> listFiles(File parentFile, FileFilter filter) {
        if (parentFile == null) {
            throw new NullPointerException("parentFile == null");
        }
        File[] files = filter == null
                ? parentFile.listFiles()
                : parentFile.listFiles(filter);
        return files == null
                ? Collections.<File>emptyList()
                : Arrays.asList(files);
    }

    private FileUtil() {
    }
}
