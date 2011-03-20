package org.jphototagger.program.view.dialogs;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jphototagger.lib.dialog.Dialog;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.FileUtil.FileChooserProperties;
import org.jphototagger.lib.system.SystemUtil;
import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.image.thumbnail.creator.ExternalThumbnailCreator;
import org.jphototagger.program.image.thumbnail.creator.ExternalThumbnailCreators;
import org.jphototagger.program.resource.JptBundle;

/**
 *
 *
 * @author Elmar Baumann
 */
public class ExternalThumbnailCreatorChooserDialog extends Dialog {
    private static final long serialVersionUID = 7080040991720533273L;
    private final JPanel creatorContainerPanel = new JPanel(new GridBagLayout());
    private String creatorCommand = "";
    private boolean accepted;

    public ExternalThumbnailCreatorChooserDialog() {
        super((java.awt.Frame) null, true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        List<ExternalThumbnailCreator> creators = ExternalThumbnailCreators.getCreators();

        for (ExternalThumbnailCreator creator : creators) {
            JPanel creatorPanel = createCreatorPanel(creator);

            addCreatorPanel(creatorPanel);
        }

        addVerticalFillPanel();
        scrollPaneCreators.setViewportView(creatorContainerPanel);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getCreatorCommand() {
        return creatorCommand;
    }

    private void addCreatorPanel(JPanel creatorPanel) {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        creatorContainerPanel.add(creatorPanel, gbc);
    }

    private void addVerticalFillPanel() {
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel fillPanel = new JPanel();

        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 0, 0, 0);

        creatorContainerPanel.add(fillPanel, gbc);
    }

    private JPanel createCreatorPanel(ExternalThumbnailCreator creator) {
        JPanel creatorPanel = new JPanel(new GridBagLayout());
        JLabel labelDescription = new JLabel(creator.getDisplayName());
        JButton buttonDownload = new JButton();
        JButton buttonChoose = new JButton();

        buttonDownload.setEnabled(SystemUtil.canBrowse());
        buttonDownload.addActionListener(new BrowseDownloadSiteAction(creator.getDownloadUrl()));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        creatorPanel.add(labelDescription, gbc);

        buttonDownload.setText(JptBundle.INSTANCE.getString("ExternalThumbnailCreatorChooserDialog.ButtonDownload.Text"));
        buttonDownload.setToolTipText(creator.getDownloadUrl());
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.EAST;
        gbc.insets = new java.awt.Insets(5, 5, 5, 0);
        creatorPanel.add(buttonDownload, gbc);

        buttonChoose.setText(JptBundle.INSTANCE.getString("ExternalThumbnailCreatorChooserDialog.ButtonChoose.Text"));
        buttonChoose.addActionListener(new ChooseThumbnailCreatorProgramFileAction(creator));
        gbc = new java.awt.GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.EAST;
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        creatorPanel.add(buttonChoose, gbc);

        creatorPanel.setBorder(BorderFactory.createEtchedBorder());
        creatorPanel.setOpaque(true);
        creatorPanel.setBackground(Color.WHITE);

        return creatorPanel;
    }

    private class ChooseThumbnailCreatorProgramFileAction implements ActionListener {

        private static final String KEY_LAST_DIR = "ChooseThumbnailCreatorProgramFileAction.LastDir";
        private final ExternalThumbnailCreator creator;

        ChooseThumbnailCreatorProgramFileAction(ExternalThumbnailCreator creator) {
            this.creator = creator;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            chooseFile();
        }

        private void chooseFile() {
            FileChooserProperties fcProps = new FileChooserProperties();
            Settings settings = UserSettings.INSTANCE.getSettings();
            File lastFile = new File(settings.getString(KEY_LAST_DIR));
            File lastDir = lastFile.getParentFile();

            fcProps.dialogTitle(JptBundle.INSTANCE.getString("ExternalThumbnailCreatorChooserDialog.ChooseProram.Dialogtitle"));
            fcProps.currentDirectoryPath(lastDir == null ? "" : lastDir.getAbsolutePath());
            fcProps.multiSelectionEnabled(false);
            fcProps.fileFilter(creator.getThumbnailCreatorFileFilter());
            fcProps.fileSelectionMode(JFileChooser.FILES_ONLY);

            File file = FileUtil.chooseFile(fcProps);

            if (file != null) {
                settings.set(file.getAbsolutePath(), KEY_LAST_DIR);
                UserSettings.INSTANCE.writeToFile();

                creatorCommand = creator.getThumbnailCreationCommand(file.getAbsolutePath());
                accepted = true;
                setVisible(false);
            }
        }
    }

    private static class BrowseDownloadSiteAction implements ActionListener {

        private final String downloadUrl;

        BrowseDownloadSiteAction(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                Desktop.getDesktop().browse(new URI(downloadUrl));
            } catch (Exception ex) {
                AppLogger.logSevere(ExternalThumbnailCreatorChooserDialog.class, ex);
            }
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPaneCreators = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle(JptBundle.INSTANCE.getString("ExternalThumbnailCreatorChooserDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneCreators, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPaneCreators, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ExternalThumbnailCreatorChooserDialog dialog = new ExternalThumbnailCreatorChooserDialog();
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
    private javax.swing.JScrollPane scrollPaneCreators;
    // End of variables declaration//GEN-END:variables

}
