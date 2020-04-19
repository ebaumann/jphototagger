package org.jphototagger.importfiles.subdircreators.templates;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.lib.swing.InputDialog2;
import org.jphototagger.lib.swing.ListModelExt;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.MouseEventUtil;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;

/**
 * Let the user edit subdirectory creation templates via a GUI.
 *
 * @author Elmar Baumann
 */
public final class EditSubdirectoryTemplatesController {

    private final SubdirectoryTemplates templates;
    private final ListModelExt<SubdirectoryTemplate> templatesListModel = new ListModelExt<>();
    private final EditSubdirectoryTemplatesPanel view = new EditSubdirectoryTemplatesPanel();
    private Component parentComponent;

    /**
     * @param templates instance to modify; after editing the instances contains
     *                  modified / deleted / added {@link SubdirectoryTemplate}
     *                  instances.
     */
    public EditSubdirectoryTemplatesController(SubdirectoryTemplates templates) {
        this.templates = Objects.requireNonNull(templates, "templates == null");
        initView();
    }

    private void initView() {
        view.getListTemplates().setModel(templatesListModel);
        view.getListTemplates().setCellRenderer(new SubdirectoryTemplateListCellRenderer());

        DeleteTemplatesAction deleteTemplatesAction = new DeleteTemplatesAction();
        EditTemplatesAction editTemplatesAction = new EditTemplatesAction();
        MoveDownAction moveDownAction = new MoveDownAction();
        MoveUpAction moveUpAction = new MoveUpAction();

        view.getListTemplates().addListSelectionListener(deleteTemplatesAction);
        view.getListTemplates().addListSelectionListener(editTemplatesAction);
        view.getListTemplates().addListSelectionListener(moveDownAction);
        view.getListTemplates().addListSelectionListener(moveUpAction);
        view.getListTemplates().addMouseListener(new TemplatesListDoubleClickListener());
        view.getListTemplates().addKeyListener(new TemplatesListEnterListener());
        view.getListTemplates().addKeyListener(new TemplatesListDeleteListener());

        view.getButtonCreate().setAction(new CreateTemplateAction());
        view.getButtonDelete().setAction(deleteTemplatesAction);
        view.getButtonEdit().setAction(editTemplatesAction);

        view.getButtonMoveUp().setAction(moveUpAction);
        view.getButtonMoveDown().setAction(moveDownAction);

        MnemonicUtil.setMnemonics(view);
    }

    /**
     * @param parentComponent component, relative to that the UI shall be
     *                        positioned.
     */
    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public boolean execute() {
        modelToView();

        InputDialog2 dlg = createDialog();

        dlg.setVisible(true);
        if (dlg.isAccepted()) {
            viewToModel();
            return true;
        }

        return false;
    }


    private InputDialog2 createDialog() {
        InputDialog2 dlg = new InputDialog2(ComponentUtil.findFrameWithIcon(), true);

        dlg.setTitle(Bundle.getString(EditSubdirectoryTemplatesController.class, "EditSubdirectoryTemplatesController.Dialog.Title"));
        dlg.setOkButtonText(Bundle.getString(EditSubdirectoryTemplatesController.class, "EditSubdirectoryTemplatesController.Dialog.OkButton.Text"));
        MnemonicUtil.setMnemonics(dlg);
        dlg.setComponent(view);
        dlg.setModal(true);
        dlg.setLocationRelativeTo(parentComponent);

        dlg.pack();

        return dlg;
    }

    private void modelToView() {
        ensureCorrectTemplatesPosition();
        templatesListModel.setElements(templates.getTemplates());
    }

    private void viewToModel() {
        templates.getTemplates().clear();
        templates.getTemplates().addAll(templatesListModel.getElements());
        ensureCorrectTemplatesPosition();
    }

    public void ensureCorrectTemplatesPosition() {
        int templateCount = templates.getTemplates().size();
        int templatePosition = 1_000_001;
        for (int index = 0; index < templateCount; index++) {
            templates.getTemplates().get(index).setPosition(templatePosition);
            templatePosition++;
        }
    }

    private void createTemplate() {
        SubdirectoryTemplate template = new SubdirectoryTemplate();
        EditSubdirectoryTemplateController ctrl = new EditSubdirectoryTemplateController(template);
        ctrl.setParentComponent(view);
        ctrl.setDialogTitle(Bundle.getString(EditSubdirectoryTemplatesController.class, "EditSubdirectoryTemplatesController.CreateTemplate.DialogTitle"));
        ctrl.setOkButtonText(Bundle.getString(EditSubdirectoryTemplatesController.class, "EditSubdirectoryTemplatesController.CreateTemplate.OkButtonText"));
        if (ctrl.execute()) {
            templatesListModel.addToElements(Arrays.asList(template));
        }
    }

