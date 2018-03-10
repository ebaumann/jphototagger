package org.jphototagger.maintainance.browse;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableStringConverter;
import javax.swing.text.Document;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.storage.PreferencesDirectoryProvider;
import org.jphototagger.domain.repository.browse.ResultSetBrowser;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.DocumentChangeListener;
import org.jphototagger.lib.swing.InputDialog;
import org.jphototagger.lib.swing.InputDialog2;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.TableTextFilter;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.ComponentUtil.DisposeWindowAction;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.StringUtil;
import org.jphototagger.lib.xml.bind.XmlObjectExporter;
import org.jphototagger.lib.xml.bind.XmlObjectImporter;
import org.openide.util.Lookup;

/**
 * Displays a view for a SQL query, which result is displayed in a table.
 *
 * @author Elmar Baumann
 */
public final class ResultSetBrowserController {

    private static final String KEY_SQL = "ResultSetBrowserController.Sql";
    private static final String KEY_DLG = "ResultSetBrowserController.Dlg";
    private static final String SQL_COMMANDS_FILENAME = "ResultSetBrowserSqlCommands.xml";
    private boolean isLoad;
    private ResultSetBrowserPanel view;
    private ExcecuteSqlAction excecuteSqlAction;
    private LoadSqlAction loadSqlAction;
    private SaveSqlAction saveSqlAction;
    private DisposeWindowAction disposeWindowAction;
    private TableTextFilter tableTextFilter; // Holds a reference to get not garbage collected

    public ResultSetBrowserController() {
        initView();
    }

    private void initView() {
        if (view == null) {
            view = new ResultSetBrowserPanel();
            excecuteSqlAction = new ExcecuteSqlAction();
            loadSqlAction = new LoadSqlAction();
            saveSqlAction = new SaveSqlAction();
            SqlListener sqlListener = new SqlListener();

            view.getButtonExecuteSql().setAction(excecuteSqlAction);
            view.getButtonLoad().setAction(loadSqlAction);
            view.getButtonSave().setAction(saveSqlAction);
            view.getTextAreaSql().getDocument().addDocumentListener(sqlListener);

            JTable table = view.getTable();
            Document document = view.getTextFieldFilter().getDocument();
            TableRowSorter<?> rowSorter = (TableRowSorter<?>) table.getRowSorter();
            TableStringConverterImpl stringConverter = new TableStringConverterImpl();
            tableTextFilter = new TableTextFilter(table, stringConverter);
            rowSorter.setStringConverter(stringConverter);
            document.addDocumentListener(tableTextFilter);

            MnemonicUtil.setMnemonics(view);
            sqlListener.changedUpdate(null);
        }
    }

    /**
     * Displays the view.
     */
    public void execute() {
        Frame parent = ComponentUtil.findFrameWithIcon();
        InputDialog2 dlg = new InputDialog2(parent, true);

        disposeWindowAction = new DisposeWindowAction(dlg);
        ComponentUtil.registerForEscape(dlg, disposeWindowAction);

        dlg.setTitle(Bundle.getString(ResultSetBrowserController.class, "ResultSetBrowserController.Dlg.Title"));
        dlg.setShowCancelButton(false);
        dlg.setOkButtonText(Bundle.getString(ResultSetBrowserController.class, "ResultSetBrowserController.Dlg.OkButtonText"));
        dlg.setComponent(view);
        dlg.pack();
        dlg.setLocationRelativeTo(parent);

        restoreDlg(dlg);
        restoreSql();

        dlg.setVisible(true);

        persistSql();
        persistDlg(dlg);
    }

