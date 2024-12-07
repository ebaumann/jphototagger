package org.jphototagger.program.module.thumbnails;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXList;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.lib.api.LookAndFeelChangedEvent;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class WarnOnEqualBasenamesTaskDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private final DefaultListModel<Object> filesListModel;
    private boolean isDisplayInFuture;
    private boolean listenToDisplayInFutureCheckBox = false;

    public WarnOnEqualBasenamesTaskDialog(Collection<? extends File> files) {
        super(ComponentUtil.findFrameWithIcon(), false);
        if (files == null) {
            throw new NullPointerException("files == null");
        }
        filesListModel = new DefaultListModel<>();
        addFilesToListModel(files);
        initComponents();
        postInitComponents();
        listenToDisplayInFutureCheckBox = true;
    }

    private void addFilesToListModel(Collection<? extends File> files) {
        for (File file : files) {
            filesListModel.addElement(file);
        }
    }

    private void postInitComponents() {
        readDisplayInFutureFromPreferences();
        checkBoxDisplayInFuture.setSelected(isDisplayInFuture);
        AnnotationProcessor.process(this);
    }

    private void readDisplayInFutureFromPreferences() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        isDisplayInFuture = prefs == null
                ? true
                : prefs.containsKey(AppPreferencesKeys.KEY_DISPLAY_IN_FUTURE_WARN_ON_EQUAL_BASENAMES)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_DISPLAY_IN_FUTURE_WARN_ON_EQUAL_BASENAMES)
                : true;
    }

    private void storePropertyDisplayInFuture() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setBoolean(AppPreferencesKeys.KEY_DISPLAY_IN_FUTURE_WARN_ON_EQUAL_BASENAMES, isDisplayInFuture);
    }

    @EventSubscriber(eventClass = PreferencesChangedEvent.class)
    public void userPropertyChanged(PreferencesChangedEvent evt) {
        String propertyKey = evt.getKey();
        if (AppPreferencesKeys.KEY_DISPLAY_IN_FUTURE_WARN_ON_EQUAL_BASENAMES.equals(propertyKey)) {
            listenToDisplayInFutureCheckBox = false;
            boolean newValue = (Boolean) evt.getNewValue();
            checkBoxDisplayInFuture.setSelected(newValue);
            ComponentUtil.forceRepaint(checkBoxDisplayInFuture);
            listenToDisplayInFutureCheckBox = true;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            if (isDisplayInFuture) {
                super.setVisible(true);
            }
        } else {
            super.setVisible(false);
        }
    }

    @EventSubscriber(eventClass = LookAndFeelChangedEvent.class)
    public void lookAndFeelChanged(LookAndFeelChangedEvent evt) {
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void initComponents() {
        GridBagConstraints gridBagConstraints;

        labelInfo = UiFactory.jxLabel();
        scrollPanlFiles = UiFactory.scrollPane();
        listFiles = UiFactory.jxList();
        checkBoxDisplayInFuture = UiFactory.checkBox();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "WarnOnEqualBasenamesTaskDialog.title")); // NOI18N
        
        getContentPane().setLayout(new GridBagLayout());

        labelInfo.setText(Bundle.getString(getClass(), "WarnOnEqualBasenamesTaskDialog.labelInfo.text")); // NOI18N
        labelInfo.setLineWrap(true);
        labelInfo.setName("labelInfo"); // NOI18N
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        getContentPane().add(labelInfo, gridBagConstraints);

        scrollPanlFiles.setName("scrollPanlFiles"); // NOI18N

        listFiles.setModel(filesListModel);
        listFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listFiles.setName("listFiles"); // NOI18N
        scrollPanlFiles.setViewportView(listFiles);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        getContentPane().add(scrollPanlFiles, gridBagConstraints);

        checkBoxDisplayInFuture.setText(Bundle.getString(getClass(), "WarnOnEqualBasenamesTaskDialog.checkBoxDisplayInFuture.text")); // NOI18N
        checkBoxDisplayInFuture.setName("checkBoxDisplayInFuture"); // NOI18N
        checkBoxDisplayInFuture.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                checkBoxDisplayInFutureActionPerformed(evt);
            }
        });
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        getContentPane().add(checkBoxDisplayInFuture, gridBagConstraints);

        pack();
    }

    private void checkBoxDisplayInFutureActionPerformed(ActionEvent evt) {
        if (listenToDisplayInFutureCheckBox) {
            isDisplayInFuture = checkBoxDisplayInFuture.isSelected();
            storePropertyDisplayInFuture();
        }
    }

    private JCheckBox checkBoxDisplayInFuture;
    private JXLabel labelInfo;
    private JXList listFiles;
    private JScrollPane scrollPanlFiles;
}
