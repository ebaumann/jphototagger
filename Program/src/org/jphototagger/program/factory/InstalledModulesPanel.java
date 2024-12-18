package org.jphototagger.program.factory;

import java.awt.Component;
import java.util.Collection;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import org.jphototagger.api.modules.Module;
import org.jphototagger.api.modules.ModuleDescription;
import org.jphototagger.api.windows.OptionPageProvider;
import org.jphototagger.lib.swing.PanelExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = OptionPageProvider.class)
public class InstalledModulesPanel extends PanelExt implements OptionPageProvider {

    private static final long serialVersionUID = 1L;
    private final ModulesListModel modulesListModel = new ModulesListModel();
    private Module selectedModule;

    public InstalledModulesPanel() {
        initComponents();
    }

    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public String getTitle() {
        return Bundle.getString(InstalledModulesPanel.class, "InstalledModulesPanel.Name");
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public boolean isMiscOptionPage() {
        return true;
    }

    @Override
    public int getPosition() {
        return 1000000;
    }

    private static class ModulesListModel extends DefaultListModel<Object> {

        private static final long serialVersionUID = 1L;

        ModulesListModel() {
            Collection<Module> modules = MetaFactory.INSTANCE.getModules();
            for (Module module : modules) {
                addElement(module);
            }
        }
    }

    public Module getSelectedModule() {
        return selectedModule;
    }

    public void setSelectedModule(Module selectedModule) {
        this.selectedModule = selectedModule;
        if (selectedModule instanceof ModuleDescription) {
            ModuleDescription moduleDescription = (ModuleDescription) selectedModule;
            labelModuleDescription.setText(moduleDescription.getLocalizedDescription());
            ComponentUtil.forceRepaint(this);
        }
    }

    @SuppressWarnings({"rawtypes"})
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        labelInfo = UiFactory.label();
        scrollPaneModules = UiFactory.scrollPane();
        listModules = UiFactory.list();
        panelModuleDescription = UiFactory.panel();
        labelModuleDescription = UiFactory.jxLabel();

        
        setLayout(new java.awt.GridBagLayout());

        labelInfo.setText(Bundle.getString(getClass(), "InstalledModulesPanel.labelInfo.text")); // NOI18N
        labelInfo.setName("labelInfo"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        add(labelInfo, gridBagConstraints);

        scrollPaneModules.setName("scrollPaneModules"); // NOI18N

        listModules.setModel(modulesListModel);
        listModules.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listModules.setName("listModules"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${selectedModule}"), listModules, org.jdesktop.beansbinding.BeanProperty.create("selectedElement"));
        bindingGroup.addBinding(binding);

        scrollPaneModules.setViewportView(listModules);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        add(scrollPaneModules, gridBagConstraints);

        panelModuleDescription.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "InstalledModulesPanel.panelModuleDescription.border.title"))); // NOI18N
        panelModuleDescription.setName("panelModuleDescription"); // NOI18N
        panelModuleDescription.setPreferredSize(UiFactory.dimension(100, 75));
        panelModuleDescription.setLayout(new java.awt.GridBagLayout());

        labelModuleDescription.setLineWrap(true);
        labelModuleDescription.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        labelModuleDescription.setName("labelModuleDescription"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelModuleDescription.add(labelModuleDescription, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        add(panelModuleDescription, gridBagConstraints);

        bindingGroup.bind();
    }

    private javax.swing.JLabel labelInfo;
    private org.jdesktop.swingx.JXLabel labelModuleDescription;
    private javax.swing.JList<Object> listModules;
    private javax.swing.JPanel panelModuleDescription;
    private javax.swing.JScrollPane scrollPaneModules;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
}
