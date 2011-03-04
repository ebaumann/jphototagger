package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

import javax.swing.JMenuItem;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerAddKeywordsToEditPanel extends ControllerKeywords {
    public ControllerAddKeywordsToEditPanel() {
        listenToActionsOf(getMenuItem());
    }

    private JMenuItem getMenuItem() {
        return PopupMenuKeywordsList.INSTANCE.getItemAddToEditPanel();
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        EditMetadataPanels editPanels = GUI.getAppPanel().getEditMetadataPanels();

        if (editPanels.isEditable()) {
            for (String keyword : keywords) {
                editPanels.addText(ColumnXmpDcSubjectsSubject.INSTANCE, keyword);
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
