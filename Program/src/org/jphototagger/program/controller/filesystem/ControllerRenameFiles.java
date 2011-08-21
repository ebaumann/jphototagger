package org.jphototagger.program.controller.filesystem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.file.event.FileRenamedEvent;
import org.jphototagger.lib.awt.EventQueueUtil;
import org.jphototagger.program.cache.RenderedThumbnailCache;
import org.jphototagger.program.cache.ThumbnailCache;
import org.jphototagger.program.cache.XmpCache;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.dialogs.RenameDialog;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

/**
 * Listens to key events of {@link ThumbnailsPanel} and when
 * <code>F2</code> was pressed shows the {@link RenameDialog} to renameFile the
 * selected files.
 *
 * @author Elmar Baumann
 */
public final class ControllerRenameFiles implements ActionListener, KeyListener {

    private static final Logger LOGGER = Logger.getLogger(ControllerRenameFiles.class.getName());

    public ControllerRenameFiles() {
        listen();
    }

    private void listen() {
        GUI.getThumbnailsPanel().addKeyListener(this);
        AnnotationProcessor.process(this);
        PopupMenuThumbnails.INSTANCE.getItemFileSystemRenameFiles().addActionListener(this);
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_F2) {
            renameSelectedFiles();
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(PopupMenuThumbnails.INSTANCE.getItemFileSystemRenameFiles())) {
            renameSelectedFiles();
        }
    }

    private void renameFile(final File fromFile, final File toFile) {
        LOGGER.log(Level.INFO, "Rename in the database file ''{0}'' to ''{1}''", new Object[]{fromFile, toFile});
        DatabaseImageFiles.INSTANCE.updateRename(fromFile, toFile);
        EventQueueUtil.invokeInDispatchThread(new Runnable() {

            @Override
            public void run() {
                ThumbnailCache.INSTANCE.updateFiles(fromFile, toFile);
                XmpCache.INSTANCE.updateFiles(fromFile, toFile);
                RenderedThumbnailCache.INSTANCE.updateFiles(fromFile, toFile);
                GUI.getThumbnailsPanel().renameFile(fromFile, toFile);
            }
        });
    }

    private void renameSelectedFiles() {
        List<File> selFiles = GUI.getSelectedImageFiles();

        if (selFiles.size() > 0) {
            RenameDialog dlg = new RenameDialog();

            Collections.sort(selFiles);
            dlg.setImageFiles(selFiles);
            dlg.setEnabledTemplates(GUI.getThumbnailsPanel().getContent().isUniqueFileSystemDirectory());
            dlg.setVisible(true);
        }
    }

    @Override
    public void keyTyped(KeyEvent evt) {
        // ignore
    }

    @Override
    public void keyReleased(KeyEvent evt) {
        // ignore
    }

    @EventSubscriber(eventClass = FileRenamedEvent.class)
    public void fileRenamed(final FileRenamedEvent evt) {
        File fromFile = evt.getSourceFile();
        File toFile = evt.getTargetFile();

        renameFile(fromFile, toFile);
    }
}
