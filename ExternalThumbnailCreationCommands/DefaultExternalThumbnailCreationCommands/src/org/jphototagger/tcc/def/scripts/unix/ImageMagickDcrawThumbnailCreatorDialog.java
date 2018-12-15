package org.jphototagger.tcc.def.scripts.unix;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Icon;
import org.jphototagger.api.branding.Branding;
import org.jphototagger.lib.swing.DialogExt;
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
public class ImageMagickDcrawThumbnailCreatorDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private File dcraw;
    private File convert;
    private File identify;
    private File mplayer;
    private final FileChooser dcrawFileChooser = createDcrawFileChooser();
    private final FileChooser convertFileChooser = createConvertFileChooser();
    private final FileChooser identifyFileChooser = createIdentifyFileChooser();
    private final FileChooser mplayerFileChooser = createMPlayerFileChooser();
    private static final Icon OK_ICON = org.jphototagger.resources.Icons.getIcon("icon_ok.png");
    private static final Icon ERROR_ICON = org.jphototagger.resources.Icons.getIcon("icon_error.png");
    private boolean accepted;

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

    public File getIdentify() {
        return identify;
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

    private void chooseIdentify() {
        File file = identifyFileChooser.chooseFileFixedName();
        if (file != null) {
            identify = file;
        }
        labelIdentifyOk.setIcon(identify == null ? ERROR_ICON : OK_ICON);
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
        buttonOk.setEnabled(dcraw != null && convert != null && identify != null);
    }

    private FileChooser createDcrawFileChooser() {
        String filename = "dcraw";
        String fileDescription = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.Dcraw.FileChooser.Description");
        String fileChooserTitle = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.Dcraw.FileChooser.Title");
        return createFileChooser(new HashSet<>(Arrays.asList(filename)), fileDescription, fileChooserTitle);
    }

    private FileChooser createConvertFileChooser() {
        String filename = "convert";
        String fileDescription = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.Convert.FileChooser.Description");
        String fileChooserTitle = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.Convert.FileChooser.Title");
        return createFileChooser(new HashSet<>(Arrays.asList(filename)), fileDescription, fileChooserTitle);
    }

    private FileChooser createIdentifyFileChooser() {
        String filename = "identify";
        String fileDescription = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.Identify.FileChooser.Description");
        String fileChooserTitle = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.Identify.FileChooser.Title");
        return createFileChooser(new HashSet<>(Arrays.asList(filename)), fileDescription, fileChooserTitle);
    }

    private FileChooser createMPlayerFileChooser() {
        String filename = "mplayer";
        String fileDescription = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.MPlayer.FileChooser.Description");
        String fileChooserTitle = Bundle.getString(ImageMagickDcrawThumbnailCreatorDialog.class, "ImageMagickDcrawThumbnailCreatorDialog.MPlayer.FileChooser.Title");
        return createFileChooser(new HashSet<>(Arrays.asList(filename)), fileDescription, fileChooserTitle);
    }

    private FileChooser createFileChooser(Set<String> filenames, String fileDescription, String fileChooserTitle) {
        return new FileChooser.Builder(filenames)
                .fileChooserTitle(fileChooserTitle)
                .fileChooserDirPath("/usr/bin")
                .fileDescription(fileDescription)
                .build();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        labelInfo = UiFactory.jxLabel();
        panelImageButtons = UiFactory.panel();
        labelDcrawOk = UiFactory.label();
        buttonChooseDcraw = UiFactory.button();
        buttonBrowseDcraw = UiFactory.button();
        labelConvertOk = UiFactory.label();
        buttonChooseConvert = UiFactory.button();
        buttonBrowseConvert = UiFactory.button();
        labelIdentifyOk = UiFactory.label();
        buttonChooseIdentify = UiFactory.button();
        panelVideo = UiFactory.panel();
        labelInfoVideo = UiFactory.jxLabel();
        buttonAddUserDefinedFileTypes = UiFactory.button();
        labelMPlayerOk = UiFactory.label();
        buttonChooseMPlayer = UiFactory.button();
        buttonBrowserMPlayer = UiFactory.button();
        panelOkCancelButtons = UiFactory.panel();
        buttonCancel = UiFactory.button();
        buttonOk = UiFactory.button();

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
        gridBagConstraints.insets = UiFactory.insets(5, 10, 0, 10);
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 3, 0, 0);
        panelImageButtons.add(buttonBrowseDcraw, gridBagConstraints);

        labelConvertOk.setIcon(ERROR_ICON);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(3, 5, 0, 0);
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 0, 0);
        panelImageButtons.add(buttonBrowseConvert, gridBagConstraints);

        labelIdentifyOk.setIcon(ERROR_ICON);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(3, 0, 0, 0);
        panelImageButtons.add(labelIdentifyOk, gridBagConstraints);

        buttonChooseIdentify.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonChooseIdentify.text")); // NOI18N
        buttonChooseIdentify.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseIdentifyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 5, 0, 0);
        panelImageButtons.add(buttonChooseIdentify, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 0, 10);
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
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 5);
        panelVideo.add(labelInfoVideo, gridBagConstraints);

        buttonAddUserDefinedFileTypes.setAction(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.INSTANCE);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 0, 0);
        panelVideo.add(buttonAddUserDefinedFileTypes, gridBagConstraints);

        labelMPlayerOk.setIcon(ERROR_ICON);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
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
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 0);
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
        gridBagConstraints.insets = UiFactory.insets(5, 3, 5, 5);
        panelVideo.add(buttonBrowserMPlayer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 10, 0, 10);
        panelContent.add(panelVideo, gridBagConstraints);

        panelOkCancelButtons.setLayout(new java.awt.GridLayout(1, 0, UiFactory.scale(3), 0));

        buttonCancel.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelOkCancelButtons.add(buttonCancel);

        buttonOk.setText(Bundle.getString(getClass(), "ImageMagickDcrawThumbnailCreatorDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        panelOkCancelButtons.add(buttonOk);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 10, 10);
        panelContent.add(panelOkCancelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void buttonOkActionPerformed(java.awt.event.ActionEvent evt) {
        accepted = true;
        setVisible(false);
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private void buttonChooseDcrawActionPerformed(java.awt.event.ActionEvent evt) {
        chooseDcraw();
    }

    private void buttonChooseConvertActionPerformed(java.awt.event.ActionEvent evt) {
        chooseConvert();
    }

    private void buttonBrowseDcrawActionPerformed(java.awt.event.ActionEvent evt) {
        Util.browse("http://www.cybercom.net/~dcoffin/dcraw/");
    }

    private void buttonBrowseConvertActionPerformed(java.awt.event.ActionEvent evt) {
        Util.browse("http://www.imagemagick.org/");
    }

    private void buttonChooseIdentifyActionPerformed(java.awt.event.ActionEvent evt) {
        chooseIdentify();
    }

    private void buttonChooseMPlayerActionPerformed(java.awt.event.ActionEvent evt) {
        chooseMPlayer();
    }

    private void buttonBrowserMPlayerActionPerformed(java.awt.event.ActionEvent evt) {
        Util.browse("http://www.mplayerhq.hu/design7/dload.html");
    }

    private javax.swing.JButton buttonAddUserDefinedFileTypes;
    private javax.swing.JButton buttonBrowseConvert;
    private javax.swing.JButton buttonBrowseDcraw;
    private javax.swing.JButton buttonBrowserMPlayer;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseConvert;
    private javax.swing.JButton buttonChooseDcraw;
    private javax.swing.JButton buttonChooseIdentify;
    private javax.swing.JButton buttonChooseMPlayer;
    private javax.swing.JButton buttonOk;
    private javax.swing.JLabel labelConvertOk;
    private javax.swing.JLabel labelDcrawOk;
    private javax.swing.JLabel labelIdentifyOk;
    private org.jdesktop.swingx.JXLabel labelInfo;
    private org.jdesktop.swingx.JXLabel labelInfoVideo;
    private javax.swing.JLabel labelMPlayerOk;
    private javax.swing.JPanel panelContent;
    private javax.swing.JPanel panelImageButtons;
    private javax.swing.JPanel panelOkCancelButtons;
    private javax.swing.JPanel panelVideo;
}
