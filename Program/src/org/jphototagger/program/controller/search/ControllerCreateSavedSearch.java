package org.jphototagger.program.controller.search;

import org.jphototagger.program.controller.Controller;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.AdvancedSearchDialog;
import org.jphototagger.program.view.popupmenus.PopupMenuSavedSearches;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerCreateSavedSearch extends Controller {
    public ControllerCreateSavedSearch() {
        listenToActionsOf(PopupMenuSavedSearches.INSTANCE.getItemCreate());
        listenToKeyEventsOf(GUI.getSavedSearchesList());
    }

    public void displayEmptySearchDialog() {
        AdvancedSearchDialog.INSTANCE.getPanel().empty();
        ControllerFactory.INSTANCE.getController(ControllerShowAdvancedSearchDialog.class).showDialog();
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

        return evt.getSource() == PopupMenuSavedSearches.INSTANCE.getItemCreate();
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
