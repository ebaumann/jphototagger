package org.jphototagger.userdefinedfilters;

import java.awt.Container;
import javax.swing.DefaultComboBoxModel;
import org.jphototagger.domain.filefilter.UserDefinedFileFilter;
import org.jphototagger.domain.repository.UserDefinedFileFiltersRepository;
import org.jphototagger.lib.beansbinding.MaxLengthValidator;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class EditUserDefinedFileFilterDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private final UserDefinedFileFilter udf = new UserDefinedFileFilter();
    private final UserDefinedFileFiltersRepository repo = Lookup.getDefault().lookup(UserDefinedFileFiltersRepository.class);
    private boolean accepted;
    private boolean update;

    public EditUserDefinedFileFilterDialog() {
        super(ComponentUtil.findFrameWithIcon(), true);
        initComponents();
        postInitComponents();
    }

    public EditUserDefinedFileFilterDialog(UserDefinedFileFilter filter) {
        super(ComponentUtil.findFrameWithIcon(), true);
        if (filter == null) {
            throw new NullPointerException("filter == null");
        }
        udf.set(filter);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        setHelpPage();
        MnemonicUtil.setMnemonics((Container) this);
    }

    private void setHelpPage() {
        setHelpPageUrl(Bundle.getString(EditUserDefinedFileFilterDialog.class, "EditUserDefinedFileFilterDialog.HelpPage"));
    }

    /**
     * @param update true, if an existing filter should be updated in the repository.
     *        false, if a new filter should be created in the repository.
     *        Default: false.
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }

    public UserDefinedFileFilter getFilter() {
        return new UserDefinedFileFilter(filter);
    }

    public boolean isAccepted() {
        return accepted;
    }

    private void handleOkButtonClicked() {
        if (filter.isValid()) {
            if (checkName(textFieldName.getText())) {
                accepted = true;
                setVisible(false);
            }
        } else {
            String message = Bundle.getString(EditUserDefinedFileFilterDialog.class, "EditUserDefinedFileFilterDialog.Error.Valid");
            MessageDisplayer.error(this, message);
        }
    }

    private boolean checkName(String name) {
        if (!update && repo.existsUserDefinedFileFilter(name)) {
            String message = Bundle.getString(EditUserDefinedFileFilterDialog.class, "EditUserDefinedFileFilterDialog.Error.NameExists", name);
            MessageDisplayer.error(this, message);
            return false;
        }

        return true;
    }

    private static class ComboBoxModel extends DefaultComboBoxModel<Object> {

        private static final long serialVersionUID = 1L;

        private ComboBoxModel() {
            addElements();
        }

        private void addElements() {
            for (UserDefinedFileFilter.Type type : UserDefinedFileFilter.Type.values()) {
                addElement(type);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        filter = udf;
        panelContent = new javax.swing.JPanel();
        labelName = new javax.swing.JLabel();
        textFieldName = new javax.swing.JTextField();
        comboBoxType = new javax.swing.JComboBox<>();
        textFieldExpression = new javax.swing.JTextField();
        checkBoxNot = new javax.swing.JCheckBox();
        panelButtons = new javax.swing.JPanel();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/userdefinedfilters/Bundle"); // NOI18N
        setTitle(bundle.getString("EditUserDefinedFileFilterDialog.title")); // NOI18N
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.setLayout(new java.awt.GridBagLayout());

        labelName.setLabelFor(textFieldName);
        labelName.setText(bundle.getString("EditUserDefinedFileFilterDialog.labelName.text")); // NOI18N
        labelName.setName("labelName"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelContent.add(labelName, gridBagConstraints);

        textFieldName.setColumns(45);
        textFieldName.setName("textFieldName"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, filter, org.jdesktop.beansbinding.ELProperty.create("${name}"), textFieldName, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setValidator(new MaxLengthValidator(45));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelContent.add(textFieldName, gridBagConstraints);

        comboBoxType.setModel(new ComboBoxModel());
        comboBoxType.setName("comboBoxType"); // NOI18N
        comboBoxType.setRenderer(new UserDefinedFileFiltersListCellRenderer());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, filter, org.jdesktop.beansbinding.ELProperty.create("${type}"), comboBoxType, org.jdesktop.beansbinding.BeanProperty.create("selectedItem"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(comboBoxType, gridBagConstraints);

        textFieldExpression.setName("textFieldExpression"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, filter, org.jdesktop.beansbinding.ELProperty.create("${expression}"), textFieldExpression, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setValidator(new MaxLengthValidator(128));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 0);
        panelContent.add(textFieldExpression, gridBagConstraints);

        checkBoxNot.setText(bundle.getString("EditUserDefinedFileFilterDialog.checkBoxNot.text")); // NOI18N
        checkBoxNot.setName("checkBoxNot"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, filter, org.jdesktop.beansbinding.ELProperty.create("${isNot}"), checkBoxNot, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        binding.setSourceNullValue(false);
        binding.setSourceUnreadableValue(false);
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 0, 0, 0);
        panelContent.add(checkBoxNot, gridBagConstraints);

        panelButtons.setName("panelButtons"); // NOI18N
        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonCancel.setText(bundle.getString("EditUserDefinedFileFilterDialog.buttonCancel.text")); // NOI18N
        buttonCancel.setName("buttonCancel"); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelButtons.add(buttonCancel, gridBagConstraints);

        buttonOk.setText(bundle.getString("EditUserDefinedFileFilterDialog.buttonOk.text")); // NOI18N
        buttonOk.setName("buttonOk"); // NOI18N
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        panelButtons.add(buttonOk, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(7, 7, 7, 7);
        getContentPane().add(panelContent, gridBagConstraints);

        bindingGroup.bind();

        pack();
    }//GEN-END:initComponents

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        handleOkButtonClicked();
    }//GEN-LAST:event_buttonOkActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        accepted = false;
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonOk;
    private javax.swing.JCheckBox checkBoxNot;
    private javax.swing.JComboBox<Object> comboBoxType;
    private org.jphototagger.domain.filefilter.UserDefinedFileFilter filter;
    private javax.swing.JLabel labelName;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
    private javax.swing.JTextField textFieldExpression;
    private javax.swing.JTextField textFieldName;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
