package org.jphototagger.program.controller.keywords.list;

import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;
import org.jphototagger.lib.event.util.KeyEventUtil;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

import javax.swing.JMenuItem;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerInsertKeywords extends ControllerKeywords {
    private final JMenuItem itemInsert =
        PopupMenuKeywordsList.INSTANCE.getItemInsert();

    public ControllerInsertKeywords() {
        listenToActionsOf(itemInsert);
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        KeywordsHelper.insertDcSubject();
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcut(evt, KeyEvent.VK_N);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == itemInsert;
    }
}
