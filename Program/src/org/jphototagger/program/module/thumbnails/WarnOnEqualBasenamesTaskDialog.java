package org.jphototagger.program.module.thumbnails;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.JXLabel;
import org.jdesktop.swingx.JXList;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class WarnOnEqualBasenamesTaskDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private final DefaultListModel filesListModel;
    private boolean isDisplayInFuture;
    private boolean listenToDisplayInFutureCheckBox = false;

    public WarnOnEqualBasenamesTaskDialog(Collection<? extends File> files) {
        super(ComponentUtil.findFrameWithIcon(), false);
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        filesListModel = new DefaultListModel();
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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        labelInfo = new JXLabel();
        scrollPanlFiles = new JScrollPane();
        listFiles = new JXList();
        checkBoxDisplayInFuture = new JCheckBox();

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        ResourceBundle bundle = ResourceBundle.getBundle("org/jphototagger/program/module/thumbnails/Bundle"); // NOI18N
        setTitle(bundle.getString("WarnOnEqualBasenamesTaskDialog.title")); // NOI18N
        setName("Form"); // NOI18N

        labelInfo.setText(bundle.getString("WarnOnEqualBasenamesTaskDialog.labelInfo.text")); // NOI18N
        labelInfo.setLineWrap(true);
        labelInfo.setName("labelInfo"); // NOI18N

        scrollPanlFiles.setName("scrollPanlFiles"); // NOI18N

        listFiles.setModel(filesListModel);
        listFiles.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listFiles.setName("listFiles"); // NOI18N
        scrollPanlFiles.setViewportView(listFiles);

        checkBoxDisplayInFuture.setText(bundle.getString("WarnOnEqualBasenamesTaskDialog.checkBoxDisplayInFuture.text")); // NOI18N
        checkBoxDisplayInFuture.setName("checkBoxDisplayInFuture"); // NOI18N
        checkBoxDisplayInFuture.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                checkBoxDisplayInFutureActionPerformed(evt);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(Alignment.LEADING)
                    .addComponent(checkBoxDisplayInFuture)
                    .addComponent(scrollPanlFiles, GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
                    .addComponent(labelInfo, GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(ComponentPlacement.RELATED)
                .addComponent(scrollPanlFiles, GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(checkBoxDisplayInFuture)
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

    private void checkBoxDisplayInFutureActionPerformed(ActionEvent evt) {//GEN-FIRST:event_checkBoxDisplayInFutureActionPerformed
        if (listenToDisplayInFutureCheckBox) {
            isDisplayInFuture = checkBoxDisplayInFuture.isSelected();
            storePropertyDisplayInFuture();
        }
    }//GEN-LAST:event_checkBoxDisplayInFutureActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                WarnOnEqualBasenamesTaskDialog dialog = new WarnOnEqualBasenamesTaskDialog(Collections.<File>emptyList());
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JCheckBox checkBoxDisplayInFuture;
    private JXLabel labelInfo;
    private JXList listFiles;
    private JScrollPane scrollPanlFiles;
    // End of variables declaration//GEN-END:variables
}
