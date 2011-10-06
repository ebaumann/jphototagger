package org.jphototagger.program.module.search;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.jphototagger.program.module.Controller;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.SavedSearchesPopupMenu;

/**
 *
 * @author Elmar Baumann
 */
public final class CreateSavedSearchController extends Controller {

    public CreateSavedSearchController() {
        listenToActionsOf(SavedSearchesPopupMenu.INSTANCE.getItemCreate());
        listenToKeyEventsOf(GUI.getSavedSearchesList());
    }

    public void displayEmptySearchDialog() {
        AdvancedSearchDialog.INSTANCE.getPanel().empty();
        ControllerFactory.INSTANCE.getController(ShowAdvancedSearchDialogController.class).showDialog();
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getKeyCode() == KeyEvent.VK_N;
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == SavedSearchesPopupMenu.INSTANCE.getItemCreate();
    }

    @Override
    protected void action(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        displayEmptySearchDialog();
    }

    @Override
    protected void action(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        displayEmptySearchDialog();
    }
}
