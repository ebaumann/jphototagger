package org.jphototagger.maintainance;

import java.awt.Component;
import java.text.MessageFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.file.FilenameTokens;
import org.jphototagger.domain.metadata.MetaDataValue;
import org.jphototagger.domain.metadata.selections.RepositoryInfoCountOfMetaDataValues;
import org.jphototagger.domain.repository.FileRepositoryProvider;
import org.jphototagger.domain.repository.RepositoryStatistics;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectDeletedEvent;
import org.jphototagger.domain.repository.event.dcsubjects.DcSubjectInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifDeletedEvent;
import org.jphototagger.domain.repository.event.exif.ExifInsertedEvent;
import org.jphototagger.domain.repository.event.exif.ExifUpdatedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileDeletedEvent;
import org.jphototagger.domain.repository.event.imagefiles.ImageFileInsertedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpDeletedEvent;
import org.jphototagger.domain.repository.event.xmp.XmpUpdatedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.lib.swing.TableModelExt;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class RepositoryInfoCountPanel extends javax.swing.JPanel {
    private static final long  serialVersionUID = 1L;
    private RepositoryInfoCountTableModel repositoryInfoTableModel;
    private volatile boolean listenToDbChanges;

    public RepositoryInfoCountPanel() {
        initComponents();
        table.setDefaultRenderer(Object.class, new RepositoryInfoColumnsTableCellRenderer());
        setLabelFilename();
    }

    public void listenToRepositoryChanges(boolean listen) {
        listenToDbChanges = listen;
        if (listen) {
            setModelRepositoryInfo();
        }
        if (repositoryInfoTableModel != null) {
            repositoryInfoTableModel.setListenToRepository(listen);
        }
    }

    private void setLabelFilename() {
        String pattern = Bundle.getString(RepositoryInfoCountPanel.class, "RepositoryInfoCountPanel.labelFilename.Filename");
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        if (provider != null) {
            String repositoryFileName = provider.getFileRepositoryFileName(FilenameTokens.FULL_PATH);
            String message = MessageFormat.format(pattern, repositoryFileName);
            labelFilename.setText(message);
        }
    }

    private void setModelRepositoryInfo() {
        if (repositoryInfoTableModel == null) {
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    EventQueueUtil.invokeInDispatchThread(new Runnable() {

                        @Override
                        public void run() {
                            repositoryInfoTableModel = new RepositoryInfoCountTableModel();
                            repositoryInfoTableModel.setListenToRepository(listenToDbChanges);
                            table.setModel(repositoryInfoTableModel);
                            repositoryInfoTableModel.update();
                        }
                    });
                }
            }, "JPhotoTagger: Updating repository info");

            thread.start();
        } else {
            repositoryInfoTableModel.setListenToRepository(true);
            repositoryInfoTableModel.update();
        }
    }

    private static class RepositoryInfoColumnsTableCellRenderer implements TableCellRenderer {

        private static final String PADDING_LEFT = "  ";
        private final TableCellRenderer delegate = new DefaultTableCellRenderer();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 0) {
                MetaDataValue metaDataValue = (MetaDataValue) value;
                label.setIcon(metaDataValue.getCategoryIcon());
                label.setText(metaDataValue.getDescription());
            } else {
                label.setText(PADDING_LEFT + value.toString());
            }
            int labelHeight = label.getPreferredSize().height;
            if (table.getRowHeight(row) < labelHeight) {
                table.setRowHeight(labelHeight + 4);
            }
            return label;
        }
    }

    private static class RepositoryInfoCountTableModel extends TableModelExt {

        private static final long serialVersionUID = 1L;
        private final LinkedHashMap<MetaDataValue, StringBuffer> bufferOfMetaDataValue = new LinkedHashMap<>();
        private boolean listenToRepository;
        private final RepositoryStatistics repo = Lookup.getDefault().lookup(RepositoryStatistics.class);

        private RepositoryInfoCountTableModel() {
            initBufferOfMetaDataValue();
            addColumnHeaders();
            addRows();
            listen();
        }

        private void listen() {
            AnnotationProcessor.process(this);
        }

        private void initBufferOfMetaDataValue() {
            List<MetaDataValue> metaDataValues = RepositoryInfoCountOfMetaDataValues.get();

            for (MetaDataValue mdValue : metaDataValues) {
                bufferOfMetaDataValue.put(mdValue, new StringBuffer());
            }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        public void update() {
            if (listenToRepository) {
                setCount();
            }
        }

        public void setListenToRepository(boolean listen) {
            listenToRepository = listen;
        }

        private void addColumnHeaders() {
            addColumn(Bundle.getString(RepositoryInfoCountPanel.class, "RepositoryInfoTableModel.HeaderColumn.1"));
            addColumn(Bundle.getString(RepositoryInfoCountPanel.class, "RepositoryInfoTableModel.HeaderColumn.2"));
        }

        private void addRows() {
            Set<MetaDataValue> columns = bufferOfMetaDataValue.keySet();
            for (MetaDataValue column : columns) {
                addRow(getRow(column, bufferOfMetaDataValue.get(column)));
            }
        }

        private Object[] getRow(MetaDataValue rowHeader, StringBuffer bufferDifferent) {
            return new Object[]{rowHeader, bufferDifferent};
        }

        private void setCount() {
            new SetCountThread().start();
        }

        @EventSubscriber(eventClass = ImageFileDeletedEvent.class)
        public void imageFileDeleted(ImageFileDeletedEvent evt) {
            update();
        }

        @EventSubscriber(eventClass = ImageFileInsertedEvent.class)
        public void imageFileInserted(ImageFileInsertedEvent evt) {
            update();
        }

        @EventSubscriber(eventClass = XmpUpdatedEvent.class)
        public void xmpUpdated(XmpUpdatedEvent evt) {
            update();
        }

        @EventSubscriber(eventClass = DcSubjectDeletedEvent.class)
        public void dcSubjectDeleted(DcSubjectDeletedEvent evt) {
            update();
        }

        @EventSubscriber(eventClass = DcSubjectInsertedEvent.class)
        public void dcSubjectInserted(DcSubjectInsertedEvent evt) {
            update();
        }

        @EventSubscriber(eventClass = XmpDeletedEvent.class)
        public void xmpDeleted(XmpDeletedEvent evt) {
            update();
        }

        @EventSubscriber(eventClass = ExifInsertedEvent.class)
        public void exifInserted(ExifInsertedEvent evt) {
            update();
        }

        @EventSubscriber(eventClass = ExifUpdatedEvent.class)
        public void exifUpdated(ExifUpdatedEvent evt) {
            update();
        }

        @EventSubscriber(eventClass = ExifDeletedEvent.class)
        public void exifDeleted(ExifDeletedEvent evt) {
            update();
        }

        private void setCountToBuffer(StringBuffer buffer, Integer count) {
            buffer.replace(0, buffer.length(), count.toString());
        }

        private class SetCountThread extends Thread {

            SetCountThread() {
                super("JPhotoTagger: Setting count in repository info");
                setPriority(MIN_PRIORITY);
            }

            @Override
            public void run() {
                for (MetaDataValue mdValue : bufferOfMetaDataValue.keySet()) {
                    setCountToBuffer(bufferOfMetaDataValue.get(mdValue), repo.getCountOfMetaDataValue(mdValue));
                }

                EventQueueUtil.invokeInDispatchThread(new Runnable() {

                    @Override
                    public void run() {
                        fireTableDataChanged();
                    }
                });
            }
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

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/maintainance/Bundle"); // NOI18N
        labelTable.setText(Bundle.getString(getClass(), "RepositoryInfoCountPanel.labelTable.text")); // NOI18N
        labelTable.setName("labelTable"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(12, 12, 0, 12);
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
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(6, 12, 0, 12);
        add(scrollPane, gridBagConstraints);

        labelFilename.setText(Bundle.getString(getClass(), "RepositoryInfoCountPanel.labelFilename.text")); // NOI18N
        labelFilename.setName("labelFilename"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(6, 12, 12, 12);
        add(labelFilename, gridBagConstraints);
    }//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel labelFilename;
    private javax.swing.JLabel labelTable;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
