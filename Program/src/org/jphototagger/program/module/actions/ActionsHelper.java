package org.jphototagger.program.module.actions;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.openide.util.Lookup;

import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.module.programs.StartPrograms;
import org.jphototagger.program.module.actions.AddProgramAction;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.module.thumbnails.ThumbnailsPanel;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ActionsHelper {

    private static final JMenuItem ADD_ACTION_MENU_ITEM = new JMenuItem(new AddProgramAction());

    public static JMenu actionsAsMenu() {
        JMenu menu = new JMenu(Bundle.getString(ActionsHelper.class, "ActionsHelper.ActionMenu.DisplayName"));
        ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

        if (repo != null) {
            List<Program> actions = repo.findAllPrograms(ProgramType.ACTION);

            for (Program action : actions) {
                menu.add(new JMenuItem(new ActionStarter(action)));
            }
        }

        menu.add(ADD_ACTION_MENU_ITEM);

        return menu;
    }

    private static void reorderActions(JMenu actionsMenu) {
        actionsMenu.removeAll();
        ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

        List<Program> actions = repo.findAllPrograms(ProgramType.ACTION);

        for (Program action : actions) {
            actionsMenu.add(new JMenuItem(new ActionStarter(action)));
        }

        actionsMenu.add(ADD_ACTION_MENU_ITEM);
    }

    public static boolean existsAction(JMenu actionsMenu, Program action) {
        if (actionsMenu == null) {
            throw new NullPointerException("actionsMenu == null");
        }

        if (action == null) {
            throw new NullPointerException("action == null");
        }

        return getIndexOfAction(actionsMenu, action) >= 0;
    }

    public static void addAction(JMenu actionsMenu, Program action) {
        if (actionsMenu == null) {
            throw new NullPointerException("actionsMenu == null");
        }

        if (action == null) {
            throw new NullPointerException("action == null");
        }

        actionsMenu.remove(ADD_ACTION_MENU_ITEM);
        actionsMenu.add(new ActionStarter(action));
        actionsMenu.add(ADD_ACTION_MENU_ITEM);
    }

    public static void removeAction(JMenu actionsMenu, Program action) {
        if (actionsMenu == null) {
            throw new NullPointerException("actionsMenu == null");
        }

        if (action == null) {
            throw new NullPointerException("action == null");
        }

        int index = getIndexOfAction(actionsMenu, action);

        if (index >= 0) {
            actionsMenu.remove(index);
        }
    }

    public static void updateAction(JMenu actionsMenu, Program action) {
        if (actionsMenu == null) {
            throw new NullPointerException("actionsMenu == null");
        }

        if (action == null) {
            throw new NullPointerException("action == null");
        }

        int index = getIndexOfAction(actionsMenu, action);
        int seqNr = action.getSequenceNumber();

        if ((index >= 0) && (index == seqNr)) {
            Action a = actionsMenu.getItem(index).getAction();

            if (a instanceof ActionStarter) {
                Program actionProgram = ((ActionStarter) a).getAction();

                actionProgram.set(action);
                setNameAndIcon(a, actionProgram);
            }
        } else if ((index >= 0) && (index != seqNr)) {
            reorderActions(actionsMenu);
        }
    }

    private static void setNameAndIcon(Action action, Program ap) {
        action.putValue(Action.NAME, ap.getAlias());

        if (ap.getFile().exists()) {
            action.putValue(Action.SMALL_ICON, IconUtil.getSystemIcon(ap.getFile()));
        }
    }

    private static int getIndexOfAction(JMenu actionsMenu, Program action) {
        int itemCount = actionsMenu.getItemCount();

        for (int i = 0; i < itemCount; i++) {
            Action a = actionsMenu.getItem(i).getAction();

            if (a instanceof ActionStarter) {
                Program actionProgram = ((ActionStarter) a).getAction();

                if (actionProgram.equals(action)) {
                    return i;
                }
            }
        }

        return -1;
    }

    private static class ActionStarter extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final transient Program action;

        ActionStarter(Program action) {
            this.action = action;
            setNameAndIcon(this, action);
        }

        public Program getAction() {
            return action;
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            ThumbnailsPanel tnPanel = GUI.getThumbnailsPanel();

            if (!tnPanel.isAFileSelected()) {
                return;
            }

            StartPrograms starter = new StartPrograms();

            starter.startProgram(action, tnPanel.getSelectedFiles(), true);
        }
    }

    private ActionsHelper() {
    }
}
