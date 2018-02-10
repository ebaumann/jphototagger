package org.jphototagger.program.help;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
final class BrowseUserForumAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    BrowseUserForumAction() {
        super(Bundle.getString(BrowseUserForumAction.class, "BrowseUserForumAction.Name"));
        putValue(SMALL_ICON, Icons.getIcon("icon_web.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String prefrencesKeyForAlternateBrowser = "JPhotoTagger";
        DesktopUtil.browse(AppInfo.URI_USER_FORUM, prefrencesKeyForAlternateBrowser);
    }
}