    private void persistSql() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setString(KEY_SQL, view.getTextAreaSql().getText().trim());
    }

    private void restoreSql() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        String sql = prefs.getString(KEY_SQL);


        view.getTextAreaSql().setText(StringUtil.hasContent(sql)
                ? sql
                : "SELECT * FROM FILES");
    }

    private void persistDlg(Component dlg) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.setSize(KEY_DLG, dlg);
        prefs.setLocation(KEY_DLG, dlg);
    }

    private void restoreDlg(Component dlg) {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        prefs.applySize(KEY_DLG, dlg);
        prefs.applyLocation(KEY_DLG, dlg);
    }

    private final class SqlListener extends DocumentChangeListener {
        @Override
        public void documentChanged(DocumentEvent ignored) {
            boolean sqlDefined = !view.getTextAreaSql().getText().trim().isEmpty();

            excecuteSqlAction.setEnabled(sqlDefined);
            saveSqlAction.setEnabled(sqlDefined);

            if (!isLoad) {
                JLabel labelDescription = view.getLabelDescription();
                String description = labelDescription.getText();
                String changedPrefix = "(*) ";

                if (StringUtil.hasContent(description) && !description.startsWith(changedPrefix)) {
                    labelDescription.setText(changedPrefix + description);
                }
            }
        }
    };

    private final class ExcecuteSqlAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private ExcecuteSqlAction() {
            super(Bundle.getString(ExcecuteSqlAction.class, "ExcecuteSqlAction.Name"));
            enabled = false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            DefaultTableModel tableModel = new DefaultTableModel();
            ResultSetTableModelBrowser browser = new ResultSetTableModelBrowser(tableModel);
            String sql = view.getTextAreaSql().getText().trim();
            Browser swingBrowser = new Browser(sql, browser);

            view.getTextFieldFilter().setText("");
            view.getTable().setModel(tableModel);
            view.getProgressBar().setVisible(true);
            disposeWindowAction.setEnabled(false);

            swingBrowser.execute();
        }
    }

    private final class Browser extends SwingWorkerResultSetBrowser {

        public Browser(String sql, ResultSetBrowser browser) {
            super(sql, browser);
        }

        @Override
        protected void done() {
            view.getTextFieldFilter().requestFocusInWindow();
            disposeWindowAction.setEnabled(true);
        }
    }

    private final class SaveSqlAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private SaveSqlAction() {
            super(Bundle.getString(SaveSqlAction.class, "SaveSqlAction.Name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            InputDialog dlg = new InputDialog();

            dlg.setLocationRelativeTo(view);
            dlg.setTitle(Bundle.getString(SaveSqlAction.class, "SaveSqlAction.Title"));
            dlg.setInfo(Bundle.getString(SaveSqlAction.class, "SaveSqlAction.Prompt"));

            dlg.setVisible(true);
            if (dlg.isAccepted()) {
                String input = dlg.getInput();
                if (StringUtil.hasContent(input)) {
                    SqlCommand command = new SqlCommand();

                    command.setDescription(input.trim());
                    command.setSql(view.getTextAreaSql().getText().trim());

                    try {
                        SqlCommands commands = loadOrCreateSqlCommands();

                        commands.getSqlCommands().add(command);
                        XmlObjectExporter.export(commands, getXmlFile());
                    } catch (Throwable t) {
                        Logger.getLogger(SaveSqlAction.class.getName()).log(Level.SEVERE, null, t);
                    }
                }
            }
        }
    }

    private static SqlCommands loadOrCreateSqlCommands() {
        File xmlFile = getXmlFile();
        try {
            return FileUtil.existsFile(xmlFile)
                    ? (SqlCommands) XmlObjectImporter.importObject(xmlFile, SqlCommands.class)
                    : new SqlCommands();
        } catch (Throwable t) {
            Logger.getLogger(ResultSetBrowserController.class.getName()).log(Level.SEVERE, null, t);
            return new SqlCommands();
        }
    }

    private static File getXmlFile() {
        PreferencesDirectoryProvider p = Lookup.getDefault().lookup(PreferencesDirectoryProvider.class);
        File prefDir = p.getPluginPreferencesDirectory();

        return new File(prefDir, SQL_COMMANDS_FILENAME);
    }

    private final class LoadSqlAction extends AbstractAction {

        private static final long serialVersionUID = 1L;

        private LoadSqlAction() {
            super(Bundle.getString(LoadSqlAction.class, "LoadSqlAction.Name"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                SqlCommandsDialog dlg = new SqlCommandsDialog(ComponentUtil.findFrameWithIcon(), true);

                dlg.setLocationRelativeTo(view);
                SqlCommands commands = loadOrCreateSqlCommands();
                dlg.setSqlCommands(commands.getSqlCommands());

                dlg.setVisible(true);
                if (dlg.isAccepted()) {
                    SqlCommand command = dlg.getSelectedCommand();

                    if (command != null) {
                        isLoad = true;
                        view.getLabelDescription().setText(command.getDescription());
                        view.getTextAreaSql().setText(command.getSql());
                        isLoad = false;
                    }

                    if (dlg.isEdit()) {
                        commands.getSqlCommands().clear();
                        commands.getSqlCommands().addAll(dlg.getSqlCommands());
                        XmlObjectExporter.export(commands, getXmlFile());
                    }
                }
            } catch (Throwable t) {
                Logger.getLogger(LoadSqlAction.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    private static class TableStringConverterImpl extends TableStringConverter {

        @Override
        public String toString(TableModel model, int row, int column) {
            Object value = model.getValueAt(row, column);
            return StringUtil.toStringNullToEmptyString(value);
        }
    }

    private final class ResultSetTableModelBrowser extends AbstractTableModelResultSetBrowser {

        private ResultSetTableModelBrowser(DefaultTableModel tableModel) {
            super(tableModel);
        }

        @Override
        public void finished(Result result) {
            view.getProgressBar().setVisible(false);

            Frame f = ComponentUtil.findFrameWithIcon();
            switch (result.getValue()) {
                case NOT_SUPPORTED:
                    MessageDisplayer.information(f, Bundle.getString(ResultSetTableModelBrowser.class, "ResultSetTableModelBrowser.Result.NotSupported"));
                    break;
                case THRWON:
                    MessageDisplayer.thrown(Bundle.getString(ResultSetTableModelBrowser.class, "ResultSetTableModelBrowser.Result.Thrown"), result.getThrown());
                    break;
                default: // Nothing
            }
        }
    }
}
