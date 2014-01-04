package org.jphototagger.program.module.keywords.tree;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.resource.GUI;

/**
 * Modal dialog to select a path.
 *
 * @author Elmar Baumann
 */
public class PathSelectionDialog extends Dialog implements ListSelectionListener {

    private static final long serialVersionUID = 1L;
    private boolean accepted;
    private final Collection<Collection<String>> paths;
    private Collection<Collection<String>> selPaths;
    private final Mode mode;

    public enum Mode {
        PATHS,
        DISTINCT_ELEMENTS,
    }

    public PathSelectionDialog(Collection<Collection<String>> paths, Mode mode) {
        super(GUI.getAppFrame(), true);
        if (paths == null) {
            throw new NullPointerException("paths == null");
        }

        if (mode == null) {
            throw new NullPointerException("mode == null");
        }
        this.paths = paths;
        this.mode  = mode;
        assert paths != null : "paths == null!";
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        list.addListSelectionListener(this);
        if (mode.equals(Mode.DISTINCT_ELEMENTS)) {
            list.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
            list.setVisibleRowCount(-1);
        }
        MnemonicUtil.setMnemonics((Container) this);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setInfoMessage(String message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }
        labelInfo.setText(message);
        labelInfo.setDisplayedMnemonic(message.charAt(0));
    }

    public Collection<Collection<String>> getSelPaths() {
        return Collections.unmodifiableCollection(selPaths);
    }

    private void handleButtonSelectNothingActionPerformed() {
        selPaths = new ArrayList<>();
        accepted = false;
        setVisible(false);
    }

    private void handleButtonSelectAllActionPerformed() {
        accepted = true;
        selPaths = new ArrayList<>(paths);
        setVisible(false);
    }

    @SuppressWarnings("unchecked")
    private void handleButtonSelectSelectedActionPerformed() {
        accepted = true;
        List<Collection<String>> sel = new ArrayList<>();
        Object[] selValues = list.getSelectedValues();
        for (Object selValue : selValues) {
            if (selValue instanceof Collection<?>) {
                Collection<String> collection = (Collection<String>) selValue;
                sel.add(collection);
            } else if (selValue instanceof String) {
                Collection<String> collection = Collections.singletonList((String) selValue);
                sel.add(collection);
            }
        }
        selPaths = sel;
        setVisible(false);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }
        if (!evt.getValueIsAdjusting()) {
            buttonSelectSelected.setEnabled(list.getSelectedIndex() >= 0);
        }
    }

    private class Model extends DefaultListModel<Object> {

        private static final long serialVersionUID = 1L;

        Model() {
            if (mode.equals(Mode.DISTINCT_ELEMENTS)) {
                addDistinctElements();
            } else {
                addPaths();
            }
        }

        private void addPaths() {
            for (Collection<?> path : paths) {
                addElement(path);
            }
        }

        private void addDistinctElements() {
            for (Collection<String> path : paths) {
                for (String element : path) {
                    addElement(element);
                }
            }
        }
    }


    private static class Renderer extends DefaultListCellRenderer {

        private static final long serialVersionUID = 1L;
        private final Icon ICON = AppLookAndFeel.getIcon("icon_keyword.png");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Collection<?>) {
                renderCollection(value, label);
            } else if (value instanceof String) {
                String padding = "  ";
                label.setText((String) value + padding);
            }
            label.setIcon(ICON);
            return label;
        }

        private void renderCollection(Object value, JLabel label) {
            Collection<? extends Object> collection = (Collection<? extends Object>) value;
            StringBuilder sb = new StringBuilder();
            String pathDelim = " > ";
            int i = 0;
            for (Object element : collection) {
                sb.append(i == 0
                        ? ""
                        : pathDelim).append(element.toString());
                i++;
            }
            label.setText(sb.toString());
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

        labelInfo = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        list = new org.jdesktop.swingx.JXList();
        buttonSelectNothing = new javax.swing.JButton();
        buttonSelectAll = new javax.swing.JButton();
        buttonSelectSelected = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/program/module/keywords/tree/Bundle"); // NOI18N
        setTitle(bundle.getString("PathSelectionDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelInfo.setLabelFor(list);
        labelInfo.setName("labelInfo"); // NOI18N

        scrollPane.setName("scrollPane"); // NOI18N

        list.setModel(new Model());
        list.setCellRenderer(new Renderer());
        list.setName("list"); // NOI18N
        scrollPane.setViewportView(list);

        buttonSelectNothing.setText(bundle.getString("PathSelectionDialog.buttonSelectNothing.text")); // NOI18N
        buttonSelectNothing.setName("buttonSelectNothing"); // NOI18N
        buttonSelectNothing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectNothingActionPerformed(evt);
            }
        });

        buttonSelectAll.setText(bundle.getString("PathSelectionDialog.buttonSelectAll.text")); // NOI18N
        buttonSelectAll.setName("buttonSelectAll"); // NOI18N
        buttonSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectAllActionPerformed(evt);
            }
        });

        buttonSelectSelected.setText(bundle.getString("PathSelectionDialog.buttonSelectSelected.text")); // NOI18N
        buttonSelectSelected.setEnabled(false);
        buttonSelectSelected.setName("buttonSelectSelected"); // NOI18N
        buttonSelectSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSelectSelectedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelInfo, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(buttonSelectNothing)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelectAll)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonSelectSelected)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonSelectSelected)
                    .addComponent(buttonSelectAll)
                    .addComponent(buttonSelectNothing))
                .addGap(12, 12, 12))
        );

        pack();
    }//GEN-END:initComponents

    private void buttonSelectNothingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectNothingActionPerformed
        handleButtonSelectNothingActionPerformed();
    }//GEN-LAST:event_buttonSelectNothingActionPerformed

    private void buttonSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectAllActionPerformed
        handleButtonSelectAllActionPerformed();
    }//GEN-LAST:event_buttonSelectAllActionPerformed

    private void buttonSelectSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSelectSelectedActionPerformed
        handleButtonSelectSelectedActionPerformed();
    }//GEN-LAST:event_buttonSelectSelectedActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        setVisible(false);    // writes properties
    }                         // GEN-LAST:event_formWindowClosing

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                @SuppressWarnings({"unchecked", "rawtypes"}) PathSelectionDialog dialog =
                        new PathSelectionDialog(new ArrayList(new ArrayList<String>()), Mode.PATHS);
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
    private javax.swing.JButton buttonSelectAll;
    private javax.swing.JButton buttonSelectNothing;
    private javax.swing.JButton buttonSelectSelected;
    private javax.swing.JLabel labelInfo;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
