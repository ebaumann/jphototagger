package org.jphototagger.program.help;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.ui.AppFrame;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.resources.Icons;

/**
 * @author Elmar Baumann
 */
public final class ShowAboutJPhotoTaggerAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public ShowAboutJPhotoTaggerAction() {
        super(Bundle.getString(ShowAboutJPhotoTaggerAction.class, "ShowAboutJPhotoTaggerAction.Name"));
        putValue(SMALL_ICON, Icons.getIcon("icon_app_about.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ImageIcon icon = Icons.getIcon("icon_logo.png");
        String key = "ShowAboutJPhotoTaggerAction.Info.About";
        String title = "JPhotoTagger";
        AppFrame parentComponent = GUI.getAppFrame();
        String message = Bundle.getString(ShowAboutJPhotoTaggerAction.class, key, AppInfo.APP_NAME, AppInfo.APP_VERSION); // NOI18N
        int messageType = JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(parentComponent, message, title, messageType, icon);
    }
}
