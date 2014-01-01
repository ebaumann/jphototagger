package org.jphototagger.program.module.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.JMenuItem;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.program.module.keywords.KeywordsUtil;

/**
 * @author Elmar Baumann
 */
public final class InsertKeywordsController extends KeywordsListController {

    private final JMenuItem itemInsert = KeywordsListPopupMenu.INSTANCE.getItemInsert();

    public InsertKeywordsController() {
        listenToActionsOf(itemInsert);
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        KeywordsUtil.insertDcSubject();
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
