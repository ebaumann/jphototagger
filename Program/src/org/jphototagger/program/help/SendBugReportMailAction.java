package org.jphototagger.program.help;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.logging.AppLoggingSystem;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
final class SendBugReportMailAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    SendBugReportMailAction() {
        super(Bundle.getString(SendBugReportMailAction.class, "SendBugReportMailAction.Name"));
        putValue(SMALL_ICON, Icons.getIcon("icon_mail.png"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        sentMail();
    }

    private void sentMail() {
        String mailto = AppInfo.MAIL_TO_ADDRESS_BUGS;
        String subject = AppInfo.MAIL_SUBJECT_BUGS;
        String allMessagesLogfilePath = AppLoggingSystem.getAllMessagesLogfilePath();
        String errorMessagesLogfilePath = AppLoggingSystem.getErrorMessagesLogfilePath();
        String message = Bundle.getString(ShowHelpAction.class, "SendBugReportMailAction.Info.AttachLogfile",
                allMessagesLogfilePath, errorMessagesLogfilePath);

        openLogfileDirectory();
        SendMail.sendMail(mailto, subject, message);
    }

    private void openLogfileDirectory() {
        String message = Bundle.getString(SendBugReportMailAction.class, "SendBugReportMailAction.Confirm.OpenLogfileDir");
        if (MessageDisplayer.confirmYesNo(null, message)) {
            DesktopUtil.open(AppLoggingSystem.getLofileDirectory(), "SendBugReportMailAction.OpenLogfileDirectory");
        }
    }
}
