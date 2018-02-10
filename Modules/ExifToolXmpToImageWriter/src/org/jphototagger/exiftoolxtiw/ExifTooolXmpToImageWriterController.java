package org.jphototagger.exiftoolxtiw;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.progress.ProgressEvent;
import org.jphototagger.api.progress.ProgressListener;
import org.jphototagger.lib.api.AppIconProvider;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.DirectoryChooser;
import org.jphototagger.lib.swing.InputDialog2;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.lib.util.SystemProperties;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class ExifTooolXmpToImageWriterController {

    private static final String KEY_START_DIR = "ExifTooolXmpToImageWriterController.StartDir";
    private final ExifTooolXmpToImageWriterModel model = new ExifTooolXmpToImageWriterModel();
    private final InputDialog2 viewDlg = new InputDialog2(ComponentUtil.findFrameWithIcon(), true);
    private final ExifTooolXmpToImageWriterPanel view = new ExifTooolXmpToImageWriterPanel();
    private final DefaultListModel<File> selDirsListModel = new DefaultListModel<File>();

    public ExifTooolXmpToImageWriterController() {
        initView();
    }

    private void initView() {
        viewDlg.setTitle(Bundle.getString(ExifTooolXmpToImageWriterController.class, "ExifTooolXmpToImageWriterController.Dlg.Title"));
        viewDlg.getButtonOk().setText(Bundle.getString(ExifTooolXmpToImageWriterController.class, "ExifTooolXmpToImageWriterController.Dlg.ButtonOkText"));
        viewDlg.getButtonCancel().setVisible(false);
        viewDlg.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        viewDlg.setComponent(view);
        viewDlg.setLocationRelativeTo(ComponentUtil.findFrameWithIcon());
        viewDlg.pack();

        view.getButtonConfigure().addActionListener(configListener);

        view.getListDirs().setModel(selDirsListModel);
        view.getListDirs().setCellRenderer(new DirListCellRenderer());
        view.getListDirs().addListSelectionListener(removeDirsSelectionListener);

        view.getButtonAddDirs().addActionListener(addDirsListener);
        view.getButtonRemoveSelectedDirs().addActionListener(removeSelDirsActionListener);

        view.getButtonExecute().addActionListener(executeListener);
        view.getButtonCancelExecute().addActionListener(cancelExcecuteListener);

        setViewEnabled();

        model.addProgressListener(progressListener);
    }

    public void executeInDialog() {
        viewDlg.setVisible(true);
    }

    private final ActionListener cancelExcecuteListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            model.setCancel(true);
        }
    };

    private final ActionListener executeListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            execute();
        }
    };

    private void execute() {
        Worker worker = new Worker();

        setEnabledForExecute(true);
        setFiles();
        worker.execute();
    }

    private void setFiles() {
        boolean subDirs = view.getCheckBoxIncludeSubDirs().isSelected();
        Collection<File> allDirs = new HashSet<File>();
        Collection<File> allFiles = new HashSet<File>();
        Settings settings = new Settings();
        FileFilter filter = settings.createFilenameFilter();

        for (Enumeration<File> e = selDirsListModel.elements(); e.hasMoreElements(); ) {
            File selDir = e.nextElement();
            allDirs.add(selDir);
            if (subDirs) {
                allDirs.addAll(FileUtil.getSubDirectoriesRecursive(selDir, null));
            }
        }

        for (File dir : allDirs) {
            allFiles.addAll(Arrays.asList(dir.listFiles(filter)));
        }

        model.setImageFiles(allFiles);
    }

    private final class Worker extends SwingWorker<Void, Void> {

        @Override
        protected Void doInBackground() throws Exception {
            model.execute();
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (Throwable t) {
                Logger.getLogger(Worker.class.getName()).log(Level.SEVERE, null, t);
            } finally {
                setEnabledForExecute(false);
            }
        }
    }

    private final ProgressListener progressListener = new  ProgressListener() {

        private final JProgressBar progressBar = view.getProgressBarExecute();

        {
            progressBar.setStringPainted(true);
            progressBar.setString("");
        }

        @Override
        public void progressStarted(final ProgressEvent evt) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    progressBar.setMinimum(evt.getMinimum());
                    progressBar.setMaximum(evt.getMaximum());
                    progressBar.setValue(evt.getValue());
                }
            });
        }

        @Override
        public void progressPerformed(final ProgressEvent evt) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    progressBar.setValue(evt.getValue());
                    progressBar.setString(evt.getStringToPaint());
                }
            });
        }

        @Override
        public void progressEnded(final ProgressEvent evt) {
            EventQueueUtil.invokeInDispatchThread(new Runnable() {

                @Override
                public void run() {
                    progressBar.setValue(evt.getValue());
                }
            });
        }
    };

    private void setEnabledForExecute(boolean isExecuting) {
        if (!canWrite()) {
            return;
        }
        viewDlg.getButtonOk().setEnabled(!isExecuting);
        view.getButtonExecute().setEnabled(!isExecuting && dirsSelected());
        view.getButtonCancelExecute().setEnabled(isExecuting);
        view.getButtonAddDirs().setEnabled(!isExecuting);
        view.getButtonRemoveSelectedDirs().setEnabled(!isExecuting && canRemoveSelDirs());
        view.getButtonConfigure().setEnabled(!isExecuting);
        view.getCheckBoxIncludeSubDirs().setEnabled(!isExecuting);
    }

    private boolean dirsSelected() {
        return selDirsListModel.getSize() > 0;
    }

    private void setViewEnabled() {
        boolean canWrite = canWrite();

        view.getLabelConfigError().setVisible(!canWrite);
        view.getButtonAddDirs().setEnabled(canWrite);
        view.getButtonRemoveSelectedDirs().setEnabled(canRemoveSelDirs());
        view.getButtonExecute().setEnabled(canWrite && dirsSelected());
        view.getButtonCancelExecute().setEnabled(false);
        view.getCheckBoxIncludeSubDirs().setEnabled(canWrite);
    }

    private boolean canWrite() {
        Settings settings = new Settings();

        return settings.isSelfResponsible()
                && settings.isExifToolEnabled()
                && settings.canWrite();
    }

    private final ListSelectionListener removeDirsSelectionListener = new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                view.getButtonRemoveSelectedDirs().setEnabled(canRemoveSelDirs());
            }
        }
    };

    private boolean canRemoveSelDirs() {
        return !view.getListDirs().isSelectionEmpty() && canWrite();
    }

    private final ActionListener removeSelDirsActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (File selDir : view.getListDirs().getSelectedValuesList()) {
                selDirsListModel.removeElement(selDir);
            }
            setViewEnabled();
        }
    };

    private final ActionListener addDirsListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            addDirs();
        }
    };

    private void addDirs() {
        DirectoryChooser chooser = createDirChooser();

        chooser.setVisible(true);
        if (chooser.isAccepted()) {
            List<File> selDirs = chooser.getSelectedDirectories();

            for (File selDir : selDirs) {
                if (!selDirsListModel.contains(selDir)) {
                    selDirsListModel.addElement(selDir);
                }
            }

            persistStartDir(selDirs);
        }
        setViewEnabled();
    }

    private DirectoryChooser createDirChooser() {
        DirectoryChooser chooser = new DirectoryChooser(
                ComponentUtil.findFrameWithIcon(),
                getStartDir(),
                getDirChooserOptions()
                );

        chooser.setTitle(Bundle.getString(ExifTooolXmpToImageWriterController.class, "ExifTooolXmpToImageWriterController.DirChooser.Title"));
        chooser.setModal(true);

        return chooser;
    }

    private DirectoryChooser.Option[] getDirChooserOptions() {
        return isAcceptHiddenDirectories()
                ? new DirectoryChooser.Option[] {
                    DirectoryChooser.Option.DISPLAY_HIDDEN_DIRECTORIES,
                    DirectoryChooser.Option.MULTI_SELECTION}
                : new DirectoryChooser.Option[] {
                    DirectoryChooser.Option.NO_OPTION,
                    DirectoryChooser.Option.MULTI_SELECTION};
    }

    private boolean isAcceptHiddenDirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? prefs.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
    }

    private void persistStartDir(List<File> selDirs) {
        if (selDirs.isEmpty()) {
            return;
        }

        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setString(KEY_START_DIR, selDirs.get(0).getAbsolutePath());
    }

    private File getStartDir() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        String dir = prefs.getString(KEY_START_DIR);

        return StringUtil.hasContent(dir)
                ? new File(dir)
                : new File(SystemProperties.getUserHome());
    }

    private final ActionListener configListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            InputDialog2 dlg = ExifToolCommon.createSettingsDialog();

            dlg.setLocationRelativeTo(view);
            dlg.setVisible(true);
            setViewEnabled();
        }
    };

    private static final class DirListCellRenderer implements ListCellRenderer<File> {

        private final DefaultListCellRenderer delegate = new DefaultListCellRenderer();
        private final Icon iconDir = Lookup.getDefault().lookup(AppIconProvider.class).getIcon("icon_folder.png");

        @Override
        public Component getListCellRendererComponent(JList<? extends File> list, File value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) delegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setIcon(iconDir);
            return label;
        }
    }
}
