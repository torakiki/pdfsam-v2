/*
 * Created on 14-Dec-2006
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
package org.pdfsam.plugin.encrypt.GUI;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.pdfsam.guiclient.business.listeners.EnterDoClickListener;
import org.pdfsam.guiclient.commons.business.actions.SetOutputPathSelectionTableAction;
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
import org.pdfsam.guiclient.utils.EncryptionUtility;
import org.pdfsam.i18n.GettextResource;
import org.pdfsam.plugin.encrypt.listeners.EncryptionTypeComboActionListener;
import org.pdfsam.plugin.encrypt.listeners.RunButtonActionListener;

/**
 * Plugable JPanel provides a GUI for encrypt functions.
 * 
 * @author Andrea Vacondio
 * @see javax.swing.JPanel
 */
public class EncryptMainGUI extends AbstractPlugablePanel {

    private static final long serialVersionUID = -190757698317016422L;

    private static final Logger log = Logger.getLogger(EncryptMainGUI.class.getPackage().getName());

    private JTextField outPrefixTextField = CommonComponentsFactory.getInstance().createTextField(
            CommonComponentsFactory.PREFIX_TEXT_FIELD_TYPE);
    private SpringLayout encryptPanelLayout;
    private SpringLayout destinationPanelLayout;
    private SpringLayout labelsPanelLayout;
    private SpringLayout fieldsPanelLayout;
    private SpringLayout optionsPanelLayout;
    private JTextField destFolderText = CommonComponentsFactory.getInstance().createTextField(
            CommonComponentsFactory.DESTINATION_TEXT_FIELD_TYPE);;
    private JTextField userPwdField;
    private JTextField ownerPwdField;
    private JComboBox encryptType;
    private JHelpLabel prefixHelpLabel;
    private JHelpLabel destinationHelpLabel;
    private JPdfVersionCombo versionCombo = new JPdfVersionCombo();
    private JPdfSelectionPanel selectionPanel = new JPdfSelectionPanel(
            JPdfSelectionPanel.UNLIMTED_SELECTABLE_FILE_NUMBER,
            AbstractPdfSelectionTableModel.DEFAULT_SHOWED_COLUMNS_NUMBER, true, false);

    private Configuration config;

    // button
    private final JButton browseDestButton = CommonComponentsFactory.getInstance().createButton(
            CommonComponentsFactory.BROWSE_BUTTON_TYPE);
    private final JButton runButton = CommonComponentsFactory.getInstance().createButton(
            CommonComponentsFactory.RUN_BUTTON_TYPE);
    // key_listeners
    private final EnterDoClickListener browseEnterKeyListener = new EnterDoClickListener(browseDestButton);
    private final EnterDoClickListener runEnterKeyListener = new EnterDoClickListener(runButton);

    // encrypt_check
    private final JCheckBox[] permissionsCheck = new JCheckBox[8];
    private final JCheckBox allowAllCheck = new JCheckBox();
    private final JCheckBox overwriteCheckbox = CommonComponentsFactory.getInstance().createCheckBox(
            CommonComponentsFactory.OVERWRITE_CHECKBOX_TYPE);
    private final JCheckBox outputCompressedCheck = CommonComponentsFactory.getInstance().createCheckBox(
            CommonComponentsFactory.COMPRESS_CHECKBOX_TYPE);

    // focus policy
    private final EncryptFocusPolicy encryptfocusPolicy = new EncryptFocusPolicy();

    // panels
    private final JPanel encryptOptionsPanel = new JPanel();
    private final JPanel destinationPanel = new JPanel();
    private final JPanel outputOptionsPanel = new JPanel();
    private final JPanel encryptOptsPanel = new JPanel();
    private final JPanel encryptPwdsPanel = new JPanel();
    private final JPanel mainOptionsPanel = new JPanel();
    private final JPanel labelsPanel = new JPanel();
    private final JPanel fieldsPanel = new JPanel();

    // labels
    private final JLabel outPrefixLabel = new JLabel();
    private final JLabel ownerPwdLabel = new JLabel();
    private final JLabel userPwdLabel = new JLabel();
    private final JLabel encryptTypeLabel = new JLabel();
    private final JLabel outputVersionLabel = CommonComponentsFactory.getInstance().createLabel(
            CommonComponentsFactory.PDF_VERSION_LABEL);

