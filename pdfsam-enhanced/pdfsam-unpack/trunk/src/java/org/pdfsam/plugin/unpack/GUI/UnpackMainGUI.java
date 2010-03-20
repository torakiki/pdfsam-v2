/*
 * Created on 29-Feb-2008
 * Copyright (C) 2006 by Andrea Vacondio.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.plugin.unpack.GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.actions.SetOutputPathSelectionTableAction;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooser;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooserType;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.unpack.listeners.RunButtonActionListener;

/**
 * Plugable JPanel provides a GUI for unpack functions.
 * 
 * @author Andrea Vacondio
 * @see org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel
 * @see javax.swing.JPanel
 */
public class UnpackMainGUI extends AbstractPlugablePanel {

    private static final long serialVersionUID = -3486940947325560929L;

    private static final Logger log = Logger.getLogger(UnpackMainGUI.class.getPackage().getName());

    private SpringLayout destinationPanelLayout;
    private JPanel destinationPanel = new JPanel();
    private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(
            JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER,
            AbstractPdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER, true, false);
    private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(
            CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private JTextField destinationTextField = CommonComponentsFactory.getInstance().createTextField(
            CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);
    private JHelpLabel destinationHelpLabel;
    private Configuration config;

    private final UnpackFocusPolicy unpackFocusPolicy = new UnpackFocusPolicy();
    // buttons
    private final JButton runButton = CommonComponentsFactory.getInstance().createButton(
            CommonComponentsFactory.RUN_BUTTON_TYPE);
    private final JButton browseButton = CommonComponentsFactory.getInstance().createButton(
            CommonComponentsFactory.BROWSE_BUTTON_TYPE);

    private final EnterDoClickListener runEnterkeyListener = new EnterDoClickListener(runButton);
    private final EnterDoClickListener browseEnterkeyListener = new EnterDoClickListener(browseButton);

    private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
    private static final String PLUGIN_VERSION = "0.1.0e";

    /**
     * Constructor
     */
    public UnpackMainGUI() {
        super();
        initialize();
    }

    private void initialize() {
        config = Configuration.getInstance();
        setPanelIcon("/images/unpack.png");
        setPreferredSize(new Dimension(500, 450));

        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 5;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        add(selectionPanel, c);

        selectionPanel.addPopupMenuAction(new SetOutputPathSelectionTableAction(selectionPanel, destinationTextField,
                null));
        selectionPanel.setFullAccessRequired(false);

        // DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(GettextResource.gettext(config
                .getI18nResourceBundle(), "Destination folder"));
        destinationPanel.setBorder(titledBorder);
        destinationPanel.setPreferredSize(new Dimension(200, 110));
        destinationPanel.setMinimumSize(new Dimension(150, 100));

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 1.0;
        c.weighty = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        add(destinationPanel, c);

        // END_DESTINATION_PANEL

        destinationPanel.add(destinationTextField);

