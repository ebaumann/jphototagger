package org.jphototagger.lib.io;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.swing.JFileChooser;
import org.jphototagger.lib.dialog.FileChooserExt;
import org.jphototagger.lib.util.Settings;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FileChooserHelper {
    
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
            if (p.getDialogTitle() != null) {
                fc.setDialogTitle(p.getDialogTitle());
            }

            if (p.getFileFilter() != null) {
                fc.setFileFilter(p.getFileFilter());
                fc.setAcceptAllFileFilterUsed(false);
            }

            if (p.getSaveFilenameExtension() != null) {
                fc.setSaveFilenameExtension(p.getSaveFilenameExtension());
            }

            fc.setMultiSelectionEnabled(p.isMultiSelectionEnabled());
            fc.setConfirmOverwrite(p.isConfirmOverwrite());
            fc.setFileSelectionMode(p.getFileSelectionMode());
        }

        return fc;
    }

    private static Component getParent(FileChooserProperties p) {
        return (p == null)
               ? null
               : p.getParent();
    }

    private static boolean isOpen(FileChooserProperties p) {
        return (p == null)
               ? true
               : p.isOpen();
    }

    private static void writeCurrentDirectoryPath(List<File> files, FileChooserProperties p) {
        if (canWriteProperties(p) &&!files.isEmpty()) {
            p.getProperties().setProperty(getKeyCurrentDirectoryPath(p), files.get(0).getAbsolutePath());
        }
    }

    private static String getCurrentDirectoryPath(FileChooserProperties p) {
        if (p == null) {
            return "";
        }

        if (p.getCurrentDirectoryPath() != null) {
            return p.getCurrentDirectoryPath();
        }

        if (canWriteProperties(p)) {
            String dp = p.getProperties().getProperty(getKeyCurrentDirectoryPath(p));

            if (dp != null) {
                return dp;
            }
        }

        return "";
    }

    private static String getKeyCurrentDirectoryPath(FileChooserProperties p) {
        assert canWriteProperties(p);

        return p.getPropertyKeyPrefix() + ".CurrentDirectoryPath";
    }

    private static void applySize(JFileChooser fc, FileChooserProperties p) {
        if (canWriteProperties(p)) {
            Settings settings = new Settings(p.getProperties());
            int width = settings.getInt(p.getPropertyKeyPrefix() + Settings.KEY_POSTFIX_WIDTH);
            int height = settings.getInt(p.getPropertyKeyPrefix() + Settings.KEY_POSTFIX_HEIGHT);

            if ((width > 0) && (height > 0)) {
                fc.setPreferredSize(new Dimension(width, height));
            }
        }
    }

    private static void writeSize(JFileChooser fc, FileChooserProperties p) {
        if (canWriteProperties(p)) {
            Settings settings = new Settings(p.getProperties());

            settings.set(fc.getWidth(), p.getPropertyKeyPrefix() + Settings.KEY_POSTFIX_WIDTH);
            settings.set(fc.getHeight(), p.getPropertyKeyPrefix() + Settings.KEY_POSTFIX_HEIGHT);
        }
    }

    private static boolean canWriteProperties(FileChooserProperties p) {
        return (p != null) && (p.getPropertyKeyPrefix() != null) && (p.getProperties() != null);
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
            fcProperties.multiSelectionEnabled(false);
        }

        List<File> files = chooseFiles(fcProperties);

        return files.isEmpty()
               ? null
               : files.get(0);
    }

    private FileChooserHelper() {
    }
}