    private final String PLUGIN_AUTHOR = "Andrea Vacondio";
    private final String PLUGIN_VERSION = "0.3.2e";

    public final static int DPRINT = 0;
    public final static int PRINT = 1;
    public final static int COPY = 2;
    public final static int MODIFY = 3;
    public final static int FILL = 4;
    public final static int SCREEN = 5;
    public final static int ASSEMBLY = 6;
    public final static int ANNOTATION = 7;

    /**
     * Constructor
     * 
     */
    public EncryptMainGUI() {
        super();
        initialize();
    }

    private void initialize() {
        config = Configuration.getInstance();
        setPanelIcon("/images/encrypt.png");
        setPreferredSize(new Dimension(500, 700));
        //        
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

        selectionPanel.addPopupMenuAction(new SetOutputPathSelectionTableAction(selectionPanel, destFolderText, null));

        mainOptionsPanel.setLayout(new GridBagLayout());
        GridBagConstraints mainCons = new GridBagConstraints();
        mainCons.fill = GridBagConstraints.BOTH;
        mainCons.ipady = 5;
        mainCons.insets = new Insets(0, 0, 5, 0);
        mainCons.weightx = 1.0;
        mainCons.weighty = 1.0;
        mainCons.gridwidth = 3;
        mainCons.gridx = 0;
        mainCons.gridy = 0;

        // ENCRYPT_SECTION
        encryptOptionsPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config
                .getI18nResourceBundle(), "Encrypt options")));
        optionsPanelLayout = new SpringLayout();
        encryptOptionsPanel.setLayout(optionsPanelLayout);

        encryptPwdsPanel.setLayout(new BoxLayout(encryptPwdsPanel, BoxLayout.X_AXIS));
        encryptPwdsPanel.setMinimumSize(new Dimension(330, 70));
        encryptPwdsPanel.setMaximumSize(new Dimension(400, 90));

        labelsPanelLayout = new SpringLayout();
        labelsPanel.setLayout(labelsPanelLayout);
        labelsPanel.setPreferredSize(new Dimension(140, 80));
        fieldsPanelLayout = new SpringLayout();
        fieldsPanel.setLayout(fieldsPanelLayout);
        fieldsPanel.setPreferredSize(new Dimension(140, 80));

        ownerPwdLabel.setPreferredSize(new Dimension(140, 20));
        ownerPwdLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(), "Owner password:"));
        labelsPanel.add(ownerPwdLabel);

        ownerPwdField = new JTextField();
        ownerPwdField.setPreferredSize(new Dimension(140, 20));
        ownerPwdField.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),
                "Owner password (Max 32 chars long)"));
        ownerPwdField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        fieldsPanel.add(ownerPwdField);

        userPwdLabel.setPreferredSize(new Dimension(140, 20));
        userPwdLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(), "User password:"));
        labelsPanel.add(userPwdLabel);

        userPwdField = new JTextField();
        userPwdField.setPreferredSize(new Dimension(140, 20));
        userPwdField.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(),
                "User password (Max 32 chars long)"));
        userPwdField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        fieldsPanel.add(userPwdField);

        encryptTypeLabel.setPreferredSize(new Dimension(140, 20));
        encryptTypeLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(), "Encryption algorithm:"));
        labelsPanel.add(encryptTypeLabel);

        encryptPwdsPanel.add(labelsPanel);
        encryptPwdsPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        encryptPwdsPanel.add(fieldsPanel);

        encryptOptsPanel.setLayout(new GridLayout(3, 3, 5, 5));

        allowAllCheck.setText(GettextResource.gettext(config.getI18nResourceBundle(), "Allow all"));
        encryptOptsPanel.add(allowAllCheck);

        permissionsCheck[EncryptMainGUI.PRINT] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),
                "Print"));
        encryptOptsPanel.add(permissionsCheck[EncryptMainGUI.PRINT]);

        permissionsCheck[EncryptMainGUI.DPRINT] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),
                "Low quality print"));
        permissionsCheck[EncryptMainGUI.DPRINT].setEnabled(false);
        encryptOptsPanel.add(permissionsCheck[EncryptMainGUI.DPRINT]);

        permissionsCheck[EncryptMainGUI.COPY] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),
                "Copy or extract"));
        encryptOptsPanel.add(permissionsCheck[EncryptMainGUI.COPY]);

        permissionsCheck[EncryptMainGUI.MODIFY] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),
                "Modify"));
        encryptOptsPanel.add(permissionsCheck[EncryptMainGUI.MODIFY]);

        permissionsCheck[EncryptMainGUI.ANNOTATION] = new JCheckBox(GettextResource.gettext(config
                .getI18nResourceBundle(), "Add or modify text annotations"));
        encryptOptsPanel.add(permissionsCheck[EncryptMainGUI.ANNOTATION]);

        permissionsCheck[EncryptMainGUI.FILL] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),
                "Fill form fields"));
        permissionsCheck[EncryptMainGUI.FILL].setEnabled(false);
        encryptOptsPanel.add(permissionsCheck[EncryptMainGUI.FILL]);

        permissionsCheck[EncryptMainGUI.SCREEN] = new JCheckBox(GettextResource.gettext(config.getI18nResourceBundle(),
                "Extract for use by accessibility dev."));
        permissionsCheck[EncryptMainGUI.SCREEN].setEnabled(false);
        encryptOptsPanel.add(permissionsCheck[EncryptMainGUI.SCREEN]);

        permissionsCheck[EncryptMainGUI.ASSEMBLY] = new JCheckBox(GettextResource.gettext(config
                .getI18nResourceBundle(), "Manipulate pages and add bookmarks"));
        permissionsCheck[EncryptMainGUI.ASSEMBLY].setEnabled(false);
        encryptOptsPanel.add(permissionsCheck[EncryptMainGUI.ASSEMBLY]);

        encryptOptionsPanel.setMinimumSize(new Dimension(200, 190));
        encryptOptionsPanel.setPreferredSize(new Dimension(200, 190));
        encryptOptionsPanel.add(encryptPwdsPanel);
        encryptOptionsPanel.add(encryptOptsPanel);

        // END_ENCRYPT_SECTION

        // UNSELECT_OTHERS_LISTENER
        allowAllCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (allowAllCheck.isSelected()) {
                    for (int i = 0; i < permissionsCheck.length; i++) {
                        permissionsCheck[i].setEnabled(false);
                    }
                } else {
                    String encType = (String) encryptType.getSelectedItem();
                    if (encType.equals(EncryptionUtility.RC4_40)) {
                        permissionsCheck[EncryptMainGUI.PRINT].setEnabled(true);
                        permissionsCheck[EncryptMainGUI.DPRINT].setEnabled(false);
                        permissionsCheck[EncryptMainGUI.COPY].setEnabled(true);
                        permissionsCheck[EncryptMainGUI.MODIFY].setEnabled(true);
                        permissionsCheck[EncryptMainGUI.ANNOTATION].setEnabled(true);
                        permissionsCheck[EncryptMainGUI.FILL].setEnabled(false);
                        permissionsCheck[EncryptMainGUI.SCREEN].setEnabled(false);
                        permissionsCheck[EncryptMainGUI.ASSEMBLY].setEnabled(false);
                    } else {
                        for (int i = 0; i < permissionsCheck.length; i++) {
                            permissionsCheck[i].setEnabled(true);
                        }
                    }
                }
            }
        });
        // END_RADIO_LISTENERS
        // DESTINATION_PANEL
        destinationPanelLayout = new SpringLayout();
        destinationPanel.setLayout(destinationPanelLayout);
        destinationPanel.setPreferredSize(new Dimension(200, 160));
        destinationPanel.setMinimumSize(new Dimension(160, 150));
        destinationPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config
                .getI18nResourceBundle(), "Destination folder")));
        // END_DESTINATION_PANEL

        destinationPanel.add(destFolderText);

        // CHECK_BOX
        overwriteCheckbox.setSelected(true);
        destinationPanel.add(overwriteCheckbox);

        outputCompressedCheck.addItemListener(new CompressCheckBoxItemListener(versionCombo));
        outputCompressedCheck.setSelected(true);
        destinationPanel.add(outputCompressedCheck);

        destinationPanel.add(versionCombo);
        // END_CHECK_BOX
        destinationPanel.add(outputVersionLabel);
        browseDestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = SharedJFileChooser.getInstance(SharedJFileChooserType.NO_FILTER,
                        JFileChooser.DIRECTORIES_ONLY, destFolderText.getText());
                if (fileChooser.showOpenDialog(browseDestButton.getParent()) == JFileChooser.APPROVE_OPTION) {
                    File chosenFile = fileChooser.getSelectedFile();
                    if (chosenFile != null) {
                        destFolderText.setText(chosenFile.getAbsolutePath());
                    }
                }
            }
        });
        destinationPanel.add(browseDestButton);
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
                        "Check the box if you want to overwrite the output files if they already exist.")
                + "</p>"
                + "<p>"
                + GettextResource.gettext(config.getI18nResourceBundle(),
                        "Check the box if you want compressed output files (Pdf version 1.5 or higher).") + "</p>"
                + "<p>"
                + GettextResource.gettext(config.getI18nResourceBundle(), "Set the pdf version of the ouput document.")
                + "</p>" + "</body></html>";
        destinationHelpLabel = new JHelpLabel(helpTextDest, true);
        destinationPanel.add(destinationHelpLabel);
        // END_HELP_LABEL_DESTINATION

        String[] eTypes = { EncryptionUtility.RC4_40, EncryptionUtility.RC4_128, EncryptionUtility.AES_128 };
        encryptType = new JComboBox(eTypes);
        encryptType
                .addItemListener(new EncryptionTypeComboActionListener(allowAllCheck, permissionsCheck, versionCombo));
        encryptType.setPreferredSize(new Dimension(140, 20));
        encryptType.setSelectedIndex(0);
        fieldsPanel.add(encryptType);

        // S_PANEL
        outputOptionsPanel.setBorder(BorderFactory.createTitledBorder(GettextResource.gettext(config
                .getI18nResourceBundle(), "Output options")));
        outputOptionsPanel.setPreferredSize(new Dimension(200, 55));
        outputOptionsPanel.setMinimumSize(new Dimension(160, 50));
        encryptPanelLayout = new SpringLayout();
        outputOptionsPanel.setLayout(encryptPanelLayout);

        mainOptionsPanel.add(encryptOptionsPanel, mainCons);
        mainCons.fill = GridBagConstraints.HORIZONTAL;
        mainCons.weightx = 0.0;
        mainCons.weighty = 0.0;
        mainCons.gridy = 1;
        mainOptionsPanel.add(destinationPanel, mainCons);
        mainCons.fill = GridBagConstraints.HORIZONTAL;
        mainCons.weightx = 0.0;
        mainCons.weighty = 0.0;
        mainCons.gridy = 2;
        mainOptionsPanel.add(outputOptionsPanel, mainCons);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 5;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        add(mainOptionsPanel, c);

        outPrefixLabel.setText(GettextResource.gettext(config.getI18nResourceBundle(), "Output file names prefix:"));
        outputOptionsPanel.add(outPrefixLabel);

        outPrefixTextField.setPreferredSize(new Dimension(180, 20));
        outputOptionsPanel.add(outPrefixTextField);
        // END_S_PANEL
        // HELP_LABEL_PREFIX
        String helpTextPrefix = "<html><body><b>"
                + GettextResource.gettext(config.getI18nResourceBundle(), "Output files prefix")
                + "</b>"
                + "<p> "
                + GettextResource.gettext(config.getI18nResourceBundle(),
                        "If it contains \"[TIMESTAMP]\" it performs variable substitution.")
                + "</p>"
                + "<p> "
                + GettextResource.gettext(config.getI18nResourceBundle(),
                        "Ex. [BASENAME]_prefix_[TIMESTAMP] generates FileName_prefix_20070517_113423471.pdf.")
                + "</p>"
                + "<br><p> "
                + GettextResource.gettext(config.getI18nResourceBundle(),
                        "If it doesn't contain \"[TIMESTAMP]\" it generates oldstyle output file names.")
                + "</p>"
                + "<br><p> "
                + GettextResource.gettext(config.getI18nResourceBundle(),
                        "Available variables: [TIMESTAMP], [BASENAME].") + "</p>" + "</body></html>";
        prefixHelpLabel = new JHelpLabel(helpTextPrefix, true);
        outputOptionsPanel.add(prefixHelpLabel);
        // END_HELP_LABEL_PREFIX
        // RUN_BUTTON
        // listener
        runButton.addActionListener(new RunButtonActionListener(this));
        runButton.setToolTipText(GettextResource.gettext(config.getI18nResourceBundle(), "Encrypt selected files"));
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
        // KEY_LISTENER
        runButton.addKeyListener(runEnterKeyListener);
        browseDestButton.addKeyListener(browseEnterKeyListener);

        destFolderText.addKeyListener(runEnterKeyListener);

        // LAYOUT
        setLayout();
    }

    /**
     * @return Returns the Plugin author.
     */
    public String getPluginAuthor() {
        return PLUGIN_AUTHOR;
    }

    /**
     * @return Returns the Plugin name.
     */
    public String getPluginName() {
        return GettextResource.gettext(config.getI18nResourceBundle(), "Encrypt");
    }

    /**
     * @return Returns the version.
     */
    public String getVersion() {
        return PLUGIN_VERSION;
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

                Element allowall = ((Element) arg0).addElement("allowall");
                if (allowAllCheck.isSelected()) {
                    allowall.addAttribute("value", TRUE);
                } else {
                    Element permissions = ((Element) arg0).addElement("permissions");
                    for (int i = 0; i <= EncryptMainGUI.ANNOTATION; i++) {
                        if (permissionsCheck[i].isSelected()) {
                            Element enabled = permissions.addElement("enabled");
                            enabled.addAttribute("value", Integer.toString(i));
                        }
                    }
                }

                Element ownerPwd = ((Element) arg0).addElement("ownerpwd");
                if (savePasswords) {
                    ownerPwd.addAttribute("value", ownerPwdField.getText());
                }

                Element encType = ((Element) arg0).addElement("enctype");
                encType.addAttribute("value", (String) encryptType.getSelectedItem());

                Element userPwd = ((Element) arg0).addElement("usrpwd");
                if (savePasswords) {
                    userPwd.addAttribute("value", userPwdField.getText());
                }

                Element fileDestination = ((Element) arg0).addElement("destination");
                fileDestination.addAttribute("value", destFolderText.getText());

                Element filePrefix = ((Element) arg0).addElement("prefix");
                filePrefix.addAttribute("value", outPrefixTextField.getText());

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

            Node allowAll = (Node) arg0.selectSingleNode("allowall/@value");
            if (allowAll != null && TRUE.equals(allowAll.getText())) {
                allowAllCheck.doClick();
            } else {
                Node permissions = (Node) arg0.selectSingleNode("permissions");
                if (permissions != null) {
                    List listEnab = permissions.selectNodes("enabled");
                    for (int j = 0; listEnab != null && j < listEnab.size(); j++) {
                        Node enabledNode = (Node) listEnab.get(j);
                        if (enabledNode != null) {
                            permissionsCheck[Integer.parseInt(enabledNode.selectSingleNode("@value").getText())]
                                    .setSelected(true);
                        }
                    }
                }
            }

            Node encType = (Node) arg0.selectSingleNode("enctype/@value");
            if (encType != null) {
                encryptType.setSelectedItem((String) encType.getText());
            }

            Node userPwd = (Node) arg0.selectSingleNode("usrpwd/@value");
            if (userPwd != null) {
                userPwdField.setText(userPwd.getText());
            }

            Node ownerPwd = (Node) arg0.selectSingleNode("ownerpwd/@value");
            if (ownerPwd != null) {
                ownerPwdField.setText(ownerPwd.getText());
            }

            Node fileDestination = (Node) arg0.selectSingleNode("destination/@value");
            if (fileDestination != null) {
                destFolderText.setText(fileDestination.getText());
            }

            Node fileOverwrite = (Node) arg0.selectSingleNode("overwrite/@value");
            if (fileOverwrite != null) {
                overwriteCheckbox.setSelected(TRUE.equals(fileOverwrite.getText()));
            }

            Node fileCompressed = (Node) arg0.selectSingleNode("compressed/@value");
            if (fileCompressed != null && TRUE.equals(fileCompressed.getText())) {
                outputCompressedCheck.doClick();
            }

            Node filePrefix = (Node) arg0.selectSingleNode("prefix/@value");
            if (filePrefix != null) {
                outPrefixTextField.setText(filePrefix.getText());
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
            log.info(GettextResource.gettext(config.getI18nResourceBundle(), "Encrypt section loaded."));
        } catch (Exception ex) {
            log.error(GettextResource.gettext(config.getI18nResourceBundle(), "Error: "), ex);
        }
    }

    /**
     * Set plugin layout for each component
     * 
     */
    private void setLayout() {
        // LAYOUT

        destinationPanelLayout.putConstraint(SpringLayout.EAST, destFolderText, -105, SpringLayout.EAST,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, destFolderText, 10, SpringLayout.NORTH,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destFolderText, 30, SpringLayout.NORTH,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, destFolderText, 5, SpringLayout.WEST, destinationPanel);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, overwriteCheckbox, 17, SpringLayout.NORTH,
                overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, overwriteCheckbox, 5, SpringLayout.SOUTH,
                destFolderText);
        destinationPanelLayout
                .putConstraint(SpringLayout.WEST, overwriteCheckbox, 0, SpringLayout.WEST, destFolderText);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputCompressedCheck, 17, SpringLayout.NORTH,
                outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputCompressedCheck, 5, SpringLayout.SOUTH,
                overwriteCheckbox);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputCompressedCheck, 0, SpringLayout.WEST,
                destFolderText);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, outputVersionLabel, 17, SpringLayout.NORTH,
                outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, outputVersionLabel, 8, SpringLayout.SOUTH,
                outputCompressedCheck);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, outputVersionLabel, 0, SpringLayout.WEST,
                destFolderText);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, versionCombo, 0, SpringLayout.SOUTH,
                outputVersionLabel);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, versionCombo, 2, SpringLayout.EAST, outputVersionLabel);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, browseDestButton, 25, SpringLayout.NORTH,
                browseDestButton);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, browseDestButton, -5, SpringLayout.EAST,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.NORTH, browseDestButton, 0, SpringLayout.NORTH,
                destFolderText);
        destinationPanelLayout.putConstraint(SpringLayout.WEST, browseDestButton, -93, SpringLayout.EAST,
                destinationPanel);

        destinationPanelLayout.putConstraint(SpringLayout.SOUTH, destinationHelpLabel, -1, SpringLayout.SOUTH,
                destinationPanel);
        destinationPanelLayout.putConstraint(SpringLayout.EAST, destinationHelpLabel, -1, SpringLayout.EAST,
                destinationPanel);

        labelsPanelLayout.putConstraint(SpringLayout.NORTH, ownerPwdLabel, 5, SpringLayout.NORTH, labelsPanel);
        labelsPanelLayout.putConstraint(SpringLayout.WEST, ownerPwdLabel, 0, SpringLayout.WEST, labelsPanel);
        labelsPanelLayout.putConstraint(SpringLayout.NORTH, userPwdLabel, 5, SpringLayout.SOUTH, ownerPwdLabel);
        labelsPanelLayout.putConstraint(SpringLayout.WEST, userPwdLabel, 0, SpringLayout.WEST, ownerPwdLabel);
        labelsPanelLayout.putConstraint(SpringLayout.NORTH, encryptTypeLabel, 5, SpringLayout.SOUTH, userPwdLabel);
        labelsPanelLayout.putConstraint(SpringLayout.WEST, encryptTypeLabel, 0, SpringLayout.WEST, userPwdLabel);

        fieldsPanelLayout.putConstraint(SpringLayout.NORTH, ownerPwdField, 5, SpringLayout.NORTH, fieldsPanel);
        fieldsPanelLayout.putConstraint(SpringLayout.WEST, ownerPwdField, 0, SpringLayout.WEST, fieldsPanel);
        fieldsPanelLayout.putConstraint(SpringLayout.NORTH, userPwdField, 5, SpringLayout.SOUTH, ownerPwdField);
        fieldsPanelLayout.putConstraint(SpringLayout.WEST, userPwdField, 0, SpringLayout.WEST, ownerPwdField);
        fieldsPanelLayout.putConstraint(SpringLayout.NORTH, encryptType, 5, SpringLayout.SOUTH, userPwdField);
        fieldsPanelLayout.putConstraint(SpringLayout.WEST, encryptType, 0, SpringLayout.WEST, userPwdField);

        optionsPanelLayout.putConstraint(SpringLayout.NORTH, encryptPwdsPanel, 5, SpringLayout.NORTH,
                encryptOptionsPanel);
        optionsPanelLayout
                .putConstraint(SpringLayout.WEST, encryptPwdsPanel, 5, SpringLayout.WEST, encryptOptionsPanel);
        optionsPanelLayout.putConstraint(SpringLayout.NORTH, encryptOptsPanel, 5, SpringLayout.SOUTH, encryptPwdsPanel);
        optionsPanelLayout.putConstraint(SpringLayout.WEST, encryptOptsPanel, 0, SpringLayout.WEST, encryptPwdsPanel);

        encryptPanelLayout
                .putConstraint(SpringLayout.SOUTH, outPrefixLabel, 20, SpringLayout.NORTH, outputOptionsPanel);
        encryptPanelLayout.putConstraint(SpringLayout.NORTH, outPrefixLabel, 5, SpringLayout.NORTH, outputOptionsPanel);
        encryptPanelLayout.putConstraint(SpringLayout.WEST, outPrefixLabel, 5, SpringLayout.WEST, outputOptionsPanel);
        encryptPanelLayout.putConstraint(SpringLayout.SOUTH, outPrefixTextField, 0, SpringLayout.SOUTH, outPrefixLabel);
        encryptPanelLayout.putConstraint(SpringLayout.WEST, outPrefixTextField, 10, SpringLayout.EAST, outPrefixLabel);
        encryptPanelLayout.putConstraint(SpringLayout.EAST, outPrefixTextField, -30, SpringLayout.EAST,
                outputOptionsPanel);

        encryptPanelLayout.putConstraint(SpringLayout.SOUTH, prefixHelpLabel, -1, SpringLayout.SOUTH,
                outputOptionsPanel);
        encryptPanelLayout.putConstraint(SpringLayout.EAST, prefixHelpLabel, -1, SpringLayout.EAST, outputOptionsPanel);

    }

    public FocusTraversalPolicy getFocusPolicy() {
        return encryptfocusPolicy;

    }

    /**
     * 
     * @author Andrea Vacondio Focus policy for encrypt panel
     * 
     */
    public class EncryptFocusPolicy extends FocusTraversalPolicy {
        public EncryptFocusPolicy() {
            super();
        }

        public Component getComponentAfter(Container CycleRootComp, Component aComponent) {
            if (aComponent.equals(selectionPanel.getAddFileButton())) {
                return selectionPanel.getRemoveFileButton();
            } else if (aComponent.equals(selectionPanel.getRemoveFileButton())) {
                return selectionPanel.getClearButton();
            } else if (aComponent.equals(selectionPanel.getClearButton())) {
                return ownerPwdField;
            } else if (aComponent.equals(ownerPwdField)) {
                return userPwdField;
            } else if (aComponent.equals(userPwdField)) {
                return encryptType;
            } else if (aComponent.equals(encryptType)) {
                return allowAllCheck;
            } else if (aComponent.equals(allowAllCheck)) {
                if (allowAllCheck.isSelected()) {
                    return destFolderText;
                } else {
                    return permissionsCheck[EncryptMainGUI.PRINT];
                }
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.PRINT])) {
                return permissionsCheck[EncryptMainGUI.DPRINT];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.DPRINT])) {
                return permissionsCheck[EncryptMainGUI.COPY];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.COPY])) {
                return permissionsCheck[EncryptMainGUI.MODIFY];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.MODIFY])) {
                return permissionsCheck[EncryptMainGUI.ANNOTATION];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.ANNOTATION])) {
                return permissionsCheck[EncryptMainGUI.FILL];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.FILL])) {
                return permissionsCheck[EncryptMainGUI.SCREEN];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.SCREEN])) {
                return permissionsCheck[EncryptMainGUI.ASSEMBLY];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.ASSEMBLY])) {
                return destFolderText;
            } else if (aComponent.equals(destFolderText)) {
                return browseDestButton;
            } else if (aComponent.equals(browseDestButton)) {
                return overwriteCheckbox;
            } else if (aComponent.equals(overwriteCheckbox)) {
                return outputCompressedCheck;
            } else if (aComponent.equals(outputCompressedCheck)) {
                return versionCombo;
            } else if (aComponent.equals(versionCombo)) {
                return outPrefixTextField;
            } else if (aComponent.equals(outPrefixTextField)) {
                return runButton;
            } else if (aComponent.equals(runButton)) {
                return selectionPanel.getAddFileButton();
            }
            return selectionPanel.getAddFileButton();
        }

        public Component getComponentBefore(Container CycleRootComp, Component aComponent) {

            if (aComponent.equals(runButton)) {
                return outPrefixTextField;
            } else if (aComponent.equals(outPrefixTextField)) {
                return versionCombo;
            } else if (aComponent.equals(versionCombo)) {
                return outputCompressedCheck;
            } else if (aComponent.equals(outputCompressedCheck)) {
                return overwriteCheckbox;
            } else if (aComponent.equals(overwriteCheckbox)) {
                return browseDestButton;
            } else if (aComponent.equals(browseDestButton)) {
                return destFolderText;
            } else if (aComponent.equals(destFolderText)) {
                if (allowAllCheck.isSelected()) {
                    return allowAllCheck;
                } else {
                    return permissionsCheck[EncryptMainGUI.ASSEMBLY];
                }
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.ASSEMBLY])) {
                return permissionsCheck[EncryptMainGUI.SCREEN];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.SCREEN])) {
                return permissionsCheck[EncryptMainGUI.FILL];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.FILL])) {
                return permissionsCheck[EncryptMainGUI.ANNOTATION];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.ANNOTATION])) {
                return permissionsCheck[EncryptMainGUI.MODIFY];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.MODIFY])) {
                return permissionsCheck[EncryptMainGUI.COPY];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.COPY])) {
                return permissionsCheck[EncryptMainGUI.DPRINT];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.DPRINT])) {
                return permissionsCheck[EncryptMainGUI.PRINT];
            } else if (aComponent.equals(permissionsCheck[EncryptMainGUI.PRINT])) {
                return allowAllCheck;
            } else if (aComponent.equals(allowAllCheck)) {
                return encryptType;
            } else if (aComponent.equals(encryptType)) {
                return userPwdField;
            } else if (aComponent.equals(userPwdField)) {
                return ownerPwdField;
            } else if (aComponent.equals(ownerPwdField)) {
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
        destFolderText.setText("");
        versionCombo.resetComponent();
        outputCompressedCheck.setSelected(false);
        overwriteCheckbox.setSelected(false);
        destFolderText.setText("");
        userPwdField.setText("");
        outPrefixTextField.setText("");
        encryptType.setSelectedItem(EncryptionUtility.RC4_40);
        allowAllCheck.setSelected(false);
        for (int i = 0; i < permissionsCheck.length; i++) {
            permissionsCheck[i].setSelected(false);
        }
    }

    /**
     * @return the outPrefixTextField
     */
    public JTextField getOutPrefixTextField() {
        return outPrefixTextField;
    }

    /**
     * @return the destFolderText
     */
    public JTextField getDestFolderText() {
        return destFolderText;
    }

    /**
     * @return the userPwdField
     */
    public JTextField getUserPwdField() {
        return userPwdField;
    }

    /**
     * @return the ownerPwdField
     */
    public JTextField getOwnerPwdField() {
        return ownerPwdField;
    }

    /**
     * @return the encryptType
     */
    public JComboBox getEncryptType() {
        return encryptType;
    }

    /**
     * @return the versionCombo
     */
    public JPdfVersionCombo getVersionCombo() {
        return versionCombo;
    }

    /**
     * @return the permissionsCheck
     */
    public JCheckBox[] getPermissionsCheck() {
        return permissionsCheck;
    }

    /**
     * @return the allowAllCheck
     */
    public JCheckBox getAllowAllCheck() {
        return allowAllCheck;
    }

    /**
     * @return the overwriteCheckbox
     */
    public JCheckBox getOverwriteCheckbox() {
        return overwriteCheckbox;
    }

    /**
     * @return the selectionPanel
     */
    public JPdfSelectionPanel getSelectionPanel() {
        return selectionPanel;
    }

    /**
     * @return the outputCompressedCheck
     */
    public JCheckBox getOutputCompressedCheck() {
        return outputCompressedCheck;
    }

}
