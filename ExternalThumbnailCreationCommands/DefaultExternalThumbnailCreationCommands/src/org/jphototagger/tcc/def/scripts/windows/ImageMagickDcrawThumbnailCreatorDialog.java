package org.jphototagger.tcc.def.scripts.windows;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.Icon;
import org.jphototagger.api.branding.Branding;
import org.jphototagger.lib.swing.Dialog;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.jphototagger.tcc.def.FileChooser;
import org.jphototagger.tcc.def.SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction;
import org.jphototagger.tcc.def.Util;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class ImageMagickDcrawThumbnailCreatorDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private File dcraw;
    private File convert;
    private File mplayer;
    private final FileChooser dcrawFileChooser = createDcrawFileChooser();
    private final FileChooser convertFileChooser = createConvertFileChooser();
    private final FileChooser mplayerFileChooser = createMPlayerFileChooser();
    private boolean accepted;
    private static final Icon OK_ICON = org.jphototagger.resources.Icons.getIcon("icon_ok.png");
    private static final Icon ERROR_ICON = org.jphototagger.resources.Icons.getIcon("icon_error.png");

    public ImageMagickDcrawThumbnailCreatorDialog() {
        super((java.awt.Frame) null, true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public File getConvert() {
        return convert;
    }

    public File getDcraw() {
        return dcraw;
    }

    public File getMplayer() {
        return mplayer;
    }

    private void chooseDcraw() {
        File file = dcrawFileChooser.chooseFileFixedName();
        if (file != null) {
            dcraw = file;
        }
        labelDcrawOk.setIcon(dcraw == null ? ERROR_ICON : OK_ICON);
        setEnabledOkButton();
    }

    private void chooseConvert() {
        File file = convertFileChooser.chooseFileFixedName();
        if (file != null) {
            convert = file;
        }
        labelConvertOk.setIcon(convert == null ? ERROR_ICON : OK_ICON);
        setEnabledOkButton();
    }

    private void chooseMPlayer() {
        File file = mplayerFileChooser.chooseFileFixedName();
        if (file != null) {
            mplayer = file;
        }
        labelMPlayerOk.setIcon(mplayer == null ? ERROR_ICON : OK_ICON);
    }

    private void setEnabledOkButton() {
        buttonOk.setEnabled(dcraw != null && convert != null);
    }

    private FileChooser createDcrawFileChooser() {
        List<String> filenames = Arrays.asList("dcraw.exe", "dcrawMS.exe");
        String fileDescription = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.Dcraw.FileChooser.Description");
        String fileChooserTitle = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.Dcraw.FileChooser.Title");
        return createFileChooser(new HashSet<>(filenames), fileDescription, fileChooserTitle);
    }

    private FileChooser createConvertFileChooser() {
        String filename = "convert.exe";
        String fileDescription = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.Convert.FileChooser.Description");
        String fileChooserTitle = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.Convert.FileChooser.Title");
        return createFileChooser(new HashSet<>(Arrays.asList(filename)), fileDescription, fileChooserTitle);
    }

    private FileChooser createMPlayerFileChooser() {
        String filename = "mplayer.exe";
        String fileDescription = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.MPlayer.FileChooser.Description");
        String fileChooserTitle = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.MPlayer.FileChooser.Title");
        return createFileChooser(new HashSet<>(Arrays.asList(filename)), fileDescription, fileChooserTitle);
    }

    private FileChooser createFileChooser(Set<String> filenames, String fileDescription, String fileChooserTitle) {
        String programsDirectory = System.getenv("ProgramFiles");
        return new FileChooser.Builder(filenames)
                .fileChooserTitle(fileChooserTitle)
                .fileDescription(fileDescription)
                .fileChooserDirPath(programsDirectory == null ? "" : programsDirectory)
                .build();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = org.jphototagger.resources.UiFactory.panel();
        labelInfo = org.jphototagger.resources.UiFactory.jxLabel();
        panelImageButtons = org.jphototagger.resources.UiFactory.panel();
        labelDcrawOk = org.jphototagger.resources.UiFactory.label();
        buttonChooseDcraw = org.jphototagger.resources.UiFactory.button();
        buttonBrowseDcraw = org.jphototagger.resources.UiFactory.button();
        labelConvertOk = org.jphototagger.resources.UiFactory.label();
        buttonChooseConvert = org.jphototagger.resources.UiFactory.button();
        buttonBrowseConvert = org.jphototagger.resources.UiFactory.button();
        panelVideo = org.jphototagger.resources.UiFactory.panel();
        labelInfoVideo = org.jphototagger.resources.UiFactory.jxLabel();
        buttonAddUserDefinedFileTypes = org.jphototagger.resources.UiFactory.button();
        labelMPlayerOk = org.jphototagger.resources.UiFactory.label();
        buttonChooseMPlayer = org.jphototagger.resources.UiFactory.button();
        buttonBrowserMPlayer = org.jphototagger.resources.UiFactory.button();
        panelOkCancelButtons = org.jphototagger.resources.UiFactory.panel();
        buttonOk = org.jphototagger.resources.UiFactory.button();
        buttonCancel = org.jphototagger.resources.UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.title")); // NOI18N
        setIconImages(Lookup.getDefault().lookup(Branding.class).getAppIcons());
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        labelInfo.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.labelInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        panelContent.add(labelInfo, gridBagConstraints);

        panelImageButtons.setLayout(new java.awt.GridBagLayout());

        labelDcrawOk.setIcon(ERROR_ICON);
        panelImageButtons.add(labelDcrawOk, new java.awt.GridBagConstraints());

        buttonChooseDcraw.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonChooseDcraw.text")); // NOI18N
        buttonChooseDcraw.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDcrawActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 5, 0, 0);
        panelImageButtons.add(buttonChooseDcraw, gridBagConstraints);

        buttonBrowseDcraw.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonBrowseDcraw.text")); // NOI18N
        buttonBrowseDcraw.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseDcrawActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(0, 3, 0, 0);
        panelImageButtons.add(buttonBrowseDcraw, gridBagConstraints);

        labelConvertOk.setIcon(ERROR_ICON);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 0, 0, 0);
        panelImageButtons.add(labelConvertOk, gridBagConstraints);

        buttonChooseConvert.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonChooseConvert.text")); // NOI18N
        buttonChooseConvert.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseConvertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 5, 0, 0);
        panelImageButtons.add(buttonChooseConvert, gridBagConstraints);

        buttonBrowseConvert.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonBrowseConvert.text")); // NOI18N
        buttonBrowseConvert.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseConvertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(3, 3, 0, 0);
        panelImageButtons.add(buttonBrowseConvert, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        panelContent.add(panelImageButtons, gridBagConstraints);

        panelVideo.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.panelVideo.border.title"))); // NOI18N
        panelVideo.setLayout(new java.awt.GridBagLayout());

        labelInfoVideo.setLineWrap(true);
        labelInfoVideo.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.labelInfoVideo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 5);
        panelVideo.add(labelInfoVideo, gridBagConstraints);

        buttonAddUserDefinedFileTypes.setAction(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.INSTANCE);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 0, 0);
        panelVideo.add(buttonAddUserDefinedFileTypes, gridBagConstraints);

        labelMPlayerOk.setIcon(ERROR_ICON);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        panelVideo.add(labelMPlayerOk, gridBagConstraints);

        buttonChooseMPlayer.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonChooseMPlayer.text")); // NOI18N
        buttonChooseMPlayer.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseMPlayerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 5, 5, 0);
        panelVideo.add(buttonChooseMPlayer, gridBagConstraints);

        buttonBrowserMPlayer.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonBrowserMPlayer.text")); // NOI18N
        buttonBrowserMPlayer.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowserMPlayerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(5, 3, 5, 5);
        panelVideo.add(buttonBrowserMPlayer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 0, 10);
        panelContent.add(panelVideo, gridBagConstraints);

        panelOkCancelButtons.setLayout(new java.awt.GridLayout(1, 0, UiFactory.scale(3), 0));

        buttonOk.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        panelOkCancelButtons.add(buttonOk);

        buttonCancel.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelOkCancelButtons.add(buttonCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        panelContent.add(panelOkCancelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = org.jphototagger.resources.UiFactory.insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonOkActionPerformed
        accepted = true;
        setVisible(false);
    }//GEN-LAST:event_buttonOkActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void buttonChooseDcrawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseDcrawActionPerformed
        chooseDcraw();
    }//GEN-LAST:event_buttonChooseDcrawActionPerformed

    private void buttonChooseConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseConvertActionPerformed
        chooseConvert();
    }//GEN-LAST:event_buttonChooseConvertActionPerformed

    private void buttonBrowseDcrawActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseDcrawActionPerformed
        Util.browse("http://www.cybercom.net/~dcoffin/dcraw/");
    }//GEN-LAST:event_buttonBrowseDcrawActionPerformed

    private void buttonBrowseConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowseConvertActionPerformed
        Util.browse("http://www.imagemagick.org/");
    }//GEN-LAST:event_buttonBrowseConvertActionPerformed

    private void buttonChooseMPlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseMPlayerActionPerformed
        chooseMPlayer();
}//GEN-LAST:event_buttonChooseMPlayerActionPerformed

    private void buttonBrowserMPlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowserMPlayerActionPerformed
        Util.browse("http://www.mplayerhq.hu/design7/dload.html");
}//GEN-LAST:event_buttonBrowserMPlayerActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonAddUserDefinedFileTypes;
    private javax.swing.JButton buttonBrowseConvert;
    private javax.swing.JButton buttonBrowseDcraw;
    private javax.swing.JButton buttonBrowserMPlayer;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseConvert;
    private javax.swing.JButton buttonChooseDcraw;
    private javax.swing.JButton buttonChooseMPlayer;
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel labelConvertOk;
    private javax.swing.JLabel labelDcrawOk;
    private org.jdesktop.swingx.JXLabel labelInfo;
    private org.jdesktop.swingx.JXLabel labelInfoVideo;
    private javax.swing.JLabel labelMPlayerOk;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelImageButtons;
    private javax.swing.JPanel panelOkCancelButtons;
    private javax.swing.JPanel panelVideo;
    // End of variables declaration//GEN-END:variables
}
