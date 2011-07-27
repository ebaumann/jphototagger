package org.jphototagger.dtncreators.scripts.unix;

import java.io.File;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.jphototagger.dtncreators.FileChooser;
import org.jphototagger.dtncreators.SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction;
import org.jphototagger.dtncreators.Util;
import org.jphototagger.lib.componentutil.MnemonicUtil;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.services.core.Branding;

/**
 *
 *
 * @author Elmar Baumann
 */
public class ImageMagickDcrawThumbnailCreatorDialog extends Dialog {

    private static final long serialVersionUID = 1L;
    private File dcraw;
    private File convert;
    private File identify;
    private File mplayer;
    private final FileChooser dcrawFileChooser = createDcrawFileChooser();
    private final FileChooser convertFileChooser = createConvertFileChooser();
    private final FileChooser identifyFileChooser = createIdentifyFileChooser();
    private final FileChooser mplayerFileChooser = createMPlayerFileChooser();
    private static final Icon OK_ICON = new ImageIcon(ImageMagickDcrawThumbnailCreatorDialog.class .getResource("/org/jphototagger/dtncreators/icons/icon_ok.png"));
    private static final Icon ERROR_ICON = new ImageIcon(ImageMagickDcrawThumbnailCreatorDialog.class .getResource("/org/jphototagger/dtncreators/icons/icon_error.png"));
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
        ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/dtncreators/scripts/unix/Bundle");
        String filename = "dcraw";
        String fileDescription = bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.Dcraw.FileChooser.Description");
        String fileChooserTitle = bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.Dcraw.FileChooser.Title");

