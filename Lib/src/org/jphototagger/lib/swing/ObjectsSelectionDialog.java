package org.jphototagger.lib.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;

/**
 * @param <T> Object type
 * @author Elmar Baumann
 */
public class ObjectsSelectionDialog<T> extends Dialog {

    private static final long serialVersionUID = 1L;
    private final DefaultListModel<T> listModel = new DefaultListModel<>();
    private final List<T> selectedObjects = new ArrayList<>();
    private boolean accepted;

    public ObjectsSelectionDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
        list.addListSelectionListener(objectSelectionListener);
        list.addMouseListener(doubleClickListener);
        setOkButtonEnabled();
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setObjects(Collection<? extends T> objects) {
        if (objects == null) {
            throw new NullPointerException("objects == null");
        }
        listModel.clear();
        for (T object : objects) {
            listModel.addElement(object);
        }
    }

    public List<T> getSelectedObjects() {
        return Collections.unmodifiableList(selectedObjects);
    }

    /**
     * @param selectionMode {@link javax.swing.ListSelectionModel#SINGLE_SELECTION} or
     * {@link javax.swing.ListSelectionModel#SINGLE_INTERVAL_SELECTION} or
     * {@link javax.swing.ListSelectionModel#MULTIPLE_INTERVAL_SELECTION}
     */
    public void setSelectionMode(int selectionMode) {
        list.setSelectionMode(selectionMode);
    }

    private final ListSelectionListener objectSelectionListener = new ListSelectionListener() {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                setSelectedObjects();
                setOkButtonEnabled();
            }
        }
    };

    private void setOkButtonEnabled() {
        boolean objectSelected = list.getSelectedIndex() >= 0;
        buttonOk.setEnabled(objectSelected);
    }

    private void setSelectedObjects() {
        selectedObjects.clear();
        selectedObjects.addAll(list.getSelectedValuesList());
    }

    private final MouseListener doubleClickListener = new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (MouseEventUtil.isDoubleClick(e)) {
                setSelectedObjects();
                acceptInput();
            }
        }
    };

    private void acceptInput() {
        if (!selectedObjects.isEmpty()) {
            accepted = true;
            dispose();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents

        scrollPane = new javax.swing.JScrollPane();
        list = new javax.swing.JList<>();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        list.setModel(listModel);
        scrollPane.setViewportView(list);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/lib/swing/Bundle"); // NOI18N
        buttonOk.setText(bundle.getString("ObjectsSelectionDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonOk))
                    .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonOk)
                .addContainerGap())
        );

        pack();
    }//GEN-END:initComponents

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        acceptInput();
    }//GEN-LAST:event_buttonOkActionPerformed

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ObjectsSelectionDialog<?> dialog = new ObjectsSelectionDialog<>();
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
    private javax.swing.JButton buttonOk;
    private javax.swing.JList<T> list;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

}
