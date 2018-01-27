package org.jphototagger.program.help;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.ui.AppLookAndFeel;

/**
 * @author Elmar Baumann
 */
final class BrowseWebsiteAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    BrowseWebsiteAction() {
        super(Bundle.getString(BrowseWebsiteAction.class, "BrowseWebsiteAction.Name"));
        putValue(SMALL_ICON, AppLookAndFeel.getIcon("icon_web.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String prefrencesKeyForAlternateBrowser = "JPhotoTagger";
        DesktopUtil.browse(AppInfo.URI_WEBSITE, prefrencesKeyForAlternateBrowser);
    }
}
