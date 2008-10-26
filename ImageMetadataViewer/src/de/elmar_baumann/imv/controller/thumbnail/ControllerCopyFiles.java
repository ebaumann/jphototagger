package de.elmar_baumann.imv.controller.thumbnail;

import de.elmar_baumann.imv.controller.Controller;
import de.elmar_baumann.imv.resource.Panels;
import de.elmar_baumann.imv.view.panels.ImageFileThumbnailsPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/26
 */
public class ControllerCopyFiles extends Controller implements ActionListener {
    
    ImageFileThumbnailsPanel thumbnailsPanel = Panels.getInstance().getAppPanel().getPanelThumbnails();

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isControl() && thumbnailsPanel.getSelectionCount() > 0) {
            
        }
    }

}
