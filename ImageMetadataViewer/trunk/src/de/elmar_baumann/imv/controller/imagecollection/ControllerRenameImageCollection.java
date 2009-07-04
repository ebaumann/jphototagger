package de.elmar_baumann.imv.controller.imagecollection;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.model.ListModelImageCollections;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.imv.tasks.ImageCollectionDatabaseUtils;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.view.panels.AppPanel;
import de.elmar_baumann.imv.view.popupmenus.PopupMenuImageCollections;
import de.elmar_baumann.lib.event.util.KeyEventUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 * Renames the selected image collection when the
 * {@link de.elmar_baumann.imv.view.popupmenus.PopupMenuImageCollections} fires.
 *
 * Also listenes to the {@link JTree}'s key events and renames the selected
 * image collection when the keys <code>Ctrl+R</code> or <code>F2</code> were
 * pressed.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/00/10
 */
public final class ControllerRenameImageCollection
        implements ActionListener, KeyListener {

    private final PopupMenuImageCollections popupMenu =
            PopupMenuImageCollections.INSTANCE;
    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final JList list = appPanel.getListImageCollections();
    private final ListModelImageCollections model =
            (ListModelImageCollections) list.getModel();

    public ControllerRenameImageCollection() {
        listen();
    }

    private void listen() {
        popupMenu.getItemRename().addActionListener(this);
        list.addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (isRename(e) && !list.isSelectionEmpty()) {
            Object value = list.getSelectedValue();
            if (value instanceof String) {
                renameImageCollection((String) value);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        renameImageCollection(popupMenu.getImageCollectionName());
    }

    private boolean isRename(KeyEvent e) {
        return KeyEventUtil.isControl(e, KeyEvent.VK_R) ||
                e.getKeyCode() == KeyEvent.VK_F2;
    }

    private void renameImageCollection(final String oldName) {
        if (oldName != null) {
            final String newName = ImageCollectionDatabaseUtils.
                    renameImageCollection(oldName);
            if (newName != null) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        model.rename(oldName, newName);
                    }
                });
            }
        } else {
            AppLog.logWarning(ControllerRenameImageCollection.class, Bundle.
                    getString(
                    "ControllerRenameImageCollection.ErrorMessage.NameIsNull"));
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ignore
    }
}
