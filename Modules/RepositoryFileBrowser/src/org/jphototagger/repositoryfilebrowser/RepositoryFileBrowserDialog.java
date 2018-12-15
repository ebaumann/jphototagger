package org.jphototagger.repositoryfilebrowser;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jphototagger.api.applifecycle.generics.Functor;
import org.jphototagger.api.branding.Branding;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.lookup.NodesListModel;
import org.jphototagger.lib.swing.DialogExt;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.swing.util.MnemonicUtil;
import org.jphototagger.lib.swingx.BusyPanel;
import org.jphototagger.lib.swingx.ListTextFilter;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class RepositoryFileBrowserDialog extends DialogExt {

    private static final long serialVersionUID = 1L;
    private RepositoryImageFileInfo imageFileInfo;
    private FileNode selectedFileNode;
    private final NodesListModel nodesListModel = new NodesListModel();
    private ListTextFilter listTextFilter;
    private final BusyPanel busyPanel = new BusyPanel(UiFactory.dimension(200, 200));

    public RepositoryFileBrowserDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        postInitComponents();
    }

    private void postInitComponents() {
        initFileFilter();
        listFiles.addListSelectionListener(new FileSelectionListener());
        MnemonicUtil.setMnemonics(this);
        panelContent.setGlassPane(busyPanel);
        busyPanel.setVisible(true);
    }

    private void initFileFilter() {
        listTextFilter = new ListTextFilter(listFiles);
        listTextFilter.filterOnActionPerformed(buttonApplyFilter, textFieldFilter.getDocument());
    }

    public void setSelectedFileNode(FileNode fileNode) {
        RepositoryImageFileInfo oldImageFileInfo = this.imageFileInfo;
        FileNode oldSelectedFileNode = this.selectedFileNode;
        selectedFileNode = fileNode;
        imageFileInfo = new RepositoryImageFileInfo(fileNode);
        firePropertyChange("imageFileInfo", oldImageFileInfo, imageFileInfo);
        firePropertyChange("selectedFileNode", oldSelectedFileNode, selectedFileNode);
    }

    public RepositoryImageFileInfo getImageFileInfo() {
        return imageFileInfo;
    }

    public FileNode getSelectedFileNode() {
        return selectedFileNode;
    }

    // Beans Binding makes trouble with sorted lists, have no time
    // to find a solution
    private class FileSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                FileNode selectedNode = (FileNode) listFiles.getSelectedValue();
                setSelectedFileNode(selectedNode);
            }
        }
    }

    private void insertImageFiles() {
        LookupImageFilesSwingWorker task = new LookupImageFilesSwingWorker();
        task.execute();
    }

    private class LookupImageFilesSwingWorker extends SwingWorker<Void, FileNode> {

        @Override
        protected Void doInBackground() throws Exception {
            ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

            repo.eachImage(new Functor<File>() {

                @Override
                public void execute(File file) {
                    FileNode node = new FileNode(file);
                    publish(node);
                }
            });

            return null;
        }

        @Override
        protected void process(List<FileNode> chunks) {
            nodesListModel.addNodes(chunks);
        }

        @Override
        protected void done() {
            panelListInfo.remove(progressBarGetFiles);
            updateFileCountLabel();
            busyPanel.setVisible(false);
        }
    }

    private void updateFileCountLabel() {
        int fileCount = nodesListModel.getSize();
        labelFileCount.setText(Integer.toString(fileCount));
        ComponentUtil.forceRepaint(labelFileCount);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            insertImageFiles();
        }
        super.setVisible(visible);
    }

    @Override
    protected void restoreSizeAndLocation() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.applySize(RepositoryFileBrowserDialog.class.getName(), this);
        prefs.applyLocation(RepositoryFileBrowserDialog.class.getName(), this);
    }

    @Override
    protected void persistSizeAndLocation() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        prefs.setSize(RepositoryFileBrowserDialog.class.getName(), this);
        prefs.setLocation(RepositoryFileBrowserDialog.class.getName(), this);
    }

    private static class FileNodeListCellRenderer extends DefaultListCellRenderer {
        public static final ImageIcon ICON = org.jphototagger.resources.Icons.getIcon("icon_file.png");
        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof FileNode) {
                FileNode fileNode = (FileNode) value;
                setText(fileNode.getDisplayName());
                setIcon(ICON);
            }
            return label;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        panelContent = new org.jdesktop.swingx.JXRootPane();
        panelFilter = UiFactory.panel();
        labelFilterPrompt = UiFactory.label();
        textFieldFilter = UiFactory.textField();
        buttonApplyFilter = UiFactory.button();
        scrollPaneFiles = UiFactory.scrollPane();
        listFiles = new org.jphototagger.lib.lookup.LookupList();
        panelListInfo = UiFactory.panel();
        labelSelectedFilepathPrompt = UiFactory.label();
        labelSelectedFilepath = UiFactory.label();
        labelFileCountPrompt = UiFactory.label();
        labelFileCount = UiFactory.label();
        progressBarGetFiles = UiFactory.progressBar();
        panelInfo = UiFactory.panel();
        panelThumbnail = UiFactory.panel();
        imagePanel = new org.jphototagger.lib.swing.ImagePanel();
        panelDetails = UiFactory.panel();
        panelImageFile = UiFactory.panel();
        booleanLabelImageFileExists = new org.jphototagger.lib.swing.BooleanLabel();
        labelimeImageFileInRepositoryPrompt = UiFactory.label();
        labelimeImageFileInRepository = UiFactory.label();
        labelimeImageFileInFileSystemPrompt = UiFactory.label();
        labelimeImageFileInFileSystem = UiFactory.label();
        labelTimeImageFileWarning = UiFactory.label();
        panelXMPFile = UiFactory.panel();
        booleanLabelXmpFileExists = new org.jphototagger.lib.swing.BooleanLabel();
        labelPromptTimeXmpFileInRepository = UiFactory.label();
        labelTimeXmpFileInRepository = UiFactory.label();
        labelPromptTimeXmpFileInFileSystem = UiFactory.label();
        labelTimeXmpFileInFileSystem = UiFactory.label();
        labelTimeXmpFileWarning = UiFactory.label();
        labelThumbnailSizeInfo = UiFactory.label();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.title")); // NOI18N
        setIconImages(Lookup.getDefault().lookup(Branding.class).getAppIcons());
        setName("Form"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        panelContent.setName("panelContent"); // NOI18N
        panelContent.getContentPane().setLayout(new java.awt.GridBagLayout());

        panelFilter.setName("panelFilter"); // NOI18N
        panelFilter.setLayout(new java.awt.GridBagLayout());

        labelFilterPrompt.setLabelFor(textFieldFilter);
        labelFilterPrompt.setText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.labelFilterPrompt.text")); // NOI18N
        labelFilterPrompt.setName("labelFilterPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelFilter.add(labelFilterPrompt, gridBagConstraints);

        textFieldFilter.setName("textFieldFilter"); // NOI18N
        textFieldFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldFilterKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelFilter.add(textFieldFilter, gridBagConstraints);

        buttonApplyFilter.setText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.buttonApplyFilter.text")); // NOI18N
        buttonApplyFilter.setName("buttonApplyFilter"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelFilter.add(buttonApplyFilter, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        panelContent.getContentPane().add(panelFilter, gridBagConstraints);

        scrollPaneFiles.setName("scrollPaneFiles"); // NOI18N
        scrollPaneFiles.setPreferredSize(UiFactory.dimension(400, 131));

        listFiles.setModel(nodesListModel);
        listFiles.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFiles.setAutoCreateRowSorter(true);
        listFiles.setCellRenderer(new FileNodeListCellRenderer());
        listFiles.setComparator(org.jphototagger.repositoryfilebrowser.FileNodeAscendingComparator.INSTANCE);
        listFiles.setName("listFiles"); // NOI18N
        scrollPaneFiles.setViewportView(listFiles);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.6;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.getContentPane().add(scrollPaneFiles, gridBagConstraints);

        panelListInfo.setName("panelListInfo"); // NOI18N
        panelListInfo.setLayout(new java.awt.GridBagLayout());

        labelSelectedFilepathPrompt.setText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.labelSelectedFilepathPrompt.text")); // NOI18N
        labelSelectedFilepathPrompt.setName("labelSelectedFilepathPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        panelListInfo.add(labelSelectedFilepathPrompt, gridBagConstraints);

        labelSelectedFilepath.setForeground(new java.awt.Color(0, 0, 255));
        labelSelectedFilepath.setName("labelSelectedFilepath"); // NOI18N

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.imageFileIcon}"), labelSelectedFilepath, org.jdesktop.beansbinding.BeanProperty.create("icon"));
        binding.setSourceNullValue(null);
        binding.setSourceUnreadableValue(null);
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.imageFilepath}"), labelSelectedFilepath, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelListInfo.add(labelSelectedFilepath, gridBagConstraints);

        labelFileCountPrompt.setText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.labelFileCountPrompt.text")); // NOI18N
        labelFileCountPrompt.setName("labelFileCountPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        panelListInfo.add(labelFileCountPrompt, gridBagConstraints);

        labelFileCount.setName("labelFileCount"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelListInfo.add(labelFileCount, gridBagConstraints);

        progressBarGetFiles.setName("progressBarGetFiles"); // NOI18N
        progressBarGetFiles.setString(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.progressBarGetFiles.string")); // NOI18N
        progressBarGetFiles.setStringPainted(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = UiFactory.insets(0, 10, 0, 0);
        panelListInfo.add(progressBarGetFiles, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.getContentPane().add(panelListInfo, gridBagConstraints);

        panelInfo.setName("panelInfo"); // NOI18N
        panelInfo.setLayout(new java.awt.GridBagLayout());

        panelThumbnail.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.panelThumbnail.border.title"))); // NOI18N
        panelThumbnail.setName("panelThumbnail"); // NOI18N
        panelThumbnail.setPreferredSize(UiFactory.dimension(150, 150));
        panelThumbnail.setLayout(new java.awt.GridBagLayout());

        imagePanel.setImageIsAbsentText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.imagePanel.imageIsAbsentText")); // NOI18N
        imagePanel.setName("imagePanel"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.thumbnail}"), imagePanel, org.jdesktop.beansbinding.BeanProperty.create("image"));
        bindingGroup.addBinding(binding);

        imagePanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelThumbnail.add(imagePanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 1.0;
        panelInfo.add(panelThumbnail, gridBagConstraints);

        panelDetails.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.panelDetails.border.title"))); // NOI18N
        panelDetails.setName("panelDetails"); // NOI18N
        panelDetails.setPreferredSize(UiFactory.dimension(325, 229));
        panelDetails.setLayout(new java.awt.GridBagLayout());

        panelImageFile.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.panelImageFile.border.title"))); // NOI18N
        panelImageFile.setName("panelImageFile"); // NOI18N
        panelImageFile.setLayout(new java.awt.GridBagLayout());

        booleanLabelImageFileExists.setFalseText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.booleanLabelImageFileExists.falseText")); // NOI18N
        booleanLabelImageFileExists.setTrueText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.booleanLabelImageFileExists.trueText")); // NOI18N
        booleanLabelImageFileExists.setName("booleanLabelImageFileExists"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.imageFileExists}"), booleanLabelImageFileExists, org.jdesktop.beansbinding.BeanProperty.create("isTrue"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelImageFile.add(booleanLabelImageFileExists, gridBagConstraints);

        labelimeImageFileInRepositoryPrompt.setText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.labelimeImageFileInRepositoryPrompt.text")); // NOI18N
        labelimeImageFileInRepositoryPrompt.setName("labelimeImageFileInRepositoryPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 0);
        panelImageFile.add(labelimeImageFileInRepositoryPrompt, gridBagConstraints);

        labelimeImageFileInRepository.setName("labelimeImageFileInRepository"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.timeImageFileInRepository}"), labelimeImageFileInRepository, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelImageFile.add(labelimeImageFileInRepository, gridBagConstraints);

        labelimeImageFileInFileSystemPrompt.setText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.labelimeImageFileInFileSystemPrompt.text")); // NOI18N
        labelimeImageFileInFileSystemPrompt.setName("labelimeImageFileInFileSystemPrompt"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 0);
        panelImageFile.add(labelimeImageFileInFileSystemPrompt, gridBagConstraints);

        labelimeImageFileInFileSystem.setName("labelimeImageFileInFileSystem"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.timeImageFileInFileSystem}"), labelimeImageFileInFileSystem, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 0);
        panelImageFile.add(labelimeImageFileInFileSystem, gridBagConstraints);

        labelTimeImageFileWarning.setForeground(new java.awt.Color(255, 0, 51));
        labelTimeImageFileWarning.setName("labelTimeImageFileWarning"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.timeImageFileWarning}"), labelTimeImageFileWarning, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 10, 3, 3);
        panelImageFile.add(labelTimeImageFileWarning, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 0, 3);
        panelDetails.add(panelImageFile, gridBagConstraints);

        panelXMPFile.setBorder(javax.swing.BorderFactory.createTitledBorder(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.panelXMPFile.border.title"))); // NOI18N
        panelXMPFile.setName("panelXMPFile"); // NOI18N
        panelXMPFile.setLayout(new java.awt.GridBagLayout());

        booleanLabelXmpFileExists.setFalseText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.booleanLabelXmpFileExists.falseText")); // NOI18N
        booleanLabelXmpFileExists.setTrueText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.booleanLabelXmpFileExists.trueText")); // NOI18N
        booleanLabelXmpFileExists.setName("booleanLabelXmpFileExists"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.xmpFileExists}"), booleanLabelXmpFileExists, org.jdesktop.beansbinding.BeanProperty.create("isTrue"));
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelXMPFile.add(booleanLabelXmpFileExists, gridBagConstraints);

        labelPromptTimeXmpFileInRepository.setText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.labelPromptTimeXmpFileInRepository.text")); // NOI18N
        labelPromptTimeXmpFileInRepository.setName("labelPromptTimeXmpFileInRepository"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 0);
        panelXMPFile.add(labelPromptTimeXmpFileInRepository, gridBagConstraints);

        labelTimeXmpFileInRepository.setName("labelTimeXmpFileInRepository"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.timeXmpFileInRepository}"), labelTimeXmpFileInRepository, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 3);
        panelXMPFile.add(labelTimeXmpFileInRepository, gridBagConstraints);

        labelPromptTimeXmpFileInFileSystem.setText(Bundle.getString(getClass(), "RepositoryFileBrowserDialog.labelPromptTimeXmpFileInFileSystem.text")); // NOI18N
        labelPromptTimeXmpFileInFileSystem.setName("labelPromptTimeXmpFileInFileSystem"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 0);
        panelXMPFile.add(labelPromptTimeXmpFileInFileSystem, gridBagConstraints);

        labelTimeXmpFileInFileSystem.setName("labelTimeXmpFileInFileSystem"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.timeXmpFileInFileSystem}"), labelTimeXmpFileInFileSystem, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 3, 0);
        panelXMPFile.add(labelTimeXmpFileInFileSystem, gridBagConstraints);

        labelTimeXmpFileWarning.setForeground(new java.awt.Color(255, 0, 51));
        labelTimeXmpFileWarning.setName("labelTimeXmpFileWarning"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.timeXmpFileWarning}"), labelTimeXmpFileWarning, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = UiFactory.insets(3, 10, 3, 3);
        panelXMPFile.add(labelTimeXmpFileWarning, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = UiFactory.insets(3, 3, 0, 3);
        panelDetails.add(panelXMPFile, gridBagConstraints);

        labelThumbnailSizeInfo.setName("labelThumbnailSizeInfo"); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${imageFileInfo.thumbnailSizeInfo}"), labelThumbnailSizeInfo, org.jdesktop.beansbinding.BeanProperty.create("text"));
        binding.setSourceNullValue("");
        binding.setSourceUnreadableValue("");
        bindingGroup.addBinding(binding);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(5, 5, 5, 5);
        panelDetails.add(labelThumbnailSizeInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(0, 5, 0, 0);
        panelInfo.add(panelDetails, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = UiFactory.insets(5, 0, 0, 0);
        panelContent.getContentPane().add(panelInfo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = UiFactory.insets(10, 10, 10, 10);
        getContentPane().add(panelContent, gridBagConstraints);

        bindingGroup.bind();

        pack();
    }//GEN-END:initComponents

    private void textFieldFilterKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldFilterKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            buttonApplyFilter.doClick();
        }
    }//GEN-LAST:event_textFieldFilterKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jphototagger.lib.swing.BooleanLabel booleanLabelImageFileExists;
    private org.jphototagger.lib.swing.BooleanLabel booleanLabelXmpFileExists;
    private javax.swing.JButton buttonApplyFilter;
    private org.jphototagger.lib.swing.ImagePanel imagePanel;
    private javax.swing.JLabel labelFileCount;
    private javax.swing.JLabel labelFileCountPrompt;
    private javax.swing.JLabel labelFilterPrompt;
    private javax.swing.JLabel labelPromptTimeXmpFileInFileSystem;
    private javax.swing.JLabel labelPromptTimeXmpFileInRepository;
    private javax.swing.JLabel labelSelectedFilepath;
    private javax.swing.JLabel labelSelectedFilepathPrompt;
    private javax.swing.JLabel labelThumbnailSizeInfo;
    private javax.swing.JLabel labelTimeImageFileWarning;
    private javax.swing.JLabel labelTimeXmpFileInFileSystem;
    private javax.swing.JLabel labelTimeXmpFileInRepository;
    private javax.swing.JLabel labelTimeXmpFileWarning;
    private javax.swing.JLabel labelimeImageFileInFileSystem;
    private javax.swing.JLabel labelimeImageFileInFileSystemPrompt;
    private javax.swing.JLabel labelimeImageFileInRepository;
    private javax.swing.JLabel labelimeImageFileInRepositoryPrompt;
    private org.jphototagger.lib.lookup.LookupList listFiles;
    private org.jdesktop.swingx.JXRootPane panelContent;
    private javax.swing.JPanel panelDetails;
    private javax.swing.JPanel panelFilter;
    private javax.swing.JPanel panelImageFile;
    private javax.swing.JPanel panelInfo;
    private javax.swing.JPanel panelListInfo;
    private javax.swing.JPanel panelThumbnail;
    private javax.swing.JPanel panelXMPFile;
    private javax.swing.JProgressBar progressBarGetFiles;
    private javax.swing.JScrollPane scrollPaneFiles;
    private javax.swing.JTextField textFieldFilter;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
