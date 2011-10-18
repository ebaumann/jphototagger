package org.jphototagger.xmpmodule;

import java.awt.event.ActionEvent;

import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import org.openide.util.Lookup;

import org.jphototagger.domain.filefilter.AppFileFilterProvider;
import org.jphototagger.lib.swing.IconUtil;
import org.jphototagger.lib.swing.KeyEventUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * @author Elmar Baumann
 */
public final class ExtractEmbeddedXmpAction extends AbstractAction {

    private static final long serialVersionUID = 1L;

    public ExtractEmbeddedXmpAction() {
        super(Bundle.getString(ExtractEmbeddedXmpAction.class, "ExtractEmbeddedXmpAction.Name"));
        putValue(SMALL_ICON, IconUtil.getImageIcon(ExtractEmbeddedXmpAction.class, "xmp.png"));
        putValue(ACCELERATOR_KEY, KeyEventUtil.getKeyStrokeMenuShortcutWithShiftDown(KeyEvent.VK_X));
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        showDialog();
    }

    private void showDialog() {
        FileEditorDialog dlg = new FileEditorDialog();
        FileEditorPanel panel = dlg.getFileEditorPanel();
        AppFileFilterProvider fileFilterProvider = Lookup.getDefault().lookup(AppFileFilterProvider.class);

        panel.setEditor(new ExtractEmbeddedXmp());
        panel.setTitle(Bundle.getString(ExtractEmbeddedXmpAction.class, "ExtractEmbeddedXmpAction.Panel.Title"));
        panel.setDescription(Bundle.getString(ExtractEmbeddedXmpAction.class, "ExtractEmbeddedXmpAction.Panel.Description"));
        panel.setDirChooserFileFilter(fileFilterProvider.getAcceptedImageFilesFileFilter());
        panel.setSelectDirs(true);
        setHelpPage(dlg);
        dlg.setVisible(true);
    }

    private void setHelpPage(FileEditorDialog dlg) {
        // Has to be localized!
        dlg.setHelpPageUrl("import_xmp.html");
    }
}