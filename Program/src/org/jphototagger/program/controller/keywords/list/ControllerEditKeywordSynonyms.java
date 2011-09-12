package org.jphototagger.program.controller.keywords.list;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JPopupMenu.Separator;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.jdesktop.swingx.JXList;
import org.jphototagger.domain.metadata.xmp.XmpDcSubjectsSubjectMetaDataValue;
import org.jphototagger.domain.repository.SynonymsRepository;
import org.jphototagger.lib.dialog.InputDialog;
import org.jphototagger.lib.event.util.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.InputHelperDialog;
import org.jphototagger.program.view.panels.EditRepeatableTextEntryPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuKeywordsList;
import org.openide.util.Lookup;

/**
 *
 * @author Elmar Baumann
 */
public final class ControllerEditKeywordSynonyms extends ControllerKeywords implements PopupMenuListener {

    private static final String DELIM = ";";
    private static final String DISPLAYNAME = Bundle.getString(ControllerEditKeywordSynonyms.class, "ControllerEditKeywordSynonyms.MenuItemEditSynonyms.DisplayName");
    private final JMenuItem itemEditSynonyms = new JMenuItem(DISPLAYNAME);
    private final SynonymsRepository repo = Lookup.getDefault().lookup(SynonymsRepository.class);

    public ControllerEditKeywordSynonyms() {
        addMenuItem();
        listen();
    }

    private void listen() {
        listenToActionsOf(PopupMenuKeywordsList.INSTANCE.getItemEditSynonyms(), itemEditSynonyms);
        getKeywordsList().addKeyListener(this);
        getPopupMenu().addPopupMenuListener(this);
    }

    private EditRepeatableTextEntryPanel getKeywordsPanel() {
        return (EditRepeatableTextEntryPanel) GUI.getAppPanel().getEditMetadataPanels().getEditPanel(
                XmpDcSubjectsSubjectMetaDataValue.INSTANCE);
    }

    private JXList getKeywordsList() {
        return getKeywordsPanel().getList();
    }

    private JPopupMenu getPopupMenu() {
        return getKeywordsPanel().getPopupMenu();
    }

    private void addMenuItem() {
        itemEditSynonyms.setAccelerator(KeyEventUtil.getKeyStrokeMenuShortcut(KeyEvent.VK_S));

        JPopupMenu popupMenu = getPopupMenu();

        popupMenu.add(new Separator());
        popupMenu.add(itemEditSynonyms);
    }

    private void editInEditList() {
        List<String> keywords = new ArrayList<String>();

        for (Object selValue : getKeywordsList().getSelectedValues()) {
            keywords.add(selValue.toString());
        }

        if (!keywords.isEmpty()) {
            action(keywords);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == itemEditSynonyms) {
            editInEditList();
        } else {
            super.actionPerformed(evt);
        }
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if ((evt.getSource() == getKeywordsList()) && myKey(evt)) {
            editInEditList();
        } else {
            super.keyPressed(evt);
        }
    }

    private boolean itemsInEditListSelected() {
        return getKeywordsList().getSelectedValue() != null;
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent evt) {
        boolean selected = itemsInEditListSelected();
        EditRepeatableTextEntryPanel editPanel = getKeywordsPanel();

        editPanel.getItemRename().setEnabled(selected);
        editPanel.getItemRemove().setEnabled(selected);
        itemEditSynonyms.setEnabled(selected);
    }

    @Override
    protected boolean myKey(KeyEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return KeyEventUtil.isMenuShortcutWithAlt(evt, KeyEvent.VK_S);
    }

    @Override
    protected boolean myAction(ActionEvent evt) {
        if (evt == null) {
            throw new NullPointerException("evt == null");
        }

        return evt.getSource() == PopupMenuKeywordsList.INSTANCE.getItemEditSynonyms();
    }

    @Override
    protected void action(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        for (String keyword : keywords) {
            editSynonyms(keyword);
        }
    }

    private void editSynonyms(String keyword) {
        Set<String> oldSynonyms = repo.findSynonymsOfWord(keyword);
        InputHelperDialog owner = InputHelperDialog.INSTANCE;
        String info = Bundle.getString(ControllerEditKeywordSynonyms.class, "ControllerEditKeywordSynonyms.Info.Input", keyword, DELIM);
        String input = catSynonyms(oldSynonyms);
        InputDialog dlg = new InputDialog(owner, info, input);

        dlg.setVisible(true);

        String synonyms = dlg.getInput();

        if (dlg.isAccepted() && (synonyms != null)) {
            Set<String> newSynonyms = splitSynonyms(synonyms);

            for (String synonym : newSynonyms) {
                repo.saveSynonym(keyword, synonym);
            }

            SynonymsRepository synonymsRepo = Lookup.getDefault().lookup(SynonymsRepository.class);

            for (String synonym : oldSynonyms) {
                if (!newSynonyms.contains(synonym)) {
                    synonymsRepo.deleteSynonym(keyword, synonym);
                }
            }
        }
    }

    private Set<String> splitSynonyms(String synonymString) {
        Set<String> synonyms = new HashSet<String>();
        StringTokenizer st = new StringTokenizer(synonymString, DELIM);

        while (st.hasMoreTokens()) {
            String synonym = st.nextToken().trim();

            if (!synonym.isEmpty()) {
                synonyms.add(synonym);
            }
        }

        return synonyms;
    }

    private String catSynonyms(Set<String> synonyms) {
        StringBuilder sb = new StringBuilder();
        int i = 0;

        for (String synonym : synonyms) {
            sb.append((i++ == 0)
                    ? ""
                    : DELIM);
            sb.append(synonym);
        }

        return sb.toString();
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent evt) {
        // ignore
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent evt) {
        // ignore
    }
}
