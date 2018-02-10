package org.jphototagger.program.help;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
final class SendIssueMailAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    SendIssueMailAction() {
        super(Bundle.getString(SendIssueMailAction.class, "SendIssueMailAction.Name"));
        putValue(SMALL_ICON, Icons.getIcon("icon_mail.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sentMail();
    }

    private void sentMail() {
        SendMail.sendMail(AppInfo.MAIL_TO_ADDRESS_FEATURES, AppInfo.MAIL_SUBJECT_FEATURES, null);
    }
}
