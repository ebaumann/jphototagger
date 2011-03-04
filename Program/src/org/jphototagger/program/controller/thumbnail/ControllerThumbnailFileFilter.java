package org.jphototagger.program.controller.thumbnail;

import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.data.UserDefinedFileFilter;
import org.jphototagger.program.model.ComboBoxModelFileFilters;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.dialogs.UserDefinedFileFilterDialog;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.WaitDisplay;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.FileFilter;

import javax.swing.JComboBox;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerThumbnailFileFilter implements ActionListener, ItemListener {
    public ControllerThumbnailFileFilter() {
        getFileFilterComboBox().addItemListener(this);
        GUI.getAppFrame().getMenuItemUserDefinedFileFilter().addActionListener(this);
    }

    private JComboBox getFileFilterComboBox() {
        return GUI.getAppPanel().getComboBoxFileFilters();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        new UserDefinedFileFilterDialog().setVisible(true);
    }

    @Override
    public void itemStateChanged(ItemEvent evt) {
        Object item = evt.getItem();
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        WaitDisplay.show();

        if (item instanceof FileFilter) {
            tnPanel.setFileFilter((FileFilter) item);
        } else if (item instanceof UserDefinedFileFilter) {
            tnPanel.setFileFilter(((UserDefinedFileFilter) item).getFileFilter());
        }

        writeSettings();
        WaitDisplay.hide();
    }

    private void writeSettings() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        settings.set(getFileFilterComboBox().getSelectedIndex(), ComboBoxModelFileFilters.SETTINGS_KEY_SEL_INDEX);
        UserSettings.INSTANCE.writeToFile();
    }
}
