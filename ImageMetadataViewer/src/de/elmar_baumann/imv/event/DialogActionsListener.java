package de.elmar_baumann.imv.event;

/**
 * Listens for actions in  
 * {@link de.elmar_baumann.imv.view.dialogs.ActionsDialog}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/06
 */
public interface DialogActionsListener {

    public void actionPerformed(DialogActionsEvent evt);
}
