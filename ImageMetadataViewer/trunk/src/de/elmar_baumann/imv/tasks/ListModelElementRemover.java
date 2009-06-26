package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.database.DatabaseStatistics;
import de.elmar_baumann.imv.database.metadata.Column;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

/**
 * Removes elements from list models when no corresponding value is an a
 * database column.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/05
 */
public final class ListModelElementRemover {

    private final DatabaseStatistics dbStatistics = DatabaseStatistics.INSTANCE;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final DefaultListModel model;
    private CheckForRemove removeChecker = new CheckForRemove();
    private Column column;
    private List<Column> columns;

    /**
     * Constructor.
     * 
     * @param model   model to remove elements from
     * @param column  database column to search for not existing values. All
     *                elements of the model will be queried for existence with
     *                their <code>toString()</code> method as value in
     *                ({@link de.elmar_baumann.imv.database.DatabaseStatistics#exists(de.elmar_baumann.imv.database.metadata.Column, java.lang.String)}).
     */
    public ListModelElementRemover(DefaultListModel model, Column column) {
        this.model = model;
        this.column = column;
        removeChecker = new CheckForRemove();
    }

    /**
     * Constructor.
     * 
     * @param model    model to remove elements from
     * @param columns  database columns to search for not existing values. All
     *                 elements of the model will be queried for existence with
     *                 their <code>toString()</code> method as value in
     *                 ({@link de.elmar_baumann.imv.database.DatabaseStatistics#exists(java.util.List, java.lang.String)}).
     */
    public ListModelElementRemover(DefaultListModel model, List<Column> columns) {
        this.model = model;
        this.columns = columns;
        removeChecker = new CheckForRemove();
    }

    /**
     * Checks for elements to remove. Starts a new thread and locks the model
     * only while retrieving it's elements. After retrieving the elements
     * the database will be queryied for exstance of these elements.
     */
    public void removeNotExistingElements() {
        executor.execute(removeChecker);
    }

    private class CheckForRemove implements Runnable {

        @Override
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    List<Object> existingElements = new ArrayList<Object>();
                    int size = model.getSize();
                    for (int i = 0; i < size; i++) {
                        existingElements.add(model.get(i));
                    }
                    for (Object element : existingElements) {
                        if (column != null && !dbStatistics.exists(column,
                                element.toString())) {
                            model.removeElement(element);
                        } else if (columns != null && !dbStatistics.exists(
                                columns, element.toString())) {
                            model.removeElement(element);
                        }
                    }
                }
            });
        }
    }
}