        // BROWSE_BUTTON
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = SharedJFileChooser.getInstance(SharedJFileChooserType.NO_FILTER,
                        JFileChooser.DIRECTORIES_ONLY, destinationTextField.getText());
                if (fileChooser.showOpenDialog(browseButton.getParent()) == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    if (chosenFile != null) {
                        destinationTextField.setText(chosenFile.getAbsolutePath());
                    }
                }
            }
        });
        destinationPanel.add(browseButton);
        // END_BROWSE_BUTTON

        // CHECK_BOX
        destinationPanel.add(overwriteCheckbox);

        // END_CHECK_BOX
        // HELP_LABEL_DESTINATION
        String helpTextDest = "<html><body><b>"
                + GettextResource.gettext(config.getI18nResourceBundle(), "Destination output directory")
                + "</b>"
                + "<p>"
                + GettextResource.gettext(config.getI18nResourceBundle(),
                        "To choose a folder browse or enter the full path to the destination output directory.")
                + "</p>"
                + "<p>"
                + GettextResource.gettext(config.getI18nResourceBundle(),
                        "Check the box if you want to overwrite the output files if they already exist.") + "</p>"
                + "</body></html>";
        destinationHelpLabel = new JHelpLabel(helpTextDest, true);
        destinationPanel.add(destinationHelpLabel);
        // END_HELP_LABEL_DESTINATION
        // RUN_BUTTON
        runButton.addActionListener(new RunButtonActionListener(this));
        runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(), "Unpack selected files"));
        runButton.setSize(new Dimension(88, 25));

        c.fill = GridBagConstraints.NONE;
        c.ipadx = 5;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        c.gridwidth = 1;
        c.gridx = 2;
        c.gridy = 2;
        c.insets = new Insets(10, 10, 10, 10);
        add(runButton, c);
        // END_RUN_BUTTON

        destinationTextField.addKeyListener(runEnterkeyListener);
        runButton.addKeyListener(runEnterkeyListener);
        browseButton.addKeyListener(browseEnterkeyListener);
        setLayout();
    }

    /**
     * Set plugin layout for each component
     * 
     */
    private void setLayout() {

        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationTextField, -105, SpringLayout.EAST,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, destinationTextField, 10, SpringLayout.NORTH,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationTextField, 30, SpringLayout.NORTH,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, destinationTextField, 5, SpringLayout.WEST,
                destinationPanel);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, overwriteCheckbox, 17, SpringLayout.NORTH,
                overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, overwriteCheckbox, 5, SpringLayout.SOUTH,
                destinationTextField);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, overwriteCheckbox, 0, SpringLayout.WEST,
                destinationTextField);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseButton, 25, SpringLayout.NORTH, browseButton);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, browseButton, -10, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseButton, 0, SpringLayout.NORTH,
                destinationTextField);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, browseButton, -88, SpringLayout.EAST, browseButton);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST,
                destinationPanel);

    }

    /**
     * @return the Plugin author
     */
    public String getPluginAuthor() {
        return PLUGIN_AUTHOR;
    }

    /**
     * @return the Plugin name
     */
    public String getPluginName() {
        return GettextResource.gettext(config.getI18nResourceBundle(), "Unpack");
    }

    /**
     * @return the Plugin version
     */
    public String getVersion() {
        return PLUGIN_VERSION;
    }

    /**
     * @return the FocusTraversalPolicy associated with the plugin
     */
    public FocusTraversalPolicy getFocusPolicy() {
        return (FocusTraversalPolicy) unpackFocusPolicy;

    }

    public Node getJobNode(Node arg0, boolean savePasswords) throws SaveJobException {
        try {
            if (arg0 != null) {
                Element filelist = ((Element) arg0).addElement("filelist");
                PdfSelectionTableItem[] items = selectionPanel.getTableRows();
                for (int i = 0; i < items.length; i++) {
                    Element fileNode = ((Element) filelist).addElement("file");
                    fileNode.addAttribute("name", items[i].getInputFile().getAbsolutePath());
                    if (savePasswords) {
                        fileNode.addAttribute("password", items[i].getPassword());
                    }
                }

                Element fileDestination = ((Element) arg0).addElement("destination");
                fileDestination.addAttribute("value", destinationTextField.getText());

                Element fileOverwrite = ((Element) arg0).addElement("overwrite");
                fileOverwrite.addAttribute("value", overwriteCheckbox.isSelected() ? "true" : "false");

            }
            return arg0;
        } catch (Exception ex) {
            throw new SaveJobException(ex.getMessage(), ex);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadJobNode(Node arg0) throws LoadJobException {
        try {
            List fileList = arg0.selectNodes("filelist/file");
            for (int i = 0; fileList != null && i < fileList.size(); i++) {
                Node fileNode = (Node) fileList.get(i);
                if (fileNode != null) {
                    Node fileName = (Node) fileNode.selectSingleNode("@name");
                    if (fileName != null && fileName.getText().length() > 0) {
                        Node filePwd = (Node) fileNode.selectSingleNode("@password");
                        selectionPanel.getLoader().addFile(new File(fileName.getText()),
                                (filePwd != null) ? filePwd.getText() : null);
                    }
                }
            }

            Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
            if (fileDestination != null) {
                destinationTextField.setText(fileDestination.getText());
            }

            Node fileOverwrite = (Node) arg0.selectSingleNode("overwrite/@value");
            if (fileOverwrite != null) {
                overwriteCheckbox.setSelected(fileOverwrite.getText().equals("true"));
            }

            log.info(GettextResource.gettext(config.getI18nResourceBundle(), "Unpack section loaded."));
        } catch (Exception ex) {
            log.error(GettextResource.gettext(config.getI18nResourceBundle(), "Error: "), ex);
        }
    }

    /**
     * 
     * @author Andrea Vacondio Focus policy for unpack panel
     * 
     */
    public class UnpackFocusPolicy extends FocusTraversalPolicy {
        public UnpackFocusPolicy() {
            super();
        }

        public Component getComponentAfter(Container CycleRootComp, Component aComponent) {
            if (aComponent.equals(selectionPanel.getAddFileButton())) {
                return selectionPanel.getRemoveFileButton();
            } else if (aComponent.equals(selectionPanel.getRemoveFileButton())) {
                return selectionPanel.getClearButton();
            } else if (aComponent.equals(selectionPanel.getClearButton())) {
                return destinationTextField;
            } else if (aComponent.equals(destinationTextField)) {
                return browseButton;
            } else if (aComponent.equals(browseButton)) {
                return overwriteCheckbox;
            } else if (aComponent.equals(overwriteCheckbox)) {
                return runButton;
            } else if (aComponent.equals(runButton)) {
                return selectionPanel.getAddFileButton();
            }
            return selectionPanel.getAddFileButton();
        }

        public Component getComponentBefore(Container CycleRootComp, Component aComponent) {

            if (aComponent.equals(runButton)) {
                return overwriteCheckbox;
            } else if (aComponent.equals(overwriteCheckbox)) {
                return browseButton;
            } else if (aComponent.equals(browseButton)) {
                return destinationTextField;
            } else if (aComponent.equals(destinationTextField)) {
                return selectionPanel.getClearButton();
            } else if (aComponent.equals(selectionPanel.getClearButton())) {
                return selectionPanel.getRemoveFileButton();
            } else if (aComponent.equals(selectionPanel.getRemoveFileButton())) {
                return selectionPanel.getAddFileButton();
            } else if (aComponent.equals(selectionPanel.getAddFileButton())) {
                return runButton;
            }
            return selectionPanel.getAddFileButton();
        }

        public Component getDefaultComponent(Container CycleRootComp) {
            return selectionPanel.getAddFileButton();
        }

        public Component getLastComponent(Container CycleRootComp) {
            return runButton;
        }

        public Component getFirstComponent(Container CycleRootComp) {
            return selectionPanel.getAddFileButton();
        }
    }

    public void resetPanel() {
        selectionPanel.clearSelectionTable();
        destinationTextField.setText("");
        overwriteCheckbox.setSelected(false);
    }

    /**
     * @return the overwriteCheckbox
     */
    public JCheckBox getOverwriteCheckbox() {
        return overwriteCheckbox;
    }

    /**
     * @return the destinationTextField
     */
    public JTextField getDestinationTextField() {
        return destinationTextField;
    }

    /**
     * @return the selectionPanel
     */
    public JPdfSelectionPanel getSelectionPanel() {
        return selectionPanel;
    }

}
