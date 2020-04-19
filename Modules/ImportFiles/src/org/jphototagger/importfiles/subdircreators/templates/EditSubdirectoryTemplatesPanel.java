package org.jphototagger.importfiles.subdircreators.templates;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import org.jphototagger.resources.UiFactory;

/**
 * UI for editing a {@link SubdirectoryTemplates} instance.
 *
 * @author Elmar Baumann
 */
public final class EditSubdirectoryTemplatesPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private final JList<SubdirectoryTemplate> listTemplates = new JList<>();
    private final JButton buttonCreate = UiFactory.button();
    private final JButton buttonEdit = UiFactory.button();
    private final JButton buttonDelete = UiFactory.button();
    private final JButton buttonMoveUp = UiFactory.button();
    private final JButton buttonMoveDown = UiFactory.button();

    public EditSubdirectoryTemplatesPanel() {
        initUi();
    }

    public JList<SubdirectoryTemplate> getListTemplates() {
        return listTemplates;
    }

    public JButton getButtonCreate() {
        return buttonCreate;
    }

    public JButton getButtonEdit() {
        return buttonEdit;
    }

    public JButton getButtonDelete() {
        return buttonDelete;
    }

    public JButton getButtonMoveUp() {
        return buttonMoveUp;
    }

    public JButton getButtonMoveDown() {
        return buttonMoveDown;
    }

    private void initUi() {
        setLayout(new GridBagLayout());

        buttonCreate.setHorizontalAlignment(SwingConstants.LEFT);
        buttonEdit.setHorizontalAlignment(SwingConstants.LEFT);
        buttonDelete.setHorizontalAlignment(SwingConstants.LEFT);
        buttonMoveDown.setHorizontalAlignment(SwingConstants.LEFT);
        buttonMoveUp.setHorizontalAlignment(SwingConstants.LEFT);

        JScrollPane scrollPaneTemplates = UiFactory.scrollPane();

        scrollPaneTemplates.setViewportView(listTemplates);
        scrollPaneTemplates.setPreferredSize(UiFactory.dimension(250, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(scrollPaneTemplates, gbc);

        JPanel panelActions = UiFactory.panel(new GridLayout(0, 1, 0, UiFactory.scale(5)));
        panelActions.add(buttonCreate);
        panelActions.add(buttonEdit);
        panelActions.add(buttonDelete);
        panelActions.add(buttonMoveUp);
        panelActions.add(buttonMoveDown);

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, UiFactory.scale(7), 0, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(panelActions, gbc);
    }
}
