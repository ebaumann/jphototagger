package org.jphototagger.program.module.keywords;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.windows.MainWindowMenuProvider;
import org.jphototagger.api.windows.MenuItemProvider;
import org.jphototagger.domain.metadata.SelectedFilesMetaDataEditor;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.lib.api.MainWindowMenuProviderAdapter;
import org.jphototagger.lib.api.MenuItemProviderImpl;
import org.jphototagger.lib.swing.InputDialog2;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.keywords.AddKeywortsViaShortcutsModel.KeywordNumber;
import org.jphototagger.program.resource.GUI;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Controller for adding Keywords via Shortcuts.
 *
 * @author Elmar Baumann
 */
public final class AddKeywordsViaShortcutsController {

    private final AddKeywortsViaShortcutsModel model = new AddKeywortsViaShortcutsModel();

    public AddKeywordsViaShortcutsController() {
        init();
    }

    private void init() {
        InputMap inputMap = GUI.getThumbnailsPanel().getInputMap();
        ActionMap actionMap = GUI.getThumbnailsPanel().getActionMap();
        for (KeywordNumber keywordNumber : KeywordNumber.values()) {
            inputMap.put(keywordNumber.getKeyStroke(), keywordNumber.getActionMapKey());
            actionMap.put(keywordNumber.getActionMapKey(), new AddKeywordsAction(keywordNumber));
        }
    }

    private final class AddKeywordsAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final KeywordNumber keywordNumber;

        private AddKeywordsAction(KeywordNumber number) {
            this.keywordNumber = number;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SelectedFilesMetaDataEditor editor = Lookup.getDefault().lookup(SelectedFilesMetaDataEditor.class);
            for (String keyword : model.load(keywordNumber)) {
                editor.setOrAddText(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, keyword);
            }
        }
    }

    @ServiceProvider(service = MainWindowMenuProvider.class)
    public static final class EditGuiActionProvider extends MainWindowMenuProviderAdapter {
        @Override
        public Collection<? extends MenuItemProvider> getWindowMenuItems() {
            return Arrays.asList(new MenuItemProviderImpl(new EditGuiAction(), 260, false));
        }
    }

    private static final class EditGuiAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private EditGuiAction() {
            super(Bundle.getString(EditGuiAction.class, "AddKeywordsViaShortcutsController.EditGuiAction.Name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AddKeywordsViaShortcutsController ctrl = new AddKeywordsViaShortcutsController();
            ctrl.showEditGui();
        }
    }

    public void showEditGui() {
        Frame frame = ComponentUtil.findFrameWithIcon();
        InputDialog2 dlg = new InputDialog2(frame, true);
        TableModelImpl tm = new TableModelImpl();
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        AddKeywortsViaShortcutsPanel panel = new AddKeywortsViaShortcutsPanel();
        final String persistenceKey = "AddKeywordsViaShortcutsController.InputDlg";

        tm.load();
        panel.getLabelInfo().setText(Bundle.getString(AddKeywordsViaShortcutsController.class, "AddKeywordsViaShortcutsController.LabelInfo.Text", AddKeywortsViaShortcutsModel.DELIMITER));
        panel.getTable().setModel(tm);
        panel.getTable().setDefaultRenderer(KeywordNumber.class, new KeywordNumberRenderer());
        panel.getTable().setCellEditor(new DefaultCellEditor(new JTextField()));
        dlg.setTitle(Bundle.getString(AddKeywordsViaShortcutsController.class, "AddKeywordsViaShortcutsController.InputDlg.Title"));
        dlg.setComponent(panel);
        dlg.setLocationRelativeTo(frame);
        dlg.pack();
        prefs.applySize(persistenceKey, dlg);
        prefs.applyLocation(persistenceKey, dlg);
        ComponentUtil.registerForSetInvisibleOnEscape(dlg);

        dlg.setVisible(true);
        prefs.setSize(persistenceKey, dlg);
        prefs.setLocation(persistenceKey, dlg);

        if (dlg.isAccepted()) {
            tm.save();
        }
    }

    private static final class KeywordNumberRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //To change body of generated methods, choose Tools | Templates.

            if (value instanceof KeywordNumber) {
                KeywordNumber keywordNumber = (KeywordNumber) value;
                label.setText(keywordNumber.getDisplayName());
            }

            return label;
        }
    }

    private final class TableModelImpl extends DefaultTableModel {

        private static final long serialVersionUID = 1L;
        private static final int COLUMN_KEYWORD_NUMBER = 0;
        private static final int COLUMN_KEYWORDS = 1;

        private final String[] columnNames = new String[]{
            Bundle.getString(AddKeywordsViaShortcutsController.class, "AddKeywordsViaShortcutsController.TableModelImpl.ColumnName.KeyStroke"),
            Bundle.getString(AddKeywordsViaShortcutsController.class, "AddKeywordsViaShortcutsController.TableModelImpl.ColumnName.Keywords"),
        };

        private void load() {
            for (KeywordNumber keywordNumber : KeywordNumber.values()) {
                addRow(new Object[]{keywordNumber, model.loadSingleString(keywordNumber)});
            }
        }

        private void save() {
            int rowCount = getRowCount();
            for (int row = 0; row < rowCount; row++) {
                KeywordNumber keywordNumber = (KeywordNumber) getValueAt(row, COLUMN_KEYWORD_NUMBER);
                String keywords = (String) getValueAt(row, COLUMN_KEYWORDS);
                model.save(keywordNumber, keywords);
            }
        }

        private final Class<?>[] columnClasses = new Class<?>[]{
            KeywordNumber.class,
            String.class
        };

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnClasses[columnIndex];
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public int getColumnCount() {
            return columnClasses.length;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == COLUMN_KEYWORDS;
        }
    }
}
