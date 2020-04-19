package org.jphototagger.importfiles.subdircreators.templates;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.UiFactory;

/**
 * UI for editing a single {@link SubdirectoryTemplate}.
 *
 * @author Elmar Baumann
 */
public final class EditSubdirectoryTemplatePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel labelTextFieldTemplate = UiFactory.label(Bundle.getString(EditSubdirectoryTemplatePanel.class, "EditSubdirectoryTemplatesPanel.LabelTextFieldTemplate.Text"));
    private final JLabel labelTextFieldDisplayName = UiFactory.label(Bundle.getString(EditSubdirectoryTemplatePanel.class, "EditSubdirectoryTemplatesPanel.LabelTextFieldDisplayName.Text"));
    private final JLabel labelExample = UiFactory.label();
    private final JTextField textFieldTemplate = UiFactory.textField();
    private final JTextField textFieldDisplayName = UiFactory.textField();
    private final JButton buttonAddYear = UiFactory.button(Bundle.getString(EditSubdirectoryTemplatePanel.class, "EditSubdirectoryTemplatesPanel.ButtonAddYear.Text"));
    private final JButton buttonAddMonth = UiFactory.button(Bundle.getString(EditSubdirectoryTemplatePanel.class, "EditSubdirectoryTemplatesPanel.ButtonAddMonth.Text"));
    private final JButton buttonAddDay = UiFactory.button(Bundle.getString(EditSubdirectoryTemplatePanel.class, "EditSubdirectoryTemplatesPanel.ButtonAddDay.Text"));
    private final JButton buttonAddFileSeparator = UiFactory.button(Bundle.getString(EditSubdirectoryTemplatePanel.class, "EditSubdirectoryTemplatesPanel.ButtonAddFileSeparator.Text"));

    public EditSubdirectoryTemplatePanel() {
        initUi();
    }

    public JLabel getLabelExample() {
        return labelExample;
    }

    public JTextField getTextFieldTemplate() {
        return textFieldTemplate;
    }

    public JTextField getTextFieldDisplayName() {
        return textFieldDisplayName;
    }

    public JButton getButtonAddYear() {
        return buttonAddYear;
    }

    public JButton getButtonAddMonth() {
        return buttonAddMonth;
    }

    public JButton getButtonAddDay() {
        return buttonAddDay;
    }

    public JButton getButtonAddFileSeparator() {
        return buttonAddFileSeparator;
    }

    private void initUi() {
        setLayout(new GridBagLayout());

        labelTextFieldDisplayName.setLabelFor(textFieldDisplayName);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        add(labelTextFieldDisplayName, gbc);

        textFieldDisplayName.setColumns(20);

        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = UiFactory.insets(0, 5, 0, 0);
        add(textFieldDisplayName, gbc);

        JPanel panelActions = UiFactory.panel(new FlowLayout(FlowLayout.LEFT, UiFactory.scale(5), UiFactory.scale(5)));
        panelActions.setBorder(BorderFactory.createTitledBorder(Bundle.getString(EditSubdirectoryTemplatePanel.class, "EditSubdirectoryTemplatesPanel.PanelActions.BorderTitle")));
        panelActions.add(buttonAddYear);
        panelActions.add(buttonAddMonth);
        panelActions.add(buttonAddDay);
        panelActions.add(buttonAddFileSeparator);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = UiFactory.insets(7, 0, 0, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(panelActions, gbc);

        labelTextFieldTemplate.setLabelFor(textFieldTemplate);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = UiFactory.insets(5, 0, 0, 0);
        add(labelTextFieldTemplate, gbc);

        textFieldTemplate.setColumns(20);

        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = UiFactory.insets(5, 5, 0, 0);
        add(textFieldTemplate, gbc);

        labelExample.setBorder(BorderFactory.createTitledBorder(Bundle.getString(EditSubdirectoryTemplatePanel.class, "EditSubdirectoryTemplatesPanel.LabelExample.BorderTitle")));

        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = UiFactory.insets(7, 0, 0, 0);
        add(labelExample, gbc);
    }
}
