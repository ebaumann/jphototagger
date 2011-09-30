package org.jphototagger.program.controller.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JMenuItem;

import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.popupmenus.KeywordsListPopupMenu;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class AddKeywordsToEditPanelController extends KeywordsController {

    public AddKeywordsToEditPanelController() {
        listenToActionsOf(getMenuItem());
    }

    private JMenuItem getMenuItem() {
        return KeywordsListPopupMenu.INSTANCE.getItemAddToEditPanel();
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        EditMetadataPanels editPanels = GUI.getAppPanel().getEditMetadataPanels();

        if (editPanels.isEditable()) {
            for (String keyword : keywords) {
                editPanels.setOrAddText(XmpDcSubjectsSubjectMetaDataValue.INSTANCE, keyword);
            }
        }
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_B);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == getMenuItem();
    }
}