        return createFileChooser(filename, fileDescription, fileChooserTitle);
    }

    private FileChooser createConvertFileChooser() {
        ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/dtncreators/scripts/unix/Bundle");
        String filename = "convert";
        String fileDescription = bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.Convert.FileChooser.Description");
        String fileChooserTitle = bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.Convert.FileChooser.Title");

        return createFileChooser(filename, fileDescription, fileChooserTitle);
    }

    private FileChooser createIdentifyFileChooser() {
        ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/dtncreators/scripts/unix/Bundle");
        String filename = "identify";
        String fileDescription = bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.Identify.FileChooser.Description");
        String fileChooserTitle = bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.Identify.FileChooser.Title");

        return createFileChooser(filename, fileDescription, fileChooserTitle);
    }

    private FileChooser createMPlayerFileChooser() {
        ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/dtncreators/scripts/unix/Bundle");
        String filename = "mplayer";
        String fileDescription = bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.MPlayer.FileChooser.Description");
        String fileChooserTitle = bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.MPlayer.FileChooser.Title");

        return createFileChooser(filename, fileDescription, fileChooserTitle);
    }

    private FileChooser createFileChooser(String filename, String fileDescription, String fileChooserTitle) {

        return new FileChooser.Builder(filename)
                .fileChooserTitle(fileChooserTitle)
                .fileChooserDirPath("/usr/bin")
                .fileDescription(fileDescription)
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

        panelContent = new javax.swing.JPanel();
        labelInfo = new org.jdesktop.swingx.JXLabel();
        panelImageButtons = new javax.swing.JPanel();
        labelDcrawOk = new javax.swing.JLabel();
        buttonChooseDcraw = new javax.swing.JButton();
        buttonBrowseDcraw = new javax.swing.JButton();
        labelConvertOk = new javax.swing.JLabel();
        buttonChooseConvert = new javax.swing.JButton();
        buttonBrowseConvert = new javax.swing.JButton();
        labelIdentifyOk = new javax.swing.JLabel();
        buttonChooseIdentify = new javax.swing.JButton();
        panelVideo = new javax.swing.JPanel();
        labelInfoVideo = new org.jdesktop.swingx.JXLabel();
        buttonAddUserDefinedFileTypes = new javax.swing.JButton();
        labelMPlayerOk = new javax.swing.JLabel();
        buttonChooseMPlayer = new javax.swing.JButton();
        buttonBrowserMPlayer = new javax.swing.JButton();
        panelOkCancelButtons = new javax.swing.JPanel();
        buttonCancel = new javax.swing.JButton();
        buttonOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jphototagger/dtncreators/scripts/unix/Bundle"); // NOI18N
        setTitle(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.title")); // NOI18N
        setIconImages(ServiceLookup.lookup(Branding.class).getAppIcons());
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setLayout(new java.awt.GridBagLayout());

        labelInfo.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.labelInfo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        panelContent.add(labelInfo, gridBagConstraints);

        panelImageButtons.setLayout(new java.awt.GridBagLayout());

        labelDcrawOk.setIcon(ERROR_ICON);
        panelImageButtons.add(labelDcrawOk, new java.awt.GridBagConstraints());

        buttonChooseDcraw.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.buttonChooseDcraw.text")); // NOI18N
        buttonChooseDcraw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseDcrawActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        panelImageButtons.add(buttonChooseDcraw, gridBagConstraints);

        buttonBrowseDcraw.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.buttonBrowseDcraw.text")); // NOI18N
        buttonBrowseDcraw.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseDcrawActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        panelImageButtons.add(buttonBrowseDcraw, gridBagConstraints);

        labelConvertOk.setIcon(ERROR_ICON);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelImageButtons.add(labelConvertOk, gridBagConstraints);

        buttonChooseConvert.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.buttonChooseConvert.text")); // NOI18N
        buttonChooseConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseConvertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        panelImageButtons.add(buttonChooseConvert, gridBagConstraints);

        buttonBrowseConvert.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.buttonBrowseConvert.text")); // NOI18N
        buttonBrowseConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseConvertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        panelImageButtons.add(buttonBrowseConvert, gridBagConstraints);

        labelIdentifyOk.setIcon(ERROR_ICON);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        panelImageButtons.add(labelIdentifyOk, gridBagConstraints);

        buttonChooseIdentify.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.buttonChooseIdentify.text")); // NOI18N
        buttonChooseIdentify.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseIdentifyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 0, 0);
        panelImageButtons.add(buttonChooseIdentify, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        panelContent.add(panelImageButtons, gridBagConstraints);

        panelVideo.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.panelVideo.border.title"))); // NOI18N
        panelVideo.setLayout(new java.awt.GridBagLayout());

        labelInfoVideo.setLineWrap(true);
        labelInfoVideo.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.labelInfoVideo.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        panelVideo.add(labelInfoVideo, gridBagConstraints);

        buttonAddUserDefinedFileTypes.setAction(SaveMPlayerFileSuffixesAsUserDefinedFileTypesAction.INSTANCE);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        panelVideo.add(buttonAddUserDefinedFileTypes, gridBagConstraints);

        labelMPlayerOk.setIcon(ERROR_ICON);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        panelVideo.add(labelMPlayerOk, gridBagConstraints);

        buttonChooseMPlayer.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.buttonChooseMPlayer.text")); // NOI18N
        buttonChooseMPlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseMPlayerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panelVideo.add(buttonChooseMPlayer, gridBagConstraints);

        buttonBrowserMPlayer.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.buttonBrowserMPlayer.text")); // NOI18N
        buttonBrowserMPlayer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowserMPlayerActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 5);
        panelVideo.add(buttonBrowserMPlayer, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 10);
        panelContent.add(panelVideo, gridBagConstraints);

        panelOkCancelButtons.setLayout(new java.awt.GridLayout(1, 0, 3, 0));

        buttonCancel.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelOkCancelButtons.add(buttonCancel);

        buttonOk.setText(bundle.getString("ImageMagickDcrawThumbnailCreatorDialog.buttonOk.text")); // NOI18N
        buttonOk.setEnabled(false);
        buttonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonOkActionPerformed(evt);
            }
        });
        panelOkCancelButtons.add(buttonOk);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        panelContent.add(panelOkCancelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
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

    private void buttonChooseIdentifyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseIdentifyActionPerformed
        chooseIdentify();
    }//GEN-LAST:event_buttonChooseIdentifyActionPerformed

    private void buttonChooseMPlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonChooseMPlayerActionPerformed
        chooseMPlayer();
    }//GEN-LAST:event_buttonChooseMPlayerActionPerformed

    private void buttonBrowserMPlayerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBrowserMPlayerActionPerformed
        Util.browse("http://www.mplayerhq.hu/design7/dload.html");
    }//GEN-LAST:event_buttonBrowserMPlayerActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ImageMagickDcrawThumbnailCreatorDialog dialog = new ImageMagickDcrawThumbnailCreatorDialog();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    // End of variables declaration//GEN-END:variables
}
