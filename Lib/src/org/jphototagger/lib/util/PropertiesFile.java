package org.jphototagger.lib.util;

import org.jphototagger.lib.io.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;

/**
 * Reads from and writes to a file values of an
 * {@link java.util.Properties} instance.
 *
 * All functions with object-reference-parameters are throwing a
 * <code>NullPointerException</code> if an object reference is null and it is
 * not documentet that it can be null.
 *
 * @author Elmar Baumann
 */
public final class PropertiesFile {
    private final String     filename;
    private final String     domainName;
    private final Properties properties;
    private final String     projectName;
    private final String     directoryName;

    /**
     * Constructor.
     *
     * <p>Read and write operations applied to a file named
     * <code>.domainName/projectName/filename</code> e.g.
     * <code>.org.jphototagger/myapp/Settings.properties</code>
     * in the user's home directory.
     *
     * <p>The slash is the system's file system path delimiter.
     *
     * @param domainName   name of the domain from right to left, e.g.
     *                     <code>org.jphototagger</code>.
     *                     <p><em>Must comply with the file system's naming
     *                     restrictions!</em>
     * @param projectName  name of the project
     *                     <p><em>Must comply with the file system's naming
     *                     restrictions!</em>
     * @param filename     name of the file to store the properties, should
     *                     have the suffix <code>.properties</code>
     *                     <p><em>Must comply with the file system's naming
     *                     restrictions!</em>
     * @param properties   properties to retrieve values from a file and store
     *                     values in a file
     */
    public PropertiesFile(String domainName, String projectName,
                          String filename, Properties properties) {
        if (domainName == null) {
            throw new NullPointerException("domainName == null");
        }

        if (projectName == null) {
            throw new NullPointerException("appName == null");
        }

        if (filename == null) {
            throw new NullPointerException("filename == null");
        }

        if (properties == null) {
            throw new NullPointerException("properties == null");
        }

        this.domainName    = domainName;
        this.projectName   = projectName;
        this.filename      = filename;
        this.properties    = properties;
        this.directoryName = initGetDirectoryName();
    }

    /**
     * Returns the name of the directory holding the propertie's file.
     *
     * @return directory name
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * Writes the propertie's values to the propertie's file. If the file does
     * not exist it will be created.
     *
     * @throws IOException if the file couldn't be written
     */
    public void writeToFile() throws IOException {
        FileUtil.ensureDirectoryExists(directoryName);

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(getPropertyFilePathName());
            properties.store(out,
                             "--- " + projectName + " persistent settings ---");
        } catch (Exception ex) {
            Logger.getLogger(PropertiesFile.class.getName()).log(Level.SEVERE,
                             null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(PropertiesFile.class.getName()).log(
                        Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Reads values from the propertie's file to the properties instance.
     * Does nothing if the properties file doesn't exist.
     */
    public void readFromFile() {
        String propertyFilename = getPropertyFilePathName();

        if (FileUtil.existsFile(new File(propertyFilename))) {
            FileInputStream in = null;

            try {
                in = new FileInputStream(propertyFilename);
                properties.load(in);
            } catch (Exception ex) {
                Logger.getLogger(PropertiesFile.class.getName()).log(
                    Level.SEVERE, null, ex);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(PropertiesFile.class.getName()).log(
                            Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    private String getPropertyFilePathName() {
        return directoryName + File.separator + filename;
    }

    private String initGetDirectoryName() {
        String homeDir = System.getProperty("user.home");

        return homeDir + File.separator + "." + domainName + File.separator
               + projectName;
    }
}
