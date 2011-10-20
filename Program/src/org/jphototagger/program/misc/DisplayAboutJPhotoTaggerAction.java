package org.jphototagger.program.misc;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppInfo;
import org.jphototagger.program.app.ui.AppFrame;
import org.jphototagger.program.app.ui.AppLookAndFeel;
import org.jphototagger.program.resource.GUI;

/**
 * Kontrolliert die Aktion: Informationen über die Anwendung sollen angezeigt
 * werden.
 *
 * @author Elmar Baumann
 */
public final class DisplayAboutJPhotoTaggerAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public DisplayAboutJPhotoTaggerAction() {
        super(Bundle.getString(DisplayAboutJPhotoTaggerAction.class, "DisplayAboutJPhotoTaggerAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(DisplayAboutJPhotoTaggerAction.class, "about.png"));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        ImageIcon icon = AppLookAndFeel.getIcon("icon_logo.png");
        String key = "DisplayAboutJPhotoTaggerAction.Info.About";
        String title = "JPhotoTagger";
        AppFrame parentComponent = GUI.getAppFrame();
        String message = Bundle.getString(DisplayAboutJPhotoTaggerAction.class, key, AppInfo.APP_NAME, AppInfo.APP_VERSION);
        int messageType = JOptionPane.INFORMATION_MESSAGE;

        JOptionPane.showMessageDialog(parentComponent, message, title, messageType, icon);
    }
}
