package org.jphototagger.program.view.panels;

import java.text.MessageFormat;

import org.openide.util.Lookup;

import org.jphototagger.api.file.FilenameTokens;
import org.jphototagger.api.storage.UserFilesProvider;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.model.DatabaseInfoTableModel;
import org.jphototagger.program.view.renderer.DatabaseInfoColumnsTableCellRenderer;

/**
 * Dislplays the database record count total and of specific columns.
 *
 * @author Elmar Baumann
 */
public final class DatabaseInfoCountPanel extends javax.swing.JPanel {
    private static final long  serialVersionUID = -8537559082830438692L;
    private DatabaseInfoTableModel modelDatabaseInfo;
    private volatile boolean listenToDbChanges;

    public DatabaseInfoCountPanel() {
        initComponents();
        table.setDefaultRenderer(Object.class, new DatabaseInfoColumnsTableCellRenderer());
        setLabelFilename();
    }

    public void listenToDatabaseChanges(boolean listen) {
        listenToDbChanges = listen;

        if (listen) {
            setModelDatabaseInfo();
        }

        if (modelDatabaseInfo != null) {
            modelDatabaseInfo.setListenToDatabase(listen);
        }
    }

    private void setLabelFilename() {
        String pattern = Bundle.getString(DatabaseInfoCountPanel.class, "DatabaseInfoCountPanel.labelFilename.Filename");
        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);

        if (provider != null) {
            String databaseFileName = provider.getDatabaseFileName(FilenameTokens.FULL_PATH);
            String message = MessageFormat.format(pattern, databaseFileName);

            labelFilename.setText(message);
        }
    }

    private void setModelDatabaseInfo() {
        if (modelDatabaseInfo == null) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    EventQueueUtil.invokeInDispatchThread(new Runnable() {

                        @Override
                        public void run() {
                            modelDatabaseInfo = new DatabaseInfoTableModel();
                            modelDatabaseInfo.setListenToDatabase(listenToDbChanges);
                            table.setModel(modelDatabaseInfo);
                            modelDatabaseInfo.update();
                        }
                    });
                }
            }, "JPhotoTagger: Updating database info");

            thread.start();
        } else {
            modelDatabaseInfo.setListenToDatabase(true);
            modelDatabaseInfo.update();
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        labelTable = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        labelFilename = new javax.swing.JLabel();

        setName("Form"); // NOI18N
        setLayout(new java.awt.GridBagLayout());

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/view/panels/Bundle"); // NOI18N
        labelTable.setText(bundle.getString("DatabaseInfoCountPanel.labelTable.text")); // NOI18N
        labelTable.setName("labelTable"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(12, 12, 0, 12);
        add(labelTable, gridBagConstraints);

        scrollPane.setName("scrollPane"); // NOI18N

        table.setName("table"); // NOI18N
        scrollPane.setViewportView(table);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 0, 12);
        add(scrollPane, gridBagConstraints);

        labelFilename.setText(bundle.getString("DatabaseInfoCountPanel.labelFilename.text")); // NOI18N
        labelFilename.setName("labelFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 12, 12, 12);
        add(labelFilename, gridBagConstraints);
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelFilename;
    private javax.swing.JLabel labelTable;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
