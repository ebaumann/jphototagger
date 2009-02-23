package de.elmar_baumann.imv.controller.filesystem;

import de.elmar_baumann.imv.app.AppIcons;
import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.imv.view.dialogs.UserSettingsDialog;
import de.elmar_baumann.lib.io.DirectoryFilter.Option;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/19
 */
public final class ControllerAutocopyDirectory implements ActionListener {

    public ControllerAutocopyDirectory() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemAutocopyDirectory().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        copy();
    }

    private void copy() {
        File dir = UserSettings.INSTANCE.getAutocopyDirectory();
        if (dir == null && confirmSetAutocopyDirectory()) {
            setAutocopyDirectory();
            copy(); // recursive
        } else {
            copy(dir);
        }
    }

    private void setAutocopyDirectory() {
        UserSettingsDialog dialog = UserSettingsDialog.INSTANCE;
        dialog.selectTab(UserSettingsDialog.Tab.MISC);
        if (dialog.isVisible()) {
            dialog.toFront();
        } else {
            dialog.setVisible(true);
        }
    }

    private synchronized void copy(File srcDir) {
        List<File> directories = new ArrayList<File>();
        directories.add(srcDir);
        directories.addAll(FileUtil.getAllSubDirectories(srcDir, new HashSet<Option>()));
        List<File> files = ImageFilteredDirectory.getImageFilesOfDirectories(directories);
        if (files.size() > 0) {
            CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
            dialog.setSourceFiles(files);
            dialog.setVisible(true);
        } else {
            messageNoFilesFound();
        }
    }

    private void messageNoFilesFound() {
        JOptionPane.showMessageDialog(
                null,
                Bundle.getString("ControllerAutocopyDirectory.InformationMessage.NoFilesFound"),
                Bundle.getString("ControllerAutocopyDirectory.InformationMessage.NoFilesFound.Title"),
                JOptionPane.INFORMATION_MESSAGE,
                AppIcons.getMediumAppIcon());
    }

    private boolean confirmSetAutocopyDirectory() {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString("ControllerAutocopyDirectory.ConfirmMessage.DefineDirectory"),
                Bundle.getString("ControllerAutocopyDirectory.ConfirmMessage.DefineDirectory.Title"),
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                AppIcons.getMediumAppIcon()) == JOptionPane.YES_OPTION;
    }
}
