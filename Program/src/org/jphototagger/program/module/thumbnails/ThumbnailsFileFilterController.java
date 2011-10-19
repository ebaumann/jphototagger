package org.jphototagger.program.module.thumbnails;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileFilter;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.openide.util.Lookup;

import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.WaitDisplayer;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.program.resource.GUI;

/**
 * @author Elmar Baumann
 */
public final class ThumbnailsFileFilterController implements ItemListener, ListDataListener {

    private final JComboBox fileFilterComboBox = getFileFilterComboBox();

    public ThumbnailsFileFilterController() {
        listen();
    }

    private void listen() {
        ComboBoxModel fileFilterComboBoxModel = fileFilterComboBox.getModel();

        fileFilterComboBox.addItemListener(this);
        fileFilterComboBoxModel.addListDataListener(this);
    }

    private JComboBox getFileFilterComboBox() {
        return GUI.getAppPanel().getComboBoxFileFilters();
    }

    @Override
    public void itemStateChanged(ItemEvent evt) {
        setItem(evt.getItem());
    }

    private void setItem(Object item) {
        ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

        WaitDisplayer waitDisplayer = Lookup.getDefault().lookup(WaitDisplayer.class);
        waitDisplayer.show();
        if (item instanceof FileFilter) {
            tnPanel.setFileFilter((FileFilter) item);
        } else if (item instanceof UserDefinedFileFilter) {
            tnPanel.setFileFilter(((UserDefinedFileFilter) item).getFileFilter());
        }

        writeSettings();
        waitDisplayer.hide();
    }

    private void writeSettings() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        storage.setInt(FileFiltersComboBoxModel.SETTINGS_KEY_SEL_INDEX, getFileFilterComboBox().getSelectedIndex());
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        int index0 = e.getIndex0();
        int index1 = e.getIndex1();

        if (index0 != index1 || index0 < 0) {
            return;
        }

        int selectedIndex = fileFilterComboBox.getSelectedIndex();

        if (selectedIndex == index0) {
            Object selectedItem = fileFilterComboBox.getSelectedItem();
            if (selectedItem != null) {
                setItem(selectedItem);
            }
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        // ignore
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        // ignore
    }
}
