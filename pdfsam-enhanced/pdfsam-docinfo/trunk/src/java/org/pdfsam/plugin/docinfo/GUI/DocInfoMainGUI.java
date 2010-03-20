/*
 * Created on 28-Nov-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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
package org.pdfsam.plugin.docinfo.GUI;

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
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.TitledBorder;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.listeners.CompressCheckBoxItemListener;
import org.pdfsam.guiclient.commons.components.CommonComponentsFactory;
import org.pdfsam.guiclient.commons.components.JPdfVersionCombo;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooser;
import org.pdfsam.guiclient.commons.components.sharedchooser.SharedJFileChooserType;
import org.pdfsam.guiclient.commons.models.AbstractPdfSelectionTableModel;
import org.pdfsam.guiclient.commons.panels.JPdfSelectionPanel;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.guiclient.dto.PdfSelectionTableItem;
import org.pdfsam.guiclient.dto.StringItem;
import org.pdfsam.guiclient.exceptions.LoadJobException;
import org.pdfsam.guiclient.exceptions.SaveJobException;
import org.pdfsam.guiclient.gui.components.JHelpLabel;
import org.pdfsam.guiclient.plugins.interfaces.AbstractPlugablePanel;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.docinfo.listeners.RunButtonActionListener;

/**
 * Plugable JPanel provides a GUI for the docinfo console command.
 * 
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class DocInfoMainGUI extends AbstractPlugablePanel {

    private static final long serialVersionUID = 3187805591202436824L;

    private static final Logger log = Logger.getLogger(DocInfoMainGUI.class.getPackage().getName());

    private SpringLayout destinationPanelLayout;
    private SpringLayout docInfoPanelLayout;
    private final JPanel destinationPanel = new JPanel();
    private final JPanel topPanel = new JPanel();
    private final JPanel docInfoPanel = new JPanel();
    private final JPanel docInfoFieldPanel = new JPanel();
    private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(JPdfSelectionPanel.SINGLE_SELECTABLE_FILE,
            AbstractPdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER, false, false);
    private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(
            CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private final JCheckBox outputCompressedCheck = CommonComponentsFactory.getInstance().createCheckBox(
            CommonComponentsFactory.COMPRESS_CHECKBOX_TYPE);
    private JTextField destinationTextField = CommonComponentsFactory.getInstance().createTextField(
            CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);
    private JTextField titleTextField = CommonComponentsFactory.getInstance().createTextField(
            CommonComponentsFactory.SIMPLE_TEXT_FIELD_TYPE);
    private JTextField authorTextField = CommonComponentsFactory.getInstance().createTextField(
            CommonComponentsFactory.SIMPLE_TEXT_FIELD_TYPE);
    private JTextField subjectTextField = CommonComponentsFactory.getInstance().createTextField(
            CommonComponentsFactory.SIMPLE_TEXT_FIELD_TYPE);
    private JTextField keywordsTextField = CommonComponentsFactory.getInstance().createTextField(
            CommonComponentsFactory.SIMPLE_TEXT_FIELD_TYPE);
    private JHelpLabel destinationHelpLabel;
    private JHelpLabel docInfoHelpLabel;
    private JPdfVersionCombo versionCombo = new JPdfVersionCombo(true);

    private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(
            CommonComponentsFactory.PDF_VERSION_LABEL);
    private final JLabel titleLabel = new JLabel();
    private final JLabel authorLabel = new JLabel();
    private final JLabel subjectLabel = new JLabel();
    private final JLabel keywordsLabel = new JLabel();

    // radio
    private final JRadioButton sameAsSourceRadio = new JRadioButton();
    private final JRadioButton chooseAFileRadio = new JRadioButton();

    private final DocInfoFocusPolicy docinfoFocusPolicy = new DocInfoFocusPolicy();
    // buttons
    private final JButton runButton = CommonComponentsFactory.getInstance().createButton(
            CommonComponentsFactory.RUN_BUTTON_TYPE);
    private final JButton browseButton = CommonComponentsFactory.getInstance().createButton(
            CommonComponentsFactory.BROWSE_BUTTON_TYPE);

    private final EnterDoClickListener runEnterkeyListener = new EnterDoClickListener(runButton);
    private final EnterDoClickListener browseEnterkeyListener = new EnterDoClickListener(browseButton);

    private static final String PLUGIN_AUTHOR = "Andrea Vacondio";
    private static final String PLUGIN_VERSION = "0.0.2e";

    /**
     * Constructor
     */
    public DocInfoMainGUI() {
        super();
        initialize();
    }

    private void initialize() {
        setPanelIcon("/images/docinfo.png");
        setPreferredSize(new Dimension(500, 500));

        setLayout(new GridBagLayout());

        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints topConst = new GridBagConstraints();
        topConst.fill = GridBagConstraints.BOTH;
        topConst.ipady = 5;
        topConst.weightx = 1.0;
        topConst.weighty = 1.0;
        topConst.gridwidth = 3;
        topConst.gridheight = 1;
        topConst.gridx = 0;
        topConst.gridy = 0;
        topPanel.add(selectionPanel, topConst);

        selectionPanel.getLoader().setHook(new SetMetaFieldsHook(this));

        // DOCINFO PANEL
        docInfoPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(Configuration.getInstance()
                .getI18nResourceBundle(), "Document metadata")));
        docInfoPanel.setPreferredSize(new Dimension(200, 160));
        docInfoPanel.setMinimumSize(new Dimension(160, 155));
        docInfoPanelLayout = new SpringLayout();
        docInfoPanel.setLayout(docInfoPanelLayout);

        GroupLayout docinfoLayout = new GroupLayout(docInfoFieldPanel);
        docInfoFieldPanel.setLayout(docinfoLayout);
        docinfoLayout.setAutoCreateGaps(true);

        titleLabel.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Title") + ":");
        authorLabel.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Author")
                + ":");
        subjectLabel.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Subject")
                + ":");
        keywordsLabel.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Keywords")
                + ":");

        StringBuffer sb1 = new StringBuffer();
        sb1.append("<html><body><b>"
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Document metadata")
                + "</b>");
        sb1.append("<p> "
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Set the document title.") + "</p>");
        sb1.append("<p> "
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Set the document author.") + "</p>");
        sb1.append("<p> "
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Set the document subject.") + "</p>");
        sb1.append("<p> "
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Set the document keywords.") + "</p>");
        sb1.append("</body></html>");
        docInfoHelpLabel = new JHelpLabel(sb1.toString(), true);

        docInfoFieldPanel.add(titleLabel);
        docInfoFieldPanel.add(titleTextField);
        docInfoFieldPanel.add(authorLabel);
        docInfoFieldPanel.add(authorTextField);
        docInfoFieldPanel.add(subjectLabel);
        docInfoFieldPanel.add(subjectTextField);
        docInfoFieldPanel.add(keywordsLabel);
        docInfoFieldPanel.add(keywordsTextField);
        docInfoFieldPanel.add(docInfoHelpLabel);

        docinfoLayout.setHorizontalGroup(docinfoLayout.createSequentialGroup().addGroup(
                docinfoLayout.createParallelGroup().addComponent(titleLabel).addComponent(authorLabel).addComponent(
                        subjectLabel).addComponent(keywordsLabel)).addGroup(
                docinfoLayout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(titleTextField)
                        .addComponent(authorTextField).addComponent(subjectTextField).addComponent(keywordsTextField)));

        docinfoLayout.setVerticalGroup(docinfoLayout.createSequentialGroup().addGroup(
                docinfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(titleLabel)
                        .addComponent(titleTextField)).addGroup(
                docinfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(authorLabel)
                        .addComponent(authorTextField)).addGroup(
                docinfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(subjectLabel)
                        .addComponent(subjectTextField)).addGroup(
                docinfoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(keywordsLabel)
                        .addComponent(keywordsTextField)));

        docInfoPanel.add(docInfoFieldPanel);
        docInfoPanel.add(docInfoHelpLabel);

        topConst.fill = GridBagConstraints.HORIZONTAL;
        topConst.weightx = 0.0;
        topConst.weighty = 0.0;
        topConst.gridwidth = 3;
        topConst.gridheight = 1;
        topConst.gridx = 0;
        topConst.gridy = 1;
        topPanel.add(docInfoPanel, topConst);

        // DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(GettextResource.gettext(Configuration
                .getInstance().getI18nResourceBundle(), "Destination output file"));
        destinationPanel.setBorder(titledBorder);
        destinationPanel.setPreferredSize(new Dimension(200, 160));
        destinationPanel.setMinimumSize(new Dimension(160, 150));

        // END_DESTINATION_PANEL

        // DESTINATION_RADIOS
        sameAsSourceRadio.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                "Same as source"));
        destinationPanel.add(sameAsSourceRadio);

        chooseAFileRadio.setSelected(true);
        chooseAFileRadio.setText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                "Choose a file"));
        destinationPanel.add(chooseAFileRadio);
        final ButtonGroup outputRadioGroup = new ButtonGroup();
        outputRadioGroup.add(sameAsSourceRadio);
        outputRadioGroup.add(chooseAFileRadio);
        // END_DESTINATION_RADIOS

        destinationPanel.add(destinationTextField);
        topConst.fill = GridBagConstraints.HORIZONTAL;
        topConst.weightx = 0.0;
        topConst.weighty = 0.0;
        topConst.gridwidth = 3;
        topConst.gridheight = 1;
        topConst.gridx = 0;
        topConst.gridy = 2;
        topPanel.add(destinationPanel, topConst);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.ipady = 5;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        add(topPanel, c);
        // BROWSE_BUTTON
        browseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = SharedJFileChooser.getInstance(SharedJFileChooserType.PDF_FILE,
                        JFileChooser.FILES_ONLY, destinationTextField.getText());
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

        overwriteCheckbox.setSelected(true);
        destinationPanel.add(overwriteCheckbox);

        outputCompressedCheck.addItemListener(new CompressCheckBoxItemListener(versionCombo));
        outputCompressedCheck.setSelected(true);

        destinationPanel.add(outputCompressedCheck);
        destinationPanel.add(versionCombo);
        destinationPanel.add(outputVersionLabel);

        // HELP_LABEL_DESTINATION
        String helpTextDest = "<html><body><b>"
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Destination output file")
                + "</b>"
                + "<p>"
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Browse or enter the full path to the destination output file.")
                + "</p>"
                + "<p>"
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Check the box if you want to overwrite the output file if it already exists.")
                + "</p>"
                + "<p>"
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Check the box if you want compressed output files.")
                + " "
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "PDF version 1.5 or above.")
                + "</p>"
                + "<p>"
                + GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                        "Set the pdf version of the ouput document.") + "</p>" + "</body></html>";
        destinationHelpLabel = new JHelpLabel(helpTextDest, true);
        destinationPanel.add(destinationHelpLabel);
        // END_HELP_LABEL_DESTINATION

        // RUN_BUTTON
        runButton.addActionListener(new RunButtonActionListener(this));
        runButton.setToolTipText(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                "Set metadata on the selected file"));
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

        // RADIO_LISTENERS
        sameAsSourceRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                destinationTextField.setEnabled(false);
                browseButton.setEnabled(false);
            }
        });

        chooseAFileRadio.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                destinationTextField.setEnabled(true);
                browseButton.setEnabled(true);
            }
        });
        // END_RADIO_LISTENERS

        setLayout();
    }

    /**
     * Set plugin layout for each component
     * 
     */
    private void setLayout() {
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, sameAsSourceRadio, 25, SpringLayout.NORTH,
                sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, sameAsSourceRadio, 0, SpringLayout.NORTH,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, sameAsSourceRadio, 10, SpringLayout.WEST,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, chooseAFileRadio, 0, SpringLayout.SOUTH,
                sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, chooseAFileRadio, 0, SpringLayout.NORTH,
                sameAsSourceRadio);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, chooseAFileRadio, 20, SpringLayout.EAST,
                sameAsSourceRadio);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationTextField, 50, SpringLayout.NORTH,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, destinationTextField, 30, SpringLayout.NORTH,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationTextField, -105, SpringLayout.EAST,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, destinationTextField, 5, SpringLayout.WEST,
                destinationPanel);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, overwriteCheckbox, 17, SpringLayout.NORTH,
                overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, overwriteCheckbox, 5, SpringLayout.SOUTH,
                destinationTextField);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, overwriteCheckbox, 0, SpringLayout.WEST,
                destinationTextField);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputCompressedCheck, 17, SpringLayout.NORTH,
                outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputCompressedCheck, 5, SpringLayout.SOUTH,
                overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputCompressedCheck, 0, SpringLayout.WEST,
                destinationTextField);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputVersionLabel, 17, SpringLayout.NORTH,
                outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputVersionLabel, 8, SpringLayout.SOUTH,
                outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputVersionLabel, 0, SpringLayout.WEST,
                destinationTextField);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, versionCombo, 0, SpringLayout.SOUTH,
                outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseButton, 25, SpringLayout.NORTH, browseButton);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, browseButton, -10, SpringLayout.EAST, destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseButton, 0, SpringLayout.NORTH,
                destinationTextField);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, browseButton, -88, SpringLayout.EAST, browseButton);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST,
                destinationPanel);

        docInfoPanelLayout.putConstraint(SpringLayout.NORTH, docInfoFieldPanel, 5, SpringLayout.NORTH, docInfoPanel);
        docInfoPanelLayout.putConstraint(SpringLayout.EAST, docInfoFieldPanel, -5, SpringLayout.EAST, docInfoPanel);
        docInfoPanelLayout.putConstraint(SpringLayout.WEST, docInfoFieldPanel, 5, SpringLayout.WEST, docInfoPanel);

        docInfoPanelLayout.putConstraint(SpringLayout.SOUTH, docInfoHelpLabel, -1, SpringLayout.SOUTH, docInfoPanel);
        docInfoPanelLayout.putConstraint(SpringLayout.EAST, docInfoHelpLabel, -1, SpringLayout.EAST, docInfoPanel);

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
        return GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Document Info");
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
        return (FocusTraversalPolicy) docinfoFocusPolicy;

    }

    public Node getJobNode(Node arg0, boolean savePasswords) throws SaveJobException {
        try {
            if (arg0 != null) {
                Element fileSource = ((Element) arg0).addElement("source");
                PdfSelectionTableItem[] items = selectionPanel.getTableRows();
                if (items != null && items.length > 0) {
                    fileSource.addAttribute("value", items[0].getInputFile().getAbsolutePath());
                    if (savePasswords) {
                        fileSource.addAttribute("password", items[0].getPassword());
                    }
                }

                Element titleElement = ((Element) arg0).addElement("title");
                titleElement.addAttribute("value", titleTextField.getText());

                Element authorElement = ((Element) arg0).addElement("author");
                authorElement.addAttribute("value", authorTextField.getText());

                Element subjectElement = ((Element) arg0).addElement("subject");
                subjectElement.addAttribute("value", subjectTextField.getText());

                Element keywordsElement = ((Element) arg0).addElement("keywords");
                keywordsElement.addAttribute("value", keywordsTextField.getText());

                Element fileDestination = ((Element) arg0).addElement("destination");
                fileDestination.addAttribute("value", destinationTextField.getText());

                Element fileOverwrite = ((Element) arg0).addElement("overwrite");
                fileOverwrite.addAttribute("value", overwriteCheckbox.isSelected() ? TRUE : FALSE);

                Element fileCompress = ((Element) arg0).addElement("compressed");
                fileCompress.addAttribute("value", outputCompressedCheck.isSelected() ? TRUE : FALSE);

                Element pdfVersion = ((Element) arg0).addElement("pdfversion");
                pdfVersion.addAttribute("value", ((StringItem) versionCombo.getSelectedItem()).getId());

            }
            return arg0;
        } catch (Exception ex) {
            throw new SaveJobException(ex.getMessage(), ex);
        }
    }

    public void loadJobNode(Node arg0) throws LoadJobException {
        try {
            Node fileSource = (Node) arg0.selectSingleNode("source/@value");
            if (fileSource != null && fileSource.getText().length() > 0) {
                Node filePwd = (Node) arg0.selectSingleNode("source/@password");
                String password = null;
                if (filePwd != null && filePwd.getText().length() > 0) {
                    password = filePwd.getText();
                }
                selectionPanel.getLoader().addFile(new File(fileSource.getText()), password);
            }
            Node titleNode = (Node) arg0.selectSingleNode("title/@value");
            if (titleNode != null) {
                titleTextField.setText(titleNode.getText());
            }

            Node authoreNode = (Node) arg0.selectSingleNode("author/@value");
            if (authoreNode != null) {
                authorTextField.setText(authoreNode.getText());
            }

            Node subjectNode = (Node) arg0.selectSingleNode("subject/@value");
            if (subjectNode != null) {
                subjectTextField.setText(subjectNode.getText());
            }

            Node keywordsNode = (Node) arg0.selectSingleNode("keywords/@value");
            if (keywordsNode != null) {
                keywordsTextField.setText(keywordsNode.getText());
            }

            Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
            if (fileDestination != null && fileDestination.getText().length() > 0) {
                destinationTextField.setText(fileDestination.getText());
                chooseAFileRadio.doClick();
            } else {
                sameAsSourceRadio.doClick();
            }

            Node fileOverwrite = (Node) arg0.selectSingleNode("overwrite/@value");
            if (fileOverwrite != null) {
                overwriteCheckbox.setSelected(fileOverwrite.getText().equals("true"));
            }

            Node fileCompressed = (Node) arg0.selectSingleNode("compressed/@value");
            if (fileCompressed != null && TRUE.equals(fileCompressed.getText())) {
                outputCompressedCheck.doClick();
            }

            Node pdfVersion = (Node) arg0.selectSingleNode("pdfversion/@value");
            if (pdfVersion != null) {
                for (int i = 0; i < versionCombo.getItemCount(); i++) {
                    if (((StringItem) versionCombo.getItemAt(i)).getId().equals(pdfVersion.getText())) {
                        versionCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }

            log.info(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),
                    "Rotate section loaded."));
        } catch (Exception ex) {
            log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(), "Error: "), ex);
        }
    }

    /**
     * Focus policy for docinfo panel
     * 
     * @author Andrea Vacondio
     * 
     */
    public class DocInfoFocusPolicy extends FocusTraversalPolicy {
        public DocInfoFocusPolicy() {
            super();
        }

        public Component getComponentAfter(Container CycleRootComp, Component aComponent) {
            if (aComponent.equals(selectionPanel.getAddFileButton())) {
                return selectionPanel.getClearButton();
            } else if (aComponent.equals(selectionPanel.getClearButton())) {
                return titleTextField;
            } else if (aComponent.equals(titleTextField)) {
                return authorTextField;
            } else if (aComponent.equals(authorTextField)) {
                return subjectTextField;
            } else if (aComponent.equals(subjectTextField)) {
                return keywordsTextField;
            } else if (aComponent.equals(keywordsTextField)) {
                return sameAsSourceRadio;
            } else if (aComponent.equals(sameAsSourceRadio)) {
                return chooseAFileRadio;
            } else if (aComponent.equals(chooseAFileRadio)) {
                if (destinationTextField.isEnabled()) {
                    return destinationTextField;
                } else {
                    return overwriteCheckbox;
                }
            } else if (aComponent.equals(destinationTextField)) {
                return browseButton;
            } else if (aComponent.equals(browseButton)) {
                return overwriteCheckbox;
            } else if (aComponent.equals(overwriteCheckbox)) {
                return outputCompressedCheck;
            } else if (aComponent.equals(outputCompressedCheck)) {
                return versionCombo;
            } else if (aComponent.equals(versionCombo)) {
                return runButton;
            } else if (aComponent.equals(runButton)) {
                return selectionPanel.getAddFileButton();
            }
            return selectionPanel.getAddFileButton();
        }

        public Component getComponentBefore(Container CycleRootComp, Component aComponent) {

            if (aComponent.equals(runButton)) {
                return versionCombo;
            } else if (aComponent.equals(versionCombo)) {
                return outputCompressedCheck;
            } else if (aComponent.equals(outputCompressedCheck)) {
                return overwriteCheckbox;
            } else if (aComponent.equals(overwriteCheckbox)) {
                if (browseButton.isEnabled()) {
                    return browseButton;
                } else {
                    return chooseAFileRadio;
                }
            } else if (aComponent.equals(browseButton)) {
                return destinationTextField;
            } else if (aComponent.equals(destinationTextField)) {
                return chooseAFileRadio;
            } else if (aComponent.equals(chooseAFileRadio)) {
                return sameAsSourceRadio;
            } else if (aComponent.equals(sameAsSourceRadio)) {
                return keywordsTextField;
            } else if (aComponent.equals(keywordsTextField)) {
                return subjectTextField;
            } else if (aComponent.equals(subjectTextField)) {
                return authorTextField;
            } else if (aComponent.equals(authorTextField)) {
                return titleTextField;
            } else if (aComponent.equals(titleTextField)) {
                return selectionPanel.getClearButton();
            } else if (aComponent.equals(selectionPanel.getClearButton())) {
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
        versionCombo.resetComponent();
        outputCompressedCheck.setSelected(false);
        overwriteCheckbox.setSelected(false);
        chooseAFileRadio.setSelected(true);
    }

    /**
     * @return the selectionPanel
     */
    public JPdfSelectionPanel getSelectionPanel() {
        return selectionPanel;
    }

    /**
     * @return the overwriteCheckbox
     */
    public JCheckBox getOverwriteCheckbox() {
        return overwriteCheckbox;
    }

    /**
     * @return the outputCompressedCheck
     */
    public JCheckBox getOutputCompressedCheck() {
        return outputCompressedCheck;
    }

    /**
     * @return the destinationTextField
     */
    public JTextField getDestinationTextField() {
        return destinationTextField;
    }

    /**
     * @return the versionCombo
     */
    public JPdfVersionCombo getVersionCombo() {
        return versionCombo;
    }

    /**
     * @return the titleTextField
     */
    public JTextField getTitleTextField() {
        return titleTextField;
    }

    /**
     * @return the authorTextField
     */
    public JTextField getAuthorTextField() {
        return authorTextField;
    }

    /**
     * @return the subjectTextField
     */
    public JTextField getSubjectTextField() {
        return subjectTextField;
    }

    /**
     * @return the keywordsTextField
     */
    public JTextField getKeywordsTextField() {
        return keywordsTextField;
    }

    /**
     * @return the sameAsSourceRadio
     */
    public JRadioButton getSameAsSourceRadio() {
        return sameAsSourceRadio;
    }

}
