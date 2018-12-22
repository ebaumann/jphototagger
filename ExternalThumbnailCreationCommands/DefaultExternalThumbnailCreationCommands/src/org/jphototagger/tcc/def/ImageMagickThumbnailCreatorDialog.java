package org.jphototagger.tcc.def;

import java.awt.GridBagConstraints;
import org.jphototagger.api.branding.Branding;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public class ImageMagickThumbnailCreatorDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private boolean browse;
    private boolean chooseConvert;

    public ImageMagickThumbnailCreatorDialog() {
        super((java.awt.Frame) null, true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        MnemonicUtil.setMnemonics(this);
    }

    public boolean isBrowse() {
        return browse;
    }

    public boolean isChooseConvert() {
        return chooseConvert;
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        panelContent = UiFactory.panel();
        labelInfo = UiFactory.jxLabel();
        panelButtons = UiFactory.panel();
        buttonCancel = UiFactory.button();
        buttonBrowse = UiFactory.button();
        buttonChooseConvert = UiFactory.button();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "ImageMagickThumbnailCreatorDialog.title")); // NOI18N
        setIconImages(Lookup.getDefault().lookup(Branding.class).getAppIcons());

        panelContent.setLayout(new java.awt.GridBagLayout());

        labelInfo.setText(Bundle.getString(getClass(), "ImageMagickThumbnailCreatorDialog.labelInfo.text")); // NOI18N
        labelInfo.setLineWrap(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelContent.add(labelInfo, gridBagConstraints);

        panelButtons.setLayout(new java.awt.GridBagLayout());

        buttonCancel.setText(Bundle.getString(getClass(), "ImageMagickThumbnailCreatorDialog.buttonCancel.text")); // NOI18N
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });
        panelButtons.add(buttonCancel, new java.awt.GridBagConstraints());

        buttonBrowse.setText(Bundle.getString(getClass(), "ImageMagickThumbnailCreatorDialog.buttonBrowse.text")); // NOI18N
        buttonBrowse.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBrowseActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonBrowse, gridBagConstraints);

        buttonChooseConvert.setText(Bundle.getString(getClass(), "ImageMagickThumbnailCreatorDialog.buttonChooseConvert.text")); // NOI18N
        buttonChooseConvert.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonChooseConvertActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelButtons.add(buttonChooseConvert, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(7, 0, 0, 0);
        panelContent.add(panelButtons, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = UiFactory.insets(7, 7, 7, 7);
        getContentPane().setLayout(new java.awt.GridBagLayout());
        getContentPane().add(panelContent, gridBagConstraints);

        pack();
    }

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
    }

    private void buttonBrowseActionPerformed(java.awt.event.ActionEvent evt) {
        browse = true;
        setVisible(false);
    }

    private void buttonChooseConvertActionPerformed(java.awt.event.ActionEvent evt) {
        chooseConvert = true;
        setVisible(false);
    }

    private javax.swing.JButton buttonBrowse;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonChooseConvert;
    private org.jdesktop.swingx.JXLabel labelInfo;
    private javax.swing.JPanel panelButtons;
    private javax.swing.JPanel panelContent;
}
