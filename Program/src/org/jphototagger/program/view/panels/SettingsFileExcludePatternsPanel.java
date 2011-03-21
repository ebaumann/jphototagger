package org.jphototagger.program.view.panels;

import org.jphototagger.program.database.DatabaseFileExcludePatterns;
import org.jphototagger.program.event.listener.ProgressListener;
import org.jphototagger.program.event.ProgressEvent;
import org.jphototagger.program.model.ListModelFileExcludePatterns;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Persistence;
import org.jphototagger.lib.componentutil.MnemonicUtil;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;

import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Elmar Baumann
 */
public final class SettingsFileExcludePatternsPanel extends javax.swing.JPanel
        implements ProgressListener, Persistence, ListSelectionListener {
    private static final long serialVersionUID = -3083582823254767001L;
    private final transient DatabaseFileExcludePatterns db = DatabaseFileExcludePatterns.INSTANCE;
    private final ListModelFileExcludePatterns model = new ListModelFileExcludePatterns();
    private boolean isUpdateDatabase = false;
    private boolean cancel = false;

    public SettingsFileExcludePatternsPanel() {
        initComponents();
        textFieldInputPattern.requestFocusInWindow();
        MnemonicUtil.setMnemonics((Container) this);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            cancel = false;
            isUpdateDatabase = false;
            setEnabled();
        } else {
            cancelUpdateDatabase();
        }

        super.setVisible(visible);
    }

    private void deletePattern() {
        if (list.getSelectedIndex() < 0) {
            return;
        }

        String pattern = (String) list.getSelectedValue();

        model.delete(pattern);
        setEnabled();
        list.requestFocusInWindow();
    }

    private void handleListValueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            setSelectedPatternToInput();
            setEnabled();
        }
    }

    private void handleTextFieldInputPatternKeyReleased(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            insertPattern();
        } else {
            setEnabledButtonInsertPattern();
        }
    }

    private void handleListKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            deletePattern();
        }
    }

    private void insertPattern() {
        String input = textFieldInputPattern.getText().trim();

        if (!input.isEmpty() && !model.contains(input)) {
            model.insert(input);
            textFieldInputPattern.setText("");
        }

        setEnabled();
    }

    private void setEnabledButtonInsertPattern() {
        buttonInsertPattern.setEnabled(hasInput() && !existsInput());
    }

    private boolean hasInput() {
        return !textFieldInputPattern.getText().trim().isEmpty();
    }
    private boolean existsInput() {
        String input = textFieldInputPattern.getText().trim();

        return !input.isEmpty() && model.contains(input);
    }

    private void setEnabled() {
        int size = model.getSize();
        boolean itemIsSelected = list.getSelectedIndex() >= 0;

        setEnabledButtonInsertPattern();
        buttonDeletePattern.setEnabled(itemIsSelected);
        menuItemDeletePattern.setEnabled(itemIsSelected);
        buttonUpdateDatabase.setEnabled((size > 0) &&!isUpdateDatabase);
        buttonCancelUpdateDatabase.setEnabled(isUpdateDatabase);
    }

    private void setSelectedPatternToInput() {
        String pattern = (String) list.getSelectedValue();

        if (pattern != null) {
            textFieldInputPattern.setText(pattern);
        }
    }

    private void updateDatabase() {
        List<String> patterns = model.getPatterns();

        if (patterns.size() > 0) {
            isUpdateDatabase = true;
            cancel = false;
            setEnabled();
            db.deleteMatchingFiles(patterns, this);
        }
    }

    private void cancelUpdateDatabase() {
        cancel = true;
    }

    private void checkCancel(ProgressEvent evt) {
        if (cancel) {
            evt.cancel();
        }
    }

    @Override
    public void progressStarted(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                progressBarUpdateDatabase.setMinimum(evt.getMinimum());
                progressBarUpdateDatabase.setMaximum(evt.getMaximum());
                progressBarUpdateDatabase.setValue(evt.getValue());
                checkCancel(evt);
            }
        });
    }

    @Override
    public void progressPerformed(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                progressBarUpdateDatabase.setValue(evt.getValue());
                checkCancel(evt);
            }
        });
    }

    @Override
    public void progressEnded(final ProgressEvent evt) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                progressBarUpdateDatabase.setValue(evt.getValue());
                isUpdateDatabase = false;
                cancel = false;
                setEnabled();
            }
        });
    }

    @Override
    public void readProperties() {}

    @Override
    public void writeProperties() {}

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            setEnabled();
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popupMenu = new javax.swing.JPopupMenu();
        menuItemDeletePattern = new javax.swing.JMenuItem();
        labelInfoList = new javax.swing.JLabel();
        scrollPane = new javax.swing.JScrollPane();
        list = new org.jdesktop.swingx.JXList();
        labelTextFieldInputPattern = new javax.swing.JLabel();
        textFieldInputPattern = new javax.swing.JTextField();
        buttonDeletePattern = new javax.swing.JButton();
        buttonInsertPattern = new javax.swing.JButton();
        labelInfoDatabase = new javax.swing.JLabel();
        progressBarUpdateDatabase = new javax.swing.JProgressBar();
        buttonCancelUpdateDatabase = new javax.swing.JButton();
        buttonUpdateDatabase = new javax.swing.JButton();

        popupMenu.setName("popupMenu"); // NOI18N

        menuItemDeletePattern.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        menuItemDeletePattern.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jphototagger/program/resource/icons/icon_delete.png"))); // NOI18N
        menuItemDeletePattern.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.menuItemDeletePattern.text")); // NOI18N
        menuItemDeletePattern.setName("menuItemDeletePattern"); // NOI18N
        menuItemDeletePattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDeletePatternActionPerformed(evt);
            }
        });
        popupMenu.add(menuItemDeletePattern);

        setName("Form"); // NOI18N

        labelInfoList.setLabelFor(list);
        labelInfoList.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.labelInfoList.text")); // NOI18N
        labelInfoList.setName("labelInfoList"); // NOI18N

        scrollPane.setName("scrollPane"); // NOI18N

        list.setModel(model);
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        list.setComponentPopupMenu(popupMenu);
        list.setName("list"); // NOI18N
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        list.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                listKeyPressed(evt);
            }
        });
        scrollPane.setViewportView(list);

        labelTextFieldInputPattern.setLabelFor(textFieldInputPattern);
        labelTextFieldInputPattern.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.labelTextFieldInputPattern.text")); // NOI18N
        labelTextFieldInputPattern.setName("labelTextFieldInputPattern"); // NOI18N

        textFieldInputPattern.setName("textFieldInputPattern"); // NOI18N
        textFieldInputPattern.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldInputPatternFocusGained(evt);
            }
        });
        textFieldInputPattern.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textFieldInputPatternKeyReleased(evt);
            }
        });

        buttonDeletePattern.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonDeletePattern.text")); // NOI18N
        buttonDeletePattern.setToolTipText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonDeletePattern.toolTipText")); // NOI18N
        buttonDeletePattern.setEnabled(false);
        buttonDeletePattern.setName("buttonDeletePattern"); // NOI18N
        buttonDeletePattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDeletePatternActionPerformed(evt);
            }
        });

        buttonInsertPattern.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonInsertPattern.text")); // NOI18N
        buttonInsertPattern.setToolTipText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonInsertPattern.toolTipText")); // NOI18N
        buttonInsertPattern.setEnabled(false);
        buttonInsertPattern.setName("buttonInsertPattern"); // NOI18N
        buttonInsertPattern.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonInsertPatternActionPerformed(evt);
            }
        });

        labelInfoDatabase.setForeground(new java.awt.Color(0, 0, 255));
        labelInfoDatabase.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.labelInfoDatabase.text")); // NOI18N
        labelInfoDatabase.setName("labelInfoDatabase"); // NOI18N

        progressBarUpdateDatabase.setName("progressBarUpdateDatabase"); // NOI18N

        buttonCancelUpdateDatabase.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonCancelUpdateDatabase.text")); // NOI18N
        buttonCancelUpdateDatabase.setEnabled(false);
        buttonCancelUpdateDatabase.setName("buttonCancelUpdateDatabase"); // NOI18N
        buttonCancelUpdateDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelUpdateDatabaseActionPerformed(evt);
            }
        });

        buttonUpdateDatabase.setText(JptBundle.INSTANCE.getString("SettingsFileExcludePatternsPanel.buttonUpdateDatabase.text")); // NOI18N
        buttonUpdateDatabase.setName("buttonUpdateDatabase"); // NOI18N
        buttonUpdateDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonUpdateDatabaseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                    .addComponent(labelInfoList, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonDeletePattern)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonInsertPattern))
                    .addComponent(labelInfoDatabase, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                    .addComponent(progressBarUpdateDatabase, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 465, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(buttonCancelUpdateDatabase)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonUpdateDatabase))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(labelTextFieldInputPattern)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textFieldInputPattern, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelInfoList)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldInputPattern, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelTextFieldInputPattern))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonInsertPattern)
                    .addComponent(buttonDeletePattern))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelInfoDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBarUpdateDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonUpdateDatabase)
                    .addComponent(buttonCancelUpdateDatabase))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        handleListValueChanged(evt);
    }//GEN-LAST:event_listValueChanged

    private void textFieldInputPatternKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldInputPatternKeyReleased
        handleTextFieldInputPatternKeyReleased(evt);
    }//GEN-LAST:event_textFieldInputPatternKeyReleased

    private void buttonCancelUpdateDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelUpdateDatabaseActionPerformed
        cancelUpdateDatabase();
    }//GEN-LAST:event_buttonCancelUpdateDatabaseActionPerformed

    private void buttonDeletePatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDeletePatternActionPerformed
        deletePattern();
    }//GEN-LAST:event_buttonDeletePatternActionPerformed

    private void buttonUpdateDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonUpdateDatabaseActionPerformed
        updateDatabase();
    }//GEN-LAST:event_buttonUpdateDatabaseActionPerformed

    private void buttonInsertPatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonInsertPatternActionPerformed
        insertPattern();
    }//GEN-LAST:event_buttonInsertPatternActionPerformed

    private void listKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_listKeyPressed
        handleListKeyPressed(evt);
    }//GEN-LAST:event_listKeyPressed

    private void menuItemDeletePatternActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDeletePatternActionPerformed
        deletePattern();
    }//GEN-LAST:event_menuItemDeletePatternActionPerformed

    private void textFieldInputPatternFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldInputPatternFocusGained
        textFieldInputPattern.selectAll();
    }//GEN-LAST:event_textFieldInputPatternFocusGained

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancelUpdateDatabase;
    private javax.swing.JButton buttonDeletePattern;
    private javax.swing.JButton buttonInsertPattern;
    private javax.swing.JButton buttonUpdateDatabase;
    private javax.swing.JLabel labelInfoDatabase;
    private javax.swing.JLabel labelInfoList;
    private javax.swing.JLabel labelTextFieldInputPattern;
    private org.jdesktop.swingx.JXList list;
    private javax.swing.JMenuItem menuItemDeletePattern;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JProgressBar progressBarUpdateDatabase;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField textFieldInputPattern;
    // End of variables declaration//GEN-END:variables
}
