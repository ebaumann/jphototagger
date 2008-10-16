package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.UserSettings;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListModel;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/10/16
 */
public class ListModelOtherImageOpenApps extends DefaultListModel {

    public ListModelOtherImageOpenApps() {
        addItems();
    }

    private void addItems() {
        List<File> apps = UserSettings.getInstance().getOtherImageOpenApps();
        for (File app : apps) {
            if (app.exists()) {
                addElement(app);
            }
        }
    }

    public boolean add(File app) {
        if (app.exists() && !contains(app)) {
            addElement(app);
            return true;
        }
        return false;
    }

    public boolean remove(Object app) {
        if (contains(app)) {
            removeElement(app);
            return true;
        }
        return false;
    }
}
