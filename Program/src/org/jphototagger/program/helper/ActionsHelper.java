package org.jphototagger.program.helper;

import org.jphototagger.lib.image.util.IconUtil;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.ProgressBar;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.awt.event.ActionEvent;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.jphototagger.program.controller.actions.AddProgramAction;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ActionsHelper {

    private static final JMenuItem ADD_ACTION_MENU_ITEM = new JMenuItem(new AddProgramAction());

    public static JMenu actionsAsMenu() {
        List<Program> actions = DatabasePrograms.INSTANCE.getAll(DatabasePrograms.Type.ACTION);
        JMenu menu = new JMenu(JptBundle.INSTANCE.getString("ActionsHelper.ActionMenu.DisplayName"));

        for (Program action : actions) {
            menu.add(new JMenuItem(new ActionStarter(action, action)));
        }

        menu.add(ADD_ACTION_MENU_ITEM);

        return menu;
    }

    private static void reorderActions(JMenu actionsMenu) {
        actionsMenu.removeAll();

        List<Program> actions = DatabasePrograms.INSTANCE.getAll(DatabasePrograms.Type.ACTION);

        for (Program action : actions) {
            actionsMenu.add(new JMenuItem(new ActionStarter(action, action)));
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
        actionsMenu.add(new ActionStarter(action, action));
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
        private final Object pBarOwner;

        ActionStarter(Program action, Object progressBarOwner) {
            this.action = action;
            this.pBarOwner = progressBarOwner;
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

            StartPrograms starter = new StartPrograms(ProgressBar.INSTANCE.getResource(pBarOwner));

            starter.startProgram(action, tnPanel.getSelectedFiles(), true);
            ProgressBar.INSTANCE.releaseResource(pBarOwner);
        }
    }


    private ActionsHelper() {}
}
