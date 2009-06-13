package de.elmar_baumann.imv.model;

import de.elmar_baumann.imv.data.Exif;
import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Timeline;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.event.DatabaseAction;
import de.elmar_baumann.imv.event.DatabaseListener;
import de.elmar_baumann.lib.model.TreeModelUpdateInfo;
import java.util.Calendar;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * A {@link de.elmar_baumann.imv.data.Timeline}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/12
 */
public final class TreeModelTimeline extends DefaultTreeModel implements
        DatabaseListener {

    private final Timeline timeline;
    private final DatabaseImageFiles db;

    public TreeModelTimeline() {
        super(new DefaultMutableTreeNode());
        db = DatabaseImageFiles.INSTANCE;
        timeline = db.getTimeline();
        setRoot(timeline.getRoot());
        listen();
    }

    private void listen() {
        db.addDatabaseListener(this);
    }

    @Override
    public void actionPerformed(DatabaseAction action) {
        DatabaseAction.Type actionType = action.getType();
        if (actionType.equals(DatabaseAction.Type.IMAGEFILE_DELETED)) {
            checkDeleted(action.getImageFileData());
        } else if (actionType.equals(DatabaseAction.Type.IMAGEFILE_INSERTED)) {
            checkInserted(action.getImageFileData());
        }
    }

    private void checkDeleted(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        if (exif != null) {
            java.sql.Date day = exif.getDateTimeOriginal();
            if (day != null && !db.existsExifDay(day)) {
                Calendar calDay = Calendar.getInstance();
                calDay.setTime(day);
                TreeModelUpdateInfo.NodeAndChild info = timeline.removeDay(
                        calDay);
                nodesWereRemoved(info.getNode(), info.getUpdatedChildIndex(),
                        info.getUpdatedChild());
            }
        }
    }

    private void checkInserted(ImageFile imageFile) {
        Exif exif = imageFile.getExif();
        if (exif != null) {
            java.sql.Date day = exif.getDateTimeOriginal();
            if (day != null) {
                Calendar calDay = Calendar.getInstance();
                calDay.setTime(day);
                if (!timeline.existsDay(calDay)) {
                    TreeModelUpdateInfo.NodesAndChildIndices info =
                            timeline.add(calDay);
                    for (TreeModelUpdateInfo.NodeAndChildIndices node : info.
                            getInfo()) {
                        nodesWereInserted(node.getNode(), node.getChildIndices());
                    }
                }
            }
        }
    }
}
