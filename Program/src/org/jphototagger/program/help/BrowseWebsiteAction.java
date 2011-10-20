package org.jphototagger.program.help;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.jphototagger.lib.awt.DesktopUtil;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppInfo;

/**
 * @author Elmar Baumann
 */
final class BrowseWebsiteAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    BrowseWebsiteAction() {
        super(Bundle.getString(BrowseWebsiteAction.class, "BrowseWebsiteAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(BrowseWebsiteAction.class, "www.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        String prefrencesKeyForAlternateBrowser = "JPhotoTagger";
        DesktopUtil.browse(AppInfo.URI_WEBSITE, prefrencesKeyForAlternateBrowser);
    }
}