    private void deleteSelectedTemplates() {
        List<SubdirectoryTemplate> selectedTemplates = view.getListTemplates().getSelectedValuesList();

        if (selectedTemplates.isEmpty()) {
            return;
        }

        if (!MessageDisplayer.confirmYesNo(view, Bundle.getString(EditSubdirectoryTemplatesController.class, "EditSubdirectoryTemplatesController.RemoveSelectedTemplates.Confirm", selectedTemplates.size()))) {
            return;
        }

        templatesListModel.removeFromElements(selectedTemplates);
    }

    private void editSelectedTemplates() {
        for (SubdirectoryTemplate template : view.getListTemplates().getSelectedValuesList()) {
            editTemplate(template);
        }
    }

    private void editTemplate(SubdirectoryTemplate template) {
        EditSubdirectoryTemplateController ctrl = new EditSubdirectoryTemplateController(template);
        ctrl.setParentComponent(view);
        if (ctrl.execute()) {
            int index = templatesListModel.indexOf(template);
            templatesListModel.fireChanged(index, index);
        }
    }

    private final class TemplatesListDoubleClickListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (MouseEventUtil.isDoubleClick(e)) {
                editSelectedTemplates();
            }
        }
    }

    private final class TemplatesListEnterListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                editSelectedTemplates();
            }
        }
    }

    private final class TemplatesListDeleteListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                deleteSelectedTemplates();
            }
        }
    }

    private final class CreateTemplateAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private CreateTemplateAction() {
            super(Bundle.getString(CreateTemplateAction.class, "EditSubdirectoryTemplatesController.CreateTemplateAction.Name"));
            putValue(Action.SMALL_ICON, Icons.getIcon("icon_add.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            createTemplate();
        }
    }

    private abstract class EnabledOnSelectionAction extends AbstractAction implements ListSelectionListener {

        private static final long serialVersionUID = 1L;

        protected EnabledOnSelectionAction(String name) {
            super(name);
            setEnabled();
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                setEnabled();
            }
        }

        private void setEnabled() {
            setEnabled(!view.getListTemplates().isSelectionEmpty());
        }
    }

    private final class EditTemplatesAction extends EnabledOnSelectionAction {

        private static final long serialVersionUID = 1L;

        private EditTemplatesAction() {
            super(Bundle.getString(DeleteTemplatesAction.class, "EditSubdirectoryTemplatesController.EditTemplatesAction.Name"));
            putValue(Action.SMALL_ICON, Icons.getIcon("icon_edit.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            editSelectedTemplates();
        }
    }

    private final class DeleteTemplatesAction extends EnabledOnSelectionAction {

        private static final long serialVersionUID = 1L;

        private DeleteTemplatesAction() {
            super(Bundle.getString(DeleteTemplatesAction.class, "EditSubdirectoryTemplatesController.DeleteTemplatesAction.Name"));
            putValue(Action.SMALL_ICON, Icons.getIcon("icon_delete.png"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            deleteSelectedTemplates();
        }
    }

    private final class MoveUpAction extends AbstractAction implements ListSelectionListener {

        private static final long serialVersionUID = 1L;

        private MoveUpAction() {
            super(Bundle.getString(MoveUpAction.class, "EditSubdirectoryTemplatesController.MoveUpAction.Name"));
            putValue(Action.SMALL_ICON, Icons.getIcon("icon_arrow_up_gray.png"));
            setEnabled();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SubdirectoryTemplate selTemplate = view.getListTemplates().getSelectedValue();
            int modelSelIndex = templatesListModel.indexOf(selTemplate);
            if (modelSelIndex > 0) {
                templatesListModel.swap(modelSelIndex, modelSelIndex - 1);
                view.getListTemplates().setSelectedValue(selTemplate, true);
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                setEnabled();
            }
        }

        private void setEnabled() {
            setEnabled(!firstTemplateSelected() && exactlyOneTemplateSelected());
        }
    }

    private boolean exactlyOneTemplateSelected() {
        return view.getListTemplates().getSelectedIndices().length == 1;
    }

    private boolean firstTemplateSelected() {
        return view.getListTemplates().getSelectedIndex() == 0;
    }

    private boolean lastTemplateSelected() {
        return view.getListTemplates().getSelectedIndex() == templatesListModel.getSize() - 1;
    }

    private final class MoveDownAction extends AbstractAction implements ListSelectionListener {

        private static final long serialVersionUID = 1L;

        private MoveDownAction() {
            super(Bundle.getString(MoveDownAction.class, "EditSubdirectoryTemplatesController.MoveDownAction.Name"));
            putValue(Action.SMALL_ICON, Icons.getIcon("icon_arrow_down_gray.png"));
            setEnabled();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SubdirectoryTemplate selTemplate = view.getListTemplates().getSelectedValue();
            int modelSelIndex = templatesListModel.indexOf(selTemplate);
            if (modelSelIndex >= 0 && modelSelIndex < templatesListModel.size() - 1) {
                templatesListModel.swap(modelSelIndex, modelSelIndex + 1);
                view.getListTemplates().setSelectedValue(selTemplate, true);
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                setEnabled();
            }
        }

        private void setEnabled() {
            setEnabled(!lastTemplateSelected() && exactlyOneTemplateSelected());
        }
    }
}
